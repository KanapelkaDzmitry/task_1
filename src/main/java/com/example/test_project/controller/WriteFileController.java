package com.example.test_project.controller;

import com.example.test_project.service.WriteFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.test_project.controller.WriteFileController.ROOT_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(ROOT_URL)
public class WriteFileController {

    public static final String ROOT_URL = "/file";

    private final WriteFileService writeFileService;

    @GetMapping("/generate")
    public void generateFiles() {
        writeFileService.generateFiles();
    }

    @GetMapping("/join")
    public void joinFiles(@RequestParam String invalidSource) {
        writeFileService.joinFilesToOneFile(invalidSource);
    }

    @GetMapping("/import")
    public void importToDataBase(){
        writeFileService.importToDatabase();
    }
}

