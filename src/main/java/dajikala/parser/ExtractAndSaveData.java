/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dajikala.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import server.database.Connector;
import server.database.items.Commodity;

/**
 *
 * @author @AmirShk
 */
public class ExtractAndSaveData {

    public static void main(String[] args) throws IOException, SQLException, NoSuchAlgorithmException {
//        ExtractDijiKalaData ekdk = new ExtractDijiKalaData();
//        DijiKalaData dkd = ekdk.getLinkData("183985");
//        System.out.println(dkd.brand);
        //17130 arm db Last id
        UpdateDataFromExelWithDimension("D:/Work/Tavally/RussianShopBack/Labeled", false);
        //addDataFromExel("D:/Work/Tavally/RussianShopBack/Brands/arm/nakhordni", false);
        //addAllBrandsData("D:/Work/Tavally/RussianShopBack/Brands/arm/nakhordni", false);
        //addAllBrandsData("D:/Work/Tavally/RussianShopBack/Brands/russ/nakhordni", true);
        //getAllDistincBrandsData("D:/Work/Tavally/RussianShopBack/Brands/Categories/test");
        // getAllDistincBrandsDataWithDiscriptions("D:/Work/Tavally/RussianShopBack/Brands/Categories/AllBrands");
    }

    private static void addAllBrandsData(String xlsxPath, boolean isRuss) throws IOException, SQLException {
        ExtractAndSaveData esd = new ExtractAndSaveData();
        File folderPath = new File(xlsxPath);
        Collection<File> files = FileUtils.listFiles(folderPath, new String[]{"xlsx"}, true);
        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            List<Commodity> commodities = esd.readDataFromExcels(file, true);
            for (int i = 0; i < commodities.size(); i++) {
                // Insert Data in DataBases
                if (isRuss) {
                    Connector.getInstance().InsertRuCommodity(commodities.get(i), true);
                } else {
                    Connector.getInstance().InsertARMCommodity(commodities.get(i), true);
                }
            }
        }
    }

    private static void addDataFromExel(String xlsxPath, boolean isRuss) throws SQLException, IOException {
        //Excel  Folder path
        ExtractAndSaveData esd = new ExtractAndSaveData();
        File folderPath = new File(xlsxPath);
        Collection<File> files = FileUtils.listFiles(folderPath, new String[]{"xlsx"}, true);
        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            List<Commodity> commodities = esd.readDataFromExcels(file, false);
            for (int i = 0; i < commodities.size(); i++) {
                // Insert Data in DataBases
                if (isRuss) {
                    Connector.getInstance().InsertRuCommodity(commodities.get(i), false);
                } else {
                    Connector.getInstance().InsertARMCommodity(commodities.get(i), false);
                }
            }
        }
    }

    private static void getAllDistincBrandsDataWithDiscriptions(String xlsxPath) throws IOException, SQLException {
        File folderPath = new File(xlsxPath);
        Collection<File> files = FileUtils.listFiles(folderPath, new String[]{"xlsx"}, true);
        Iterator<File> iterator = files.iterator();
        List<DijiKalaBrand> allBrands = new ArrayList<>();
        List<Integer> allBrandsId = new ArrayList<>();
        while (iterator.hasNext()) {
            File file = iterator.next();
            //obtaining input bytes from a file  
            FileInputStream fis = new FileInputStream(file);
            //creating workbook instance that refers to .xls file  
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            //creating a Sheet object to retrieve the object  
            XSSFSheet sheet = wb.getSheetAt(0);
            ExtractDijiKalaData edkd = new ExtractDijiKalaData();
            //evaluating cell type   
            boolean skipFirstRow = false;
            for (Row row : sheet) {
                Iterator<Cell> iterator2 = row.cellIterator();
                int counter = 0;
                if (!skipFirstRow) {
                    skipFirstRow = true;
                    continue;
                }
                while (iterator2.hasNext()) {
                    Cell cell = iterator2.next();
                    counter++;
                    if (counter == 3) {
                        if (cell.getCellType() == CellType.STRING) {
                            if (cell.getStringCellValue().contains("brand")) {
                                String brandLink = cell.getStringCellValue().split("/")[4];
                                DijiKalaBrand brandData = edkd.getBrandDiscription(brandLink);
                                brandData.dijikala_uri = cell.getStringCellValue();
                                if (!allBrandsId.contains(brandData.id)) {
                                    allBrands.add(brandData);
                                    allBrandsId.add(brandData.id);
                                }
                            }
                        }
                    }
                }
            }
            System.out.println(file.getName() + "it's Done!");
        }
        DijiKala_Crowler.writeOnExcelDijikalaBrandDetails(allBrands, "all_Brands");
    }

    private static void getAllDistincBrandsData(String xlsxPath) throws IOException, SQLException {
        ExtractAndSaveData esd = new ExtractAndSaveData();
        File folderPath = new File(xlsxPath);
        Collection<File> files = FileUtils.listFiles(folderPath, new String[]{"xlsx"}, true);
        Iterator<File> iterator = files.iterator();
        Map<String, DijiKalaBrand> allBrands = new HashMap<>();
        while (iterator.hasNext()) {
            File file = iterator.next();
            List<DijiKalaBrand> brands = readBrandsFromExcels(file);
            for (int i = 0; i < brands.size(); i++) {
                if (!allBrands.containsKey(brands.get(i).titleEnglish)) {
                    allBrands.put(brands.get(i).titleEnglish, brands.get(i));
                } else {
                    allBrands.get(brands.get(i).titleEnglish).counter += brands.get(i).counter;
                }
            }
        }
        DijiKala_Crowler.writeOnExcelDijikalaBrandDetails(new ArrayList(allBrands.values()), "Mod_Poshak");
    }

    private static void UpdateDataFromExelWithDimension(String xlsxPath, boolean isRuss) throws SQLException, IOException {
        //Excel  Folder path
        ExtractAndSaveData esd = new ExtractAndSaveData();
        File folderPath = new File(xlsxPath);
        Collection<File> files = FileUtils.listFiles(folderPath, new String[]{"xlsx"}, true);
        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            List<DijiKalaData> commodities = esd.readDataFromExcels_Dimension_Label(file);
            for (int i = 0; i < commodities.size(); i++) {
                Commodity res = Connector.getInstance().getAllCarpetWithoutDimentions(!isRuss, commodities.get(i).id);
                if (res != null) {
                    System.out.println(commodities.get(i).id);
                    Connector.getInstance().updateDimension(!isRuss, commodities.get(i).id, commodities.get(i).dimension, commodities.get(i).weight, commodities.get(i).label);
                } else {
                    System.out.println("WTF is this : ????" + commodities.get(i).id);
                }
            }
        }
    }

    public List<Commodity> readDataFromExcels(File file, boolean isAllBrands) throws IOException {
        List<Commodity> commodities = new ArrayList<>();
        //obtaining input bytes from a file  
        FileInputStream fis = new FileInputStream(file);
        //creating workbook instance that refers to .xls file  
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        //creating a Sheet object to retrieve the object  
        XSSFSheet sheet = wb.getSheetAt(0);
        ExtractDijiKalaData edkd = new ExtractDijiKalaData();
        //evaluating cell type   
        for (Row row : sheet) {
            String name = "-1";
            String link = "";
            String brand = "";
            String dimention = "";
            int counter = 0;
            Iterator<Cell> iterator = row.cellIterator();
            while (iterator.hasNext()) {
                Cell cell = iterator.next();
                counter++;
                if (counter == 1) {
                    if (cell.getCellType() == CellType.STRING) {
                        link = cell.getStringCellValue();
                    }
                }
                if (counter == 2) {
                    if (cell.getCellType() == CellType.STRING) {
                        name = cell.getStringCellValue();
                    }
                }
                if (counter == 7) {
                    if (cell.getCellType() == CellType.STRING) {
                        dimention = cell.getStringCellValue();
                    }
                }
                if (counter == 4) {
                    if (cell.getCellType() == CellType.STRING) {
                        brand = cell.getStringCellValue();
                    }
                }
            }
            if (link.equals("dkd-Link")) {
                continue;
            }
            if (!name.equals("-1")) {
                DijiKalaData dkd = edkd.getLinkData(link.split("-")[1]);
                if (dkd != null) {
                    dkd.id = link;
                    dkd.brand = brand;
                    if (isAllBrands) {
                        if (dkd.price != "") {
                            commodities.add(convertDigikalaDataToCommodity(dkd, name));
                            return commodities;
                        }
                    } else {
                        if (dkd.dimension == null) {
                            dkd.dimension = dimention;
                        }
                        commodities.add(convertDigikalaDataToCommodity(dkd, name));
                    }
                }
            }
        }
        return commodities;
    }

    private static List<DijiKalaData> readSomeDataFromExcels_Dimension(File file) throws IOException {
        List<DijiKalaData> commodities = new ArrayList<>();
        //obtaining input bytes from a file  
        FileInputStream fis = new FileInputStream(file);
        //creating workbook instance that refers to .xls file  
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        //creating a Sheet object to retrieve the object  
        XSSFSheet sheet = wb.getSheetAt(0);
        //evaluating cell type   
        for (Row row : sheet) {
            DijiKalaData dkd = new DijiKalaData();
            int counter = 0;
            Iterator<Cell> iterator = row.cellIterator();
            while (iterator.hasNext()) {
                Cell cell = iterator.next();
                counter++;
                switch (counter) {
                    case 1:
                        if (cell.getCellType() == CellType.STRING) {
                            dkd.id = cell.getStringCellValue();
                        }
                        break;
                    case 7:
                        if (cell.getCellType() == CellType.STRING) {
                            dkd.dimension = cell.getStringCellValue();
                        }
                        break;
                }
            }
            if (dkd.id != "dkd-Link") {
                commodities.add(dkd);
            }
        }
        return commodities;
    }

    private static List<DijiKalaData> readDataFromExcels_Dimension_Label(File file) throws IOException {
        List<DijiKalaData> commodities = new ArrayList<>();
        //obtaining input bytes from a file  
        FileInputStream fis = new FileInputStream(file);
        //creating workbook instance that refers to .xls file  
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        //creating a Sheet object to retrieve the object  
        XSSFSheet sheet = wb.getSheetAt(0);
        //evaluating cell type   
        for (Row row : sheet) {
            DijiKalaData dkd = new DijiKalaData();
            int counter = 0;
            Iterator<Cell> iterator = row.cellIterator();
            while (iterator.hasNext()) {
                Cell cell = iterator.next();
                counter++;
                switch (counter) {
                    case 1:
                        if (cell.getCellType() == CellType.NUMERIC) {
                            dkd.id = "dkp-" + String.valueOf((int)cell.getNumericCellValue());
                        }
                        break;
                    case 9:
                        if (cell.getCellType() == CellType.STRING) {
                            dkd.dimension = cell.getStringCellValue();
                        }
                        break;
                    case 10:
                        if (cell.getCellType() == CellType.NUMERIC) {
                            dkd.weight = String.valueOf((int)cell.getNumericCellValue());
                        }
                        break;
                    case 11:
                        if (cell.getCellType() == CellType.STRING) {
                            dkd.label = cell.getStringCellValue();
                        }
                        break;

                }
            }
            if (dkd.id != "dkp-Id") {
                commodities.add(dkd);
            }
        }
        return commodities;
    }

    private static List<DijiKalaBrand> readBrandsFromExcels(File file) throws IOException {
        List<DijiKalaBrand> brands = new ArrayList<>();
        //obtaining input bytes from a file  
        FileInputStream fis = new FileInputStream(file);
        //creating workbook instance that refers to .xls file  
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        //creating a Sheet object to retrieve the object  
        XSSFSheet sheet = wb.getSheetAt(0);
        //evaluating cell type   
        for (Row row : sheet) {
            DijiKalaBrand brand = new DijiKalaBrand();
            int counter = 0;
            Iterator<Cell> iterator = row.cellIterator();
            while (iterator.hasNext()) {
                Cell cell = iterator.next();
                counter++;
                switch (counter) {
                    case 1:
                        if (cell.getCellType() == CellType.STRING) {
                            brand.titlePersian = cell.getStringCellValue();
                        }
                        break;
                    case 2:
                        if (cell.getCellType() == CellType.STRING) {
                            brand.titleEnglish = cell.getStringCellValue();
                        }
                        break;
                    case 3:
                        if (cell.getCellType() == CellType.STRING) {
                            brand.dijikala_uri = cell.getStringCellValue();
                        }
                        break;
                    case 4:
                        if (cell.getCellType() == CellType.NUMERIC) {
                            brand.counter = (int) cell.getNumericCellValue();
                        }
                        break;
                }
            }
            brands.add(brand);
        }
        return brands;
    }

    public Commodity convertDigikalaDataToCommodity(DijiKalaData dkd, String name) {
        Commodity commodity = new Commodity();
        commodity.name = name;
        commodity.brand = dkd.brand;
        commodity.id_Dijikala = dkd.id;
        commodity.attributes = dkd.attributes;
        if (!dkd.price.equals("")) {
            commodity.price = Double.parseDouble(dkd.price);
        }
        commodity.imageSrc = dkd.imageLink;
        commodity.imageSrcsSmall = dkd.smallImageLinks;
        commodity.imageSrcsBig = dkd.bigImageLinks;
        commodity.size = dkd.size;
        // should be Change
        commodity.inventory = 50;
        java.util.Date date = new java.util.Date();
        commodity.deliveryTime = new java.sql.Date(date.getTime());
        commodity.similarGoodsLinks = dkd.similarGoodsLinks;
        commodity.dimension = dkd.dimension;
        commodity.weight = dkd.weight;
        return commodity;
    }

}
