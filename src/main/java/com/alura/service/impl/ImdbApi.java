package com.alura.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.TreeMap;
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
public class ImdbApi extends AbstractDataApi {

  private String key;
  private final TreeMap<Integer, String> ratingText = new TreeMap<>();

  public ImdbApi(Endpoint endpoint, String key) throws StickerApiException {
    this.key = key;
    this.consumeApi(endpoint);
  }

  private void consumeApi(Endpoint endpoint) throws StickerApiException {
    try {
      var jsonNode = new ObjectMapper().readTree(jsonFromGet(endpoint.getUrl() + this.key));
      setData(StreamSupport.stream(jsonNode.get("items").spliterator(), false)
          .map(node -> ContentSticker.builder()
              .title(node.get("title").asText())
              .urlImage(node.get("image").asText())
              .rating(node.get("imDbRating").asText())
              .build())
          .collect(Collectors.toList()));
    } catch (StickerApiException | JsonProcessingException e) {
      throw new StickerApiException("Erro ao instanciar implementacao da API IMDB.", e);
    }
  }

  @Override
  public void generateStickers() throws StickerApiException {
    ratingText.put(0, "AVALIAR");
    ratingText.put(1, "PESSIMO");
    ratingText.put(4, "RUIM");
    ratingText.put(6, "PASSATEMPO");
    ratingText.put(8, "TOP");

    System.out.println("\nIniciando geração de Stickers do IMDB...");
    getData().stream().forEach(
        data -> {
          try {
            System.out.print(data.title() + "... ");
            Double rating = !data.rating().isBlank() ? Double.valueOf(data.rating()) : 0;
            String text = ratingText.floorEntry((int) Math.round(rating)).getValue();
            createSticker(ParamSticker.builder()
                .image(new URL(data.urlImage().replaceAll("\\._(.+).jpg$", ".jpg")).openStream())
                .targetWidth(1000)
                .targetHeight(1500)
                .text(text)
                .fontName("Impact")
                .fontSize(128)
                .isTop(ratingText.lastEntry().getValue().equals(text))
                .topImage("data/image/sticker/joinha.png")
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
