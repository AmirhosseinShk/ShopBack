/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.database.items;

/**
 *
 * @author AmirShk
 */
public class BoxDetails {

    public int weight;
    public int length;
    public int width;
    public int height;
    public String id;

    public BoxDetails(String id) {
        this.id = id;
    }

    public BoxDetails(int weight, int length, int width, int height) {
        this.weight = weight;
        this.width = width;
        this.length = length;
        this.height = height;
        this.id = "";
    }

}
