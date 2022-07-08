package server.transform;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import server.database.Connector;
import server.database.items.TransformInformation;

class CalculatePrice {

    public static void main(String[] args) throws IOException, SQLException {
        ArrayList<TransformInformation> moscoDetails = Connector.getInstance().getMoscoDetails();
        ArrayList<TransformInformation> allCityDetails = Connector.getInstance().getAllCityDetails();

        for (int i = 0; i < moscoDetails.size(); i++) {
            for (int j = 0; j < allCityDetails.size(); j++) {
                String price = calculatePrice(allCityDetails.get(j).price.split(" ")[0], Integer.parseInt(moscoDetails.get(i).boxDetails.id));
                TransformInformation tr = new TransformInformation(moscoDetails.get(i).boxDetails, allCityDetails.get(j).to, price);
                Connector.getInstance().InsertTransformInformation(tr);
            }
        }
    }

    static String calculatePrice(String p, int i) {
        double[][] coefficient_rang = range_coefficient();
        double price = Double.parseDouble(p);
        double predictionPrice = 0.0;
        if (rangeDetection(price, 2200, 2600)) {
            int range = 400;
            predictionPrice = price * (((price - 1600) / range) * coefficient_rang[i][0] + ((2000 - price) / range) * coefficient_rang[i][1]);
        } else if (rangeDetection(price, 2600, 2900)) {
            int range = 300;
            predictionPrice = price * (((price - 2000) / range) * coefficient_rang[i][1] + ((2300 - price) / range) * coefficient_rang[i][2]);
        } else if (rangeDetection(price, 2900, 3150)) {
            int range = 150;
            predictionPrice = price * (((price - 2300) / range) * coefficient_rang[i][2] + ((2450 - price) / range) * coefficient_rang[i][3]);
        } else {
            predictionPrice = coefficient_rang[i][3] * price;
        }
        return String.valueOf(predictionPrice);
    }

    static boolean rangeDetection(double price, double lowerBound, double upperBound) {
        if (price >= lowerBound && price < upperBound) {
            return true;
        } else {
            return false;
        }
    }

    static double[][] range_coefficient() {
        //upper bound
        //for box 5-40*30*20
        double[][] coefficient_rang = new double[6][4];
        coefficient_rang[2][0] = 1.58; //small_range_1600
        coefficient_rang[2][1] = 1.592; //small_range_2000
        coefficient_rang[2][2] = 1.62; //small_range_2300
        coefficient_rang[2][3] = 1.67; //small_range_2450

        //for box 30-40*20*60
        coefficient_rang[3][0] = 5.963; //meduim_range_1600
        coefficient_rang[3][1] = 6.177; //meduim_range_2000
        coefficient_rang[3][2] = 6.423; //meduim_range_2300
        coefficient_rang[3][3] = 6.9651; //meduim_range_2450

        //for box 30-40*20*60
        coefficient_rang[4][0] = 9.47; //large_range_1600
        coefficient_rang[4][1] = 9.844; //large_range_2000
        coefficient_rang[4][2] = 10.271; //large_range_2300
        coefficient_rang[4][3] = 11.2014; //large_range_2450

        //for box 80-80*75*120
        coefficient_rang[5][0] = 25.953; //huge_range_1600
        coefficient_rang[5][1] = 27.082; //huge_range_2000
        coefficient_rang[5][2] = 28.354; //huge_range_2300
        coefficient_rang[5][3] = 31.113; //huge_range_2450

        return coefficient_rang;

    }

}
