package com.smatpro.api;





import com.google.common.collect.ImmutableList;


import com.smatpro.api.Auth.AuthenticationService;
import com.smatpro.api.Auth.RegisterRequest;
import com.smatpro.api.Helpers.Hasher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Predicates.or;
import static com.smatpro.api.Helpers.Hasher.decrypt;
import static com.smatpro.api.Users.Role.ADMIN;
import static com.smatpro.api.Users.Role.MANAGER;
//import static springfox.documentation.builders.PathSelectors.regex;
//import springfox.documentation.annotations.EnableSwagger2;

//@SpringBootApplication
//@EnableScheduling
//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableSwagger2WebMvc
//@EnableSwagger2

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableWebSecurity
public class SmatRentalPro {

	 ReentrantLock lock = new ReentrantLock();
	//static String pollUrl="";
	//static Paynow paynow;
	//static WebInitResponse response;
	//static StatusResponse status;
	int counter = 0;






	public static void main(String[] args) throws Exception{

		SpringApplication.run(SmatRentalPro.class, args);

	String plainText = "76F+3mVJYhb/pEqjYdZxdQHqG78J/OdwBcwwiUO12w1Ey5Y4Oz8C3gwB+FK+6idQCyRHUAatzZJIkCBs+/Mkie9jLho79OHomS6YKYKl+YtoDu0Qau4E3qtJKJ5r3HzjhaAn0cXm1apRLWO1k8JqS8suuGOV6RkBl0I1GwjJHPA3ZGRHF/ix9S3Sz5d3AQkhgLxIZbtduU7tiicjo5qyHg==";
		System.out.println("Plain Text Before Encryption: " + plainText);

		SecretKey key = new SecretKeySpec("0123456789abcdef".getBytes(), "AES");
	//	String encryptedText = Hasher.encrypt(plainText, key);
	//	System.out.println("Encrypted Text After Encryption: " + encryptedText);

		String decryptedText = decrypt(plainText, key);
		System.out.println("Decrypted Text After Decryption: " + decryptedText);


	}




	@Bean
	public WebMvcConfigurer customConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
				configurer.defaultContentType(org.springframework.http.MediaType.valueOf(MediaType.APPLICATION_JSON));
			}
		};
	}


	@Bean
	//public CorsFilter corsFilter(){
		public void corsFilter(){
	//	X-Frame-Options "allow-from *"
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Arrays.asList("http://biactutors.co.zw","http://localhost:3000","http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("X-Frame-Options","Origin","Access-Control-Allow-Origin",
				"Content-Type","Accept","Jwt-Token","Authorization",
				"Origin,Accept","X-Requested-With","Access-Control-Requested-Method",
				"Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("X-Frame-Options","Origin","Access-Control-Allow-Origin",
				"Content-Type","Accept","Jwt-Token","Authorization",
				"Origin,Accept","X-Requested-With","Access-Control-Requested-Method",
				"Access-Control-Request-Headers","filename"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET","POST","DELETE","PUT","PATCH","OPTIONS"));
		//urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
		urlBasedCorsConfigurationSource.registerCorsConfiguration("allow-from *",corsConfiguration);
		//return new CorsFilter(urlBasedCorsConfigurationSource);
	}



	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(ImmutableList.of("*"));
		configuration.setAllowedMethods(ImmutableList.of("HEAD",
				"GET", "POST", "PUT", "DELETE", "PATCH"));
		// setAllowCredentials(true) is important, otherwise:
		// The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
		configuration.setAllowCredentials(true);
		// setAllowedHeaders is important! Without it, OPTIONS preflight request
		// will fail with 403 Invalid CORS request
		configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}



	@Override
	public boolean equals(Object o) {
		Double dataObj_=0.0;
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DataObj dataObj = (DataObj) o;
		return Double.compare(dataObj.dataObj, dataObj_) == 0 && Objects.equals(dataObj.dataObj, dataObj_);
	}
	class DataObj{
	Double dataObj;
	}


	/*@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	) {
		return args -> {
			var admin = RegisterRequest.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("admin@mail.com")
					.password("password")
					.role(ADMIN)
					.build();
			System.out.println("Admin token: " + service.register(admin).getAccessToken());

			var manager = RegisterRequest.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("manager@mail.com")
					.password("password")
					.role(MANAGER)
					.build();
			System.out.println("Manager token: " + service.register(manager).getAccessToken());

		};
	}
	*/



}




