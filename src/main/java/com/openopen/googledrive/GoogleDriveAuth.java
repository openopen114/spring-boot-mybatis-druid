package com.openopen.googledrive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;


@Component
public class GoogleDriveAuth {


    // Logger
    private static Logger logger = LoggerFactory.getLogger(GoogleDriveAuth.class);


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
    public static ServiceAccountCredentials authorize() throws IOException {

        logger.info("===> authorize");
        logger.info("===> SERVICE_ACCOUNT_EMANIL:" + SERVICE_ACCOUNT_EMANIL);
        logger.info("===> SERVICE_ACCOUNT_JSON_PATH:" + SERVICE_ACCOUNT_JSON_PATH);

        ServiceAccountCredentials clientSecrets =
                ServiceAccountCredentials.fromStream(GoogleDriveAuth.class.getResourceAsStream(SERVICE_ACCOUNT_JSON_PATH));
        PrivateKey privateKey = clientSecrets.getPrivateKey();
        String privateKeyId = clientSecrets.getPrivateKeyId();


        ServiceAccountCredentials credentials =
                ServiceAccountCredentials.newBuilder()
                        .setPrivateKey(privateKey)
                        .setPrivateKeyId(privateKeyId)
                        .setScopes(SCOPES)
                        .setClientEmail(SERVICE_ACCOUNT_EMANIL)
                        .build();

        return credentials;
    }


    /*
     *
     *  取得google Drive Service
     *
     * */
    public static Drive getGoogleDriveService() throws IOException {
        ServiceAccountCredentials credentials = authorize();
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        logger.info("===> authorize ok");

        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer).setApplicationName(APPLICATION_NAME).build();
    }


}
