package com.example.test_project.service;

import com.example.test_project.dto.ResultDto;
import com.example.test_project.entity.FileModel;
import com.example.test_project.entity.Source;
import com.example.test_project.repository.FileRepository;
import com.example.test_project.repository.SourceRepository;
import com.example.test_project.utils.Constants;
import com.example.test_project.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WriteFileServiceImpl implements WriteFileService {

    private final SourceRepository sourceRepository;
    private final FileRepository fileRepository;

    @Override
    public void generateFiles() {
        FileUtils.createDataDirectory();
        final Random random = new Random();
        for (int i = 1; i <= Constants.QUANTITY_OF_FILES; i++) {
            generateOneFile(random, Constants.PATTERN_OF_FILENAME + i + ".txt");
        }
    }

    @Override
    public void joinFilesToOneFile(String invalidSource) {
        FileUtils.deleteFileIfExist(Path.of(Constants.PATH_TO_COMMON_FILE));
        List<File> files = FileUtils.readFilesFromDataDirectory();

        for (File file : files) {
            try {
                processFileForJoin(file, invalidSource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void importToDatabase() {
        List<File> files = FileUtils.readFilesFromDataDirectory();

        for (File file : files) {
            try {
                if (!file.getName().endsWith(Constants.NAME_OF_COMMON_FILE)){
                    processFileForDataBase(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResultDto getResultOfStatistic() {
        final Long sumOfWholeDigits = sourceRepository.getSumOfWholeDigits();
        final Double medianOfFractionalDigits = sourceRepository.getMedianOfFractionalDigits();

        return ResultDto.builder()
                .sumOfWholeDigits(sumOfWholeDigits)
                .medianOfFractionalDigits(medianOfFractionalDigits)
                .build();
    }

    private void generateOneFile(Random random, String fileName) {
        for (int i = 1; i <= Constants.QUANTITY_OF_SOURCES_IN_EACH_FILE; i++) {
            String source = generateOneSource(random);
            Path path = Paths.get(Constants.PATH_TO_FILES + fileName);
            try {
                FileUtils.openFileIfExist(path);
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
        return LocalDate.ofEpochDay(randomQuantityOfDays).toString().replace("-", ".");
    }

    private String generateRandomSourceOfSymbols(Random random, String listOfSymbols) {
        StringBuilder buffer = new StringBuilder(Constants.QUANTITY_OF_SYMBOLS);
        for (int i = 0; i < Constants.QUANTITY_OF_SYMBOLS; i++) {
            int randomLimitedInt = random.nextInt(listOfSymbols.length());
            buffer.append(listOfSymbols.charAt(randomLimitedInt));
        }
        return buffer.toString();
    }

    private void processFileForJoin(File file, String invalidSource) throws IOException {
        Path pathToCommonFile = Path.of(Constants.PATH_TO_COMMON_FILE);
        FileUtils.openFileIfExist(pathToCommonFile);
        List<String> sources = Files.readAllLines(file.toPath());
        if (!ObjectUtils.isEmpty(invalidSource)) {
            sources = sources.stream()
                    .filter(source -> !source.contains(invalidSource))
                    .collect(Collectors.toList());
        }

        for (String source : sources) {
            Files.writeString(pathToCommonFile, source + "\n", StandardOpenOption.APPEND);
            log.info("wrote source {} in file common file", source);
        }
    }

    private void processFileForDataBase(File file) throws IOException {
        final List<String> rows = Files.readAllLines(file.toPath());
        log.info("processing file {} with {} rows", file.getName(), rows.size());
        FileModel fileModel = FileModel.builder()
                .fileName(file.getName())
                .build();
        fileModel = fileRepository.save(fileModel);

        int countProcessedRows = 0;

        for (String row : rows) {
            String[] splitSource = row.split("\\|\\|");
            Source source = Source.builder()
                    .fileModel(fileModel)
                    .randomDate(splitSource[0])
                    .latinSymbols(splitSource[1])
                    .cyrillicSymbols(splitSource[2])
                    .wholeDigit(Integer.parseInt(splitSource[3]))
                    .fractionalDigit(Double.parseDouble(splitSource[4].replaceAll(",",".")))
                    .build();
            sourceRepository.save(source);
            countProcessedRows ++;
            log.info("have written {} rows in database, {} rows left", countProcessedRows, rows.size() - countProcessedRows);
        }
    }

}
