package com.openopen.googlecloudstorage;

import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.*;
import org.apache.commons.io.IOUtils;
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

        // 取得 google Cloud Storage Service
        Storage googleCloudStorageService = GoogleCloudStorageAuth.getGoogleCloudStorageService();


        // The ID of your GCP project
        // String projectId = "your-project-id";

        // The ID of your GCS bucket
        // String bucketName = "your-unique-bucket-name";

        // The ID of your GCS object
        // String objectName = "your-object-name";

        // The path to your file to upload
        // String filePath = "path/to/your/file"


        /* ◢◤◢◤◢◤◢◤◢◤ 2. 取得檔案名稱, mime type ◢◤◢◤◢◤◢◤◢◤ */
        logger.info("2. 取得檔案名稱, mime type 0");

        // 完整檔案名稱 xxx.yyy
        String fullFileName = _fileMetaData.getOriginalFilename();

        String fileType = fullFileName.substring(fullFileName.lastIndexOf(".") + 1, fullFileName.length());


        BlobId blobId = BlobId.of(bucketName, fullFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        googleCloudStorageService.create(blobInfo, IOUtils.toByteArray(_fileInputStream));


        logger.info("blobId getMediaLink :" + blobInfo.getMediaLink());
        logger.info("blobId getName :" + blobId.getName());


    }

}
