/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brand.catalog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AmirShk
 */
public class BrandDetails {

    public BrandDetails() {
        products = new ArrayList<>();
    }

    public String brandName;
    public String description;
    public String link;
    public String logo;
    public List<Product> products;

}
