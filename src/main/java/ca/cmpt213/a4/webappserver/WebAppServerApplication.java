package ca.cmpt213.a4.webappserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

/**
 * SpringBoot server
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
@SpringBootApplication(exclude = {JacksonAutoConfiguration.class}) // disable Jackson (using Gson)
public class WebAppServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebAppServerApplication.class, args);
	}

}
