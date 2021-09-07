package com.openopen.googledrive;

import com.google.api.services.drive.Drive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleDriveAction {


    // Logger
    private static Logger logger = LoggerFactory.getLogger(GoogleDriveAction.class);


    /*
     *
     * 上傳圖檔
     *
     * */
    public void uploadImage() throws IOException {
        logger.info("===> upload image");

        logger.info("===> getGoogleDriveService 0");
        Drive googleDriveService = GoogleDriveAuth.getGoogleDriveService();
        logger.info("===> getGoogleDriveService 1");

    }

}
