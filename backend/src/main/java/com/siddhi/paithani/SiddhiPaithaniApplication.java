package com.siddhi.paithani;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class SiddhiPaithaniApplication {

	@PostConstruct
	public void init() {
		// Set default application TimeZone to Indian Standard Time (IST - Asia/Kolkata)
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	}

	public static void main(String[] args) {
		SpringApplication.run(SiddhiPaithaniApplication.class, args);
	}

}
