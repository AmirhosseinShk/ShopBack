/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.database;

import com.google.gson.Gson;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import server.database.items.Carpet;
import server.database.items.Order;
import server.database.items.OrderTour;
import server.properties.ProjectProperties;

/**
 *
 * @author @AmirShk
 */
public class Connector {

    public static Connector instance;

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
                    + " SIZE           TEXT[]     NOT NULL,"
                    + " INVENTORY      INTEGER       NOT NULL,"
                    + " DELIVERYTIME   DATE          NOT NULL,"
                    + " IMAGESRC       TEXT          NOT NULL,"
                    + " IMAGESSRC      TEXT[]        NOT NULL,"
                    + " DISCOUNTPRICE  REAL,"
                    + " ATTRIBUTES     JSON)";
            stmt.executeUpdate(carpetTable);

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

            //Orders Details
            String ordersTourTable = "CREATE TABLE IF NOT EXISTS OrdersTour "
                    + "(ID INT PRIMARY KEY           NOT NULL,"
                    + " NAME           TEXT          NOT NULL,"
                    + " EMAIL          TEXT          NOT NULL,"
                    + " CONTACTNUMBER  INTEGER       NOT NULL,"
                    + " TOURDATE       DATE          NOT NULL,"
                    + " TOURTIME       TIME          NOT NULL)";
            stmt.executeUpdate(ordersTourTable);

            //Orders Details
            String ordersTable = "CREATE TABLE IF NOT EXISTS Orders "
                    + "(ID INT PRIMARY KEY           NOT NULL,"
                    + " NAME           TEXT          NOT NULL,"
                    + " EMAIL          TEXT          NOT NULL,"
                    + " CONTACTNUMBER  INTEGER       NOT NULL,"
                    + " ADDRESS        TEXT          NOT NULL)";
            stmt.executeUpdate(ordersTable);
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        } finally {
            stmt.close();
            c.close();
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Table created successfully");
    }

    public boolean InsertOrderData(Order order) {
        try {
            Connection c = openConnection();
            String sql = "INSERT INTO Orders (NAME,EMAIL,CONTACTNUMBER,TOURDATE,TOURTIME) "
                    + "VALUES (?,?,?,?,?);";

            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, order.name);
            pstmt.setString(2, order.email);
            pstmt.setInt(3, order.contactNumber);
            pstmt.setString(4, order.address);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, ex.getMessage(), ex);
            return false;
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Table created successfully");
        return true;
    }

    public boolean InsertOrderTourData(OrderTour orderTour) {
        try {
            Connection c = openConnection();
            String sql = "INSERT INTO OrdersTour (NAME,EMAIL,CONTACTNUMBER,ADDRESS) "
                    + "VALUES (?,?,?,?,?);";

            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, orderTour.name);
            pstmt.setString(2, orderTour.email);
            pstmt.setInt(3, orderTour.contactNumber);
            pstmt.setDate(4, orderTour.tourDate);
            pstmt.setTime(5, orderTour.tourTime);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
            return false;
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Table created successfully");
        return true;
    }

    public boolean InsertCarpetDetails(Carpet carpet) {
        Logger.getLogger(Connector.class).info(new Gson().toJson(carpet));
        try {
            Connection c = openConnection();
            String sql = "INSERT INTO CarpetDetails (NAME,PRICE,BRAND,SIZE,INVENTORY,DELIVERYTIME,IMAGESRC,IMAGESSRC,DISCOUNTPRICE,ATTRIBUTES) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, carpet.name);
            pstmt.setDouble(2, carpet.price);
            pstmt.setString(3, carpet.brand);
            pstmt.setArray(4, c.createArrayOf("TEXT", carpet.size));
            pstmt.setInt(5, carpet.inventory);
            pstmt.setDate(6, carpet.deliveryTime);
            pstmt.setString(7, carpet.imageSrc);
            pstmt.setArray(8, c.createArrayOf("TEXT", carpet.imageSrcs));
            pstmt.setDouble(9, carpet.discountPrice);
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(carpet.attributes);
            pstmt.setObject(10, jsonObject);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
            return false;
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Carpet update successfully");
        return true;
    }

    public JSONObject getCarpetDetailsbyID(int id) throws SQLException {
        Carpet carpet = new Carpet();
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CarpetDetails where ID=" + id + ";");
            while (rs.next()) {
                carpet.id = rs.getInt("ID");
                carpet.name = rs.getString("NAME");
                carpet.brand = rs.getString("BRAND");
                carpet.price = rs.getDouble("PRICE");
                PGobject jsonObject = rs.getObject("ATTRIBUTES", PGobject.class);
                carpet.attributes = jsonObject.getValue();
                carpet.discountPrice = rs.getDouble("DISCOUNTPRICE");
                carpet.size = (String[]) rs.getArray("SIZE").getArray();
                carpet.deliveryTime = rs.getDate("DELIVERYTIME");
                carpet.imageSrcs = (String[]) rs.getArray("IMAGESSRC").getArray();
                carpet.imageSrc = rs.getString("IMAGESRC");
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

    public JSONObject getAllCarpetsDetails() throws SQLException {
        ArrayList<Carpet> carpets = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM carpetdetails;");
            while (rs.next()) {
                Carpet carpet = new Carpet();
                carpet.id = rs.getInt("ID");
                carpet.name = rs.getString("NAME");
                carpet.brand = rs.getString("BRAND");
                carpet.price = rs.getDouble("PRICE");
                PGobject jsonObject = rs.getObject("ATTRIBUTES", PGobject.class);
                carpet.attributes = jsonObject.getValue();
                carpet.discountPrice = rs.getDouble("DISCOUNTPRICE");
                carpet.size = (String[]) rs.getArray("SIZE").getArray();
                carpet.deliveryTime = rs.getDate("DELIVERYTIME");
                carpet.imageSrcs = (String[]) rs.getArray("IMAGESSRC").getArray();
                carpet.imageSrc = rs.getString("IMAGESRC");
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

    public JSONObject getAllMostRecent() throws SQLException {
        ArrayList<Carpet> carpets = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM MostRecent;");
            while (rs.next()) {
                Carpet carpet = new Carpet();
                carpet.id = rs.getInt("ID");
                carpet.name = rs.getString("NAME");
                carpet.price = rs.getDouble("PRICE");
                carpet.discountPrice = rs.getDouble("DISCOUNTPRICE");
                carpet.imageSrc = rs.getString("IMAGESRC");
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
    
    public JSONObject getAllPopularProduct() throws SQLException {
        ArrayList<Carpet> carpets = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM PopularProduct;");
            while (rs.next()) {
                Carpet carpet = new Carpet();
                carpet.id = rs.getInt("ID");
                carpet.name = rs.getString("NAME");
                carpet.price = rs.getDouble("PRICE");
                carpet.discountPrice = rs.getDouble("DISCOUNTPRICE");
                carpet.imageSrc = rs.getString("IMAGESRC");
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
}
