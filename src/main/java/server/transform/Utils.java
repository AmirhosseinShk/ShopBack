/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.transform;

/**
 *
 * @author AmirShk
 */
public class Utils {

    private static Utils instance;

    private Utils() {
    }

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    public int boxDetailsDetection(String weightString, String dimension) {
        if (dimension.contains("a")) {
            return 1;
        } else if (dimension.contains("b")) {
            return 1;
        } else if (dimension.contains("c")) {
            return 2;
        } else if (dimension.contains("d")) {
            return 3;
        } else if (dimension.contains("e")) {
            return 4;
        } else if (dimension.contains("f")) {
            return 5;
        } else if (weightString == null ){
             return -1;
        }else {
            String[] weightRes = weightString.split("گرم");
            if (weightRes.length > 1) {
                int weight = 0;
                if (weightRes[0].contains(".")) {
                    weight = (int) Double.parseDouble(weightRes[0].replace(" ", ""));
                } else {
                    weight = Integer.parseInt(weightRes[0].replace(" ", ""));
                }
                dimensionDetection(dimension, weight);
            } else {
                weightRes = weightString.split("کیلوگرم");
                if (weightRes.length > 1) {
                    int weight = 0;
                    if (weightRes[0].contains(".")) {
                        weight = (int) Double.parseDouble(weightRes[0].replace(" ", ""));
                    } else {
                        weight = Integer.parseInt(weightRes[0].replace(" ", ""));
                    }
                    weight *= 1000; // convert kiloGeram to geram
                    dimensionDetection(dimension, weight);
                }
            }
        }
        return -1;
    }

    private static int dimensionDetection(String dimension, int weight) { // weight on Geram
        String[] dimentionsType = {"a", "b", "c", "d", "e", "f"};
        boolean isMili = dimension.contains("میلی متر");
        String res = dimension.replace("سانتی‌متر", "").replace("میلی‌متر", "");
        String[] dimensions = res.split("×");
        int boxDetailsId = -1;
        boolean isMatch = false;
        String matchType = "";

        if (dimension.length() != 3) {
            dimensions = res.split("\\*");
            if (dimension.length() != 3) {
                dimensions = res.split("X");
                if (dimension.length() != 3) {
                    return boxDetailsId;
                }
            }
        }

        for (int i = 0; i < dimentionsType.length; i++) {
            String match = dimentionsType[i];
            isMatch = dimensionChecker(dimensions, match, weight, isMili);
            if (isMatch) {
                matchType = match;
                break;
            }
        }

        switch (matchType) {
            case "a":
            case "b":
                boxDetailsId = 1;
                break;
            case "c":
                boxDetailsId = 2;
                break;
            case "d":
                boxDetailsId = 3;
                break;
            case "e":
                boxDetailsId = 4;
                break;
            case "f":
                boxDetailsId = 5;
                break;
        }
        return boxDetailsId;
    }

    private static boolean dimensionChecker(String[] dimensions, String match, int weight, boolean isMili) {
        boolean isWidth = false;
        boolean isLength = false;
        boolean isHeight = false;
        boolean isWeight = false;
        String dimension;
        for (int i = 0; i < dimensions.length; i++) {
            int d = 0;
            dimension = dimensions[i].replace(" ", "");
            if (dimension.contains(".")) {
                d = (int) Double.parseDouble(dimension);
                if (isMili) {
                    d /= 10;
                }
            } else {
                d = Integer.parseInt(dimension);
                if (isMili) {
                    d /= 10;
                }
            }
            switch (match) {
                case "a":
                case "b":
                    if (d < 6 && !isHeight) {
                        isHeight = true;
                    } else if (d < 26 && !isWidth) {
                        isWidth = true;
                    } else if (d < 36) {
                        isLength = true;
                    }
                    if (weight < 2001) {
                        isWeight = true;
                    }
                    break;
                case "c":
                    if (d < 21 && !isLength) {
                        isLength = true;
                    } else if (d < 31 && !isWidth) {
                        isWidth = true;
                    } else if (d < 41) {
                        isHeight = true;
                    }
                    if (weight < 5001) {
                        isWeight = true;
                    }
                    break;
                case "d":
                    if (d < 21 && !isLength) {
                        isLength = true;
                    } else if (d < 41 && !isWidth) {
                        isWidth = true;
                    } else if (d < 61) {
                        isHeight = true;
                    }
                    if (weight < 30001) {
                        isWeight = true;
                    }
                    break;
                case "e":
                    if (d < 51 && isLength) {
                        isLength = true;
                    } else if (d < 51 && !isWidth) {
                        isWidth = true;
                    } else if (d < 101) {
                        isHeight = true;
                    }
                    if (weight < 50001) {
                        isWeight = true;
                    }
                    break;
                case "f":
                    if (d < 76 && !isLength) {
                        isLength = true;
                    } else if (d < 81 && !isWidth) {
                        isWidth = true;
                    } else if (d < 121) {
                        isHeight = true;
                    }
                    if (weight < 80001) {
                        isWeight = true;
                    }
                    break;
            }
        }
        return isWidth && isHeight && isLength && isWeight;
    }
}
