package server.rest;

import com.google.gson.Gson;
import dajikala.parser.DijiKalaData;
import static dajikala.parser.Main.getLinkData;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import server.database.Connector;
import server.database.items.Carpet;
import server.database.items.Order;
import server.database.items.OrderTour;
import server.properties.ProjectProperties;

/**
 *
 * @author amirshk
 */
@Path("/")
public class RestJsonAPI {

    @POST
    @Path("order")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveOrder(@Context HttpServletRequest request, String jsonData) throws Exception {
        Order order = new Gson().fromJson(jsonData, Order.class);
        Connector connector = Connector.getInstance();
        boolean status = connector.InsertOrderData(order);
        if (status) {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "success"));
        } else {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "fail"));
        }
    }

    @POST
    @Path("orderTour")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveOrderTour(@Context HttpServletRequest request, String jsonData) throws Exception {
        //convert times and date
        JSONObject jsonObject = new JSONObject(jsonData);
        DateFormat df = new SimpleDateFormat("yy:MM:dd", Locale.ENGLISH);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date(df.parse(jsonObject.getString("date")).getTime());
        Time time = new java.sql.Time(formatter.parse(jsonObject.getString("time")).getTime());

        OrderTour orderTour = new Gson().fromJson(jsonData, OrderTour.class);
        orderTour.tourDate = date;
        orderTour.tourTime = time;
        Connector connector = Connector.getInstance();
        boolean status = connector.InsertOrderTourData(orderTour);
        if (status) {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "success"));
        } else {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "fail"));
        }
    }

    @POST
    @Path("pushCarpet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pushCarpetDetails(@Context HttpServletRequest request, String jsonData) throws Exception {
        Carpet carpet = new Gson().fromJson(jsonData, Carpet.class);
        Connector connector = Connector.getInstance();
        boolean status = connector.InsertCarpetDetails(carpet);
        if (status) {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "success"));
        } else {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "fail"));
        }
    }

    @GET
    @Path("getCarpet/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarpetById(@Context HttpServletRequest request, @PathParam("id") int id) throws SQLException {
        Connector connector = Connector.getInstance();
        connector.carpetViewLogs(id);
        JSONObject jsonCarpet = connector.getCarpetDetailsbyID(id);
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("getCarpets/{query}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCarpets(@Context HttpServletRequest request, @PathParam("query") String query) throws SQLException {
        Connector connector = Connector.getInstance();
        JSONObject jsonCarpet = connector.getAllCarpetsDetails(query.toLowerCase());
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("getMostRecent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMostRecent(@Context HttpServletRequest request) throws SQLException {
        Connector connector = Connector.getInstance();
        JSONObject jsonCarpet = connector.getAllRecentOrPopularCarpet(true);
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("getPopularCarpet")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPoularCarpet(@Context HttpServletRequest request) throws SQLException {
        Connector connector = Connector.getInstance();
        JSONObject jsonCarpet = connector.getAllRecentOrPopularCarpet(false);
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("initialTestData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response initialTestData(@Context HttpServletRequest request) throws SQLException {
        Connector connector = Connector.getInstance();
        Carpet carpet = new Carpet();
        carpet.name = "Iranian Carpet";
        carpet.brand = "Tabriz";
        carpet.inventory = 50;
        carpet.discountPrice = 325000;
        carpet.imageSrc = "1";
        String[] images = new String[4];
        images[0] = "1.1";
        images[1] = "1.2";
        images[2] = "1.3";
        images[3] = "1.4";
        carpet.imageSrcs = images;
        carpet.price = 500000;
        String[] sizes = new String[3];
        sizes[0] = "80*60";
        sizes[1] = "100*50";
        sizes[2] = "120*80";
        carpet.size = sizes;
        carpet.deliveryTime = new Date(2021, 2, 20);
        JSONObject json = new JSONObject();
        json.put("Shape", "Squere");
        json.put("Color", "blue");
        json.put("Shane", 1000);
        json.put("Test", "test");
        carpet.attributes = json.toString();
        System.out.println(carpet.attributes);
        boolean status = connector.InsertCarpetDetails(carpet);
        if (status) {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "success"));
        } else {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "fail"));
        }
    }

    @GET
    @Path("dijikalaParser/{link}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dijikalaParser(@Context HttpServletRequest request, String link) throws SQLException {
        Connector connector = Connector.getInstance();
        JSONObject res = connector.insertDijikalaData(link);
        return RestApplication.returnJsonObject(request, res);
    }

}
