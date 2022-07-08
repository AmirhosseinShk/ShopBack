/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dajikala.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author @AmirShk
 */
public class DijiKala_Crowler {

    private static void ExtractCategeriosLink(String dijikalaFullAddress, String fileName) {
        List<DijiKalaBrand> dijikalaBrands = ExtractDijiKalaData.getInstance().getCategoriesBrands(dijikalaFullAddress, 100);
        DijiKala_Crowler dijiKala_Crowler = new DijiKala_Crowler();
        dijiKala_Crowler.writeOnExcelDijikalaBrandDetails(dijikalaBrands, fileName);
    }

    public ArrayList<BrandLink> readLinksFromExcels(String path, boolean isPersian) throws IOException {
        ArrayList<BrandLink> name_links = new ArrayList<BrandLink>();
        //obtaining input bytes from a file  
        FileInputStream fis = new FileInputStream(new File(path));
        //creating workbook instance that refers to .xls file  
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        //creating a Sheet object to retrieve the object  
        XSSFSheet sheet = wb.getSheetAt(0);
        boolean skipFirst = false;
        //evaluating cell type   
        for (Row row : sheet) {
            String brand = "";
            String link = "";
            int counter = 0;
            int isExist = 0;
            boolean persianBrand = false;
            for (Cell cell : row) {
                if (!skipFirst) {
                    skipFirst = true;
                    continue;
                }
                counter++;
                switch (counter) {
                    case 1:
                        if (cell.getCellType() == CellType.STRING) {
                            if (cell.getStringCellValue().equals("ایرانی")) {
                                persianBrand = true;
                            }
                        }
                        break;
                    case 2:
                        if (cell.getCellType() == CellType.NUMERIC) {
                            isExist = (int) cell.getNumericCellValue();
                        }
                        break;
                    case 5:
                        if (cell.getCellType() == CellType.STRING) {
                            link = cell.getStringCellValue();
                        }
                        break;
                }
            }
            if (isExist != 0 && isPersian == persianBrand) {
                brand = link.split("/")[4];
                name_links.add(new BrandLink(brand, link));
            }
        }
        return name_links;
    }

    public static void main(String[] args) throws IOException {
        ExtractDijikalaDataPerLink("D:/Work/Tavally/RussianShopBack/All_Brands/All_Brands2.xlsx", true); // ta kaveh 
        //ExtractCategeriosLink("https://api.digikala.com/v1/categories/telescope/search/?price[min]=20000000&price[max]=1000000000", "category-telescope");
    }

    private static void ExtractDijikalaDataPerLink(String filePath, boolean isPersian) throws IOException {
        DijiKala_Crowler dijiKala_Crowler = new DijiKala_Crowler();
        ArrayList<BrandLink> name_links = dijiKala_Crowler.readLinksFromExcels(filePath, isPersian);
        //Enter Ur link
        for (int j = 0; j < name_links.size(); j++) {
            String brand = name_links.get(j).brand;
            String link = name_links.get(j).link;
            if (link.startsWith("https")) {
                List<String> links = dijiKala_Crowler.getBrandKalas(brand);
                ExtractDijiKalaData edkd = new ExtractDijiKalaData();
                List<DijiKalaData> dijiKalaDataInfo = new ArrayList<>();
                for (int i = 0; i < links.size(); i++) {
                    DijiKalaData dkd = edkd.getLinkData(links.get(i));
                    if (dkd != null) {
                        dkd.brandLink = link;
                        dkd.link = "https://www.digikala.com/product/dkp-" + links.get(i);
                        dkd.id = links.get(i);
                        //we are looking for more than 3.5 scores
                        if (Double.parseDouble(dkd.score.split("\\(")[0]) >= 3.5) {
                            dijiKalaDataInfo.add(dkd);
                        } else {
                            System.out.println("Score is lower :" + Double.parseDouble(dkd.score.split("\\(")[0]));
                        }
                    }
                }
                dijiKala_Crowler.writeOnExcelDijikalaDataDetails(dijiKalaDataInfo, brand);
            }
        }
    }

    public List<String> getBrandKalas(String brand) throws IOException {
        List<String> allKalaLinks = new ArrayList<>();
        Client client = ClientBuilder.newClient();
        int total_pages = 1;
        int current_pages = 1;
        String pageUri = "/?page=";
        boolean pages_calculated = false;
        do {
            System.out.println("https://api.digikala.com/v1/brands/" + brand + pageUri + current_pages);
            WebTarget resource = client.target("https://api.digikala.com/v1/brands/" + brand + pageUri + current_pages);

            Invocation.Builder request = resource.request();
            request.accept(MediaType.APPLICATION_JSON);
            Response response = request.get();
            JSONObject jsonObject = new JSONObject(response.readEntity(String.class));
            if (jsonObject.getInt("status") == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray products = data.getJSONArray("products");
                if (!pages_calculated) {
                    JSONObject pager = data.getJSONObject("pager");
                    total_pages = pager.getInt("total_pages");
                    pages_calculated = true;
                }

                for (int j = 0; j < products.length(); j++) {
                    JSONObject product = products.getJSONObject(j);
                    if (product != null) {
                        JSONObject url = product.getJSONObject("url");
                        if (url != null) {
                            String uri[] = url.getString("uri").split("/");
                            allKalaLinks.add(uri[2].split("-")[1]);
                        }
                    }
                }
                current_pages++;
            } else if (jsonObject.getInt("status") == 301) {
                pageUri = "/?no_redirect=1&page=";
                total_pages = 100;
                pages_calculated = true;
            }
        } while (current_pages <= total_pages);
        return allKalaLinks;
    }

    public void writeOnExcelDijikalaDataDetails(List<DijiKalaData> dijiKalaDatas, String fileName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Kala Lists");
        createHeaderRowDijikalaDataDetails(sheet);

        for (int i = 0; i < dijiKalaDatas.size(); i++) {
            DijiKalaData kala = dijiKalaDatas.get(i);
            Row row = sheet.createRow(i + 1);
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
            Cell cellDisCountPrice = row.createCell(5);
            cellDisCountPrice.setCellValue(kala.disCountPrice);
            Cell cellLink = row.createCell(6);
            cellLink.setCellValue(kala.link);
            Cell cellBrandLink = row.createCell(7);
            cellBrandLink.setCellValue(kala.brandLink);
            Cell cellDimention = row.createCell(8);
            cellDimention.setCellValue(kala.dimension);
            Cell cellWeight = row.createCell(9);
            cellWeight.setCellValue(kala.weight);
        }

        try (FileOutputStream outputStream = new FileOutputStream("ExcelsNew/" + fileName + ".xlsx")) {
            workbook.write(outputStream);
        } catch (IOException ex) {
            Logger.getLogger(DijiKala_Crowler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createHeaderRowDijikalaDataDetails(Sheet sheet) {

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

        Cell cellDisCountPrice = row.createCell(5);
        cellDisCountPrice.setCellStyle(cellStyle);
        cellDisCountPrice.setCellValue("DisCountPrice");

        Cell cellLink = row.createCell(6);
        cellLink.setCellStyle(cellStyle);
        cellLink.setCellValue("Link");

        Cell cellBrandLink = row.createCell(7);
        cellBrandLink.setCellStyle(cellStyle);
        cellBrandLink.setCellValue("BrandLink");

        Cell cellIDimention = row.createCell(8);
        cellIDimention.setCellStyle(cellStyle);
        cellIDimention.setCellValue("Dimention");

        Cell cellIWeight = row.createCell(9);
        cellIWeight.setCellStyle(cellStyle);
        cellIWeight.setCellValue("Weight");

    }

    public static void writeOnExcelDijikalaBrandDetails(List<DijiKalaBrand> dijiKalaBrands, String fileName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Brand Lists");
        createHeaderRowDijikalaBrandDetails(sheet);

        for (int i = 0; i < dijiKalaBrands.size(); i++) {
            DijiKalaBrand brand = dijiKalaBrands.get(i);
            Row row = sheet.createRow(i + 1);
            Cell cellId = row.createCell(0);
            cellId.setCellValue(brand.id);
            Cell cellTitlePersian = row.createCell(1);
            cellTitlePersian.setCellValue(brand.titlePersian);
            Cell cellTitleEnglish = row.createCell(2);
            cellTitleEnglish.setCellValue(brand.titleEnglish);
            Cell cellUri = row.createCell(3);
            cellUri.setCellValue(brand.dijikala_uri);
            Cell cellDiscription = row.createCell(4);
            cellDiscription.setCellValue(brand.discription);

        }

        try (FileOutputStream outputStream = new FileOutputStream("Brands/\\NewBrandsWithDiscription/" + fileName + ".xlsx")) {
            workbook.write(outputStream);
        } catch (IOException ex) {
            Logger.getLogger(DijiKala_Crowler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void createHeaderRowDijikalaBrandDetails(Sheet sheet) {

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        cellStyle.setFont(font);

        Row row = sheet.createRow(0);

        Cell cellId = row.createCell(0);

        cellId.setCellStyle(cellStyle);
        cellId.setCellValue("ID");

        Cell cellPersianTitle = row.createCell(1);

        cellPersianTitle.setCellStyle(cellStyle);
        cellPersianTitle.setCellValue("PersianTitle");

        Cell cellEnglishTitle = row.createCell(2);
        cellEnglishTitle.setCellStyle(cellStyle);
        cellEnglishTitle.setCellValue("EnglishTitle");

        Cell cellDijiKalaBrandLink = row.createCell(3);
        cellDijiKalaBrandLink.setCellStyle(cellStyle);
        cellDijiKalaBrandLink.setCellValue("DijiKalaBrandLink");

        Cell cellCounter = row.createCell(4);
        cellCounter.setCellStyle(cellStyle);
        cellCounter.setCellValue("Discription");
    }
}

class BrandLink {

    String brand;
    String link;

    public BrandLink(String brand, String link) {
        this.brand = brand;
        this.link = link;
    }

}
