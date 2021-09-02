package com.openopen.googledrive;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Arrays;

public class FileManager {


    //要上傳的 folder id (要先共用給服務帳戶)
    public static String folderId;
    private static String uploadingFileMimeType = "image/jpeg";
    private static String buildFolderPath = "/Users/openopen/Desktop/aaaa.jpeg";


    private Drive googleDriveService;


    // FileManager constructor
    public FileManager(String _folderId) throws IOException {
        this.folderId = _folderId;
        //取得 Google Drive Service
        this.googleDriveService = GoogleDriveManager.getGoogleDriveService();
    }


    /*
     *
     * 上傳檔案
     *
     * */
    public void uploadFile() throws IOException, InterruptedException {
//        File remoteFileFolder = this.createRemoteApkFolder(folderId);
//        System.out.println("Remote Folder Id on Google Drive: " + remoteFileFolder.getId());

//        Thread.sleep(2000);

        String fileName = "aaaa123";


        File remoteFile = this.createFile(folderId, fileName, uploadingFileMimeType);
        System.out.println("Remote File Id on Google Drive: " + remoteFile.getId());
    }


    /*
     *
     * 新增資料夾
     *
     *
     * */
    public File createFolder(String _parentId, String _folderName) throws IOException {
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
        //String fileTitle, String parentId, String filePath, String mediaContentMimeType
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
}
