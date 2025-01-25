package com.savaava.mytvskeeper.utility;

public class FormatString {
    /**
     * format the string written by the user for the https request
     * @return the same string with formatted with %20 and correct spacing
     */
    public static String nameForHTTP(String title) {
        String out = title;
        out = out.trim();
        out = out.replaceAll("\\s+", " ");
        out = String.join("%20", out.split(" "));
        return out;
    }

    public static String compactTitle(String title) {
        return compactString(title, 40);
    }

    public static String compactDescription(String description) {
        return compactString(description,90);
    }

    public static String stringNormalize(String str) {
        return str.trim().replace("\n", " ").replaceAll("\\s+", " ");
    }

    public static String compactString(String str, int lengthMax) {
        if (str==null || str.isEmpty()) {
            return "";
        }

        str = stringNormalize(str);

        StringBuilder out = new StringBuilder();
        String[] words = str.split(" ");
        int currentLength = 0;

        for (String word : words) {
            if (currentLength > 0  &&  currentLength+word.length()+1 > lengthMax) {
                out.append("\n");
                currentLength = 0;
            } else if (currentLength > 0) {
                out.append(" ");
                currentLength++;
            }

            out.append(word);
            currentLength += word.length();
        }

        return out.toString();
    }
}
