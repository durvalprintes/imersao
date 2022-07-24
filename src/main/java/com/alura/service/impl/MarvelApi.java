package com.alura.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.alura.exception.StickerApiException;
import com.alura.model.Endpoint;
import com.alura.model.ParamSticker;
import com.alura.model.marvel.Character;
import com.alura.model.marvel.PaginateCharacters;
import com.alura.service.StickerApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Getter
@SuppressWarnings("squid:S106")
public class MarvelApi implements StickerApi {

  private static final UUID ID_REQUEST = UUID.randomUUID();
  private String publicKey;
  private String privateKey;
  private PaginateCharacters characters;

  public MarvelApi(Endpoint endpoint, String publicKey, String privateKey) throws StickerApiException {
    this.publicKey = publicKey;
    this.privateKey = privateKey;
    this.characters = getData(endpoint.getUrl());
  }

  private PaginateCharacters getData(String url) throws StickerApiException {
    try {
      var json = this.jsonFromGet(String.format("%s?ts=%s&apikey=%s&hash=%s&limit=100", url, ID_REQUEST, publicKey,
          new BigInteger(1, MessageDigest.getInstance("MD5").digest((ID_REQUEST + privateKey + publicKey).getBytes()))
              .toString(16)));
      var jsonNode = new ObjectMapper().readTree(json).get("data");
      return PaginateCharacters.builder()
          .offset(jsonNode.get("offset").asInt())
          .limit(jsonNode.get("limit").asInt())
          .total(jsonNode.get("total").asInt())
          .count(jsonNode.get("count").asInt())
          .characters(
              StreamSupport.stream(jsonNode.get("results").spliterator(), false)
                  .map(node -> Character.builder()
                      .id(node.get("id").asInt())
                      .name(node.get("name").asText())
                      .description(node.get("description").asText())
                      .thumbnailPath(node.get("thumbnail").get("path").asText())
                      .thumbnailExtension(node.get("thumbnail").get("extension").asText())
                      .resourceURI(node.get("resourceURI").asText())
                      .build())
                  .collect(Collectors.toList()))
          .build();
    } catch (StickerApiException | JsonProcessingException | NoSuchAlgorithmException e) {
      throw new StickerApiException("Erro ao instanciar implementacao da API MARVEL.", e);
    }
  }

  @Override
  public void limitData(int max) {
    // TODO Auto-generated method stub

  }

  @Override
  public void printData() {
    printField("\033[41m", "Marvel Characters");
    this.characters.getCharacters().stream()
        .forEach(character -> {
          printField("\033[1;37m", "Name: " + character.getName());
          printField("\033[1;37m",
              "Thumbnail: " + character.getThumbnailPath() + "." + character.getThumbnailExtension());
        });

  }

  @Override
  public void updateDataWithInput(InputStream input) throws StickerApiException {
    // TODO Auto-generated method stub

  }

  @Override
  public void generateStickers() {
    System.out.println("\nIniciando geração de Stickers da MARVEL...");
    this.characters.getCharacters().stream().forEach(character -> {
      try {
        System.out.print(character.getName() + "... ");
        createSticker(ParamSticker.builder()
            .image(new URL(character.getThumbnailPath() + "." + character.getThumbnailExtension()).openStream())
            .targetWidth(1000)
            .targetHeight(1500)
            .text("IMERSÃO JAVA")
            .fontName("Impact")
            .fontSize(128)
            .outputPath("data/image/sticker/")
            .outputName(character.getName()).build());
        System.out.println("Ok!");
      } catch (StickerApiException | IOException e) {
        System.out.println("Fail: " + e.getMessage());
      }
    });
    System.out.println("...Finalizado!");
  }

}
