package com.alura.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;
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
public class ChallengeApi extends AbstractApi {

  @Override
  public boolean accept(Endpoint endpoint) {
    return Endpoint.isChallenge(endpoint);
  }

  @Override
  public void consume(Endpoint endpoint) throws StickerApiException {
    try {
      var jsonNode = new ObjectMapper().readTree(jsonFromGet(endpoint.getUrl()));
      setData(StreamSupport.stream(jsonNode.get("content").spliterator(), false)
          .map(node -> ContentSticker.builder()
              .title(node.get("titulo").asText())
              .urlImage(node.get("thumbnailUrl").asText())
              .rating(Double.toString(Math.round(
                  node.get("totalAprova").asLong() * 10.0 /
                      (node.get("totalRejeita").asLong() +
                          node.get("totalAprova").asLong()))))
              .build())
          .collect(Collectors.toList()));
    } catch (StickerApiException | JsonProcessingException e) {
      throw new StickerApiException("Erro ao instanciar implementacao da API Alura Challenge.", e);
    }
  }

  @Override
  public void generateStickers() throws StickerApiException {
    System.out.println("\nIniciando geração de Stickers do Alura Challenge...");
    getData().stream().forEach(data -> {
      try {
        System.out.print(data.title() + "... ");
        createSticker(ParamSticker.builder()
            .image(new URL(data.urlImage()).openStream())
            .targetWidth(1000)
            .targetHeight(1500)
            .text("ALURA")
            .fontName("Comic Sans MS")
            .fontSize(128)
            .outputPath("data/image/sticker/challenge/")
            .outputName(data.title()).build());
        System.out.println("Ok!");
      } catch (StickerApiException | IOException e) {
        System.out.println("Fail: " + e.getMessage());
      }
    });
    System.out.println("...Finalizado!");
  }

}
