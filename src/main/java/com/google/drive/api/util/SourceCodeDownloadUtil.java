package com.google.drive.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SourceCodeDownloadUtil {
  Logger logger = LoggerFactory.getLogger(SourceCodeDownloadUtil.class);
  private static final String SOURCE_FOLDER =
    "D:\\google-drive"; // SourceFolder path
  List<String> files = null;

  SourceCodeDownloadUtil() {
    files = new ArrayList<>();
  }

  /**
   * @param node
   */
  private void generateFileList(File node) {
    // add file only
    if (node.isFile()) {
      files.add(generateZipEntry(node.toString()));
    }

    if (node.isDirectory()) {
      String[] subNote = node.list();
      for (String filename : subNote) {
        generateFileList(new File(node, filename));
      }
    }
  }

  /**
   * @param file
   * @return String
   */
  private String generateZipEntry(String file) {
    return file.substring(SOURCE_FOLDER.length() + 1, file.length());
  }

  /**
   * @param response
   */
  public void downloadZip(HttpServletResponse response) {
    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition", "attachment;filename=google-drive.zip");
    response.setStatus(HttpServletResponse.SC_OK);
    String source = new File(SOURCE_FOLDER).getName();
    generateFileList(new File(SOURCE_FOLDER));
    byte[] buffer = new byte[1024];
    try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
      this.files.forEach(
          file -> {
            ZipEntry ze = new ZipEntry(source + File.separator + file);
            try {
              zippedOut.putNextEntry(ze);
            } catch (IOException e) {
              logger.error("Error while adding files in ZipEntry :", e);
            }
            try (
              FileInputStream in = new FileInputStream(
                SOURCE_FOLDER + File.separator + file
              )
            ) {
              int len;
              while ((len = in.read(buffer)) > 0) {
                zippedOut.write(buffer, 0, len);
              }
            } catch (IOException e) {
              logger.error("Error while reading files in FileInputSTream :", e);
            }
          }
        );

      zippedOut.closeEntry();
    } catch (IOException ex) {
      logger.error("Error while opening zipoutputstream :", ex);
    }
  }
}
