package me.cometkaizo.brokenarrows.jar;

public class JarExecutionException extends RuntimeException {
    public JarExecutionException() {
    }

    public JarExecutionException(String message) {
        super(message);
    }

    public JarExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JarExecutionException(Throwable cause) {
        super(cause);
    }
}
