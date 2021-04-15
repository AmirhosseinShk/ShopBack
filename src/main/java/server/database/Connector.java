/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.database;

import com.google.gson.Gson;
import dajikala.parser.DijiKalaData;
import static dajikala.parser.Main.getLinkData;
import java.io.IOException;

import java.sql.*;
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
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " NAME           TEXT          NOT NULL,"
                    + " EMAIL          TEXT          NOT NULL,"
                    + " CONTACTNUMBER  TEXT          NOT NULL,"
                    + " TOURDATE       DATE          NOT NULL,"
                    + " TOURTIME       TIME          NOT NULL,"
                    + " CARPETIDS      TEXT[]        NOT NULL,"
                    + " TOTALAMOUNT    REAL          NOT NULL)";
            stmt.executeUpdate(ordersTourTable);

            //Orders Details
            String ordersTable = "CREATE TABLE IF NOT EXISTS Orders "
                    + "(ID             SERIAL        PRIMARY KEY,"
                    + " NAME           TEXT          NOT NULL,"
                    + " EMAIL          TEXT          NOT NULL,"
                    + " CONTACTNUMBER  TEXT          NOT NULL,"
                    + " ADDRESS        TEXT          NOT NULL,"
                    + " CARPETIDS      TEXT[]        NOT NULL,"
                    + " TOTALAMOUNT    REAL          NOT NULL)";

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

    public boolean InsertOrderData(Order order) {
        try {
            Connection c = openConnection();
            String sql = "INSERT INTO Orders (NAME,EMAIL,CONTACTNUMBER,ADDRESS,CARPETIDS,TOTALAMOUNT)"
                    + "VALUES (?,?,?,?,?,?);";

            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, order.name);
            pstmt.setString(2, order.email);
            pstmt.setString(3, order.contactNumber);
            pstmt.setString(4, order.address);
            pstmt.setArray(5, c.createArrayOf("TEXT", order.carpetIds));
            pstmt.setInt(6, order.totalAmount);

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
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Table created successfully");
        return true;
    }

    public boolean InsertCarpetDetails(Carpet carpet) {
        Logger.getLogger(Connector.class).info(new Gson().toJson(carpet));
        try {
            Connection c = openConnection();
            String sql = "INSERT INTO CarpetDetails (NAME,PRICE,BRAND,SIZE,INVENTORY,DELIVERYTIME,IMAGESRC,IMAGESSRC,DISCOUNTPRICE,ATTRIBUTES,COUNTER) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?);";
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
            pstmt.setObject(10, carpet.attributes);
            pstmt.setInt(11, 0);

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
                carpet.inventory = rs.getInt("INVENTORY");
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

    public JSONObject getAllCarpetsDetails(String query) throws SQLException {
        ArrayList<Carpet> carpets = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            query = "'%" + query + "%'";
            c = openConnection();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM carpetdetails WHERE LOWER(name) LIKE " + query + ";");
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

    public JSONObject getAllRecentOrPopularCarpet(Boolean isMost) throws SQLException {
        ArrayList<Carpet> carpets = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        try {
            c = openConnection();
            stmt = c.createStatement();
            String query;
            if (isMost) {
                query = "SELECT * FROM CarpetDetails ORDER BY LASTVISITED DESC LIMIT 5;";
            } else {
                query = "SELECT * FROM CarpetDetails ORDER BY COUNTER DESC LIMIT 5;";
            }
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Carpet carpet = new Carpet();
                carpet.id = rs.getInt("ID");
                carpet.name = rs.getString("NAME");
                carpet.price = rs.getDouble("PRICE");
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

    public void carpetViewLogs(int id) {
        try {
            Connection c = openConnection();

            String sql = "UPDATE CarpetDetails SET COUNTER = COUNTER + 1 , LASTVISITED = ? WHERE ID = ?";

            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(1, timestamp);
            pstmt.setInt(2, id);

            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception ex) {
            Logger.getLogger(Connector.class).log(Level.ERROR, null, ex);
        }
        Logger.getLogger(Connector.class).log(Level.INFO, "Table update successfully");
    }

    public JSONObject insertDijikalaData(String link) {
        JSONObject jsonObject = new JSONObject();
        try {
            DijiKalaData dkd;
            dkd = getLinkData(link);
            Connection c = openConnection();
            String sql = "INSERT INTO OrdersTour (ID,NAME,IMAGELINK,SCORE,ISEXIST,PRICE,SIMILARGOODSLINKS,SMALLIMAGELINKS) "
                    + "VALUES (?,?,?,?,?,?,?,?);";

            PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Connector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return jsonObject;
    }
}
