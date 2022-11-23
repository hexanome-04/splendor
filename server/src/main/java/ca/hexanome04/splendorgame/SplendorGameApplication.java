package ca.hexanome04.splendorgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Defines Spring Boot Application for Splendor Game.
 */
@SpringBootApplication(proxyBeanMethods = false)
public class SplendorGameApplication {

    /**
     * Start function for application.
     *
     * @param args start arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SplendorGameApplication.class, args);
    }

}
