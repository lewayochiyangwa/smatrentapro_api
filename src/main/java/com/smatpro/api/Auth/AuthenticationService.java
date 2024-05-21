package com.smatpro.api.Auth;



import com.fasterxml.jackson.databind.ObjectMapper;

import com.smatpro.api.Config.JwtService;
import com.smatpro.api.Helpers.Dao;
import com.smatpro.api.Helpers.Hasher;
import com.smatpro.api.Token.Token;
import com.smatpro.api.Token.TokenRepository;
import com.smatpro.api.Token.TokenType;
import com.smatpro.api.Users.User;
import com.smatpro.api.Users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) throws Exception {
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    SecretKey key = new SecretKeySpec("0123456789abcdef".getBytes(), "AES");
    IvParameterSpec iv = new IvParameterSpec("abcdef0123456789".getBytes());


    String activationLink="http://localhost:8081/api/v1/auth/activate?v="+ Hasher.encrypt(jwtToken, key);
    gmailMaster("lewayo.chiyangwa@gmail.com","Registration",activationLink);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  public void gmailMaster(String emailto,String emailtitle,String activationLinkx){
    String email_to=emailto;//change accordingly
    try
    {
      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", "smtp.gmail.com");
      props.put("mail.smtp.port", "587");
      //props.put("mail.smtp.ssl.enable", "false");
      props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

      Session session = Session.getInstance(props, new Authenticator()
      {
        protected PasswordAuthentication getPasswordAuthentication()
        {
          return new PasswordAuthentication("letinashe.chiyangwa@gmail.com", "owwltjhqxdxisooe");
        }
      });
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress("letinashe.chiyangwa@gmail.com", false));

      msg.setRecipients(Message.RecipientType.TO,
              InternetAddress.parse(email_to));
      msg.setSubject(emailtitle);
      msg.setSentDate(new Date());

      MimeBodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setContent("Good day, You have Successfully Registered. \n To Activate Account Click on the link below \n "+activationLinkx,
              "text/html");

      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(messageBodyPart);
      msg.setContent(multipart);
      Transport.send(msg);
    }
    catch (Exception exe)
    {
      exe.printStackTrace();
    }
  }

  public boolean checkIfActivated(String email) {
    int count = 0;

    try (Connection connection = Dao.connection()) {
      String sql = "SELECT * FROM _user WHERE activated=1 and email = " + "'" + email + "'";
      // String sql = "SELECT * FROM Token";
      try (Statement statement = connection.createStatement()) {
        try (ResultSet resultSet = statement.executeQuery(sql)) {

          if (resultSet.next()) {

            count++;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (count > 0) {
      return true;
    }else{
      return false;
    }
  }
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    if(checkIfActivated(request.getEmail())) {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      request.getEmail(),
                      request.getPassword()
              )
      );
      var user = repository.findByEmail(request.getEmail())
              .orElseThrow();
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      revokeAllUserTokens(user);
      saveUserToken(user, jwtToken);
      return AuthenticationResponse.builder()
              .accessToken(jwtToken)
              .refreshToken(refreshToken)
              .build();
    }else{
      return AuthenticationResponse.builder()
              .accessToken("Not Activated")
              .refreshToken("Not Activated")
              .build();
    }
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
