package com.example.test_project.service;

import com.example.test_project.dto.ResultDto;
import com.example.test_project.entity.FileModel;
import com.example.test_project.entity.Line;
import com.example.test_project.repository.FileRepository;
import com.example.test_project.repository.LineRepository;
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

    private final LineRepository lineRepository;
    private final FileRepository fileRepository;

    /*
     Метод генерирующий файлы
     */
    @Override
    public void generateFiles() {
        FileUtils.createDataDirectory();
        int numberOfFile = FileUtils.getLastFileNumber();
        final Random random = new Random();
        for (int i = 1; i <= Constants.QUANTITY_OF_FILES; i++) {
            generateOneFile(random,
                    Constants.PREFIX_OF_FILENAME + numberOfFile + Constants.POSTFIX_OF_FILENAME);
            numberOfFile++;
        }
    }

    /*
    Метод для объединения всех файлов в один
     */
    @Override
    public void joinFilesToOneFile(String invalidLine) {
        FileUtils.deleteFileIfExist(Path.of(Constants.PATH_TO_COMMON_FILE));
        List<File> files = FileUtils.readFilesFromDataDirectory();

        for (File file : files) {
            try {
                processFileForJoin(file, invalidLine);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Метод для импорта файлов в базу данных
     */
    @Override
    public void importToDatabase() {
        List<File> files = FileUtils.readFilesFromDataDirectory();

        for (File file : files) {
            try {
                if (!file.getName().endsWith(Constants.NAME_OF_COMMON_FILE)) {
                    processFileForDataBase(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Метод для получения результата суммы всех целых чисел и медианы всех дробных чисел
     */
    @Override
    public ResultDto getResultOfStatistic() {
        final Long sumOfWholeDigits = lineRepository.getSumOfWholeDigits();
        final Double medianOfFractionalDigits = lineRepository.getMedianOfFractionalDigits();

        return ResultDto.builder()
                .sumOfWholeDigits(sumOfWholeDigits)
                .medianOfFractionalDigits(medianOfFractionalDigits)
                .build();
    }

    /*
    Метод генерирующий один файл
     */
    private void generateOneFile(Random random, String fileName) {
        for (int i = 1; i <= Constants.QUANTITY_OF_LINES_IN_EACH_FILE; i++) {
            String line = generateOneLine(random);
            Path path = Paths.get(Constants.PATH_TO_FILES + fileName);
            try {
                FileUtils.openFileIfExist(path);
                Files.writeString(path,
                        line + "\n", StandardOpenOption.APPEND);
                log.info("wrote line {} in file {}", line, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Метод собирающий и генерирующий одну строку
     */
    private String generateOneLine(Random random) {
        String randomDate = generateRandomDateAsString(random);
        String randomLatinChars = generateRandomLineOfSymbols(random, Constants.LIST_OF_LATIN_SYMBOLS);
        String randomCyrillicChars = generateRandomLineOfSymbols(random, Constants.LIST_OF_CYRILLIC_SYMBOLS);
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

    /*
    Метод генерирующий дату в строке
     */
    private String generateRandomDateAsString(Random random) {
        LocalDate currentDate = LocalDate.now();
        long maxQuantityOfDays = currentDate.toEpochDay();
        long minQuantityOfDays = currentDate.minusYears(Constants.YEARS_TO_SUBTRACT).toEpochDay();
        long randomQuantityOfDays = minQuantityOfDays +
                (long) (Math.random() * (maxQuantityOfDays - minQuantityOfDays));
        return LocalDate.ofEpochDay(randomQuantityOfDays).toString().replace("-", ".");
    }

    /*
    Метод генерирующий случайные символы
     */
    private String generateRandomLineOfSymbols(Random random, String listOfSymbols) {
        StringBuilder buffer = new StringBuilder(Constants.QUANTITY_OF_SYMBOLS);
        for (int i = 0; i < Constants.QUANTITY_OF_SYMBOLS; i++) {
            int randomLimitedInt = random.nextInt(listOfSymbols.length());
            buffer.append(listOfSymbols.charAt(randomLimitedInt));
        }
        return buffer.toString();
    }

    /*
    Метод для присоединения файла
     */
    private void processFileForJoin(File file, String invalidLine) throws IOException {
        Path pathToCommonFile = Path.of(Constants.PATH_TO_COMMON_FILE);
        FileUtils.openFileIfExist(pathToCommonFile);
        List<String> lines = Files.readAllLines(file.toPath());
        if (!ObjectUtils.isEmpty(invalidLine)) {
            lines = lines.stream()
                    .filter(line -> !line.contains(invalidLine))
                    .collect(Collectors.toList());
        }

        for (String line : lines) {
            Files.writeString(pathToCommonFile, line + "\n", StandardOpenOption.APPEND);
            log.info("wrote line {} in file common file", line);
        }
    }

    /*
    Метод для чтения файлов и заполнения базы данных
     */
    private void processFileForDataBase(File file) throws IOException {
        final List<String> lines = Files.readAllLines(file.toPath());
        log.info("processing file {} with {} lines", file.getName(), lines.size());
        FileModel fileModel = FileModel.builder()
                .fileName(file.getName())
                .build();
        fileModel = fileRepository.save(fileModel);

        int countProcessedRows = 0;

        for (String row : lines) {
            String[] splitLine = row.split("\\|\\|");
            Line line = Line.builder()
                    .fileModel(fileModel)
                    .randomDate(splitLine[0])
                    .latinSymbols(splitLine[1])
                    .cyrillicSymbols(splitLine[2])
                    .wholeDigit(Integer.parseInt(splitLine[3]))
                    .fractionalDigit(Double.parseDouble(splitLine[4].replaceAll(",", ".")))
                    .build();
            lineRepository.save(line);
            countProcessedRows++;
            log.info("have written {} lines in database, {} lines left", countProcessedRows, lines.size() - countProcessedRows);
        }
    }

}
