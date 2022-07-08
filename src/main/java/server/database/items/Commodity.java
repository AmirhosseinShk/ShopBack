/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.database.items;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author @AmirShk
 */
public class Commodity implements Serializable {

    public Commodity() {
        size = new String[1];
        imageSrcsSmall = new String[1];
        imageSrcsBig = new String[1];
        similarGoodsLinks = new String[1];        
    }

    public int id;
    public String id_Dijikala;
    public String name;
    public double price;
    public String brand;
    public String score;
    public String[] size;
    public int inventory;
    public Date deliveryTime;
    public String imageSrc;
    public String[] imageSrcsSmall;
    public String[] imageSrcsBig;
    public String[] similarGoodsLinks;
    public double discountPrice;
    public String attributes;
    public String dimension;
    public String weight;
}
