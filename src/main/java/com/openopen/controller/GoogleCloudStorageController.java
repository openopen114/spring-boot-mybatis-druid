package com.openopen.controller;


import com.openopen.googlecloudstorage.GoogleCloudStorageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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


}
