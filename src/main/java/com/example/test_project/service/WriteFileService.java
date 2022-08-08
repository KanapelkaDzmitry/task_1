package com.example.test_project.service;

import com.example.test_project.dto.ResultDto;

public interface WriteFileService {
    void generateFiles();

    void joinFilesToOneFile(String invalidSource);

    void importToDatabase();

    ResultDto getResultOfStatistic();
}
