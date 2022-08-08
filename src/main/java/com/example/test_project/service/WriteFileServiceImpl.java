package com.example.test_project.service;

import com.example.test_project.dto.ResultDto;
import com.example.test_project.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Random;

@Slf4j
@Service
public class WriteFileServiceImpl implements WriteFileService {

    @Override
    public void generateFiles() {
        final Random random = new Random();
        for (int i = 1; i <= Constants.QUANTITY_OF_FILES; i++) {
            generateOneFile(random, Constants.PATTERN_OF_FILENAME + i + ".txt");
        }
    }

    @Override
    public void joinFilesToOneFile(String invalidSource) {
        for (int i = 0; i <= 10; i++) {

        }
    }

    @Override
    public void importToDatabase() {

    }

    @Override
    public ResultDto getResultOfStatistic() {
        return null;
    }

    private void generateOneFile(Random random, String fileName) {
        for (int i = 1; i <= Constants.QUANTITY_OF_SOURCES_IN_EACH_FILE; i++) {
            String source = generateOneSource(random);
            Path path = Paths.get(Constants.PATH_TO_FILES + fileName);
            try {
                if (!Files.exists(path)) {
                    Files.createFile(path);
                }
                Files.writeString(path,
                        source + "\n", StandardOpenOption.APPEND);
                log.info("wrote source {} in file {}", source, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateOneSource(Random random) {
        String randomDate = generateRandomDateAsString(random);
        String randomLatinChars = generateRandomSourceOfSymbols(random, Constants.LIST_OF_LATIN_SYMBOLS);
        String randomCyrillicChars = generateRandomSourceOfSymbols(random, Constants.LIST_OF_CYRILLIC_SYMBOLS);
        String randomWholeDigit = String.valueOf(random.nextInt(Constants.MAX_WHOLE_DIGIT - Constants.MIN_DIGIT + 1)
                + Constants.MIN_DIGIT);
        DecimalFormat decimalFormat = new DecimalFormat("#.########");
        String randomFractionalDigit = decimalFormat.format((random.nextDouble()
                * (Constants.MAX_FRACTIONAL_DIGIT - Constants.MIN_DIGIT) + Constants.MIN_DIGIT));

        return randomDate + Constants.SEPARATOR +
                randomLatinChars + Constants.SEPARATOR +
                randomCyrillicChars + Constants.SEPARATOR +
                randomWholeDigit + Constants.SEPARATOR +
                randomFractionalDigit;
    }

    private String generateRandomDateAsString(Random random) {
        LocalDate currentDate = LocalDate.now();
        long maxQuantityOfDays = currentDate.toEpochDay();
        long minQuantityOfDays = currentDate.minusYears(Constants.YEARS_TO_SUBTRACT).toEpochDay();
        long randomQuantityOfDays = minQuantityOfDays +
                (long) (Math.random() * (maxQuantityOfDays - minQuantityOfDays));
        return LocalDate.ofEpochDay(randomQuantityOfDays).toString().replace("-",".");
    }

    private String generateRandomSourceOfSymbols(Random random, String listOfSymbols) {
        StringBuilder buffer = new StringBuilder(Constants.QUANTITY_OF_SYMBOLS);
        for (int i = 0; i < Constants.QUANTITY_OF_SYMBOLS; i++) {
            int randomLimitedInt = random.nextInt(listOfSymbols.length());
            buffer.append(listOfSymbols.charAt(randomLimitedInt));
        }
        return buffer.toString();
    }
}
