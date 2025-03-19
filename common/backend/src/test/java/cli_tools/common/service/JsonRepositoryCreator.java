package cli_tools.common.service;

import lombok.AllArgsConstructor;
import cli_tools.common.repository.SimpleJsonRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@AllArgsConstructor
public class JsonRepositoryCreator {

    public SimpleJsonRepository<ArrayList<Integer>> createRepository(String fileName, String fileContent) throws IOException {
        return new SimpleJsonRepositoryImpl(makeTempFile(fileName, fileContent));
    }

    public SimpleJsonRepository<ArrayList<Integer>> createRepository(File file) {
        return new SimpleJsonRepositoryImpl(file);
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

    private Path tempDir;

}
