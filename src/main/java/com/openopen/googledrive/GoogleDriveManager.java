package com.openopen.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;

public class GoogleDriveManager {

    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    @Value("${google.secret.key.path}")
    private Resource gdSecretKeys;

    @Value("${google.credentials.folder.path}")
    private Resource credentialsFolder;

    @Value("${google.service.account.key}")
    private Resource serviceAccountKey;


    /*
    * google.secret.key.path: classpath:client_secret.json
google.oauth.callback.uri: http://127.0.0.1.nip.io:8080/oauth
google.credentials.folder.path: /Users/openopen/Desktop/credentials
google.service.account.key: classpath:secret/service.json
    *
    * */


    public static final String APPLICATION_NAME = "Drive API Java Quickstart";
    public static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), getJarPath());
    public static FileDataStoreFactory DATA_STORE_FACTORY;
    //    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static HttpTransport HTTP_TRANSPORT;
    public static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static String getJarPath() {
        return System.getProperty("user.dir");
    }

    public static String getApkOutputFolderPath() {
        return "/app/build/outputs/apk";
    }


    public static Credential authorize() throws IOException {
        System.out.println("===> authorize");
        System.out.println("===> System.getProperty(user.home) : " + System.getProperty("user.home"));
        System.out.println("===> System.getProperty(user.dir) : " + System.getProperty("user.dir"));
        System.out.println("===> path: " + DATA_STORE_DIR.getPath());


//        InputStream fileInputStream = GoogleDriveManager.class.getResourceAsStream("/crypto-eon-324701-4bffd884f77c.json");
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
//                new InputStreamReader(fileInputStream));
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
//                clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
//

        GoogleCredential clientSecrets =
                GoogleCredential.fromStream(GoogleDriveManager.class.getResourceAsStream("/crypto-eon-324701-4bffd884f77c.json"));
        PrivateKey privateKey = clientSecrets.getServiceAccountPrivateKey();
        String privateKeyId = clientSecrets.getServiceAccountPrivateKeyId();
        System.out.println("privateKeyId:" + privateKeyId);


        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId("my-google0drive-service@crypto-eon-324701.iam.gserviceaccount.com")
                .setServiceAccountScopes(SCOPES)
                .setServiceAccountPrivateKey(privateKey)
                .setServiceAccountPrivateKeyId(privateKeyId)
                .build();

//
//        Credential credential = new Credential.Builder()
//                .setTransport(HTTP_TRANSPORT)
//                .setJsonFactory(JSON_FACTORY)
//                .setServiceAccountId("my-google0drive-service@crypto-eon-324701.iam.gserviceaccount.com")
//                .setServiceAccountUser("openopen114@gmail.com")
//                .setServiceAccountProjectId("crypto-eon-324701")
//                .setServiceAccountScopes(SCOPES)
//                .build();

        //Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        //System.out.println("===> Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());

        return credential;
    }


    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }

}
