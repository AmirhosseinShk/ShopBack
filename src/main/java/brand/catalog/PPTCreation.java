/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brand.catalog;

import dajikala.parser.ExtractDijiKalaData;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.jsoup.Jsoup;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;

/**
 *
 * @author AmirShk
 */
public class PPTCreation {

    private static ArrayList<BrandDetails> readLinksFromExcels(String path, boolean isPersian) throws IOException {
        ArrayList<BrandDetails> brandDetailses = new ArrayList<BrandDetails>();
        //obtaining input bytes from a file  
        FileInputStream fis = new FileInputStream(new File(path));
        //creating workbook instance that refers to .xls file  
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        //creating a Sheet object to retrieve the object  
        XSSFSheet sheet = wb.getSheetAt(0);
        //evaluating cell type   
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            String brand = "";
            String link = "";
            String description = "";
            int counter = 0;
            int isExist = 0;
            row.getHeight();
            boolean persianBrand = false;
            for (Cell cell : row) {
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
                    case 3:
                        if (cell.getCellType() == CellType.STRING) {
                            brand = cell.getStringCellValue();
                        }
                        break;
                    case 5:
                        if (cell.getCellType() == CellType.STRING) {
                            link = cell.getStringCellValue();
                        }
                        break;
                    case 6:
                        if (cell.getCellType() == CellType.STRING) {
                            description = cell.getStringCellValue();
                        }
                        break;
                }
            }
            if (isExist != 0 && isPersian == persianBrand) {
                link = link.split("/")[4];
                System.out.println(link);
                BrandDetails brandDetails = new BrandDetails();
                brandDetails.brandName = brand;
                brandDetails.link = link;
                brandDetails.description = description;
                ExtractDijiKalaData.getInstance().getBrandTopProducts(brandDetails);
                if (brandDetails.products != null && brandDetails.products.size() == 4) {
                    brandDetailses.add(brandDetails);
                }
            }
        }
        return brandDetailses;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        createCatalog("D:/Work/Tavally/RussianShopBack/All_Brands/Brands.xlsx");
    }

    private static void createCatalog(String path) throws IOException {

        ArrayList<BrandDetails> brandsDetails = readLinksFromExcels(path, false);
        InputStream inputStream = PPTCreation.class.getClassLoader().getResourceAsStream("Catalog3.pptx");
        XMLSlideShow ppt = new XMLSlideShow(inputStream);
        XSLFSlide slide = ppt.getSlides().get(0);
        XSLFSlideLayout layout = slide.getSlideLayout();

        for (int i = 0; i < brandsDetails.size(); i++) {
            // Duplicate slide
            XSLFSlide newSlide = ppt.createSlide(layout);
            newSlide.importContent(slide);
            importBrandImformation(newSlide, ppt, brandsDetails.get(i));
        }

        FileOutputStream out = new FileOutputStream("D:/Work/Tavally/RussianShopBack/src/main/resources/CatalogEnglish.pptx");
        ppt.write(out);
        out.close();
    }

    private static void importBrandImformation(XSLFSlide newSlide, XMLSlideShow ppt, BrandDetails bd) throws IOException {
        //import new Brand
        CTSlide ctSlide = newSlide.getXmlObject();
        XmlObject[] allText = ctSlide.selectPath(
                "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' "
                + ".//a:t"
        );

        for (int i = 0; i < allText.length; i++) {
            if (allText[i] instanceof XmlString) {
                XmlString xmlString = (XmlString) allText[i];
                String text = xmlString.getStringValue();
                System.out.println(text);
                if (text.equals("Brand name")) {
                    xmlString.setStringValue(bd.brandName);
                } else if (text.equals("Description")) {
                    String desc = Jsoup.parse(bd.description).text();
                    if (desc.length() > 280) {
                        desc = desc.substring(0, 280) + "...";
                    }
                    xmlString.setStringValue(desc);
                } else if (text.equals("Product1")) {
                    String name = bd.products.get(0).name;
                    xmlString.setStringValue(name.substring(0, name.length() > 60 ? 60 : name.length()));
                } else if (text.equals("Price1")) {
                    String price = String.valueOf(Math.floor((bd.products.get(0).price / 280000) * 100) / 100) + "$";
                    xmlString.setStringValue(price);
                } else if (text.equals("Product2")) {
                    String name = bd.products.get(1).name;
                    xmlString.setStringValue(name.substring(0, name.length() > 60 ? 60 : name.length()));
                } else if (text.equals("Price2")) {
                    String price = String.valueOf(Math.floor((bd.products.get(1).price / 280000) * 100) / 100) + "$";
                    xmlString.setStringValue(price);
                } else if (text.equals("Product3")) {
                    String name = bd.products.get(2).name;
                    xmlString.setStringValue(name.substring(0, name.length() > 60 ? 60 : name.length()));
                } else if (text.equals("Price3")) {
                    String price = String.valueOf(Math.floor((bd.products.get(2).price / 280000) * 100) / 100) + "$";
                    xmlString.setStringValue(price);
                } else if (text.equals("Product4")) {
                    String name = bd.products.get(3).name;
                    xmlString.setStringValue(name.substring(0, name.length() > 60 ? 60 : name.length()));
                } else if (text.equals("Price4")) {
                    String price = String.valueOf(Math.floor((bd.products.get(3).price / 280000) * 100) / 100) + "$";
                    xmlString.setStringValue(price);
                }
            }
        }

        List<XSLFShape> shapes = newSlide.getShapes();
        for (int i = 0; i < shapes.size(); i++) {
            System.out.println(shapes.get(i).getShapeName());

            // this is Brand Icon
            if (shapes.get(i).getShapeName().equals("Rectangle 4")) {
                // replace picture text holder assuming at index2 2 and type autoshape
                XSLFShape pic = shapes.get(i);
                java.awt.geom.Rectangle2D anchor = pic.getAnchor();
                if (bd.logo != null) {
                    URL url = new URL(bd.logo);
                    InputStream in = new BufferedInputStream(url.openStream());
                    byte[] pictureData = IOUtils.toByteArray(in);
                    XSLFPictureData idx = ppt.addPicture(pictureData,
                            XSLFPictureData.PictureType.PNG);
                    XSLFPictureShape picture = newSlide.createPicture(idx);
                    picture.setAnchor(anchor);
                }
                newSlide.removeShape(pic);
            }

            // this is product top left
            if (shapes.get(i).getShapeName().equals("Rectangle 11")) {
                // replace picture text holder assuming at index2 2 and type autoshape
                XSLFShape pic = shapes.get(i);
                java.awt.geom.Rectangle2D anchor = pic.getAnchor();

                URL url = new URL(bd.products.get(0).imageUrl);
                InputStream in = new BufferedInputStream(url.openStream());

                byte[] pictureData = IOUtils.toByteArray(in);
                XSLFPictureData idx = ppt.addPicture(pictureData,
                        XSLFPictureData.PictureType.JPEG);
                XSLFPictureShape picture = newSlide.createPicture(idx);
                newSlide.removeShape(pic);
                picture.setAnchor(anchor);
            }

            // this is product top right
            if (shapes.get(i).getShapeName().equals("Rectangle 24")) {
                // replace picture text holder assuming at index2 2 and type autoshape
                XSLFShape pic = shapes.get(i);
                java.awt.geom.Rectangle2D anchor = pic.getAnchor();

                URL url = new URL(bd.products.get(1).imageUrl);
                InputStream in = new BufferedInputStream(url.openStream());

                byte[] pictureData = IOUtils.toByteArray(in);
                XSLFPictureData idx = ppt.addPicture(pictureData,
                        XSLFPictureData.PictureType.JPEG);
                XSLFPictureShape picture = newSlide.createPicture(idx);
                newSlide.removeShape(pic);
                picture.setAnchor(anchor);
            }

            // this is product down left
            if (shapes.get(i).getShapeName().equals("Rectangle 33")) {
                // replace picture text holder assuming at index2 2 and type autoshape
                XSLFShape pic = shapes.get(i);
                java.awt.geom.Rectangle2D anchor = pic.getAnchor();

                URL url = new URL(bd.products.get(2).imageUrl);
                InputStream in = new BufferedInputStream(url.openStream());

                byte[] pictureData = IOUtils.toByteArray(in);
                XSLFPictureData idx = ppt.addPicture(pictureData,
                        XSLFPictureData.PictureType.JPEG);
                XSLFPictureShape picture = newSlide.createPicture(idx);
                newSlide.removeShape(pic);
                picture.setAnchor(anchor);
            }

            // this is product down right
            if (shapes.get(i).getShapeName().equals("Rectangle 42")) {
                // replace picture text holder assuming at index2 2 and type autoshape
                XSLFShape pic = shapes.get(i);
                java.awt.geom.Rectangle2D anchor = pic.getAnchor();

                URL url = new URL(bd.products.get(3).imageUrl);
                InputStream in = new BufferedInputStream(url.openStream());

                byte[] pictureData = IOUtils.toByteArray(in);
                XSLFPictureData idx = ppt.addPicture(pictureData,
                        XSLFPictureData.PictureType.JPEG);
                XSLFPictureShape picture = newSlide.createPicture(idx);
                newSlide.removeShape(pic);
                picture.setAnchor(anchor);
            }
        }

        shapes = newSlide.getShapes();
        CreateRating(shapes, newSlide, ppt, "Star: 5 Points 17", bd.products.get(0).score);
        CreateRating(shapes, newSlide, ppt, "Star: 5 Points 27", bd.products.get(1).score);
        CreateRating(shapes, newSlide, ppt, "Star: 5 Points 36", bd.products.get(2).score);
        CreateRating(shapes, newSlide, ppt, "Star: 5 Points 45", bd.products.get(3).score);
    }

    private static void CreateRating(List<XSLFShape> shapes, XSLFSlide newSlide, XMLSlideShow ppt, String starLocation, double star) throws IOException {

        byte[] pictureDataHalfStar = IOUtils.toByteArray(
                new FileInputStream("D:/Work/Tavally/RussianShopBack/halfStar.PNG"));
        XSLFPictureData idxHalfStar = ppt.addPicture(pictureDataHalfStar,
                XSLFPictureData.PictureType.PNG);

        byte[] pictureDataFullStar = IOUtils.toByteArray(
                new FileInputStream("D:/Work/Tavally/RussianShopBack/fullStar.PNG"));
        XSLFPictureData idxFullStar = ppt.addPicture(pictureDataFullStar,
                XSLFPictureData.PictureType.PNG);

        byte[] pictureDataNoStar = IOUtils.toByteArray(
                new FileInputStream("D:/Work/Tavally/RussianShopBack/noStar.PNG"));
        XSLFPictureData idxNoStar = ppt.addPicture(pictureDataNoStar,
                XSLFPictureData.PictureType.PNG);

        for (int i = 0; i < shapes.size(); i++) {
            // this is product Starts
            if (shapes.get(i).getShapeName().equals(starLocation)) {
                // replace picture text holder assuming at index2 2 and type autoshape
                XSLFShape star1 = shapes.get(i);
                XSLFShape star2 = shapes.get(i + 1);
                XSLFShape star3 = shapes.get(i + 2);
                XSLFShape star4 = shapes.get(i + 3);
                XSLFShape star5 = shapes.get(i + 4);

                ArrayList<XSLFShape> stars = new ArrayList<>();
                stars.add(star1);
                stars.add(star2);
                stars.add(star3);
                stars.add(star4);
                stars.add(star5);

                if (star == 0) {
                    newSlide.removeShape(star1);
                    newSlide.removeShape(star2);
                    newSlide.removeShape(star3);
                    newSlide.removeShape(star4);
                    newSlide.removeShape(star5);
                } else {
                    for (int j = 0; j < 5; j++) {
                        if (star >= 1) {
                            XSLFShape starShape = stars.get(j);
                            java.awt.geom.Rectangle2D anchor1 = starShape.getAnchor();
                            XSLFPictureShape picture1 = newSlide.createPicture(idxFullStar);
                            newSlide.removeShape(starShape);
                            picture1.setAnchor(anchor1);
                        } else if (star >= 0.5) {
                            XSLFShape starShape = stars.get(j);
                            java.awt.geom.Rectangle2D anchor1 = starShape.getAnchor();
                            XSLFPictureShape picture1 = newSlide.createPicture(idxHalfStar);
                            newSlide.removeShape(starShape);
                            picture1.setAnchor(anchor1);
                        } else {
                            XSLFShape starShape = stars.get(j);
                            java.awt.geom.Rectangle2D anchor1 = starShape.getAnchor();
                            XSLFPictureShape picture1 = newSlide.createPicture(idxNoStar);
                            newSlide.removeShape(starShape);
                            picture1.setAnchor(anchor1);
                        }
                        star--;
                    }
//                    java.awt.geom.Rectangle2D anchor2 = star2.getAnchor();
//                    XSLFPictureShape picture2 = newSlide.createPicture(idxFullStar);
//                    newSlide.removeShape(star2);
//                    picture2.setAnchor(anchor2);
//
//                    java.awt.geom.Rectangle2D anchor3 = star3.getAnchor();
//                    XSLFPictureShape picture3 = newSlide.createPicture(idxFullStar);
//                    newSlide.removeShape(star3);
//                    picture3.setAnchor(anchor3);
//
//                    java.awt.geom.Rectangle2D anchor4 = star4.getAnchor();
//                    XSLFPictureShape picture4 = newSlide.createPicture(idxHalfStar);
//                    newSlide.removeShape(star4);
//                    picture4.setAnchor(anchor4);
//
//                    java.awt.geom.Rectangle2D anchor5 = star5.getAnchor();
//                    XSLFPictureShape picture5 = newSlide.createPicture(idxNoStar);
//                    newSlide.removeShape(star5);
//                    picture5.setAnchor(anchor5);
                }
            }
        }
    }

}
