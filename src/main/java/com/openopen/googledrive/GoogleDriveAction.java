package com.openopen.googledrive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleDriveAction {


    // Logger
    private static Logger logger = LoggerFactory.getLogger(GoogleDriveAction.class);


    //要上傳的 folder id (要先共用給服務帳戶)
    @Getter
    private static String GOOGLE_DRIVE_FOLDER_ID;

    @Value("${google.drive.folder.id}")
    private void setGoogleDriveFolderId(String _googleDriveFolderId) {
        GOOGLE_DRIVE_FOLDER_ID = _googleDriveFolderId;
    }


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

        logger.info("===> getFileList 0");
        FileList filelist = getFileList(GOOGLE_DRIVE_FOLDER_ID, false, googleDriveService);
        for (File file : filelist.getFiles()) {
            logger.info("==> file.getName" + file.getName());
            logger.info("==> file.getId" + file.getId());
        }
        logger.info("===> getFileList 1");

    }


    /*
     *
     *
     * 檔案清單
     *
     * */
    public FileList getFileList(String _parentFolderId, Boolean _isOnlyFolder, Drive _googleDriveService) throws IOException {


        // 找特定資料夾
        String queryString = "'" + _parentFolderId + "'" + " in parents and trashed = false ";


        // 只要資料夾清單
        if (_isOnlyFolder) {
            queryString = queryString + " and mimeType = 'application/vnd.google-apps.folder' ";
        }

//        FileList result = _googleDriveService.files().list()
//                .setQ(queryString)
//                .setSpaces("drive")
//                .execute();


        FileList result = _googleDriveService.files().list()
                .setCorpora("teamDrive")
                .setDriveId("0AEnjMW6oO0e0Uk9PVA")
                .setPageSize(250)
                .setIncludeItemsFromAllDrives(true)
                .setSupportsAllDrives(true)
                .setQ("'1nJG4JSalQFT4qnakIOTnEEgyM7lsczyw' in parents")
                .execute();


        return result;

    }

}
