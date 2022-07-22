package com.alura.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.alura.model.Endpoint;
import com.alura.model.marvel.Character;
import com.alura.model.marvel.PaginateCharacters;
import com.alura.service.StickerApi;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Getter
@SuppressWarnings("squid:S106")
public class MarvelApi implements StickerApi {

  private static final UUID ID_REQUEST = UUID.randomUUID();
  private String publicKey;
  private String privateKey;
  private PaginateCharacters characters;

  public MarvelApi(Endpoint endpoint, String publicKey, String privateKey)
      throws IOException, InterruptedException, NoSuchAlgorithmException {
    this.publicKey = publicKey;
    this.privateKey = privateKey;
    this.characters = getResponseData(endpoint.getUrl());
  }

  private PaginateCharacters getResponseData(String url)
      throws IOException, InterruptedException, NoSuchAlgorithmException {
    var json = this.jsonFromGet(String.format("%s?ts=%s&apikey=%s&hash=%s", url, ID_REQUEST, publicKey,
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
  }

  @Override
  public void shrinkList(int min, int max) {
    // TODO Auto-generated method stub

  }

  @Override
  public void print() {
    printField("\033[41m", "Marvel Characters");
    this.characters.getCharacters().stream()
        .forEach(character -> {
          printField("\033[1;37m", "Name: " + character.getName());
          printField("\033[1;37m",
              "Thumbnail: " + character.getThumbnailPath() + "." + character.getThumbnailExtension());
        });

  }

  @Override
  public void updateListWithInput(InputStream input) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public void generateStickerImage() {
    // TODO Auto-generated method stub

  }

}
