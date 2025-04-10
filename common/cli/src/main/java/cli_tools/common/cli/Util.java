package cli_tools.common.cli;

public class Util {
    public static int parseTempId(String str) throws IllegalArgumentException {
        try {
            int taskID = Integer.parseInt(str);
            if (taskID >= 1) {
                return taskID;
            }
        } catch (NumberFormatException ignored) {}
        throw new IllegalArgumentException("invalid temporary id: " + str);
    }

    public static String strip(String str) {
        String strippedStr = str.stripTrailing();
        if (strippedStr.isEmpty()) {
            return "";
        }

        char lastChar = strippedStr.charAt(strippedStr.length()-1);
        if (str.length() > strippedStr.length() &&
                lastChar == '\\') {
            strippedStr = strippedStr + str.charAt(strippedStr.length());
        }
        return strippedStr.stripLeading();
    }
}
