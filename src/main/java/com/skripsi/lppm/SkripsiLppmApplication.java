package com.skripsi.lppm;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication
public class SkripsiLppmApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkripsiLppmApplication.class, args);
	}

	@Bean
	public Jackson2ObjectMapperBuilder objectMapperBuilder() {
		return new Jackson2ObjectMapperBuilder()
				.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}

}
