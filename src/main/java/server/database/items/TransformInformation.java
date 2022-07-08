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
public class TransformInformation {

    public BoxDetails boxDetails;
    public String to;
    public String price;

    public TransformInformation(BoxDetails boxDetails, String to, String price) {
        this.boxDetails = boxDetails;
        this.to = to;
        this.price = price;
    }

}
