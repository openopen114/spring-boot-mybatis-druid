package com.openopen.googledrive;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Arrays;

public class FileManager {


    // Please change folder id on your goole driver
    public static final String apksLocationId = "18NwRWjS-ybEnLuVo6tBUVkVvCqK4O5B7";
    private static final String finalApksLoaction = "/ReleaseApks.zip";
    private static final String uploadingFileMimeType = "image/jpeg";
    private static final String buildFolderPath = "/Users/openopen/Desktop/aaaa.jpeg";

    private Util util;
    private Drive service;

    public FileManager() throws IOException {
        this.service = GoogleDriveManager.getDriveService();
        this.util = new Util();
    }

    public void insertFile() throws IOException, InterruptedException {
        File remoteFileFolder = this.createRemoteApkFolder(apksLocationId);
        System.out.println("Remote Folder Id on Google Drive: " + remoteFileFolder.getId());

        Thread.sleep(2000);

        String uploadingFileTitle = "gdu_android_apks";

        System.out.println("test: " + buildFolderPath);

        File remoteFile = this.createRemoteApkFiles(uploadingFileTitle, remoteFileFolder.getId(), buildFolderPath,
                uploadingFileMimeType);
        System.out.println("Remote File Id on Google Drive: " + remoteFile.getId());
    }

    public File createRemoteApkFolder(String parentId) throws IOException {
        File fileMetadata = new File();
        StringBuffer folderName = new StringBuffer();
        String fileTitle = folderName.append("gduSampleFolder").append(this.util.getDateTime()).toString();


//        fileMetadata.setTitle(fileTitle);
        System.out.println("===> Folder fileTitle: " + fileTitle);
        fileMetadata.setName(fileTitle);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        if (parentId != null && parentId.length() > 0) {
            fileMetadata.setParents(Arrays.asList(parentId));
        }

        File remoteFileFolder = this.service.files().create(fileMetadata).setFields("id").execute();

        return remoteFileFolder;
    }

    public File createRemoteApkFiles(String fileTitle, String parentId, String filePath, String mediaContentMimeType)
            throws IOException {
        File fileMetadata = new File();
        System.out.println("===> Folder fileTitle: " + fileTitle);
        fileMetadata.setName(fileTitle);
        fileMetadata.setMimeType(mediaContentMimeType);

        if (parentId != null && parentId.length() > 0) {
            fileMetadata.setParents(Arrays.asList(parentId));
        }

        java.io.File localFile = new java.io.File(filePath);
        FileContent mediaContent = new FileContent(mediaContentMimeType, localFile);
        File remoteFile = this.service.files().create(fileMetadata, mediaContent).setFields("id").execute();

        return remoteFile;
    }
}
