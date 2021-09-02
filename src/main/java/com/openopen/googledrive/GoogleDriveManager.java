package com.openopen.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;
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

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class GoogleDriveManager {

    // Logger
    private static Logger logger = LoggerFactory.getLogger(GoogleDriveManager.class);


    // Google Drive Service
    public static Drive googleDriveService;

    //用於在依賴關係注入完成之後需要執行的方法上，以執行任何初始化
    @PostConstruct
    public void init() throws IOException {
        //授權驗證取得 google drive service
        this.googleDriveService = this.getGoogleDriveService();

        //檢查上傳資料夾/縮圖資料夾是否存在
        this.checkFolderExist();
    }


    //要上傳的 folder id (要先共用給服務帳戶)
    @Getter
    private static String GOOGLE_DRIVE_FOLDER_ID;

    @Value("${google.drive.folder.id}")
    private void setGoogleDriveFolderId(String _googleDriveFolderId) {
        GOOGLE_DRIVE_FOLDER_ID = _googleDriveFolderId;
    }


    // 服務帳戶 email
    @Getter
    private static String SERVICE_ACCOUNT_EMANIL;

    @Value("${google.service.account.email}")
    private void setServiceAccountEmanil(String _serviceAccountEmail) {
        SERVICE_ACCOUNT_EMANIL = _serviceAccountEmail;
    }


    // 服務帳戶 json key path
    @Getter
    private static String SERVICE_ACCOUNT_JSON_PATH;

    @Value("${google.service.account.json.path}")
    private void setServiceAccountJsonPath(String _serviceAccountJsonPath) {
        SERVICE_ACCOUNT_JSON_PATH = _serviceAccountJsonPath;
    }


    // 檢查上傳資料夾/縮圖資料夾是否存在
    private Boolean isFolderChekced = false;

    // 檢查上傳資料夾 日期
    private String folderCheckedDate = null;

    // 今天日期 Today id
    private String todayFolderId = null;

    // 縮圖目錄 id
    private String thumbnailFolderId = null;


    public static final String APPLICATION_NAME = "Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static HttpTransport HTTP_TRANSPORT;
    public static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *
     *  授權驗證
     *
     * */
    public static Credential authorize() throws IOException {

        logger.info("===> authorize");
        logger.info("===> SERVICE_ACCOUNT_EMANIL:" + SERVICE_ACCOUNT_EMANIL);
        logger.info("===> SERVICE_ACCOUNT_JSON_PATH:" + SERVICE_ACCOUNT_JSON_PATH);
        GoogleCredential clientSecrets =
                GoogleCredential.fromStream(GoogleDriveManager.class.getResourceAsStream(SERVICE_ACCOUNT_JSON_PATH));
        PrivateKey privateKey = clientSecrets.getServiceAccountPrivateKey();
        String privateKeyId = clientSecrets.getServiceAccountPrivateKeyId();


        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(SERVICE_ACCOUNT_EMANIL)
                .setServiceAccountScopes(SCOPES)
                .setServiceAccountPrivateKey(privateKey)
                .setServiceAccountPrivateKeyId(privateKeyId)
                .build();

        return credential;
    }


    /*
     *
     *  取得google Drive Service
     *
     * */
    public static Drive getGoogleDriveService() throws IOException {
        Credential credential = authorize();
        logger.info("===> authorize ok");
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }


    /*
     *
     * 新增資料夾
     *
     *
     * */
    public File createFolder(String _parentId, String _folderName) throws IOException {

        // 取得 Google Drive Service if null
        if (this.googleDriveService == null) {
            this.googleDriveService = GoogleDriveManager.getGoogleDriveService();
        }


        //File MetaData
        File folderMetadata = new File();


        logger.info("===> Folder Name: " + _folderName);
        folderMetadata.setName(_folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        if (_parentId != null && _parentId.length() > 0) {
            folderMetadata.setParents(Arrays.asList(_parentId));
        }

        //新增資料夾並回傳 id
        return this.googleDriveService.files().create(folderMetadata).setFields("id").execute();
    }


    /*
     *
     * 新增檔案
     *
     *
     * */
    public File createFile(String _parentId, InputStream _fileInputStream, String _fileName, String _mimeType)
            throws IOException {


        // 取得 Google Drive Service if null
        if (this.googleDriveService == null) {
            this.googleDriveService = GoogleDriveManager.getGoogleDriveService();
        }

        //File Metadata
        File fileMetadata = new File();

        fileMetadata.setName(_fileName);
        fileMetadata.setMimeType(_mimeType);

        if (_parentId != null && _parentId.length() > 0) {
            fileMetadata.setParents(Arrays.asList(_parentId));
        }


        // File InputStream to  tempFile
        java.io.File tempFile = java.io.File.createTempFile("bbbb", ".jpeg");
        FileUtils.copyToFile(_fileInputStream, tempFile);
        FileContent mediaContent = new FileContent(_mimeType, tempFile);


        // 新增檔案 回傳 id
        return this.googleDriveService.files().create(fileMetadata, mediaContent).setFields("id").execute();
    }


    /*
     *
     * 上傳圖檔
     *
     * */
    public void uploadImage(InputStream _fileInputStream,
                            InputStream _fileInputStream2,
                            InputStream _fileInputStream3,
                            MultipartFile _fileMetaData) throws IOException, InterruptedException, MagicMatchNotFoundException, MagicException, MagicParseException {


        /* ◢◤◢◤◢◤◢◤◢◤ 1. 檢查上傳資料夾/縮圖資料夾是否存在 ◢◤◢◤◢◤◢◤◢◤ */
        checkFolderExist();

        /* ◢◤◢◤◢◤◢◤◢◤ 2. 取的檔案名稱, mime type ◢◤◢◤◢◤◢◤◢◤ */

        logger.info("===> _fileMetaData.getOriginalFilename():" + _fileMetaData.getOriginalFilename());

        String fileName = _fileMetaData.getOriginalFilename();

        MagicMatch fileMatchResult = Magic.getMagicMatch(IOUtils.toByteArray(_fileInputStream3));
        String mimeType = fileMatchResult.getMimeType();
        String fileExtension = fileMatchResult.getExtension();
        logger.info("===> mimeType:" + mimeType);


        /* ◢◤◢◤◢◤◢◤◢◤ 3. 大圖 ◢◤◢◤◢◤◢◤◢◤ */
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

//        logger.info("===> 大圖 0 ");
//        BufferedImage buf = Thumbnails.of(_fileInputStream).size(1500, 1500).asBufferedImage();
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        ImageIO.write(buf, fileType, os);
//        InputStream largeInputStream = new ByteArrayInputStream(os.toByteArray());
//        logger.info("===> 大圖 1");


        /* ◢◤◢◤◢◤◢◤◢◤ 4. 小圖 ◢◤◢◤◢◤◢◤◢◤ */
        logger.info("===> 小圖 0 ");
        //縮小相片
        BufferedImage thumbnail_buf = Thumbnails.of(_fileInputStream2).size(150, 150).asBufferedImage();
        ByteArrayOutputStream thumbnail_os = new ByteArrayOutputStream();
        ImageIO.write(thumbnail_buf, fileType, thumbnail_os);
        InputStream thumbnailInputStream = new ByteArrayInputStream(thumbnail_os.toByteArray());
        logger.info("===> 小圖 1 ");


        // 原圖
        File originalImageRes = this.createFile(todayFolderId, _fileInputStream, fileName, mimeType);

        // 縮圖
        File thumbnailImageRes = this.createFile(thumbnailFolderId, thumbnailInputStream, fileName, mimeType);

        logger.info("===> originalImageRes Id on Google Drive: " + originalImageRes.getId());
        logger.info("===> thumbnailImageRes Id on Google Drive: " + thumbnailImageRes.getId());
    }


    /*
     *
     *
     * 檔案清單
     *
     * */
    public FileList getFileList(String _parentFolderId, Boolean isOnlyFolder) throws IOException {

        // 取得 Google Drive Service if null
        if (this.googleDriveService == null) {
            this.googleDriveService = GoogleDriveManager.getGoogleDriveService();
        }


        // 找特定資料夾
        String queryString = "'" + _parentFolderId + "'" + " in parents and trashed = false ";


        // 只要資料夾清單
        if (isOnlyFolder) {
            queryString = queryString + " and mimeType = 'application/vnd.google-apps.folder' ";
        }

        FileList result = this.googleDriveService.files().list()
                .setQ(queryString)
                .setSpaces("drive")
                .execute();


        return result;

    }


    /*
     *
     *
     * 檢查上傳資料夾/縮圖資料夾是否存在
     *
     * */
    public void checkFolderExist() throws IOException {
        logger.info("checkFolder 0");
        DateTimeZone timeZone = DateTimeZone.forID("Asia/Taipei");
        DateTime dateTime = new DateTime(timeZone);
        String todayFolderName = dateTime.toString("yyyy-MM-dd");

        if (!isFolderChekced || !Objects.equals(folderCheckedDate, todayFolderName)) {
            // 檢查日期不一樣

            /* ◢◤◢◤◢◤◢◤◢◤ 1. 檢查是否有今天日期 Today 目錄 ◢◤◢◤◢◤◢◤◢◤ */

            Boolean isOnlyFolder = true;
            Boolean isTodayFolderExist = false;


            FileList resultForToday = getFileList(GOOGLE_DRIVE_FOLDER_ID, isOnlyFolder);

            for (File file : resultForToday.getFiles()) {
                if (Objects.equals(file.getName(), todayFolderName)) {
                    // 已存在, 返回 id
                    todayFolderId = file.getId();
                    isTodayFolderExist = true;
                }
            }

            // 今天日期 Today 目錄不存在則產生今天日期 Today 目錄
            if (isTodayFolderExist == false) {
                todayFolderId = createFolder(GOOGLE_DRIVE_FOLDER_ID, todayFolderName).getId();
            }


            /* ◢◤◢◤◢◤◢◤◢◤ 2. 檢查 今天日期 Today 目錄 下 是否有縮圖目錄 ◢◤◢◤◢◤◢◤◢◤ */
            String thumbnailFolderName = "thumbnail";
            Boolean isThumbnailFolderExist = false;


            FileList resultForThumbnail = getFileList(todayFolderId, isOnlyFolder);
            for (File file : resultForThumbnail.getFiles()) {
                if (Objects.equals(file.getName(), thumbnailFolderName)) {
                    // 已存在, 返回 id
                    thumbnailFolderId = file.getId();
                    isThumbnailFolderExist = true;
                }
            }

            // 縮圖目錄不存在則產生縮圖目錄
            if (isThumbnailFolderExist == false) {
                thumbnailFolderId = createFolder(todayFolderId, thumbnailFolderName).getId();
            }

            logger.info("===> Today目錄 id :" + todayFolderId);
            logger.info("===> 縮圖目錄 id :" + thumbnailFolderId);


            if (!Objects.equals(todayFolderId, null) && !Objects.equals(thumbnailFolderId, null)) {
                isFolderChekced = true;
                folderCheckedDate = todayFolderName;
            }

        }

        logger.info("checkFolder 1");


    }


}
