package ca.hexanome04.splendorgame.model;

/**
 * Simple exception class that allows us to differentiate between other exceptions from a known Splendor exception.
 */
public class SplendorException extends RuntimeException {

    /**
     * Initializes a new Splendor exception.
     *
     * @param errorMessage error message
     */
    public SplendorException(String errorMessage) {
        super(errorMessage);
    }

}
