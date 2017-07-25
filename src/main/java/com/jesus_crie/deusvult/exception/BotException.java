package com.jesus_crie.deusvult.exception;

public abstract class BotException extends Exception {

    BotException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getMessage();
    }
}
