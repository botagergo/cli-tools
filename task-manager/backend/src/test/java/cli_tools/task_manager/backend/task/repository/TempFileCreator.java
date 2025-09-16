package cli_tools.task_manager.backend.task.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TempFileCreator {

    private final Path tempDir;

    public TempFileCreator(String tempDirName) throws IOException {
        tempDir = Files.createTempDirectory(tempDirName);
    }

    public File makeTempFile(String name, String content) throws IOException {
        Path tempFile = Files.createTempFile(tempDir, name, ".json");
        if (content != null) {
            Files.writeString(tempFile, content);
        }
        return tempFile.toFile();
    }

    public File getTempFile(String name) {
        return Paths.get(tempDir.toString(), name + ".json").toFile();
    }

}
