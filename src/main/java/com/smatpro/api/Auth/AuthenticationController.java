package com.smatpro.api.Auth;


import com.smatpro.api.Helpers.ApiResponse;
import com.smatpro.api.Helpers.Dao;
import com.smatpro.api.Helpers.Hasher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) throws Exception {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
 // public ResponseEntity<AuthenticationResponse> authenticate(
  public ApiResponse authenticate(
      @RequestBody AuthenticationRequest request
  ) {
   // return ResponseEntity.ok(service.authenticate(request));
      return service.authenticate(request);
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  @GetMapping("/activate")
  public String  activate(@RequestParam("v") String paramValue) throws Exception {
      String decodedParam = paramValue.replace(" ", "+");
    SecretKey key = new SecretKeySpec("0123456789abcdef".getBytes(), "AES");
    IvParameterSpec iv = new IvParameterSpec("abcdef0123456789".getBytes());

    String c = paramValue;
 ///  String val = Hasher.decrypt("AES/CBC/PKCS5Padding",paramValue, key, iv);
    String val = Hasher.decrypt(decodedParam, key);
   if(tokenExist(val)){
     return "activated";
   }else{
     return "invalid token";
   }

  }


  public  boolean tokenExist(String token){
      long id1=0;
   int count = 0;
      try (Connection connection = Dao.connection()) {
         String sql = "SELECT * FROM Token WHERE token = "+"'" + token + "'";
         // String sql = "SELECT * FROM Token";
          try (Statement statement = connection.createStatement()) {
              try (ResultSet resultSet = statement.executeQuery(sql)) {

                  if (resultSet.next()) {
                      id1 = resultSet.getLong("user_id");
                      count++;
                  }
              }
          }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if(count > 0){
        try (Connection connection = Dao.connection()) {
            String sql = "UPDATE _user SET activated =1  WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id1);
                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {

                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
      return true;
    }else {
    return false;
    }

  }


}
