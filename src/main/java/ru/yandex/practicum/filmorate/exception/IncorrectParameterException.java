package ru.yandex.practicum.filmorate.exception;

public class IncorrectParameterException extends RuntimeException {
    private final String parameter;

    public IncorrectParameterException(final String parameter, final String message) {
        super(message);
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}