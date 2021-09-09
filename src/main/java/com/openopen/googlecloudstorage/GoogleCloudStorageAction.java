package com.openopen.googlecloudstorage;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GoogleCloudStorageAction {


    // Logger
    private static Logger logger = LoggerFactory.getLogger(GoogleCloudStorageAction.class);


    public void createBucket(String _bucketName) throws IOException {
        logger.info("===> createBucket");
        Storage googleCloudStorageService = GoogleCloudStorageAuth.getGoogleCloudStorageService();
        // Creates the new bucket
        Bucket bucket = googleCloudStorageService.create(BucketInfo.of(_bucketName));

        logger.info("Bucket %s created.%n", bucket.getName());
         
    }

}
