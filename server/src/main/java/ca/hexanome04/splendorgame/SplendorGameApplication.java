package ca.hexanome04.splendorgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Defines Spring Boot Application for Splendor Game.
 */
@SpringBootApplication(proxyBeanMethods = false)
public class SplendorGameApplication {

    private static ApplicationContext appCtx;

    /**
     * Construct the splendor game application.
     */
    public SplendorGameApplication() {
        //
    }

    /**
     * Start function for application.
     *
     * @param args start arguments
     */
    public static void main(String[] args) {
        appCtx = SpringApplication.run(SplendorGameApplication.class, args);
    }

    /**
     * Shutdown the spring boot application.
     *
     * @param statusCode status code to exit with
     */
    public static void shutdown(int statusCode) {
        if (appCtx != null) {
            int exitCode = SpringApplication.exit(appCtx, () -> statusCode);
            System.exit(exitCode);
        }
    }

}
