package ua.gorbatov.library.spring.exception;

public class UnableDeleteBookException extends RuntimeException{

    public UnableDeleteBookException() {
    }

    public UnableDeleteBookException(String message) {
        super(message);
    }
}
