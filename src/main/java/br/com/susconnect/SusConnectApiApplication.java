package br.com.susconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SusConnectApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SusConnectApiApplication.class, args);
	}

}
