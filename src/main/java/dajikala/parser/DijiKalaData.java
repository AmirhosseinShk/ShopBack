/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dajikala.parser;

/**
 *
 * @author @AmirShk
 */
public class DijiKalaData {

    public String id;
    public String name;
    public String imageLink;
    public String persianName;
    public String score;
    public String brand;
    public String brandLink;
    public boolean isExist;
    public String price;
    public String disCountPrice;
    public String[] smallImageLinks;
    public String[] bigImageLinks;
    public String[] similarGoodsLinks;
    public String link;
    public String[] size;
    public String dimension;
    public String weight;
    public String attributes;
    public String label;
    
    @Override
    public String toString() {
        return "DijiKalaData{"
                + "imageLink=" + imageLink
                + ", score=" + score
                + ", isExist=" + isExist
                + ", price=" + price
                + ", smallImageLinks=" + smallImageLinks
                + ", similarGoodsLinks=" + similarGoodsLinks + '}';
    }

}
