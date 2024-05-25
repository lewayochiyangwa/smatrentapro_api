package com.smatpro.api.uploads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smatpro.api.Helpers.Dao;
import com.smatpro.api.Helpers.IdEmail;
import com.smatpro.api.Property.Property;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;

@RestController
@RequestMapping("/api/v1/auth")
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
            String filename = StringUtils.cleanPath(file.getOriginalFilename().toLowerCase());
            log.info("check file exte");
            log.info(filename);
            if (!filename.endsWith(".png") && !filename.endsWith(".jpg")) {
                return ResponseEntity.badRequest().body("Invalid file format, only PNG files are allowed");
            }
            Path path = Paths.get("C:\\xampp\\htdocs\\houses\\assets\\pro_pics\\" + filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            int theId= IdEmail.getIdByEmail(request.getID());


            try {
                String sql = "UPDATE profile_pic SET image_path = ? WHERE user_id ="+theId;
                //log.info(sql);
                PreparedStatement stmt = Dao.connection().prepareStatement(sql);
                log.info("assets/pro_pics/"+request.getFileName());
                stmt.setString(1, "assets/pro_pics/"+filename);
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

    @PostMapping("/profilepic")
    public String cc(@RequestBody @Valid PostEmail postEmail) {
        String imagePath = "";
        String sql = "SELECT image_path FROM profile_pic WHERE user_id = ?";
        try (Connection connection = Dao.connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int theId= IdEmail.getIdByEmail(postEmail.getEmail());
            statement.setInt(1, theId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                     imagePath = resultSet.getString("image_path");
                    // Process the image path as needed
                    System.out.println("Image Path: " + imagePath);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving image path", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Return the appropriate response
        return imagePath;
    }

}
