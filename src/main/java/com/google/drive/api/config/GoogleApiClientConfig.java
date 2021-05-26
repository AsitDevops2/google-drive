package com.google.drive.api.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

@Configuration
public class GoogleApiClientConfig {
  @Value("${google.service.account.user}")
  private String user;

  @Value(("${google.service.account.credentials.path}"))
  private String credentialsPath;

  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  
  private  GoogleAuthorizationCodeFlow flow;
  
  private  LocalServerReceiver receiver;

  
  /**
   * Provides a unmodifiable {@link Set<String>} of Google OAuth 2.0 scopes to be used.
   *
   * @return An unmodifiable {@link Set<String>}
   */
  private Set<String> googleOAuth2Scopes() {
    Set<String> googleOAuth2Scopes = new HashSet<>();
    googleOAuth2Scopes.add(DriveScopes.DRIVE);
    return Collections.unmodifiableSet(googleOAuth2Scopes);
  }

  /**
   * Provides a global {@link GoogleCredential} instance for use for all Google API calls.
   *
   * @return {@link GoogleCredential}
   * @throws GeneralSecurityException
   */
  
  public Credential googleCredential() throws IOException, GeneralSecurityException {
    // Load client secrets.
    InputStream in = GoogleApiClientConfig.class.getResourceAsStream(credentialsPath);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + credentialsPath);
    }
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
      jacksonFactory(),
      new InputStreamReader(in)
    );

    // Build flow and trigger user authorization request.
     flow = new GoogleAuthorizationCodeFlow.Builder(
      netHttpTransport(),
      jacksonFactory(),
      clientSecrets,
      googleOAuth2Scopes()
    )
      .setDataStoreFactory(
        new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH))
      )
      .build();
     receiver = new LocalServerReceiver.Builder()
      .setPort(8888)
      .build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize(user);
  }
  
  
 

  /**
   * A preconfigured HTTP client for calling out to Google APIs.
   *
   * @return {@link NetHttpTransport}
   */
  
  public NetHttpTransport netHttpTransport()
    throws GeneralSecurityException, IOException {
    return GoogleNetHttpTransport.newTrustedTransport();
  }

  /**
   * Abstract low-level JSON factory.
   *
   * @return {@link JacksonFactory}
   */
  
  public JacksonFactory jacksonFactory() {
    return JacksonFactory.getDefaultInstance();
  }
  public Drive googleDrive() throws GeneralSecurityException, IOException {
	    return new Drive(netHttpTransport(), jacksonFactory(), googleCredential());
	  }
}
