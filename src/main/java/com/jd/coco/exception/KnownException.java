package main.java.com.jd.coco.exception;

public class KnownException extends RuntimeException {

    public KnownException() {
    }

    public KnownException(String message) {
        super(message);
    }

    public KnownException(String message, Throwable cause) {
        super(message, cause);
    }

    public KnownException(Throwable cause) {
        super(cause);
    }

    public KnownException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

