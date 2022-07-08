/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.exchange;

import server.properties.ProjectProperties;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 *
 * @author @AmirShk
 */
public class LiveExchange {

    final String exchangeUrlAmd;
    private int previusAmdPrice = 407;
    private int counter = 5000;

    public LiveExchange() {
        exchangeUrlAmd = ProjectProperties.getInstance().getProperty("exchange.url.amd");
    }

    public int getArmLivePrice() {
        if (counter == 5000) {
            try {
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(exchangeUrlAmd);
                Response res = target.request().get();
                String output = res.readEntity(String.class);
                JSONObject resJson = new JSONObject(output);
                int result = resJson.getInt("status");
                if (result == 200) {
                    JSONObject data = resJson.getJSONObject("data").getJSONArray("prices").getJSONObject(0);
                    previusAmdPrice = Integer.parseInt(data.getString("live").replace(",", ""));
                }
            } catch (Exception exception) {
                System.out.println(exception);
            }
            counter--;
        } else {
            counter--;
            if (counter == 0) {
                counter = 5000;
            }
        }
        return previusAmdPrice;
    }

}
