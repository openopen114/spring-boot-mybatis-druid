package com.openopen.googlecloudstorage;

import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.*;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class GoogleCloudStorageAction {


    // Logger
    private static Logger logger = LoggerFactory.getLogger(GoogleCloudStorageAction.class);


    /*
     *
     * 建立新 Bucket
     * https://cloud.google.com/storage/docs/creating-buckets#storage-create-bucket-code_samples
     * */
    public String createBucket(String _bucketName) throws IOException {
        logger.info("===> createBucket");

        // 取得 google Cloud Storage Service
        Storage googleCloudStorageService = GoogleCloudStorageAuth.getGoogleCloudStorageService();


        // 儲存類型
        StorageClass storageType = StorageClass.REGIONAL;
        // 位置
        String location = "ASIA-EAST1";


        Bucket bucket =
                googleCloudStorageService.create(
                        BucketInfo.newBuilder(_bucketName)
                                .setStorageClass(storageType)
                                .setLocation(location)
                                .build());


        Policy originalPolicy = googleCloudStorageService.getIamPolicy(_bucketName);
        googleCloudStorageService.setIamPolicy(
                _bucketName,
                originalPolicy
                        .toBuilder()
                        .addIdentity(StorageRoles.objectViewer(), Identity.allUsers()) // All users can view
                        .build());

        logger.info(
                "Created bucket "
                        + bucket.getName()
                        + " in "
                        + bucket.getLocation()
                        + " with storage class "
                        + bucket.getStorageClass());


        return bucket.getName();
    }


    public static void uploadObject(
            String bucketName, InputStream _fileInputStream,
            MultipartFile _fileMetaData) throws IOException {

        /* ◢◤◢◤◢◤◢◤◢◤ 1. 取得 google Cloud Storage Service ◢◤◢◤◢◤◢◤◢◤ */
        Storage googleCloudStorageService = GoogleCloudStorageAuth.getGoogleCloudStorageService();


        /* ◢◤◢◤◢◤◢◤◢◤ 2. 取得檔案名稱  ◢◤◢◤◢◤◢◤◢◤ */
        logger.info("2. 取得檔案名稱");

        // 完整檔案名稱 xxx.yyy


        DateTimeZone timeZone = DateTimeZone.forID("Asia/Taipei");
        DateTime dateTime = new DateTime(timeZone);
        String timestamp = dateTime.toString("yyyy-MM-dd_HH:mm:ss");


        String fullFileName = _fileMetaData.getOriginalFilename();
        assert fullFileName != null;
        String fileName = fullFileName.substring(0, fullFileName.lastIndexOf("."));
        String fileType = fullFileName.substring(fullFileName.lastIndexOf(".") + 1, fullFileName.length());


        String fullFileNameWithTimestamp = fileName + "_" + timestamp + "." + fileType;

        logger.info("==> fullFileNameWithTimestamp:" + fullFileNameWithTimestamp);



        /* ◢◤◢◤◢◤◢◤◢◤ 3. 上傳 Blob  ◢◤◢◤◢◤◢◤◢◤ */

        BlobId blobId = BlobId.of(bucketName, fullFileNameWithTimestamp);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        googleCloudStorageService.create(blobInfo, IOUtils.toByteArray(_fileInputStream));


        logger.info("file name " + fullFileNameWithTimestamp);
        logger.info("URLEncoder file name " + java.net.URLEncoder.encode(fullFileNameWithTimestamp, "UTF-8"));
        // URL: https://storage.googleapis.com/[BUCKET NAME]/[URL ENCODE FILE NAME]
        //java.net.URLEncoder.encode

        logger.info("upload done");

    }

}
