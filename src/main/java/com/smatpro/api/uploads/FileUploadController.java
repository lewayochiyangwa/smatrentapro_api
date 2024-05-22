package com.smatpro.api.uploads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smatpro.api.Helpers.Dao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    @PostMapping("/uploadflutter")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
//                                             @RequestBody @Valid FileUploadRequest request) {
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             FileUploadRequest request) {



        try {
            log.info(request.getID());
            log.info(request.getFileName());
            log.info("kkkkk what isn it");

            // Validate request
            if (request.getID() == null || request.getFileName() == null) {
                log.info("hanzii muno");
                return ResponseEntity.badRequest().body("Invalid request body");
            }

            // Save file to disk
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            log.info("check file exte");
            log.info(filename);
            if (!filename.endsWith(".PNG") && !filename.endsWith(".jpg")) {
                return ResponseEntity.badRequest().body("Invalid file format, only PNG files are allowed");
            }
            Path path = Paths.get("\\assets\\img\\pop\\" + filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            try {
                String sql = "UPDATE tblpics SET ProofOfPayment = ? WHERE id ="+request.getID();
                //log.info(sql);
                PreparedStatement stmt = Dao.connection().prepareStatement(sql);
                log.info("assets/img/pop/"+request.getFileName());
                stmt.setString(1, "assets/img/pop/"+filename);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("User data was updated successfully!");
                } else {
                    System.out.println("No user data was updated.");
                }
            } catch (SQLException ex) {
                System.err.println("An error occurred: " + ex.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Create response object
            FileUploadResponse response = new FileUploadResponse();
            response.setId(request.getID());
            response.setFileName(request.getFileName());

            // Return response as JSON
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(response);
            return ResponseEntity.ok(json);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
