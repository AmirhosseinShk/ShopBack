/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.database.items;

import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author @AmirShk
 */
public class OrderTour {

    public String name;
    public String email;
    public String contactNumber;
    public Date tourDate;
    public Time tourTime;
    public String[] carpetIds;
    public int totalAmount;

}
