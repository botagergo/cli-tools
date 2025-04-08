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
}
