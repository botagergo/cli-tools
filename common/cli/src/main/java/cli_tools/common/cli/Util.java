package cli_tools.common.cli;

public class Util {
    public static int parseTempId(String str) throws IllegalArgumentException {
        try {
            int taskID = Integer.parseInt(str);
            if (taskID < 1) {
                throw new IllegalArgumentException("Invalid temporary id: " + str);
            }
            return taskID;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid temporary id: " + str);
        }
    }
}
