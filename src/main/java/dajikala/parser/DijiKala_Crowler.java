/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dajikala.parser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import server.properties.ProjectProperties;

/**
 *
 * @author @AmirShk
 */
public class DijiKala_Crowler {

    public static void main(String[] args) throws IOException {
        DijiKala_Crowler dijiKala_Crowler = new DijiKala_Crowler();
        //Enter Ur link
        String link = "https://www.digikala.com/search/category-men-clothing/?brand[0]=1385&pageno=1&last_filter=brand&last_value=20988&sortby=4";
        List<String> links = dijiKala_Crowler.getBrandKalas(link);
        ExtractDijiKalaData edkd = new ExtractDijiKalaData();
        List<DijiKalaData> dijiKalaDataInfo = new ArrayList<>();
        for (int i = 0; i < links.size(); i++) {
            DijiKalaData dkd = edkd.getLinkData(links.get(i));
            dijiKalaDataInfo.add(dkd);
        }
        dijiKala_Crowler.writeOnExcel(dijiKalaDataInfo);
    }

    public List<String> getBrandKalas(String link) throws IOException {
        List<String> allKalaLinks = new ArrayList<>();
        Document doc = getLinkDocument(link);
        Elements pages = doc.getElementsByClass("c-pager__item");
        List<String> nextPages = new ArrayList<>();
        for (int i = 0; i < pages.size(); i++) {
            Element aTag = pages.get(i);
            String href = aTag.attr("href");
            if (href != null && href.contains("/")) {
                nextPages.add(href);
            }
        }
        allKalaLinks = getPageKalas(doc);
        for (int j = 0; j < nextPages.size(); j++) {
            Document root = getLinkDocument("https://www.digikala.com" + nextPages.get(j));
            allKalaLinks.addAll(getPageKalas(root));
        }
        return allKalaLinks;
    }

    private List<String> getPageKalas(Document doc) {
        List<String> kalasLinks = new ArrayList<>();
        Elements kalas = doc.getElementsByClass("c-product-box__img c-promotion-box__image js-url js-product-item js-product-url");
        for (int i = 0; i < kalas.size(); i++) {
            String href = kalas.get(i).attr("href");
            String[] hrefPart = href.split("/");
            if (hrefPart[1].equals("product") && hrefPart[2].contains("dkp")) {
                kalasLinks.add(hrefPart[2]);
            }
        }
        return kalasLinks;
    }

    private Document getLinkDocument(String link) throws IOException {
        Connection conncetion = Jsoup.connect(link);
        Document doc = conncetion.header("Accept-Encoding", "gzip, deflate")
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                .maxBodySize(0)
                .timeout(600000)
                .get();
        return doc;
    }

    public void writeOnExcel(List<DijiKalaData> dijiKalaDatas) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Kala Lists");
        createHeaderRow(sheet);

        for (int i = 0; i < dijiKalaDatas.size(); i++) {
            DijiKalaData kala = dijiKalaDatas.get(i);
            Row row = sheet.createRow(i+1);
            Cell cellId = row.createCell(0);
            cellId.setCellValue(kala.id);
            Cell cellName = row.createCell(1);
            cellName.setCellValue(kala.persianName);
            Cell cellScore = row.createCell(2);
            cellScore.setCellValue(kala.score);
            Cell cellIsExist = row.createCell(3);
            cellIsExist.setCellValue(kala.isExist);
            Cell cellPrice = row.createCell(4);
            cellPrice.setCellValue(kala.price);
            Cell cellLink = row.createCell(5);
            cellLink.setCellValue(kala.link);
        }

        try (FileOutputStream outputStream = new FileOutputStream(ProjectProperties.getInstance().getProperty("dijikala.Crowler.saveLocaiton"))) {
            workbook.write(outputStream);
        } catch (IOException ex) {
            Logger.getLogger(DijiKala_Crowler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createHeaderRow(Sheet sheet) {

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        cellStyle.setFont(font);

        Row row = sheet.createRow(0);
        Cell cellId = row.createCell(0);

        cellId.setCellStyle(cellStyle);
        cellId.setCellValue("Id");

        Cell cellName = row.createCell(1);
        cellName.setCellStyle(cellStyle);
        cellName.setCellValue("PersianName");

        Cell cellScore = row.createCell(2);
        cellScore.setCellStyle(cellStyle);
        cellScore.setCellValue("Score");

        Cell cellIsExist = row.createCell(3);
        cellIsExist.setCellStyle(cellStyle);
        cellIsExist.setCellValue("IsExist");

        Cell cellPrice = row.createCell(4);
        cellPrice.setCellStyle(cellStyle);
        cellPrice.setCellValue("Price");

        Cell cellLink = row.createCell(5);
        cellLink.setCellStyle(cellStyle);
        cellLink.setCellValue("Link");

    }
}
