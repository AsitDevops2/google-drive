package com.google.drive.api.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.drive.api.util.SourceCodeDownloadUtil;

@Service
public class GoogleDriveService {
  @Autowired
  Drive googleDrive;
  

  @Autowired
  SourceCodeDownloadUtil util;
  
  @Autowired String doGoogleSignIn;

  /**
   * Method to get list of files from google drive
   * @return list of files
   * @throws IOException
   */
  public List<File> files() throws IOException {
    // Print the names and IDs for up to 10 files.
    FileList result = googleDrive
      .files()
      .list()
      .setPageSize(10)
      .setFields("nextPageToken, files(id, name)")
      .execute();
    return result.getFiles();
  }

  /**
   * Method to download file from google drive
   * @param id
   * @param outputStream
   * @throws IOException
   */
  public void downloadFile(@Nonnull String id, OutputStream outputStream)
    throws IOException {
    googleDrive.files().get(id).executeMediaAndDownloadTo(outputStream);
  }

  /**
   * Method to get file meta data for a given file id
   * @param id
   * @return
   * @throws IOException
   */
  public File getFile(@Nonnull String id) throws IOException {
    return googleDrive.files().get(id).execute();
  }

  /**
   * Method to download source code
   * @param response
   */
  public void downloadSource(HttpServletResponse response) {
    util.downloadZip(response);
  }
  
  /**
   * Method to return google sign in url
 * @return
 * @throws IOException
 */
public String redirectURL() 
  {
	 return  doGoogleSignIn;
  }

}
