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
    public String imageLink;
    public String persianName;
    public String score;
    public boolean isExist;
    public String price;
    public String[] smallImageLinks;
    public String[] similarGoodsLinks;
    public String link;

    @Override
    public String toString() {
        return "DijiKalaData{"
                + "imageLink=" + imageLink +
                ", score=" + score +
                ", isExist=" + isExist +
                ", price=" + price +
                ", smallImageLinks=" + smallImageLinks +
                ", similarGoodsLinks=" + similarGoodsLinks + '}';
    }

}
