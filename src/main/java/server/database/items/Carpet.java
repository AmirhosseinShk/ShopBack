/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.database.items;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;

/**
 *
 * @author @AmirShk
 */
public class Carpet implements Serializable {

    public int id;
    public String name;
    public double price;
    public String brand;
    public String[] size;
    public int inventory;
    public Date deliveryTime;
    public String imageSrc;
    public String[] imageSrcs;
    public double discountPrice;
    public String attributes;
}
