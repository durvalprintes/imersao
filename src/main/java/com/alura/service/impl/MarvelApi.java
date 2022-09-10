package com.alura.service.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import com.alura.exception.StickerApiException;
import com.alura.model.ContentSticker;
import com.alura.model.Endpoint;
import com.alura.model.ParamSticker;
import com.alura.service.AbstractApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@SuppressWarnings("squid:S106")
public class MarvelApi extends AbstractApi {

  @Override
  public boolean accept(Endpoint endpoint) {
    return Endpoint.isMarvel(endpoint);
  }

  @Override
  public void consume(Endpoint endpoint) throws StickerApiException {
    var publicKey = System.getProperty("marvel_public_key");
    var privateKey = System.getProperty("marvel_private_key");
    var id = UUID.randomUUID();

    try {
      var json = jsonFromGet(String.format("%s?ts=%s&apikey=%s&hash=%s&limit=100", endpoint.getUrl(), id, publicKey,
          new BigInteger(1, MessageDigest.getInstance("MD5").digest((id + privateKey + publicKey).getBytes()))
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
          .toList());
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
            .outputPath("data/image/sticker/marvel/")
            .outputName(data.title()).build());
        System.out.println("Ok!");
      } catch (StickerApiException | IOException e) {
        System.out.println("Fail: " + e.getMessage());
      }
    });
    System.out.println("...Finalizado!");
  }

}
