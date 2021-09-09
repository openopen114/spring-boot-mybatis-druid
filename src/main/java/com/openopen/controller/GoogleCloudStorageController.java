package com.openopen.controller;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.openopen.googlecloudstorage.GoogleCloudStorageAction;
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
@RequestMapping("/api/google/cloud/storage")
public class GoogleCloudStorageController {

    // Logger
    private Logger logger = LoggerFactory.getLogger(GoogleCloudStorageController.class);


    @RequestMapping(
            value = "/create/bucket/{_id}",
            method = RequestMethod.GET)
    public String createBucket(@PathVariable("_id") String _id) throws IOException, InterruptedException {
        logger.info("====> createBucket:" + _id);
        GoogleCloudStorageAction googleCloudStorageAction = new GoogleCloudStorageAction();
        logger.info("====> createBucket 0 ");
        googleCloudStorageAction.createBucket(_id);
        logger.info("====> createBucket 1 ");
        return "OK 123";
    }


    /*
     *
     * 上傳圖片到 Google Cloud Storage
     *
     * */
    //http://localhost:8080/api/google/cloud/storage/uploadObject
    @PostMapping(
            value = "/uploadObject",
            produces = {"application/json"})
    @Transactional
    public String uploadObject(@RequestParam("file") MultipartFile fileInput,
                               @RequestParam("file") MultipartFile fileMetaData,
                               @RequestParam("json") String _json) throws IOException, InterruptedException, MagicMatchNotFoundException, MagicException, MagicParseException {
        logger.info("===> uploadObject 0");
        logger.info(_json);

        InputStream fileInputStream = fileInput.getInputStream();


//        Gson gson = new Gson();
//        List<Person> list = gson.fromJson(_json, new TypeToken<List<Person>>() {
//        }.getType());

        GoogleCloudStorageAction googleCloudStorageAction = new GoogleCloudStorageAction();

        googleCloudStorageAction.uploadObject("pub-001", fileInputStream, fileMetaData);

        logger.info("===> uploadImage 1");
        // String fileId = googleCloudStorageAction.uploadImage(fileInputStream, fileInputStream2, fileMetaData);


        JsonObject obj = new JsonObject();
        obj.addProperty("ACTION", "uploadImage");
        obj.addProperty("RESULT", "OK");
        //obj.addProperty("FILE_ID", fileId);

        return new Gson().toJson(obj);
    }


}
