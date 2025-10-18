package cli_tools.test_utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    private final Path tempDir;

    public FileUtils() throws IOException {
        tempDir = Files.createTempDirectory("testng");
    }

    public File makeTempJsonFile(String name, String content, Object... args) throws IOException {
        Path tempFile = Files.createTempFile(tempDir, name, ".json");
        Files.writeString(tempFile, content.formatted(args));
        return tempFile.toFile();
    }

    public File makeTempJsonFile(String name) throws IOException {
        return Files.createTempFile(tempDir, name, ".json").toFile();
    }

    public File getTempFile(String name) {
        return Paths.get(tempDir.toString(), name + ".json").toFile();
    }

    public static String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }

}
