package com.openopen.googledrive;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.Getter;
import net.sf.jmimemagic.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

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


    //共用雲端硬碟 id
    @Getter
    private static String GOOGLE_DRIVE_TEAMS_ID;

    @Value("${google.drive.teams.drive.id}")
    private void setGoogleDriveTeamsIdId(String _googleDriveteamsId) {
        GOOGLE_DRIVE_TEAMS_ID = _googleDriveteamsId;
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
        FileList filelist = getFileList(GOOGLE_DRIVE_FOLDER_ID, true, googleDriveService);
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
                .setDriveId(GOOGLE_DRIVE_TEAMS_ID)
                .setPageSize(250)
                .setIncludeItemsFromAllDrives(true)
                .setSupportsAllDrives(true)
                .setQ(queryString)
                .execute();


        return result;

    }


    /*
     *
     * 新增資料夾
     *
     *
     * */
    public File createFolder(String _parentId, String _folderName, Drive _googleDriveService) throws IOException {

        logger.info("==> 新增資料夾:" + _folderName);
        //File MetaData
        File folderMetadata = new File();

        folderMetadata.setName(_folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        if (_parentId != null && _parentId.length() > 0) {
            folderMetadata.setParents(Arrays.asList(_parentId));
        }

        // 設定 teams drive id
        folderMetadata.setTeamDriveId(GOOGLE_DRIVE_TEAMS_ID);


        //新增資料夾並回傳 id
        return _googleDriveService.files().create(folderMetadata).setSupportsAllDrives(true).setFields("id").execute();
    }


    /*
     *
     * 新增檔案
     *
     *
     * */
    public File createFile(String _parentId, InputStream _fileInputStream, String _fileName, String _mimeType, Drive _googleDriveService)
            throws IOException {


        logger.info("===>    createFile 1 ");
        //File Metadata
        File fileMetadata = new File();

        fileMetadata.setName(_fileName);
        fileMetadata.setMimeType(_mimeType);
        logger.info("===>    createFile 2 ");

        if (_parentId != null && _parentId.length() > 0) {
            fileMetadata.setParents(Arrays.asList(_parentId));
        }

        // 設定 teams drive id
        fileMetadata.setTeamDriveId(GOOGLE_DRIVE_TEAMS_ID);

        logger.info("===>    createFile 3 ");

        // File InputStream to  tempFile
        java.io.File tempFile = java.io.File.createTempFile("bbbb", ".jpeg");
        FileUtils.copyToFile(_fileInputStream, tempFile);
        FileContent mediaContent = new FileContent(_mimeType, tempFile);

        logger.info("===>    createFile 4 ");
        // 新增檔案 回傳 id
        return _googleDriveService.files().create(fileMetadata, mediaContent).setSupportsAllDrives(true).setFields("id").execute();
    }


    /*
     *
     * 上傳圖檔
     *
     * */
    public String uploadImage(InputStream _fileInputStream,
                              InputStream _fileInputStream2,
                              MultipartFile _fileMetaData) throws IOException, InterruptedException, MagicMatchNotFoundException, MagicException, MagicParseException {



        /* ◢◤◢◤◢◤◢◤◢◤ 1. 取得 google Drive Service ◢◤◢◤◢◤◢◤◢◤ */
        logger.info(" 1. 取得 google Drive Service 0");
        Drive googleDriveService = GoogleDriveAuth.getGoogleDriveService();
        logger.info(" 1. 取得 google Drive Service 1");


        /* ◢◤◢◤◢◤◢◤◢◤ 2. 取得檔案名稱, mime type ◢◤◢◤◢◤◢◤◢◤ */
        logger.info("2. 取得檔案名稱, mime type 0");

        // 完整檔案名稱 xxx.yyy
        String fullFileName = _fileMetaData.getOriginalFilename();

        MagicMatch fileMatchResult = Magic.getMagicMatch(IOUtils.toByteArray(_fileInputStream2));
        String mimeType = fileMatchResult.getMimeType();
        logger.info("===> mimeType:" + mimeType);
        logger.info("2. 取得檔案名稱, mime type 1");


        /* ◢◤◢◤◢◤◢◤◢◤ 3. 上傳 ◢◤◢◤◢◤◢◤◢◤ */
        // 原圖
        logger.info("===> 原圖  createFile 0 ");
        File originalImageRes = this.createFile(getGOOGLE_DRIVE_FOLDER_ID(), _fileInputStream, fullFileName, mimeType, googleDriveService);
        logger.info("===> 原圖  createFile 1 ");


        logger.info("===> originalImageRes Id on Google Drive: " + originalImageRes.getId());

        return originalImageRes.getId();

    }


    /*
     *
     *
     * 取得上傳圖檔資料夾 id map
     *
     * */
    public HashMap<String, String> getImageFolderIdMap(Drive _googleDriveService) throws IOException {
        logger.info("checkFolder 0");
        DateTimeZone timeZone = DateTimeZone.forID("Asia/Taipei");
        DateTime dateTime = new DateTime(timeZone);
        String todayFolderName = dateTime.toString("yyyy-MM-dd");

        // 資料夾 id (含縮圖)
        HashMap<String, String> folderIdMap = new HashMap<String, String>();

        // 是否有今天日期 Today 目錄 
        Boolean isTodayFolderExist = false;



        /* ◢◤◢◤◢◤◢◤◢◤ 1. 檢查是否有今天日期 Today 目錄 ◢◤◢◤◢◤◢◤◢◤ */

        Boolean isOnlyFolder = true;

        FileList resultForToday = getFileList(GOOGLE_DRIVE_FOLDER_ID, isOnlyFolder, _googleDriveService);

        logger.info("=====>  getFileList 1");

        for (File file : resultForToday.getFiles()) {
            // 今天日期 Today 目錄已存在, 返回 id
            if (Objects.equals(file.getName(), todayFolderName)) {
                // 返回 id
                String todayFolderId = file.getId();
                folderIdMap.put("todayFolderId", todayFolderId);
                isTodayFolderExist = true;
            }
        }

        // 今天日期 Today 目錄不存在則產生今天日期 Today 目錄
        if (isTodayFolderExist == false) {
            String todayFolderId = createFolder(GOOGLE_DRIVE_FOLDER_ID, todayFolderName, _googleDriveService).getId();
            folderIdMap.put("todayFolderId", todayFolderId);
        }


        /* ◢◤◢◤◢◤◢◤◢◤ 2. 檢查 今天日期 Today 目錄 下 是否有縮圖目錄 ◢◤◢◤◢◤◢◤◢◤ */
        String thumbnailFolderName = "thumbnail";
        Boolean isThumbnailFolderExist = false;


        FileList resultForThumbnail = getFileList(folderIdMap.get("todayFolderId"), isOnlyFolder, _googleDriveService);
        for (File file : resultForThumbnail.getFiles()) {
            // 縮圖目錄存在
            if (Objects.equals(file.getName(), thumbnailFolderName)) {
                //   返回 id
                String thumbnailFolderId = file.getId();
                folderIdMap.put("thumbnailFolderId", thumbnailFolderId);
                isThumbnailFolderExist = true;
            }
        }

        // 縮圖目錄不存在則產生縮圖目錄
        if (isThumbnailFolderExist == false) {
            String thumbnailFolderId = createFolder(folderIdMap.get("todayFolderId"), thumbnailFolderName, _googleDriveService).getId();
            folderIdMap.put("thumbnailFolderId", thumbnailFolderId);
        }

        logger.info("===> Today目錄 id :" + folderIdMap.get("todayFolderId"));
        logger.info("===> 縮圖目錄 id :" + folderIdMap.get("thumbnailFolderId"));


        logger.info("checkFolder 1");


        return folderIdMap;

    }


}
