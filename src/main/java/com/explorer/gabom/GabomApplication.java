package com.explorer.gabom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GabomApplication {

    public static void main(String[] args) {
        SpringApplication.run(GabomApplication.class, args);
    }

}
