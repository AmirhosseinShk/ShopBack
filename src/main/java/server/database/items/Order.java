package server.database.items;

/**
 *
 * @author @AmirShk
 */
public class Order {

    public String id;
    public String name;
    public String email;
    public String contactNumber;
    TransformInformation transformInformation;
    public String postCode;
    public String address;
    public CommidityDetails[] commidities;
    public int totalAmount;

    public class CommidityDetails {

        public String name;
        public double price;

        public String toString() {
            return "Name: " + this.name + "- Price: " + this.price;
        }
    }

}
