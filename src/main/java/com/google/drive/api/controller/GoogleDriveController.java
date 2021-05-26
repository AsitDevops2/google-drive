package com.google.drive.api.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.drive.model.File;
import com.google.drive.api.service.GoogleDriveService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class GoogleDriveController {
  @Autowired
  GoogleDriveService service;
  
 
  

  @GetMapping("/Files")
  public ResponseEntity<List<File>> files() throws IOException, GeneralSecurityException {
    List<File> files = service.files();
    return ResponseEntity.ok(files);
  }

  @GetMapping("/File/{id}")
  public File retireveFileMetadata(@PathVariable String id) throws IOException, GeneralSecurityException {
    return service.getFile(id);
  }
  
  @GetMapping("/download/{id}")
  public void download(@PathVariable String id, HttpServletResponse response)
    throws IOException, GeneralSecurityException {
    service.downloadFile(id, response.getOutputStream());
  }

  @GetMapping("/downloadSource")
  public void downloadSource(HttpServletResponse response) {
    service.downloadSource(response);
  }

  @GetMapping("/connect")
  public  ResponseEntity<String> connectDrive() throws GeneralSecurityException, IOException  {
	  service.connectDrive();
    return ResponseEntity.ok("succesfully connected");
  }
  
  @PostMapping(value = "/upload",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE} )
  public ResponseEntity<String> uploadSingleFile(@RequestBody MultipartFile file,@RequestParam(required = false) String path) {
	  String fileId = service.upload(file, path);
	  if(fileId == null){
	     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	  }
	  
	  return ResponseEntity.ok("FileId: "+ fileId);
   }
}
