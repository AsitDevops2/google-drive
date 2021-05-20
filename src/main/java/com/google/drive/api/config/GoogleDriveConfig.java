package com.google.drive.api.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleDriveConfig {
  private Credential googleCredential;
  private NetHttpTransport netHttpTransport;
  private JacksonFactory jacksonFactory;
  

  public GoogleDriveConfig(
    Credential googleCredential,
    NetHttpTransport netHttpTransport,
    JacksonFactory jacksonFactory
  ) {
    this.googleCredential = googleCredential;
    this.netHttpTransport = netHttpTransport;
    this.jacksonFactory = jacksonFactory;
  }

  /**
   * Provides a Google Drive client.s
   *
   * @return {@link Drive}
   */
  @Bean
  public Drive googleDrive() {
    return new Drive(netHttpTransport, jacksonFactory, googleCredential);
  }
  
}
