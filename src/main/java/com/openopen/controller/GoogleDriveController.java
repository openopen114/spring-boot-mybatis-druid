package com.openopen.controller;


import com.openopen.googledrive.FileManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/api/google/drive")
public class GoogleDriveController {


    @Value("${google.drive.folder.id}")
    private String GOOGLE_DRIVE_FOLDER_ID;


    @RequestMapping(
            value = "/test",
            method = RequestMethod.GET)
    public String aaaa() throws IOException, InterruptedException {
        System.out.println("==>GOOGLE_DRIVE_FOLDER_ID:" + GOOGLE_DRIVE_FOLDER_ID);
        FileManager fileManager = new FileManager(GOOGLE_DRIVE_FOLDER_ID);
        fileManager.uploadFile();

        return "OK";
    }
}
