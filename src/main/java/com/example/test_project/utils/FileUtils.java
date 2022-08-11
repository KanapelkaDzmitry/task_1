package com.example.test_project.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class FileUtils {

    public static void openFileIfExist(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
            log.info("File with path {} was created", path);
        }
    }

    public static void deleteFileIfExist(Path path) {
        if (Files.exists(path)) {
            try {
                Files.delete(path);
                log.info("File with path {} was deleted", path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createDataDirectory() {
        Path path = Paths.get(Constants.PATH_TO_FILES);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
                log.info("Directory with path {} was created", path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getLastFileNumber() {
        List<File> files = readFilesFromDataDirectory();
        return files.stream()
                .filter(file -> !file.getName().endsWith(Constants.NAME_OF_COMMON_FILE))
                .mapToInt(file -> getFileNumber(file.getName()))
                .max()
                .orElse(1);

    }

    public static List<File> readFilesFromDataDirectory() {
        FileUtils.createDataDirectory();
        File data = new File(Constants.PATH_TO_FILES);
        List<File> files = Arrays.asList(Objects.requireNonNull(data.listFiles()));

        if (files.isEmpty()) {
            throw new RuntimeException("Directory data is empty");
        }

        return files;
    }

    public static int getFileNumber(String fileName) {
        int startOfValue = fileName.lastIndexOf(Constants.PREFIX_OF_FILENAME)
                + Constants.PREFIX_OF_FILENAME.length();
        int endOfValue = fileName.length() - Constants.POSTFIX_OF_FILENAME.length();

        return Integer.parseInt(fileName.substring(startOfValue, endOfValue));
    }


}
