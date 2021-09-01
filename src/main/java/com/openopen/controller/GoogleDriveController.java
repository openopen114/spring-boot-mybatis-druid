package com.openopen.controller;


import com.openopen.googledrive.FileManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/api/google/drive")
public class GoogleDriveController {


    @RequestMapping(
            value = "/test",
            method = RequestMethod.GET)
    public String aaaa() throws IOException, InterruptedException {
        FileManager fileManager = new FileManager();
        fileManager.insertFile();

        return "OK";
    }
}
