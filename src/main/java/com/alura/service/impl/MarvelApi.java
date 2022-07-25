package com.alura.service.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.alura.exception.StickerApiException;
import com.alura.model.ContentSticker;
import com.alura.model.Endpoint;
import com.alura.model.ParamSticker;
import com.alura.service.AbstractDataApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("squid:S106")
public class MarvelApi extends AbstractDataApi {

  private static final UUID ID_REQUEST = UUID.randomUUID();
  private String publicKey;
  private String privateKey;

  public MarvelApi(Endpoint endpoint, String publicKey, String privateKey) throws StickerApiException {
    this.publicKey = publicKey;
    this.privateKey = privateKey;
    this.consumeApi(endpoint.getUrl());
  }

  private void consumeApi(String url) throws StickerApiException {
    try {
      var json = this.jsonFromGet(String.format("%s?ts=%s&apikey=%s&hash=%s&limit=100", url, ID_REQUEST, publicKey,
          new BigInteger(1, MessageDigest.getInstance("MD5").digest((ID_REQUEST + privateKey + publicKey).getBytes()))
              .toString(16)));
      setData(StreamSupport
          .stream(new ObjectMapper().readTree(json).get("data").get("results").spliterator(), false)
          .map(node -> ContentSticker.builder()
              .title(node.get("name").asText())
              .urlImage(
                  String.format("%s.%s",
                      node.get("thumbnail").get("path").asText(),
                      node.get("thumbnail").get("extension").asText()))
              .build())
          .collect(Collectors.toList()));
    } catch (StickerApiException | JsonProcessingException | NoSuchAlgorithmException e) {
      throw new StickerApiException("Erro ao instanciar implementacao da API MARVEL.", e);
    }
  }

  @Override
  public void generateStickers() {
    System.out.println("\nIniciando geração de Stickers da MARVEL...");
    getData().stream().forEach(data -> {
      try {
        System.out.print(data.title() + "... ");
        createSticker(ParamSticker.builder()
            .image(new URL(data.urlImage()).openStream())
            .targetWidth(1000)
            .targetHeight(1500)
            .text("IMERSÃO JAVA")
            .fontName("Impact")
            .fontSize(128)
            .outputPath("data/image/sticker/")
            .outputName(data.title()).build());
        System.out.println("Ok!");
      } catch (StickerApiException | IOException e) {
        System.out.println("Fail: " + e.getMessage());
      }
    });
    System.out.println("...Finalizado!");
  }

}
