package ru.accesslogparser;

class StringIsTooLongException extends RuntimeException {
    public StringIsTooLongException(String message) {
        super(message);
    }
}
