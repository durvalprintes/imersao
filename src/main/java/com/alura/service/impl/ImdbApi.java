package com.alura.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.TreeMap;
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
public class ImdbApi extends AbstractApi {

  private static final TreeMap<Integer, String> RATING_TEXT = new TreeMap<>();
  static {
    RATING_TEXT.put(0, "AVALIAR");
    RATING_TEXT.put(1, "PESSIMO");
    RATING_TEXT.put(4, "RUIM");
    RATING_TEXT.put(6, "PASSATEMPO");
    RATING_TEXT.put(8, "TOP");
  }

  @Override
  public boolean accept(Endpoint endpoint) {
    return Endpoint.isImdb(endpoint);
  }

  @Override
  public void consume(Endpoint endpoint) throws StickerApiException {
    var key = System.getProperty("imdb_key");
    try {
      var jsonNode = new ObjectMapper().readTree(jsonFromGet(endpoint.getUrl() + key));
      setData(StreamSupport.stream(jsonNode.get("items").spliterator(), false)
          .map(node -> ContentSticker.builder()
              .title(node.get("title").asText())
              .urlImage(node.get("image").asText())
              .rating(node.get("imDbRating").asText())
              .build())
          .toList());
    } catch (StickerApiException | JsonProcessingException e) {
      throw new StickerApiException("Erro ao instanciar implementacao da API IMDB.", e);
    }
  }

  @Override
  public void generateStickers() throws StickerApiException {
    System.out.println("\nIniciando geração de Stickers do IMDB...");
    getData().stream().forEach(
        data -> {
          try {
            System.out.print(data.title() + "... ");
            Double rating = !data.rating().isBlank() ? Double.valueOf(data.rating()) : 0;
            String text = RATING_TEXT.floorEntry((int) Math.round(rating)).getValue();
            createSticker(ParamSticker.builder()
                .image(new URL(data.urlImage().replaceAll("\\._(.+).jpg$", ".jpg")).openStream())
                .targetWidth(1000)
                .targetHeight(1500)
                .text(text)
                .fontName("Comic Sans MS")
                .fontSize(128)
                .isTop(RATING_TEXT.lastEntry().getValue().equals(text))
                .topImage("data/image/sticker/joinha.png")
                .outputPath("data/image/sticker/imdb/")
                .outputName(data.title()).build());
            System.out.println("Ok!");
          } catch (StickerApiException | IOException e) {
            System.out.println("Fail: " + e.getMessage());
          }
        });
    System.out.println("...Finalizado!");
  }
}
