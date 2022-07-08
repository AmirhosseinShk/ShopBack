/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dajikala.parser;

import brand.catalog.BrandDetails;
import brand.catalog.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author @AmirShk
 */
public class ExtractDijiKalaData {

    public static ExtractDijiKalaData instance;

    public static ExtractDijiKalaData getInstance() {
        if (instance == null) {
            instance = new ExtractDijiKalaData();
        }
        return instance;
    }

    Map<Integer, DijiKalaBrand> allBrands = new HashMap<>();

    public DijiKalaData getLinkData(String link) {
        System.out.println(link);
        Client client = ClientBuilder.newClient();

        WebTarget resource = client.target("https://api.digikala.com/v1/product/" + link + "/");

        Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON);
        try {
            Response response = request.get();
            String jsonString = response.readEntity(String.class);
            JSONObject jsonObj = new JSONObject(jsonString).getJSONObject("data");
            JSONObject product = jsonObj.getJSONObject("product");

            DijiKalaData dkd = new DijiKalaData();

            dkd.persianName = product.getString("title_fa");

            JSONObject brand = product.getJSONObject("brand");
            if (brand != null) {
                dkd.brand = brand.getString("title_fa");
            }

            JSONObject img = product.getJSONObject("images");
            if (img != null) {
                JSONObject main = img.getJSONObject("main");
                if (main != null) {
                    dkd.imageLink = main.getJSONArray("url").getString(0);
                }
            }

            JSONObject rating = product.getJSONObject("rating");
            if (rating != null) {
                double score = (rating.getDouble("rate") / 100) * 5;
                int count = rating.getInt("count");
                dkd.score = score + "(" + count + ")";
            }

            try {
                JSONObject default_varient = product.getJSONObject("default_variant");
                dkd.isExist = true;
                JSONObject price = default_varient.getJSONObject("price");
                dkd.price = String.valueOf(price.getDouble("rrp_price"));
                dkd.disCountPrice = String.valueOf(price.getDouble("selling_price"));
            } catch (Exception ex) {
                dkd.isExist = false;
                dkd.price = "0";
            }

            if (img != null) {
                JSONArray list = img.getJSONArray("list");
                String[] smallImageLinks = new String[list.length()];
                for (int i = 0; i < list.length(); i++) {
                    smallImageLinks[i] = list.getJSONObject(i).getJSONArray("url").getString(0);
                }
                dkd.smallImageLinks = smallImageLinks;
                dkd.bigImageLinks = smallImageLinks;
            }

            JSONObject recommendations = jsonObj.getJSONObject("recommendations");

            if (recommendations != null) {
                JSONObject related_products = recommendations.getJSONObject("related_products");
                if (related_products != null) {
                    JSONArray products = related_products.getJSONArray("products");
                    String[] similarGoodsLinks = new String[products.length()];
                    for (int i = 0; i < products.length(); i++) {
                        similarGoodsLinks[i] = "https://www.digikala.com/product/dkp-" + products.getJSONObject(i).getInt("id");
                    }
                    dkd.similarGoodsLinks = similarGoodsLinks;
                }
            }

            JSONArray variants = product.getJSONArray("variants");
            if (variants.length() > 0) {
                String size[] = new String[variants.length()];
                for (int i = 0; i < variants.length(); i++) {
                    try {
                        JSONObject sizeJson = variants.getJSONObject(i).getJSONObject("size");
                        size[i] = sizeJson.getString("title");
                    } catch (Exception ex) {
                        try {
                            JSONObject colorJson = variants.getJSONObject(i).getJSONObject("color");
                            size[i] = colorJson.getString("title");
                        } catch (Exception ex2) {

                        }
                    }
                }
                dkd.size = size;
            }

            JSONArray specifications = product.getJSONArray("specifications");
            if (specifications.length() > 0) {
                try {
                    JSONArray attributes = specifications.getJSONObject(0).getJSONArray("attributes");
                    JSONObject jsonSpecifications = new JSONObject();
                    boolean isDimension = false;
                    for (int i = 0; i < attributes.length(); i++) {
                        String title = attributes.getJSONObject(i).getString("title");
                        if (title.startsWith("وزن")) {
                            dkd.weight = attributes.getJSONObject(i).getJSONArray("values").getString(0);
                        }
                        if (title.startsWith("ابعاد")) {
                            dkd.dimension = attributes.getJSONObject(i).getJSONArray("values").getString(0);
                            isDimension = true;
                        }
                        if (!isDimension) {
                            if (title.equals("طول")) {
                                dkd.dimension += attributes.getJSONObject(i).getJSONArray("values").getString(0);
                            }
                            if (title.equals("عرض")) {
                                dkd.dimension += attributes.getJSONObject(i).getJSONArray("values").getString(0);
                            }
                            if (title.equals("ارتفاع")) {
                                dkd.dimension += attributes.getJSONObject(i).getJSONArray("values").getString(0);
                            }
                        }
                        jsonSpecifications.put(title, attributes.getJSONObject(i).getJSONArray("values").getString(0));
                    }
                    dkd.attributes = jsonSpecifications.toString();

                } catch (Exception ex) {
                    System.out.println(link + "have no specification");
                }
            }
            return dkd;
        } catch (Exception ex) {
            System.out.println(link);
            System.out.println(ex);
            return null;
        }
    }

    public DijiKalaBrand getBrandDiscription(String brand) {
        Client client = ClientBuilder.newClient();

        WebTarget resource = client.target("https://api.digikala.com/v1/brands/" + brand + "/");
        DijiKalaBrand dijikalaBrand = new DijiKalaBrand();

        Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON);
        try {
            Response response = request.get();
            String jsonString = response.readEntity(String.class);
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONObject brandJson = jsonObj.getJSONObject("data").getJSONObject("brand");
            dijikalaBrand.id = brandJson.getInt("id");
            dijikalaBrand.titleEnglish = brandJson.getString("title_en");
            dijikalaBrand.titlePersian = brandJson.getString("title_fa");
            dijikalaBrand.discription = brandJson.getString("description");
            return dijikalaBrand;
        } catch (Exception ex) {
            return dijikalaBrand;
        }
    }

    public List<DijiKalaBrand> getCategoriesBrands(String dijikala_address, int pageLimit) {
        List<DijiKalaBrand> allBrandsData = new ArrayList<>();
        int total_pages = 1;
        int current_pages = 1;
        boolean pages_calculated = false;
        Map<Integer, Integer> idCounter = new HashMap<>();
        Map<Integer, DijiKalaBrand> idBrands = new HashMap<>();
        Client client = ClientBuilder.newClient();
        do {
            System.out.println(dijikala_address + "&page=" + current_pages);
            WebTarget resource = client.target(dijikala_address + "&page=" + current_pages);

            Invocation.Builder request = resource.request();
            request.accept(MediaType.APPLICATION_JSON);
            Response response = request.get();
            String jsonString = response.readEntity(String.class);
            JSONObject jsonObj = new JSONObject(jsonString).getJSONObject("data");
            JSONArray products = jsonObj.getJSONArray("products");
            if (!pages_calculated) {
                JSONObject pager = jsonObj.getJSONObject("pager");
                total_pages = pager.getInt("total_pages");
                if (total_pages > pageLimit) {
                    total_pages = pageLimit;
                }
                pages_calculated = true;
            }

            for (int j = 0; j < products.length(); j++) {
                JSONObject product = products.getJSONObject(j);
                String id = String.valueOf(product.getInt("id"));
                DijiKalaBrand brand = getLinkBrandDetails(id, 0);
                //check brand is checking or not
                if (!allBrands.containsKey(brand.id)) {
                    if (idCounter.containsKey(brand.id)) {
                        idCounter.computeIfPresent(brand.id, (k, v) -> v + 1);
                    } else {
                        idBrands.put(brand.id, brand);
                        idCounter.put(brand.id, 1);
                    }
                }
            }
            current_pages++;
        } while (current_pages <= total_pages);

        for (Map.Entry<Integer, Integer> entry : idCounter.entrySet()) {
            DijiKalaBrand brand = idBrands.get(entry.getKey());
            brand.counter = entry.getValue();
            allBrandsData.add(brand);
        }
        allBrands.putAll(idBrands);

        return allBrandsData;
    }

    public String getLinkScore(String link) {
        Client client = ClientBuilder.newClient();

        WebTarget resource = client.target("https://api.digikala.com/v1/product/" + link + "/");

        Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON);
        try {
            Response response = request.get();
            String jsonString = response.readEntity(String.class);
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONObject product = jsonObj.getJSONObject("data").getJSONObject("product");
            JSONObject rating = product.getJSONObject("rating");
            double score = (rating.getDouble("rate") / 100) * 5;
            int count = rating.getInt("count");
            return score + "(" + count + ")";
        } catch (Exception ex) {
            return "-1";
        }
    }

    public double getLinkPrice(String link) {
        try {
            Client client = ClientBuilder.newClient();

            WebTarget resource = client.target("https://api.digikala.com/v1/product/" + link + "/");

            Builder request = resource.request();
            request.accept(MediaType.APPLICATION_JSON);

            Response response = request.get();
            String jsonString = response.readEntity(String.class
            );
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONObject product = jsonObj.getJSONObject("data").getJSONObject("product");
            JSONObject default_varient = product.getJSONObject("default_variant");
            JSONObject price = default_varient.getJSONObject("price");
            return price.getDouble("rrp_price");
        } catch (Exception ex) {
            return 0;
        }
    }

    public DijiKalaBrand getLinkBrandDetails(String link, int counter) {
        Client client = ClientBuilder.newClient();

        WebTarget resource = client.target("https://api.digikala.com/v1/product/" + link + "/");

        Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON);
        DijiKalaBrand dijiKalaBrand = new DijiKalaBrand();
        try {
            Response response = request.get();
            String jsonString = response.readEntity(String.class);
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONObject product = jsonObj.getJSONObject("data").getJSONObject("product");
            JSONObject brand = product.getJSONObject("brand");
            dijiKalaBrand.id = brand.getInt("id");
            dijiKalaBrand.titleEnglish = brand.getString("title_en");
            dijiKalaBrand.titlePersian = brand.getString("title_fa");
            JSONObject url = brand.getJSONObject("url");
            String uri = url.getString("uri");
            dijiKalaBrand.dijikala_uri = "https://www.digikala.com" + uri;
            dijiKalaBrand.counter = counter;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return dijiKalaBrand;
    }

    public void getBrandTopProducts(BrandDetails bd) {
        Client client = ClientBuilder.newClient();

        WebTarget resource = client.target("https://api.digikala.com/v1/brands/" + bd.link + "/?sort=27");
        Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON);
        Response response = request.get();
        String jsonString = response.readEntity(String.class);
        JSONObject jsonObj = new JSONObject(jsonString);
        JSONArray productsJson = jsonObj.getJSONObject("data").getJSONArray("products");
        if (productsJson.length() < 4) {
            bd.products = null;
        } else {
            for (int i = 0; i < productsJson.length(); i++) {
                try {
                    JSONObject product = productsJson.getJSONObject(i);
                    String productName = product.getString("title_fa");
                    String image = product.getJSONObject("images").getJSONObject("main").getJSONArray("url").getString(0);
                    double stars = product.getJSONObject("data_layer").getDouble("dimension9");
                    double price = product.getJSONObject("default_variant").getJSONObject("price").getDouble("rrp_price");
                    Product productData = new Product(productName, price, stars, image);
                    bd.products.add(productData);
                    if(bd.products.size() == 4){
                        break;
                    }
                } catch (Exception ex) {
                }
            }
            try {
                bd.logo = jsonObj.getJSONObject("data").getJSONObject("brand").getJSONObject("logo").getJSONArray("url").getString(0);
            } catch (Exception e) {
            }
        }
    }

    private String persianToEnglishConverter(String num) {
        String res = "";
        for (int i = 0; i < num.length(); i++) {
            String s = Character.toString(num.charAt(i));
            if (s.equals(".")) {
                res += ".";
            } else if (s.equals(",")) {
            } else {
                res += faToEn(s);
            }
        }
        return res;
    }

    private String faToEn(String num) {
        return num
                .replace("۰", "0")
                .replace("۱", "1")
                .replace("۲", "2")
                .replace("۳", "3")
                .replace("۴", "4")
                .replace("۵", "5")
                .replace("۶", "6")
                .replace("۷", "7")
                .replace("۸", "8")
                .replace("۹", "9");
    }
}
