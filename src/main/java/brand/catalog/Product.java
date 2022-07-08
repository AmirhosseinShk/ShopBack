/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brand.catalog;

/**
 *
 * @author AmirShk
 */
public class Product {

    public Product(String name, double price, double score, String imageUrl) {
        this.name = name;
        this.price = price;
        this.score = score;
        this.imageUrl = imageUrl;
    }

    public String name;
    public double price;
    public double score;
    public String imageUrl;

}
