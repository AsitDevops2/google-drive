package com.google.drive.api.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.services.drive.model.File;
import com.google.drive.api.service.GoogleDriveService;

@RestController
@RequestMapping("/")
public class GoogleDriveController {
  @Autowired
  GoogleDriveService service;

  @GetMapping("/Files")
  public ResponseEntity<List<File>> files() throws IOException {
    List<File> files = service.files();
    return ResponseEntity.ok(files);
  }

  @GetMapping("/download/{id}")
  public void download(@PathVariable String id, HttpServletResponse response)
    throws IOException {
    service.downloadFile(id, response.getOutputStream());
  }

  @GetMapping("/File/{id}")
  public File retireveFileMetadata(@PathVariable String id) throws IOException {
    return service.getFile(id);
  }

  @GetMapping("/downloadSource")
  public void downloadSource(HttpServletResponse response) {
    service.downloadSource(response);
  }

  @GetMapping("/signInUrl")
  public String doGoogleSignIn()  {
    return service.redirectURL();
  }
}
