package ca.hexanome04.splendorgame.control;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * Override the default error handling in rest template to not throw exceptions
 * if not status code == 200.
 */
public class EmptyRestTemplateErrorHandler extends DefaultResponseErrorHandler {

    /**
     * Constructs an empty rest template error handler.
     */
    public EmptyRestTemplateErrorHandler() {
        // Empty.
    }

    /**
     * Handle the rest template exchange() error (remove exception thrown).
     *
     * @param response the response with the error
     * @throws IOException exception thrown
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // Empty so that exceptions aren't thrown.
    }

}
