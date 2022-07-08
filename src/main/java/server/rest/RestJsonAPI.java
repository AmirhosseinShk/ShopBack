package server.rest;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Locale;

import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import server.database.Connector;
import server.database.items.BoxDetails;
import server.database.items.Commodity;
import server.database.items.Order;
import server.database.items.OrderTour;
import server.database.items.TransformInformation;
import server.properties.ProjectProperties;
import server.transform.Utils;

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
        int orderId = connector.InsertOrderData(order);
        return RestApplication.returnJsonObject(request, new JSONObject().put("orderId", orderId));
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
        Commodity carpet = new Gson().fromJson(jsonData, Commodity.class);
        Connector connector = Connector.getInstance();
        boolean status = connector.InsertCarpetDetails(carpet);
        if (status) {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "success"));
        } else {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "fail"));
        }
    }

    @GET
    @Path("getCarpet/{id}/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarpetById(@Context HttpServletRequest request, @PathParam("id") int id, @PathParam("language") String language, @QueryParam("city") String city) throws SQLException {
        Connector connector = Connector.getInstance();
        connector.carpetViewLogs(id);
        JSONObject jsonCarpet;
        if (language.equals("Ar")) {
            jsonCarpet = connector.getCarpetDetailsbyID(id, true);
        } else {
            jsonCarpet = connector.getCarpetDetailsbyID(id, false);
        }
        if (city != null && jsonCarpet.getString("dimension") != null) {
            int boxDetailsId = Utils.getInstance().boxDetailsDetection(jsonCarpet.getString("weight"), jsonCarpet.getString("dimension"));
            String transformPrice;
            if (boxDetailsId == -1) {
                transformPrice = "-1";
            } else {
                transformPrice = connector.getCityTransformDetails(boxDetailsId, city);
            }
            jsonCarpet.append("transformPrice", transformPrice);
        }
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("getCarpets/{query}/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCarpets(@Context HttpServletRequest request, @PathParam("query") String query, @PathParam("language") String language) throws SQLException {
        Connector connector = Connector.getInstance();
        JSONObject jsonCarpet;
        if (language.equals("Ar")) {
            jsonCarpet = connector.getAllCarpetsDetails(query.toLowerCase(), true);
        } else {
            jsonCarpet = connector.getAllCarpetsDetails(query.toLowerCase(), false);
        }
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("getPriceAndUpdate/{id}/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPriceAndUpdate(@Context HttpServletRequest request, @PathParam("id") String query, @PathParam("language") String language) throws SQLException {
        Connector connector = Connector.getInstance();
        JSONObject jsonCarpet;
        if (language.equals("Ar")) {
            jsonCarpet = connector.getCarpetPriceAndUpdate(query.toLowerCase(), true);
        } else {
            jsonCarpet = connector.getCarpetPriceAndUpdate(query.toLowerCase(), false);
        }
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("getBrandCarpets/{brand}/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBrandCarpets(@Context HttpServletRequest request, @PathParam("brand") String brand, @PathParam("language") String language) throws SQLException {
        Connector connector = Connector.getInstance();
        JSONObject jsonCarpet;
        if (language.equals("Ar")) {
            jsonCarpet = connector.getBrandAllCarpetsDetails(brand.toLowerCase(), true);
        } else {
            jsonCarpet = connector.getBrandAllCarpetsDetails(brand.toLowerCase(), false);
        }
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("getAllBrandCarpets/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBrandCarpets(@Context HttpServletRequest request, @PathParam("language") String language) throws SQLException {
        Connector connector = Connector.getInstance();
        JSONObject jsonCarpet;
        if (language.equals("Ar")) {
            jsonCarpet = connector.getAllBrandCarpet(true);
        } else {
            jsonCarpet = connector.getAllBrandCarpet(false);
        }
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("getMostRecent/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMostRecent(@Context HttpServletRequest request, @PathParam("language") String language) throws SQLException {
        Connector connector = Connector.getInstance();
        JSONObject jsonCarpet;
        if (language.equals("Ar")) {
            jsonCarpet = connector.getAllRecentOrPopularCarpet(true, true);
        } else {
            jsonCarpet = connector.getAllRecentOrPopularCarpet(true, false);
        }
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("getPopularCarpet/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPoularCarpet(@Context HttpServletRequest request, @PathParam("language") String language) throws SQLException {
        Connector connector = Connector.getInstance();
        JSONObject jsonCarpet;
        if (language.equals("Ar")) {
            jsonCarpet = connector.getAllRecentOrPopularCarpet(false, true);
        } else {
            jsonCarpet = connector.getAllRecentOrPopularCarpet(false, false);
        }
        return RestApplication.returnJsonObject(request, jsonCarpet);
    }

    @GET
    @Path("initialTestData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response initialTestData(@Context HttpServletRequest request) throws SQLException {
        Connector connector = Connector.getInstance();
        Commodity carpet = new Commodity();
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
        carpet.imageSrcsSmall = images;
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

    @GET
    @Path("setTransformPrice/{to}/{size}/{price}/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setTransformPrice(@Context HttpServletRequest request,
            @PathParam("to") String to, @PathParam("size") String size, @PathParam("price") String price, @PathParam("token") String token) throws SQLException {
        String transformToken = ProjectProperties.getInstance().getProperty("transfom.price.token");
        if (transformToken.equals(token)) {
            Connector connector = Connector.getInstance();
            String[] sizeDetail = size.split("-");
            int weight = Integer.parseInt(sizeDetail[0]);
            String[] dimentions = sizeDetail[1].split("\\*");
            BoxDetails boxDetails = new BoxDetails(weight, Integer.parseInt(dimentions[0]), Integer.parseInt(dimentions[1]), Integer.parseInt(dimentions[2]));
            TransformInformation transformInformation = new TransformInformation(boxDetails, to, price.replace("\"", ""));
            connector.InsertTransformInformation(transformInformation);
            System.out.println("to:" + to + "size:" + size + "price:" + price);
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "success"));
        } else {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "failure"));
        }
    }

    @GET
    @Path("updateTransformPrice/{boxId}/{price}/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTransformPrice(@Context HttpServletRequest request,
            @PathParam("boxId") String boxId, @PathParam("price") String price, @PathParam("token") String token) throws SQLException {
        String transformToken = ProjectProperties.getInstance().getProperty("transfom.price.token");
        if (transformToken.equals(token)) {
            Connector connector = Connector.getInstance();
            connector.UpdateMoskoTransformInformation(price.replace("\"", ""), boxId);
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "success"));
        } else {
            return RestApplication.returnJsonObject(request, new JSONObject().put("status", "failure"));
        }
    }

}
