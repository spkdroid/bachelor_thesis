package hochschule.de.bachelorthesis.utility;

public class Converter {
    public static String convertBoolean(Boolean b) {
        if(b)
            return "Yes";
        else
            return "No";
    }

    public static String convertInteger(Integer i) {
        if(i == 0)
            return "";
        else
            return String.valueOf(i);
    }
}