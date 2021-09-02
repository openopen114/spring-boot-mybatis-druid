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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class GoogleDriveManager {


    private static String uploadingFileMimeType = "image/jpeg";
    private static String buildFolderPath = "/Users/openopen/Desktop/aaaa.jpeg";


    // Google Drive Service
    private Drive googleDriveService;


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
        System.out.println("===> authorize:");
        System.out.println("===> SERVICE_ACCOUNT_EMANIL:" + SERVICE_ACCOUNT_EMANIL);
        System.out.println("===> SERVICE_ACCOUNT_JSON_PATH:" + SERVICE_ACCOUNT_JSON_PATH);
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
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }


    /*
     *
     * 新增資料夾
     *
     *
     * */
    public File createFolder(String _parentId, String _folderName) throws IOException {

        // 取得 Google Drive Service
        this.googleDriveService = GoogleDriveManager.getGoogleDriveService();


        //File MetaData
        File folderMetadata = new File();


        System.out.println("===> Folder Name: " + _folderName);
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
    public File createFile(String _parentId, String _fileName, String _mimeType)
            throws IOException {

        // 取得 Google Drive Service
        this.googleDriveService = GoogleDriveManager.getGoogleDriveService();


        //File Metadata
        File fileMetadata = new File();

        fileMetadata.setName(_fileName);
        fileMetadata.setMimeType(_mimeType);

        if (_parentId != null && _parentId.length() > 0) {
            fileMetadata.setParents(Arrays.asList(_parentId));
        }

        java.io.File localFile = new java.io.File(buildFolderPath);
        FileContent mediaContent = new FileContent(_mimeType, localFile);

        // 新增檔案 回傳 id
        return this.googleDriveService.files().create(fileMetadata, mediaContent).setFields("id").execute();
    }


    /*
     *
     * 上傳圖檔
     *
     * */
    public void uploadImage() throws IOException, InterruptedException {

        // 取得資料夾目錄
        Boolean isOnlyFolder = true;
        FileList result = getFileList(GOOGLE_DRIVE_FOLDER_ID, isOnlyFolder);

        // 檢查是否有縮圖目錄
        String thumbnailFolderName = "thumbnail";
        Boolean isThumbnailFolderExist = false;


        // 縮圖目錄 id
        String thumbnailFolderId = null;
        for (File file : result.getFiles()) {
            if (Objects.equals(file.getName(), thumbnailFolderName)) {
                thumbnailFolderId = file.getId();
                isThumbnailFolderExist = true;
            }
        }


        // 縮圖目錄不存在則產生縮圖目錄
        if (isThumbnailFolderExist == false) {
            thumbnailFolderId = createFolder(GOOGLE_DRIVE_FOLDER_ID, thumbnailFolderName).getId();
        }

        System.out.println("===> 縮圖目錄 id :" + thumbnailFolderId);


        String fileName = "aaaa123";


        File remoteFile = this.createFile(GOOGLE_DRIVE_FOLDER_ID, fileName, uploadingFileMimeType);
        System.out.println("Remote File Id on Google Drive: " + remoteFile.getId());
    }


    /*
     *
     *
     * 檔案清單
     *
     * */
    public FileList getFileList(String _parentFolderId, Boolean isOnlyFolder) throws IOException {
        System.out.println("===> file List");
        // 取得 Google Drive Service
        this.googleDriveService = GoogleDriveManager.getGoogleDriveService();

        String queryString = "'" + _parentFolderId + "'" + " in parents and trashed = false ";
        if (isOnlyFolder) {
            queryString = queryString + " and mimeType = 'application/vnd.google-apps.folder' ";
        }

        FileList result = this.googleDriveService.files().list()
                .setQ(queryString)
                .setSpaces("drive")
                .execute();


        return result;

    }


}
