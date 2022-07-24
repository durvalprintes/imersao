package com.alura.exception;

public class StickerApiException extends RuntimeException {

  public StickerApiException(String message, Throwable error) {
    super(message, error);
  }

  public StickerApiException(String message) {
    super(message);
  }

  public StickerApiException(Throwable error) {
    super(error);
  }

}
