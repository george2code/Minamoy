package core.utils;

public class StringUtils
{
    public static String GetCommaSeparatedString(String[] name) {
        if (name.length > 0) {
            StringBuilder nameBuilder = new StringBuilder();
            for (String n : name) {
                nameBuilder.append("'").append(n.replace("'", "\\'")).append("',");
                // can also do the following
                // nameBuilder.append("'").append(n.replace("'", "''")).append("',");
            }
            nameBuilder.deleteCharAt(nameBuilder.length() - 1);
            return nameBuilder.toString();
        } else {
            return "";
        }
    }

    public static String removeExtraSpaces(String word) {
        return word.replaceAll("\\s+", " ");
    }

    public static String removeComma(Object object) {
        return String.valueOf(object).replace(",", "");
    }

    public static String cutString(String s, int length) {
        if (s.length() > length)
            return s.substring(0, Math.min(s.length(), length-2)) + "...";
        return s;
    }


    public static String removeLastSymbol(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length()-1)==',') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }
}