package com.alura.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import com.alura.exception.StickerApiException;
import com.alura.model.Endpoint;
import com.alura.model.ParamSticker;

public interface StickerApi {

  boolean accept(Endpoint endpoint);

  void consume(Endpoint endpoint);

  default String jsonFromGet(String url) throws StickerApiException {
    try {
      return HttpClient.newHttpClient()
          .send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
              BodyHandlers.ofString())
          .body();
    } catch (IOException e) {
      throw new StickerApiException("Erro ao recuperar Json.", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return null;
  }

  void limitData(int max);

  void printData();

  void updateDataWithInput(InputStream input) throws StickerApiException;

  void generateStickers() throws StickerApiException;

  default void createSticker(ParamSticker param) throws StickerApiException {
    new Sticker().create(param);
  }

}
