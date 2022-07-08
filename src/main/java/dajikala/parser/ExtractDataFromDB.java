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
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import server.database.Connector;
import server.database.items.Commodity;

/**
 *
 * @author AmirShk
 */
public class ExtractDataFromDB {

    public static void main(String[] args) throws IOException, SQLException, NoSuchAlgorithmException {
        //        readDataFromDB(true);
        //        readDataFromDB(false);        
        //getDataWithoutSizeAndNotExist("D:/Work/Tavally/RussianShopBack/ExcelsNew", true);
        getDataWithSepecialDimension(true, "a");
    }

    private static void readDataFromDB(boolean isArm) throws SQLException, IOException {
        ArrayList<Commodity> commodities = Connector.getInstance().getAllCarpet(isArm);
        XSSFWorkbook workbook = writeOnExcels(commodities);
        if (isArm) {
            // .xlsx is the format for Excel Sheets...
            // writing the workbook into the file...
            FileOutputStream out = new FileOutputStream(
                    new File("D:/Work/Tavally/RussianShopBack/Brands/armCommodities.xlsx"));

            workbook.write(out);
            out.close();
        } else {
            // .xlsx is the format for Excel Sheets...
            // writing the workbook into the file...
            FileOutputStream out = new FileOutputStream(
                    new File("D:/Work/Tavally/RussianShopBack/Brands/ruCommodities.xlsx"));

            workbook.write(out);
            out.close();
        }

    }

    private static XSSFWorkbook writeOnExcels(ArrayList<Commodity> commodities) {
        // workbook object
        XSSFWorkbook workbook = new XSSFWorkbook();
        // spreadsheet object
        XSSFSheet spreadsheet
                = workbook.createSheet(" Student Data ");
        // creating a row object
        XSSFRow row;
        // This data needs to be written (Object[])
        Map<String, Object[]> commoditiesData
                = new TreeMap<String, Object[]>();
        commoditiesData.put(
                "1",
                new Object[]{"dkd-Link", "NAME", "Price", "brand", "dimension", "weight"});
        for (int i = 0; i < commodities.size(); i++) {
            commoditiesData.put(String.valueOf(i + 2), new Object[]{
                commodities.get(i).id_Dijikala, commodities.get(i).name, String.valueOf(commodities.get(i).price), commodities.get(i).brand, commodities.get(i).dimension, commodities.get(i).weight});
        }
        Set<String> keyid = commoditiesData.keySet();
        int rowid = 0;
        // writing the data into the sheets...
        for (String key : keyid) {

            row = spreadsheet.createRow(rowid++);
            Object[] objectArr = commoditiesData.get(key);
            int cellid = 0;

            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String) obj);
            }
        }
        return workbook;
    }

    private static void getDataWithoutSizeAndNotExist(String xlsxPath, boolean isArm) throws IOException, SQLException {
        File folderPath = new File(xlsxPath);
        Collection<File> files = FileUtils.listFiles(folderPath, new String[]{"xlsx"}, true);
        ArrayList<DijiKalaData> withNoDimentions = new ArrayList<>();
        ArrayList<DijiKalaData> notExistOnDB = new ArrayList<>();
        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            System.out.println(file.getName());
            List<DijiKalaData> commodities = readSomeDataFromExcels(file);
            for (int i = 0; i < commodities.size(); i++) {
                // Insert Data in DataBases
                Commodity res = Connector.getInstance().getAllCarpetWithoutDimentions(isArm, commodities.get(i).id);
                if (res != null) {
                    if (res.dimension == null) {
                        commodities.get(i).name = res.name;
                        commodities.get(i).brand = res.brand;
                        withNoDimentions.add(commodities.get(i));
                    }
                } else {
                    notExistOnDB.add(commodities.get(i));
                }
            }
        }
        XSSFWorkbook resNoDimentions = writeOnExcels2(withNoDimentions);
        XSSFWorkbook resNotExistOnDB = writeOnExcels2(notExistOnDB);

        FileOutputStream out1 = new FileOutputStream(
                new File("D:/Work/Tavally/RussianShopBack/Brands/armCommodities_NoDimentions.xlsx"));

        resNoDimentions.write(out1);
        out1.close();

        FileOutputStream out2 = new FileOutputStream(
                new File("D:/Work/Tavally/RussianShopBack/Brands/armCommodities_NotExist.xlsx"));

        resNotExistOnDB.write(out2);
        out2.close();

    }

    private static void getDataWithSepecialDimension(boolean isArm, String dimensionSize) throws IOException, SQLException {
        ArrayList<Commodity> withSpecialDimentions = new ArrayList<>();
        List<Commodity> commodities = Connector.getInstance().getAllCarpetWithDimentions(isArm);
        String[] weightRes;
        for (int i = 0; i < commodities.size(); i++) {
            Commodity com = commodities.get(i);
            if (com.weight != null) {
                weightRes = com.weight.split("گرم");
                if (weightRes.length > 1) {
                    int weight = 0;
                    if (weightRes[0].contains(".")) {
                        weight = (int) Double.parseDouble(weightRes[0].replace(" ", ""));
                    } else {
                        weight = Integer.parseInt(weightRes[0].replace(" ", ""));
                    }
                    if (weight <= 2000) {
                        // Insert Data in DataBases
                        if (dimensionConverter(com.dimension, dimensionSize)) {
                            //get Score
                            commodities.get(i).score = ExtractDijiKalaData.getInstance().getLinkScore(com.id_Dijikala.split("-")[1]);
                            withSpecialDimentions.add(com);
                        }
                    }
                } else {
                    weightRes = com.weight.split("کیلوگرم");
                    if (weightRes.length > 1) {
                        int weight = 0;
                        if (weightRes[0].contains(".")) {
                            weight = (int) Double.parseDouble(weightRes[0].replace(" ", ""));
                        } else {
                            weight = Integer.parseInt(weightRes[0].replace(" ", ""));
                        }
                        if (weight <= 2) {
                            // Insert Data in DataBases
                            if (dimensionConverter(com.dimension, dimensionSize)) {
                                //get Score
                                commodities.get(i).score = ExtractDijiKalaData.getInstance().getLinkScore(com.id_Dijikala.split("-")[1]);
                                withSpecialDimentions.add(com);
                            }
                        }
                    }
                }
            } else {
                if (dimensionConverter(com.dimension, dimensionSize)) {
                    //get Score
                    commodities.get(i).score = ExtractDijiKalaData.getInstance().getLinkScore(com.id_Dijikala.split("-")[1]);
                    withSpecialDimentions.add(com);
                }
            }
        }
        XSSFWorkbook resWithSpecialDimentions = writeOnExcels3(withSpecialDimentions);

        FileOutputStream out1 = new FileOutputStream(
                new File("D:/Work/Tavally/RussianShopBack/Brands/armCommodities_aDimensions.xlsx"));

        resWithSpecialDimentions.write(out1);
        out1.close();
    }

    private static boolean dimensionConverter(String dimension, String boxSize) {
        String res = dimension.replace("سانتی‌متر", "");
        String[] dimensions = res.split("×");
        if (dimension.length() != 3) {
            dimensions = res.split("\\*");
            if (dimension.length() != 3) {
                dimensions = res.split("X");
                if (dimension.length() != 3) {
                    if (dimension.contains("a") || dimension.contains("b")) {
                        return true;
                    }
                    return false;
                }
                return dimensionChecker(dimensions, boxSize);
            }
            return dimensionChecker(dimensions, boxSize);
        }
        return dimensionChecker(dimensions, boxSize);
    }

    private static boolean dimensionChecker(String[] dimensions, String match) {
        boolean isWidth = false;
        boolean isLength = false;
        boolean isHeight = false;
        String dimension;
        for (int i = 0; i < dimensions.length; i++) {
            int d = 0;
            switch (match) {
                case "a":
                    dimension = dimensions[i].replace(" ", "");
                    if (dimension.contains(".")) {
                        d = (int) Double.parseDouble(dimension);
                    } else {
                        d = Integer.parseInt(dimension);
                    }
                    if (d < 6) {
                        isHeight = true;
                    }
                    if (d < 36) {
                        isLength = true;
                    }
                    if (d < 26) {
                        isWidth = true;
                    }
                    break;
                case "b":
                    dimension = dimensions[i].replace(" ", "");
                    if (dimension.contains(".")) {
                        d = (int) Double.parseDouble(dimension);
                    } else {
                        d = Integer.parseInt(dimension);
                    }
                    if (d < 6) {
                        isHeight = true;
                    }
                    if (d < 36) {
                        isLength = true;
                    }
                    if (d < 26) {
                        isWidth = true;
                    }
                    break;
                case "c":
                    dimension = dimensions[i].replace(" ", "");
                    if (dimension.contains(".")) {
                        d = (int) Double.parseDouble(dimension);
                    } else {
                        d = Integer.parseInt(dimension);
                    }
                    if (d < 6) {
                        isHeight = true;
                    }
                    if (d < 36) {
                        isLength = true;
                    }
                    if (d < 26) {
                        isWidth = true;
                    }
                    break;
                case "d":
                    dimension = dimensions[i].replace(" ", "");
                    if (dimension.contains(".")) {
                        d = (int) Double.parseDouble(dimension);
                    } else {
                        d = Integer.parseInt(dimension);
                    }
                    if (d < 6) {
                        isHeight = true;
                    }
                    if (d < 36) {
                        isLength = true;
                    }
                    if (d < 26) {
                        isWidth = true;
                    }
                    break;
                case "e":
                    dimension = dimensions[i].replace(" ", "");
                    if (dimension.contains(".")) {
                        d = (int) Double.parseDouble(dimension);
                    } else {
                        d = Integer.parseInt(dimension);
                    }
                    if (d < 6) {
                        isHeight = true;
                    }
                    if (d < 36) {
                        isLength = true;
                    }
                    if (d < 26) {
                        isWidth = true;
                    }
                    break;
            }
        }
        return isWidth && isHeight && isLength;
    }

    private static XSSFWorkbook writeOnExcels2(ArrayList<DijiKalaData> commodities) {
        // workbook object
        XSSFWorkbook workbook = new XSSFWorkbook();
        // spreadsheet object
        XSSFSheet spreadsheet
                = workbook.createSheet(" Student Data ");
        // creating a row object
        XSSFRow row;
        // This data needs to be written (Object[])
        Map<String, Object[]> commoditiesData
                = new TreeMap<String, Object[]>();
        commoditiesData.put(
                "1",
                new Object[]{"dkd-Link", "NAME", "Price", "brand", "brandLink", "Score"});
        for (int i = 0; i < commodities.size(); i++) {
            commoditiesData.put(String.valueOf(i + 2), new Object[]{
                commodities.get(i).id, commodities.get(i).name, commodities.get(i).price, commodities.get(i).brand, commodities.get(i).brandLink, commodities.get(i).score});
        }
        Set<String> keyid = commoditiesData.keySet();
        int rowid = 0;
        // writing the data into the sheets...
        for (String key : keyid) {

            row = spreadsheet.createRow(rowid++);
            Object[] objectArr = commoditiesData.get(key);
            int cellid = 0;

            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String) obj);
            }
        }
        return workbook;
    }

    private static XSSFWorkbook writeOnExcels3(ArrayList<Commodity> commodities) {
        // workbook object
        XSSFWorkbook workbook = new XSSFWorkbook();
        // spreadsheet object
        XSSFSheet spreadsheet
                = workbook.createSheet(" Student Data ");
        // creating a row object
        XSSFRow row;
        // This data needs to be written (Object[])
        Map<String, Object[]> commoditiesData
                = new TreeMap<String, Object[]>();
        commoditiesData.put(
                "1",
                new Object[]{"dkd-Link", "NAME", "Price", "brand", "Score", "TransferPrice"});
        for (int i = 0; i < commodities.size(); i++) {
            commoditiesData.put(String.valueOf(i + 2), new Object[]{
                commodities.get(i).id_Dijikala, commodities.get(i).name, String.valueOf(commodities.get(i).price), commodities.get(i).brand, commodities.get(i).score, "1740.22 ₽"});
        }
        Set<String> keyid = commoditiesData.keySet();
        int rowid = 0;
        // writing the data into the sheets...
        for (String key : keyid) {

            row = spreadsheet.createRow(rowid++);
            Object[] objectArr = commoditiesData.get(key);
            int cellid = 0;

            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String) obj);
            }
        }
        return workbook;
    }

    private static List<DijiKalaData> readSomeDataFromExcels(File file) throws IOException {
        List<DijiKalaData> commodities = new ArrayList<>();
        //obtaining input bytes from a file  
        FileInputStream fis = new FileInputStream(file);
        //creating workbook instance that refers to .xls file  
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        //creating a Sheet object to retrieve the object  
        XSSFSheet sheet = wb.getSheetAt(0);
        ExtractDijiKalaData edkd = new ExtractDijiKalaData();
        boolean skipFirst = false;
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
                    case 2:
                        if (cell.getCellType() == CellType.STRING) {
                            dkd.name = cell.getStringCellValue();
                        }
                        break;
                    case 3:
                        if (cell.getCellType() == CellType.STRING) {
                            dkd.score = cell.getStringCellValue();
                        }
                        break;
                    case 4:
                        break;
                    case 5:
                        if (cell.getCellType() == CellType.STRING) {
                            dkd.price = cell.getStringCellValue();
                        }
                        break;
                    case 6:
                        if (cell.getCellType() == CellType.STRING) {
                            dkd.link = cell.getStringCellValue();
                        }
                        break;
                    case 7:
                        if (cell.getCellType() == CellType.STRING) {
                            dkd.brandLink = cell.getStringCellValue();
                        }
                        break;
                }
            }
            if (dkd.price != "" && skipFirst) {
                commodities.add(dkd);
            }
            skipFirst = true;
        }
        return commodities;
    }

}
