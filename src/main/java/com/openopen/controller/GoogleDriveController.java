package com.openopen.controller;


import com.google.api.services.drive.Drive;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.openopen.googledrive.GoogleDriveAction;
import com.openopen.googledrive.GoogleDriveAuth;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@RestController
@RequestMapping("/api/google/drive")
public class GoogleDriveController {

    // Logger
    private Logger logger = LoggerFactory.getLogger(GoogleDriveController.class);


    @RequestMapping(
            value = "/test",
            method = RequestMethod.GET)
    public String test() throws IOException, InterruptedException {

        GoogleDriveAction googleDriveAction = new GoogleDriveAction();
        googleDriveAction.uploadImage();

        return "OK 123";
    }


    /*
     *
     * 上傳圖片到 Google Drive
     *
     * */
    //http://localhost:8080/api/google/drive/uploadImage
    /*
    @PostMapping(
            value = "/uploadImage",
            produces = {"application/json"})
    @Transactional
    public String uploadImage(@RequestParam("file") MultipartFile fileInput,
                              @RequestParam("file") MultipartFile fileInput2,
                              @RequestParam("file") MultipartFile fileInput3,
                              @RequestParam("file") MultipartFile fileMetaData,
                              @RequestParam("json") String _json) throws IOException, InterruptedException, MagicMatchNotFoundException, MagicException, MagicParseException {
        logger.info("===> uploadImage");
        logger.info(_json);

        InputStream fileInputStream = fileInput.getInputStream();
        InputStream fileInputStream2 = fileInput2.getInputStream();
        InputStream fileInputStream3 = fileInput3.getInputStream();


//        Gson gson = new Gson();
//        List<Person> list = gson.fromJson(_json, new TypeToken<List<Person>>() {
//        }.getType());

        GoogleDriveManager googleDriveManager = new GoogleDriveManager();


        googleDriveManager.uploadImage(fileInputStream, fileInputStream2, fileInputStream3, fileMetaData);

        JsonObject obj = new JsonObject();
        obj.addProperty("ACTION", "uploadImage");
        obj.addProperty("RESULT", "OK");

        return new Gson().toJson(obj);
    }*/


    @RequestMapping(
            value = "/new/folder",
            method = RequestMethod.GET)
    public String newFolder() throws IOException, InterruptedException {

        GoogleDriveAction googleDriveAction = new GoogleDriveAction();
        Drive googleDriveService = GoogleDriveAuth.getGoogleDriveService();
        googleDriveAction.createFolder("1nJG4JSalQFT4qnakIOTnEEgyM7lsczyw", "新資料夾 new 123", googleDriveService);

        return "OK 123";
    }


    /*
     *
     * 上傳圖片到 Google Drive
     *
     * */
    //http://localhost:8080/api/google/drive/uploadImage
    @PostMapping(
            value = "/uploadImage",
            produces = {"application/json"})
    @Transactional
    public String uploadImage(@RequestParam("file") MultipartFile fileInput,
                              @RequestParam("file") MultipartFile fileInput2,
                              @RequestParam("file") MultipartFile fileInput3,
                              @RequestParam("file") MultipartFile fileMetaData,
                              @RequestParam("json") String _json) throws IOException, InterruptedException, MagicMatchNotFoundException, MagicException, MagicParseException {
        logger.info("===> uploadImage");
        logger.info(_json);

        InputStream fileInputStream = fileInput.getInputStream();
        InputStream fileInputStream2 = fileInput2.getInputStream();
        InputStream fileInputStream3 = fileInput3.getInputStream();


//        Gson gson = new Gson();
//        List<Person> list = gson.fromJson(_json, new TypeToken<List<Person>>() {
//        }.getType());

        GoogleDriveAction googleDriveAction = new GoogleDriveAction();


//        googleDriveAction.uploadImage(fileInputStream, fileInputStream2, fileInputStream3, fileMetaData);

        Drive googleDriveService = GoogleDriveAuth.getGoogleDriveService();
        logger.info("=====> googleDriveAction createFile 0");
        googleDriveAction.createFile("18OfsWiCtFpCpjrIiAHo8MMpvJvGYMh7S", fileInputStream, "p1.jpeg", "image/jpeg", googleDriveService);
        logger.info("=====> googleDriveAction createFile 1");
        JsonObject obj = new JsonObject();
        obj.addProperty("ACTION", "uploadImage");
        obj.addProperty("RESULT", "OK");

        return new Gson().toJson(obj);
    }
}
