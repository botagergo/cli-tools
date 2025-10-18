package cli_tools.task_manager.cli;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OsDirs {
    private static final String appName = "task_manager";

    public enum DirType { CONFIG, DATA, CACHE, LOG }

    public static File getDataDir(String profile) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return Paths.get(System.getenv("APPDATA"), appName, "profiles", profile).toFile();
        } else {
            String baseDir = System.getenv("XDG_DATA_HOME");
            if (baseDir == null) {
                baseDir = Paths.get(System.getProperty("user.home"), ".local", "share").toString();
            }
            return Paths.get(baseDir, appName, "profiles", profile).toFile();
        }
    }

    public static File getFile(DirType type, String profile, String fileName) {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return switch (type) {
                case CONFIG, DATA -> buildPath(System.getenv("APPDATA"), profile, fileName).toFile();
                case CACHE -> buildPath(System.getenv("LOCALAPPDATA"), profile, fileName).toFile();
                case LOG -> buildPath(System.getenv("LOCALAPPDATA"), profile, "Logs").toFile();
                default -> throw new IllegalArgumentException();
            };
        } else {
            return switch (type) {
                case CONFIG -> new File(System.getenv().getOrDefault("XDG_CONFIG_HOME",
                        buildPath(Paths.get(System.getProperty("user.home"), ".config").toString(), profile, fileName).toString()));
                case DATA -> new File(System.getenv().getOrDefault("XDG_DATA_HOME",
                        buildPath(Paths.get(System.getProperty("user.home"), ".local", "share").toString(), profile, fileName).toString()));
                case CACHE -> new File(System.getenv().getOrDefault("XDG_CACHE_HOME",
                        buildPath(Paths.get(System.getProperty("user.home"), ".cache").toString(), profile, fileName).toString()));
                case LOG -> new File(System.getenv().getOrDefault("XDG_STATE_HOME",
                        buildPath(Paths.get(System.getProperty("user.home"), ".local", "state").toString(), profile, "log").toString()));
                default -> throw new IllegalArgumentException();
            };
        }
    }

    private static Path buildPath(String basePath, String profile, String fileName) {
        return Paths.get(basePath, appName, "profiles", profile, fileName);
    }
}
