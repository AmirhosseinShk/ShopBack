/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.database;

import com.google.gson.Gson;
import dajikala.parser.DijiKalaData;
import dajikala.parser.ExtractDijiKalaData;
import java.io.IOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import server.database.items.BoxDetails;
import server.database.items.Commodity;
import server.database.items.Order;
import server.database.items.OrderTour;
import server.database.items.TransformInformation;
import server.email.MailServer;
import server.exchange.LiveExchange;
import server.properties.ProjectProperties;

/**
 * @author @AmirShk
 */
public class Connector {

    public static Connector instance;
    private LiveExchange liveExchange;

    public static Connector getInstance() throws SQLException {
        if (instance == null) {
            instance = new Connector();
            instance.initialTable();
        }
        return instance;
    }

    private Connection openConnection() {
        Connection c = null;
        String url = ProjectProperties.getInstance().getProperty("postgure.database.url");
        String database = ProjectProperties.getInstance().getProperty("postgure.database.name");
        String username = ProjectProperties.getInstance().getProperty("postgure.database.user");
        String password = ProjectProperties.getInstance().getProperty("postgure.database.password");
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(url + "/" + database,
                            username, password);
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        }
        return c;
    }

    private void initialTable() throws SQLException {
        liveExchange = new LiveExchange();
        Statement stmt = null;
        Connection c = null;
        try {
            c = openConnection();
            stmt = c.createStatement();

            //initial CarpetTable
            String carpetTable = "CREATE TABLE IF NOT EXISTS CarpetDetails "
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " NAME           TEXT          NOT NULL,"
                    + " PRICE          REAL          NOT NULL,"
                    + " BRAND          TEXT          NOT NULL,"
                    + " SIZE           TEXT[]        NOT NULL,"
                    + " INVENTORY      INTEGER       NOT NULL,"
                    + " DELIVERYTIME   DATE          NOT NULL,"
                    + " IMAGESRC       TEXT          NOT NULL,"
                    + " IMAGESSRC      TEXT[]        NOT NULL,"
                    + " DISCOUNTPRICE  REAL,"
                    + " LASTVISITED    TIMESTAMP,"
                    + " COUNTER        INTEGER       DEFAULT 0,"
                    + " ATTRIBUTES     TEXT)";
            stmt.executeUpdate(carpetTable);

            String commodity_RU_Table = "CREATE TABLE IF NOT EXISTS RU_Commodity "
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " ID_DIJIKALA    TEXT          NOT NULL,"
                    + " NAME           TEXT          NOT NULL,"
                    + " PRICE          REAL          NOT NULL,"
                    + " BRAND          TEXT          NOT NULL,"
                    + " SIZE           TEXT[]        NOT NULL,"
                    + " INVENTORY      INTEGER       NOT NULL,"
                    + " DELIVERYTIME   DATE          NOT NULL,"
                    + " IMAGESRC       TEXT          NOT NULL,"
                    + " IMAGESSRC      TEXT[]        NOT NULL,"
                    + " IMAGESSRCBIG   TEXT[]        NOT NULL,"
                    + " DISCOUNTPRICE  REAL,"
                    + " LASTVISITED    TIMESTAMP,"
                    + " COUNTER        INTEGER       DEFAULT 0,"
                    + " ATTRIBUTES     TEXT,"
                    + " DIMENSION          TEXT,"
                    + " WEIGHT     TEXT,"
                    + " LABEL     TEXT)";
            stmt.executeUpdate(commodity_RU_Table);

            String allBrands_RU_Table = "CREATE TABLE IF NOT EXISTS AllBrands_RU_Commodity "
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " ID_DIJIKALA    TEXT          NOT NULL,"
                    + " NAME           TEXT          NOT NULL,"
                    + " PRICE          REAL          NOT NULL,"
                    + " BRAND          TEXT          NOT NULL,"
                    + " SIZE           TEXT[]        NOT NULL,"
                    + " INVENTORY      INTEGER       NOT NULL,"
                    + " DELIVERYTIME   DATE          NOT NULL,"
                    + " IMAGESRC       TEXT          NOT NULL,"
                    + " IMAGESSRC      TEXT[]        NOT NULL,"
                    + " IMAGESSRCBIG   TEXT[]        NOT NULL,"
                    + " DISCOUNTPRICE  REAL,"
                    + " LASTVISITED    TIMESTAMP,"
                    + " COUNTER        INTEGER       DEFAULT 0,"
                    + " ATTRIBUTES     TEXT)";
            stmt.executeUpdate(allBrands_RU_Table);

            String commodity_ARM_Table = "CREATE TABLE IF NOT EXISTS ARM_Commodity "
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " ID_DIJIKALA    TEXT          NOT NULL,"
                    + " NAME           TEXT          NOT NULL,"
                    + " PRICE          REAL          NOT NULL,"
                    + " BRAND          TEXT          NOT NULL,"
                    + " SIZE           TEXT[]        NOT NULL,"
                    + " INVENTORY      INTEGER       NOT NULL,"
                    + " DELIVERYTIME   DATE          NOT NULL,"
                    + " IMAGESRC       TEXT          NOT NULL,"
                    + " IMAGESSRC      TEXT[]        NOT NULL,"
                    + " IMAGESSRCBIG   TEXT[]        NOT NULL,"
                    + " DISCOUNTPRICE  REAL,"
                    + " LASTVISITED    TIMESTAMP,"
                    + " COUNTER        INTEGER       DEFAULT 0,"
                    + " ATTRIBUTES     TEXT,"
                    + " DIMENSION          TEXT,"
                    + " WEIGHT     TEXT,"
                    + " LABEL     TEXT)";
            stmt.executeUpdate(commodity_ARM_Table);

            String allBrands_ARM_Table = "CREATE TABLE IF NOT EXISTS AllBrands_ARM_Commodity "
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " ID_DIJIKALA    TEXT          NOT NULL,"
                    + " NAME           TEXT          NOT NULL,"
                    + " PRICE          REAL          NOT NULL,"
                    + " BRAND          TEXT          NOT NULL,"
                    + " SIZE           TEXT[]        NOT NULL,"
                    + " INVENTORY      INTEGER       NOT NULL,"
                    + " DELIVERYTIME   DATE          NOT NULL,"
                    + " IMAGESRC       TEXT          NOT NULL,"
                    + " IMAGESSRC      TEXT[]        NOT NULL,"
                    + " IMAGESSRCBIG   TEXT[]        NOT NULL,"
                    + " DISCOUNTPRICE  REAL,"
                    + " LASTVISITED    TIMESTAMP,"
                    + " COUNTER        INTEGER       DEFAULT 0,"
                    + " ATTRIBUTES     TEXT)";
            stmt.executeUpdate(allBrands_ARM_Table);

            //initial popularProduct
            String popularProduct = "CREATE TABLE IF NOT EXISTS PopularProduct "
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " NAME           TEXT          NOT NULL,"
                    + " PRICE          REAL          NOT NULL,"
                    + " IMAGESRC       TEXT          NOT NULL,"
                    + " DISCOUNTPRICE  REAL)";
            stmt.executeUpdate(popularProduct);

            //initial mostRecent
            String mostRecent = "CREATE TABLE IF NOT EXISTS MostRecent "
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " NAME           TEXT          NOT NULL,"
                    + " PRICE          REAL          NOT NULL,"
                    + " IMAGESRC       TEXT          NOT NULL,"
                    + " DISCOUNTPRICE  REAL)";
            stmt.executeUpdate(mostRecent);

            //OrdersTour Details
            String ordersTourTable = "CREATE TABLE IF NOT EXISTS OrdersTour "
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " NAME           TEXT          NOT NULL,"
                    + " EMAIL          TEXT          NOT NULL,"
                    + " CONTACTNUMBER  TEXT          NOT NULL,"
                    + " TOURDATE       DATE          NOT NULL,"
                    + " TOURTIME       TIME          NOT NULL,"
                    + " CARPETIDS      TEXT[]        NOT NULL,"
                    + " TOTALAMOUNT    REAL          NOT NULL)";
            stmt.executeUpdate(ordersTourTable);

            //boxDetails Details
            String boxDetailsTable = "CREATE TABLE IF NOT EXISTS boxDetails "
                    + "(ID                SERIAL        PRIMARY KEY,"
                    + " SIZE              INTEGER          NOT NULL,"
                    + " WEIGHT         INTEGER          NOT NULL,"
                    + " WIDTH             INTEGER          NOT NULL,"
                    + " LENGTH           INTEGER       NOT NULL,"
                    + " HEIGHT             INTEGER          NOT NULL)";
            stmt.executeUpdate(boxDetailsTable);

            //transformInfomation Details
            String transfomInformationTable = "CREATE TABLE IF NOT EXISTS transfomInformation "
                    + "(ID                SERIAL        PRIMARY KEY,"
                    + " BOXDETAILSID              TEXT          NOT NULL,"
                    + " TOCITY         TEXT          NOT NULL,"
                    + " PRICE             TEXT          NOT NULL,"
                    + " CONSTRAINT fk_boxDetails  "
                    + " FOREIGN KEY(BOXDETAILSID) "
                    + " REFERENCES boxDetails(ID))";
            stmt.executeUpdate(transfomInformationTable);

            //moskoTransfomInformation Details
            String moskoTransfomInformationTable = "CREATE TABLE IF NOT EXISTS moskotransfomInformation "
                    + "(ID                TEXT        PRIMARY KEY,"
                    + " BOXDETAILSID              TEXT          NOT NULL,"
                    + " TOCITY         TEXT          NOT NULL,"
                    + " PRICE             TEXT          NOT NULL,"
                    + " CONSTRAINT fk_boxDetails  "
                    + " FOREIGN KEY(BOXDETAILSID) "
                    + " REFERENCES boxDetails(ID))";
            stmt.executeUpdate(moskoTransfomInformationTable);

            //Orders Details
            String ordersTable = "CREATE TABLE IF NOT EXISTS Orders "
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " NAME           TEXT          NOT NULL,"
                    + " EMAIL          TEXT          NOT NULL,"
                    + " CONTACTNUMBER  TEXT          NOT NULL,"
                    + " TRANSFORMINFORMATIONID  TEXT          NOT NULL,"
                    + " ADDRESS        TEXT          NOT NULL,"
                    + " POSTCODE        TEXT          NOT NULL,"
                    + " CARPETIDS      TEXT[]        NOT NULL,"
                    + " TOTALAMOUNT    REAL          NOT NULL,"
                    + " CONSTRAINT fk_transformInformation  "
                    + " FOREIGN KEY(TRANSFORMINFORMATIONID) "
                    + " REFERENCES transfomInformation(ID))";
            stmt.executeUpdate(ordersTable);

            //DijiKala Data
            String dijikalaDataTable = "CREATE TABLE IF NOT EXISTS dijikalaData "
                    + "(ID                TEXT        PRIMARY KEY,"
                    + " NAME              TEXT          NOT NULL,"
                    + " IMAGELINK         TEXT          NOT NULL,"
                    + " SCORE             TEXT          NOT NULL,"
                    + " ISEXIST           BOOLEAN       NOT NULL,"
                    + " PRICE             TEXT          NOT NULL,"
                    + " SIMILARGOODSLINKS TEXT[]        NOT NULL,"
                    + " SMALLIMAGELINKS   TEXT[]        NOT NULL)";
            stmt.executeUpdate(dijikalaDataTable);
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            stmt.close();
            c.close();
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Table created successfully");
    }

    public void InsertTransformInformation(TransformInformation transformInformation) {
        Connection c = null;
        try {
            //update
            String boxDetailsId = calculateBoxDetailsID(transformInformation.boxDetails);

            c = openConnection();
            String sql = "UPDATE  transfomInformation set price = \'" + transformInformation.price + "\' where boxdetailsid = \'" + boxDetailsId + "\' AND tocity = \'" + transformInformation.to + "\'";

            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedKeys.getInt(1);
                } else {
                    try {
                        //insert
                        c = openConnection();
                        sql = "INSERT INTO transfomInformation(BOXDETAILSID,TOCITY,PRICE)"
                                + "VALUES (?,?,?);";

                        boxDetailsId = calculateBoxDetailsID(transformInformation.boxDetails);

                        pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        pstmt.setString(1, boxDetailsId);
                        pstmt.setString(2, transformInformation.to);
                        pstmt.setString(3, transformInformation.price);

                        pstmt.executeUpdate();
                    } catch (Exception ex) {
                        Logger.getLogger(Connector.class).log(Level.ERROR, ex.getMessage(), ex);
                    }
                }
            }

            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, ex.getMessage(), ex);
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "transformInformation Insert Successfully");
    }

    public int UpdateMoskoTransformInformation(String price, String boxId) {
        int orderId = -1;
        Connection c = null;
        try {
            c = openConnection();
            String sql = "UPDATE  moskotransfomInformation set price = \'" + price + "\' where boxdetailsid = \'" + boxId + "\'";

            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, ex.getMessage(), ex);
            return orderId;
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "transformInformation Insert Successfully");
        return orderId;
    }

    public ArrayList<TransformInformation> getMoscoDetails() {
        //SELECT * FROM "moskotransfomInformation"
        Connection c = null;
        Statement stmt = null;
        ArrayList<TransformInformation> transformInformations = new ArrayList<>();

        try {
            c = openConnection();
            stmt = c.createStatement();
            String query = "SELECT * FROM moskotransfomInformation";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                TransformInformation ti = new TransformInformation(new BoxDetails(rs.getString("id")), rs.getString("tocity"), rs.getString("price"));
                transformInformations.add(ti);
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            try {
                stmt.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        return transformInformations;
    }

    public String getCityTransformDetails(int boxDetailsId, String toCity) {
        //SELECT * FROM "moskotransfomInformation"
        Connection c = null;
        Statement stmt = null;
        String transformPrice = "";

        try {
            c = openConnection();
            stmt = c.createStatement();
            String query = "SELECT * FROM transfomInformation " + "where boxdetailsid = \'" + boxDetailsId + "\' AND tocity = \'" + toCity + "\'";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                transformPrice = rs.getString("price");
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            try {
                stmt.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        return transformPrice;
    }

    public ArrayList<TransformInformation> getAllCityDetails() {
        //SELECT * FROM "transfominformation" where tocity = 'Москва'
        Connection c = null;
        Statement stmt = null;
        ArrayList<TransformInformation> transformInformations = new ArrayList<>();

        try {
            c = openConnection();
            stmt = c.createStatement();
            String query = "SELECT * FROM transfomInformation where boxdetailsid= '1' ;";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                TransformInformation ti = new TransformInformation(new BoxDetails("1"), rs.getString("tocity"), rs.getString("price"));
                transformInformations.add(ti);
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            try {
                stmt.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        return transformInformations;
    }

    private String calculateBoxDetailsID(BoxDetails boxDetails) {
        if (boxDetails.id == "") {
            // for calculate boxID from database multiply all values
            int size = boxDetails.width * boxDetails.height * boxDetails.length * boxDetails.weight;
            Connection c = null;
            Statement stmt = null;
            String id = "";
            try {
                c = openConnection();
                stmt = c.createStatement();
                String query = "SELECT * FROM boxdetails where SIZE=" + size + ";";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    id = rs.getString("ID");
                }
                rs.close();
            } catch (Exception ex) {
                Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
            } finally {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                try {
                    c.close();
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }
            return id;
        } else {
            return boxDetails.id;
        }
    }

    public int InsertOrderData(Order order) {
        int orderId = -1;
        Connection c = null;
        try {
            c = openConnection();
            String sql = "INSERT INTO Orders (NAME,EMAIL,CONTACTNUMBER,ADDRESS,CARPETIDS,TOTALAMOUNT)"
                    + "VALUES (?,?,?,?,?,?);";

            Order.CommidityDetails[] commidities = order.commidities;
            String[] commiditiesString = new String[commidities.length];
            double totalPrice = 0;

            for (int i = 0; i < commidities.length; i++) {
                commiditiesString[i] = commidities[i].toString();
                totalPrice += commidities[i].price;
            }

            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, order.name);
            pstmt.setString(2, order.email);
            pstmt.setString(3, order.contactNumber);
            pstmt.setString(4, order.address);
            pstmt.setArray(5, c.createArrayOf("TEXT", commiditiesString));
            pstmt.setInt(6, order.totalAmount);

            String text = "Hi Dear " + order.name + "<br> <br>";
            text += "Thank you for your purchase. Your Order Show as Below Table :<br>";
            text += "<table width='100%' border='1' align='center'>"
                    + "<tr align='center'>"
                    + "<td><b>Product Name<b></td>"
                    + "<td><b>Price<b></td>"
                    + "</tr>";

            for (int i = 0; i < commidities.length; i++) {
                text += "<tr align='center'>" + "<td>" + commidities[i].name + "</td>"
                        + "<td>" + commidities[i].price + "</td>" + "</tr>";
            }

            text += "<tr align='center'>" + "<td>" + "totalPrice" + "</td>"
                    + "<td>" + totalPrice + "</td>" + "</tr> </table>";

            text += "<br>" + "You Can Pay your order through the following ";
            text += "<a href=\"https://maximmagazin.ru/ShoppingItem\">link .</a> <br><br>";
            text += "Best Regards <br> Maximmagazin";

            MailServer.getInstance().SendEmail(order.email, text);

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, ex.getMessage(), ex);
            return orderId;
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Table created successfully");
        return orderId;
    }

    public boolean InsertOrderTourData(OrderTour orderTour) {
        Connection c = null;
        try {
            c = openConnection();
            String sql = "INSERT INTO OrdersTour (NAME,EMAIL,CONTACTNUMBER,TOURDATE,TOURTIME,CARPETIDS,TOTALAMOUNT) "
                    + "VALUES (?,?,?,?,?,?,?);";

            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, orderTour.name);
            pstmt.setString(2, orderTour.email);
            pstmt.setString(3, orderTour.contactNumber);
            pstmt.setDate(4, orderTour.tourDate);
            pstmt.setTime(5, orderTour.tourTime);
            pstmt.setArray(6, c.createArrayOf("TEXT", orderTour.carpetIds));
            pstmt.setInt(7, orderTour.totalAmount);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
            return false;
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Table created successfully");
        return true;
    }

    public boolean InsertCarpetDetails(Commodity carpet) {
        Logger.getLogger(Connector.class).info(new Gson().toJson(carpet));
        Connection c = null;
        try {
            c = openConnection();
            String sql = "INSERT INTO CarpetDetails (NAME,PRICE,BRAND,SIZE,INVENTORY,DELIVERYTIME,IMAGESRC,IMAGESSRC,IMAGESSRCBIG,DISCOUNTPRICE,ATTRIBUTES,COUNTER) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, carpet.name);
            pstmt.setDouble(2, carpet.price);
            pstmt.setString(3, carpet.brand);
            pstmt.setArray(4, c.createArrayOf("TEXT", carpet.size));
            pstmt.setInt(5, carpet.inventory);
            pstmt.setDate(6, carpet.deliveryTime);
            pstmt.setString(7, carpet.imageSrc);
            pstmt.setArray(8, c.createArrayOf("TEXT", carpet.imageSrcsSmall));
            pstmt.setArray(9, c.createArrayOf("TEXT", carpet.imageSrcsBig));
            pstmt.setDouble(10, carpet.discountPrice);
            pstmt.setObject(11, carpet.attributes);
            pstmt.setInt(12, 0);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
            return false;
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Carpet update successfully");
        return true;
    }

    public boolean InsertARMCommodity(Commodity commodity, boolean isAllBrands) {
        Logger.getLogger(Connector.class).info(new Gson().toJson(commodity));
        Connection c = openConnection();
        try {
            String table;
            if (isAllBrands) {
                table = "AllBrands_ARM_Commodity";
            } else {
                table = "ARM_Commodity";
            }
            String sql = "INSERT INTO " + table + " (ID_DIJIKALA,NAME,PRICE,BRAND,SIZE,INVENTORY,DELIVERYTIME,IMAGESRC,IMAGESSRC,IMAGESSRCBIG,DISCOUNTPRICE,ATTRIBUTES,COUNTER,DIMENSION,WEIGHT) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, commodity.id_Dijikala);
            pstmt.setString(2, commodity.name);
            pstmt.setDouble(3, commodity.price);
            pstmt.setString(4, commodity.brand);
            if (commodity.size != null) {
                pstmt.setArray(5, c.createArrayOf("TEXT", commodity.size));
            } else {
                String[] empety = {""};
                pstmt.setArray(5, c.createArrayOf("TEXT", empety));
            }
            pstmt.setInt(6, commodity.inventory);
            pstmt.setDate(7, commodity.deliveryTime);
            pstmt.setString(8, commodity.imageSrc);
            pstmt.setArray(9, c.createArrayOf("TEXT", commodity.imageSrcsSmall));
            pstmt.setArray(10, c.createArrayOf("TEXT", commodity.imageSrcsBig));
            pstmt.setDouble(11, commodity.discountPrice);
            pstmt.setObject(12, commodity.attributes);
            pstmt.setInt(13, 0);
            pstmt.setString(14, commodity.dimension);
            pstmt.setString(15, commodity.weight);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
            return false;
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "ARMCommodity update successfully");
        return true;
    }

    public boolean InsertRuCommodity(Commodity commodity, boolean isAllBrands) {
        Connection c = openConnection();
        try {
            String table;
            if (isAllBrands) {
                table = "AllBrands_RU_Commodity";
            } else {
                table = "RU_Commodity";
            }
            String sql = "INSERT INTO " + table + " (ID_DIJIKALA,NAME,PRICE,BRAND,SIZE,INVENTORY,DELIVERYTIME,IMAGESRC,IMAGESSRC,IMAGESSRCBIG,DISCOUNTPRICE,ATTRIBUTES,COUNTER,DIMENSION,WEIGHT)"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, commodity.id_Dijikala);
            pstmt.setString(2, commodity.name);
            pstmt.setDouble(3, commodity.price);
            pstmt.setString(4, commodity.brand);
            if (commodity.size != null) {
                pstmt.setArray(5, c.createArrayOf("TEXT", commodity.size));
            } else {
                String[] empety = {""};
                pstmt.setArray(5, c.createArrayOf("TEXT", empety));
            }
            pstmt.setInt(6, commodity.inventory);
            pstmt.setDate(7, commodity.deliveryTime);
            pstmt.setString(8, commodity.imageSrc);
            pstmt.setArray(9, c.createArrayOf("TEXT", commodity.imageSrcsSmall));
            pstmt.setArray(10, c.createArrayOf("TEXT", commodity.imageSrcsBig));
            pstmt.setDouble(11, commodity.discountPrice);
            pstmt.setObject(12, commodity.attributes);
            pstmt.setInt(13, 0);
            pstmt.setString(14, commodity.dimension);
            pstmt.setString(15, commodity.weight);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
            return false;
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        //     Logger.getLogger(Connector.class).log(Level.INFO, "ARMCommodity update successfully");
        return true;
    }

    public JSONObject getCarpetDetailsbyID(int id, boolean isArm) throws SQLException {
        int armLivePrice = liveExchange.getArmLivePrice();
        Commodity carpet = new Commodity();
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            String query;
            if (isArm) {
                query = "SELECT * FROM ARM_Commodity where  ID_DIJIKALA = \'" + "dkp-" + id + "\';";
            } else {
                query = "SELECT * FROM RU_Commodity where  ID_DIJIKALA = \'" + "dkp-" + id + "\';";
            }
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                carpet.id = Integer.parseInt(rs.getString("ID_DIJIKALA").split("-")[1]);
                carpet.name = rs.getString("NAME");
                carpet.brand = rs.getString("BRAND");
//                carpet.price = rs.getDouble("PRICE");
                //get live price and check it
                double currentPrice = rs.getDouble("PRICE");
                if (currentPrice != 0.0) {
                    carpet.price = (int) (currentPrice / armLivePrice) + 1;
                } else {
                    carpet.price = 0;
                }

                PGobject jsonObject = rs.getObject("ATTRIBUTES", PGobject.class);
                carpet.attributes = jsonObject.getValue();
                carpet.discountPrice = rs.getDouble("DISCOUNTPRICE");
                carpet.size = (String[]) rs.getArray("SIZE").getArray();
                carpet.deliveryTime = rs.getDate("DELIVERYTIME");
                carpet.imageSrcsSmall = (String[]) rs.getArray("IMAGESSRC").getArray();
                carpet.imageSrcsBig = (String[]) rs.getArray("IMAGESSRCBIG").getArray();
                carpet.imageSrc = rs.getString("IMAGESRC");
                carpet.inventory = rs.getInt("INVENTORY");
                carpet.weight = rs.getString("weight");
                if (carpet.weight == null) {
                    carpet.weight = "";
                }
                carpet.dimension = rs.getString("dimension");
                if (carpet.dimension == null) {
                    carpet.dimension = "";
                }
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            stmt.close();
            c.close();
        }
        String jsonInString = new Gson().toJson(carpet);
        JSONObject jsonCarpet = new JSONObject(jsonInString);
        return jsonCarpet;
    }

    public JSONObject getCarpetPriceAndUpdate(String id, boolean isArm) {
        double currentPrice = ExtractDijiKalaData.getInstance().getLinkPrice(id);
        int armLivePrice = liveExchange.getArmLivePrice();
        double price;
        if (currentPrice != 0.0) {
            price = (int) (currentPrice / armLivePrice) + 1;
        } else {
            price = 0;
        }
        updateCarpetPrice(id, currentPrice, isArm);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("price", price);
        return jsonObject;
    }

    public JSONObject getAllCarpetsDetails(String query, boolean isArm) throws SQLException {
        int armLivePrice = liveExchange.getArmLivePrice();
        ArrayList<Commodity> carpets = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            query = "'%" + query + "%'";
            c = openConnection();
            stmt = c.createStatement();
            String full_query;
            if (isArm) {
                full_query = "SELECT * FROM ARM_Commodity WHERE LOWER(name) LIKE " + query + " LIMIT 20;";
            } else {
                full_query = "SELECT * FROM RU_Commodity WHERE LOWER(name) LIKE " + query + " LIMIT 20;";
            }
            ResultSet rs = stmt.executeQuery(full_query);
            while (rs.next()) {
                Commodity carpet = new Commodity();
                carpet.id = Integer.parseInt(rs.getString("ID_DIJIKALA").split("-")[1]);
                carpet.name = rs.getString("NAME");
                carpet.brand = rs.getString("BRAND");
                carpet.price = rs.getDouble("PRICE");
                carpet.price = ((int) carpet.price / armLivePrice);
                PGobject jsonObject = rs.getObject("ATTRIBUTES", PGobject.class);
                carpet.attributes = jsonObject.getValue();
                carpet.discountPrice = rs.getDouble("DISCOUNTPRICE");
                carpet.size = (String[]) rs.getArray("SIZE").getArray();
                carpet.deliveryTime = rs.getDate("DELIVERYTIME");
                carpet.imageSrcsSmall = (String[]) rs.getArray("IMAGESSRC").getArray();
                carpet.imageSrcsBig = (String[]) rs.getArray("IMAGESSRCBIG").getArray();
                carpet.imageSrc = rs.getString("IMAGESRC");
                carpet.inventory = rs.getInt("INVENTORY");
                carpets.add(carpet);
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            stmt.close();
            c.close();
        }
        String jsonInString = new Gson().toJson(carpets);
        JSONArray jsonCarpet = new JSONArray(jsonInString);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Carpets", jsonCarpet);
        return jsonObject;
    }

    public JSONObject getBrandAllCarpetsDetails(String brand, boolean isArm) throws SQLException {
        int armLivePrice = liveExchange.getArmLivePrice();
        ArrayList<Commodity> carpets = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            brand = "'%" + brand + "%'";
            c = openConnection();
            stmt = c.createStatement();
            String query;
            if (isArm) {
                query = "SELECT * FROM ARM_Commodity WHERE LOWER(brand) LIKE " + brand + " LIMIT 40;";
            } else {
                query = "SELECT * FROM RU_Commodity WHERE LOWER(brand) LIKE " + brand + " LIMIT 40;";
            }
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Commodity carpet = new Commodity();
                carpet.id = Integer.parseInt(rs.getString("ID_DIJIKALA").split("-")[1]);
                carpet.name = rs.getString("NAME");
                carpet.brand = rs.getString("BRAND");
                carpet.price = rs.getDouble("PRICE");
                carpet.price = ((int) carpet.price / armLivePrice);

                PGobject jsonObject = rs.getObject("ATTRIBUTES", PGobject.class);
                carpet.attributes = jsonObject.getValue();
                carpet.discountPrice = rs.getDouble("DISCOUNTPRICE");
                carpet.size = (String[]) rs.getArray("SIZE").getArray();
                carpet.deliveryTime = rs.getDate("DELIVERYTIME");
                carpet.imageSrcsSmall = (String[]) rs.getArray("IMAGESSRC").getArray();
                carpet.imageSrcsBig = (String[]) rs.getArray("IMAGESSRCBIG").getArray();
                carpet.imageSrc = rs.getString("IMAGESRC");
                carpet.inventory = rs.getInt("INVENTORY");
                carpets.add(carpet);
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            stmt.close();
            c.close();
        }
        String jsonInString = new Gson().toJson(carpets);
        JSONArray jsonCarpet = new JSONArray(jsonInString);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Carpets", jsonCarpet);
        return jsonObject;
    }

    public JSONObject getAllBrandCarpet(boolean isArm) throws SQLException {
        int armLivePrice = liveExchange.getArmLivePrice();
        ArrayList<Commodity> carpets = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            String query;
            if (isArm) {
                query = "SELECT * FROM AllBrands_ARM_Commodity;";
            } else {
                query = "SELECT * FROM AllBrands_RU_Commodity;";
            }
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Commodity carpet = new Commodity();
                carpet.id = Integer.parseInt(rs.getString("ID_DIJIKALA").split("-")[1]);
                carpet.name = rs.getString("NAME");
                carpet.brand = rs.getString("BRAND");
                carpet.price = rs.getDouble("PRICE");
                carpet.price = ((int) carpet.price / armLivePrice);

                PGobject jsonObject = rs.getObject("ATTRIBUTES", PGobject.class);
                carpet.attributes = jsonObject.getValue();
                carpet.discountPrice = rs.getDouble("DISCOUNTPRICE");
                carpet.size = (String[]) rs.getArray("SIZE").getArray();
                carpet.deliveryTime = rs.getDate("DELIVERYTIME");
                carpet.imageSrcsSmall = (String[]) rs.getArray("IMAGESSRC").getArray();
                carpet.imageSrcsBig = (String[]) rs.getArray("IMAGESSRCBIG").getArray();
                carpet.imageSrc = rs.getString("IMAGESRC");
                carpet.inventory = rs.getInt("INVENTORY");
                carpets.add(carpet);
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            stmt.close();
            c.close();
        }
        String jsonInString = new Gson().toJson(carpets);
        JSONArray jsonCarpet = new JSONArray(jsonInString);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Carpets", jsonCarpet);
        return jsonObject;
    }

    public ArrayList<Commodity> getAllCarpet(boolean isArm) throws SQLException {
        int armLivePrice = liveExchange.getArmLivePrice();
        ArrayList<Commodity> carpets = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            String query;
            if (isArm) {
                query = "SELECT * FROM ARM_Commodity;";
            } else {
                query = "SELECT * FROM RU_Commodity;";
            }
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Commodity carpet = new Commodity();
                carpet.id = Integer.parseInt(rs.getString("ID_DIJIKALA").split("-")[1]);
                carpet.id_Dijikala = rs.getString("id_dijikala");
                carpet.name = rs.getString("NAME");
                carpet.brand = rs.getString("BRAND");
                carpet.price = rs.getDouble("PRICE");
                carpet.price = ((int) carpet.price / armLivePrice);

                PGobject jsonObject = rs.getObject("ATTRIBUTES", PGobject.class);
                carpet.attributes = jsonObject.getValue();
                carpet.discountPrice = rs.getDouble("DISCOUNTPRICE");
                carpet.size = (String[]) rs.getArray("SIZE").getArray();
                carpet.deliveryTime = rs.getDate("DELIVERYTIME");
                carpet.imageSrcsSmall = (String[]) rs.getArray("IMAGESSRC").getArray();
                carpet.imageSrcsBig = (String[]) rs.getArray("IMAGESSRCBIG").getArray();
                carpet.imageSrc = rs.getString("IMAGESRC");
                carpet.inventory = rs.getInt("INVENTORY");
                carpet.weight = rs.getString("WEIGHT");
                carpet.dimension = rs.getString("dimension");
                carpets.add(carpet);
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            stmt.close();
            c.close();
        }
        return carpets;
    }

    public void updateCarpetPrice(String dijikalaId, double price, boolean isArm) {

        Connection c = null;
        try {
            c = openConnection();
            String query;
            if (isArm) {
                query = "UPDATE ARM_Commodity set price = \'" + price + "\' where id_dijikala = \'" + "dkp-" + dijikalaId + "\';";
            } else {
                query = "UPDATE RU_Commodity set price = \'" + price + "\' where id_dijikala = \'" + "dkp-" + dijikalaId + "\';";
            }

            PreparedStatement pstmt = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();

            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, ex.getMessage(), ex);
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "price Update Successfully");
    }

    public Commodity getAllCarpetWithoutDimentions(boolean isArm, String dijikalaId) throws SQLException {
        Commodity carpet = null;
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            String query;
            if (isArm) {
                query = "SELECT * FROM ARM_Commodity where id_dijikala = \'" + dijikalaId + "\';";
            } else {
                query = "SELECT * FROM RU_Commodity where id_dijikala = \'" + dijikalaId + "\';";
            }
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                carpet = new Commodity();
                carpet.name = rs.getString("NAME");
                carpet.brand = rs.getString("BRAND");
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            stmt.close();
            c.close();
        }
        return carpet;
    }

    public void updateDimension(boolean isArm, String dijikalaId, String dimension, String weight, String label) {

        Connection c = null;
        try {
            c = openConnection();
            String query;
            if (isArm) {// "UPDATE  moskotransfomInformation set price = \'" + price + "\' where boxdetailsid = \'" + boxId + "\'";
                query = "UPDATE ARM_Commodity set dimension = \'" + dimension + "\', weight = \'" + weight + "\',  label = \'" + label + "\' where id_dijikala = \'" + dijikalaId + "\';";
            } else {
                query = "UPDATE RU_Commodity set dimension = \'" + dimension + "\', weight = \'" + weight + "\',  label = \'" + label + "\' where id_dijikala = \'" + dijikalaId + "\';";
            }

            PreparedStatement pstmt = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();

            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, ex.getMessage(), ex);
        } finally {
            try {
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "dimension Update Successfully");
    }

    public List<Commodity> getAllCarpetWithDimentions(boolean isArm) throws SQLException {
        List<Commodity> commodities = new ArrayList<Commodity>();
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            String query;
            if (isArm) {
                query = "SELECT * FROM ARM_commodity where dimension is not null;";
            } else {
                query = "SELECT * FROM RU_commodity where dimension is not null;";
            }
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Commodity carpet = new Commodity();
                carpet.id_Dijikala = rs.getString("id_dijikala");
                carpet.name = rs.getString("NAME");
                carpet.brand = rs.getString("BRAND");
                carpet.price = rs.getDouble("PRICE");
                carpet.weight = rs.getString("weight");
                carpet.dimension = rs.getString("dimension");
                commodities.add(carpet);
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            stmt.close();
            c.close();
        }
        return commodities;
    }

    public JSONObject getAllRecentOrPopularCarpet(Boolean isMost, boolean isArm) throws SQLException {
        int armLivePrice = liveExchange.getArmLivePrice();
        ArrayList<Commodity> carpets = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            String query;
            if (isArm) {
                if (isMost) {
                    query = "SELECT * FROM ARM_Commodity ORDER BY LASTVISITED DESC LIMIT 8;";
                } else {
                    query = "SELECT * FROM ARM_Commodity ORDER BY COUNTER DESC LIMIT 8;";
                }
            } else {
                if (isMost) {
                    query = "SELECT * FROM RU_Commodity ORDER BY LASTVISITED DESC LIMIT 8;";
                } else {
                    query = "SELECT * FROM RU_Commodity ORDER BY COUNTER DESC LIMIT 8;";
                }
            }
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Commodity carpet = new Commodity();
                carpet.id = Integer.parseInt(rs.getString("ID_DIJIKALA").split("-")[1]);
                carpet.name = rs.getString("NAME");
                carpet.brand = rs.getString("BRAND");
                carpet.price = rs.getDouble("PRICE");
                carpet.price = ((int) carpet.price / armLivePrice);

                carpet.discountPrice = rs.getDouble("DISCOUNTPRICE");
                carpet.imageSrc = rs.getString("IMAGESRC");
                carpet.inventory = rs.getInt("INVENTORY");
                carpets.add(carpet);
            }
            rs.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            stmt.close();
            c.close();
        }
        String jsonInString = new Gson().toJson(carpets);
        JSONArray jsonCarpet = new JSONArray(jsonInString);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Carpets", jsonCarpet);
        return jsonObject;
    }

    public void carpetViewLogs(int idDijikala) {
        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            c = openConnection();

            String sql = "UPDATE ARM_Commodity SET COUNTER = COUNTER + 1 , LASTVISITED = ? WHERE ID_DIJIKALA = ?";

            pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(1, timestamp);
            pstmt.setString(2, "dkp-" + idDijikala);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            try {
                pstmt.close();
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Table update successfully");
    }

    public JSONObject insertDijikalaData(String link) {
        JSONObject jsonObject = new JSONObject();
        ExtractDijiKalaData extractDijiKalaData = new ExtractDijiKalaData();
        PreparedStatement pstmt = null;
        Connection c = null;
        try {
            DijiKalaData dkd;
            dkd = extractDijiKalaData.getLinkData(link);
            c = openConnection();
            String sql = "INSERT INTO dijikalaData (ID,NAME,IMAGELINK,SCORE,ISEXIST,PRICE,SIMILARGOODSLINKS,SMALLIMAGELINKS) "
                    + "VALUES (?,?,?,?,?,?,?,?);";

            pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, dkd.id);
            pstmt.setString(2, dkd.persianName);
            pstmt.setString(3, dkd.imageLink);
            pstmt.setString(4, dkd.score);
            pstmt.setBoolean(5, dkd.isExist);
            pstmt.setString(6, dkd.price);
            pstmt.setArray(7, c.createArrayOf("TEXT", dkd.similarGoodsLinks));
            pstmt.setArray(8, c.createArrayOf("TEXT", dkd.smallImageLinks));

            pstmt.executeUpdate();
            pstmt.close();
            String jsonInString = new Gson().toJson(dkd);
            jsonObject.put("DijikalaData", jsonInString);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } finally {
            try {
                pstmt.close();
                c.close();
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        return jsonObject;
    }

}
