package com.alura.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.alura.exception.StickerApiException;
import com.alura.model.Endpoint;
import com.alura.model.Sticker;
import com.alura.model.imdb.Movie;
import com.alura.model.imdb.Poster;
import com.alura.model.imdb.Rating;
import com.alura.service.StickerApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("squid:S106")
public class ImdbApi implements StickerApi {

  private String key;
  private List<Movie> movies;
  private final TreeMap<Integer, String> ratingText = new TreeMap<>();

  public ImdbApi(Endpoint endpoint, String key) throws StickerApiException {
    this.key = key;
    this.movies = this.getData(endpoint);
  }

  private List<Movie> getData(Endpoint endpoint) throws StickerApiException {
    var mapper = new ObjectMapper();
    try {
      return mapper.treeToValue(
          mapper.readTree(jsonFromGet(endpoint.getUrl() + this.key)).get("items"),
          mapper.getTypeFactory().constructCollectionType(
              List.class,
              Movie.class));
    } catch (StickerApiException | JsonProcessingException e) {
      throw new StickerApiException("Erro ao instanciar implementacao da API IMDB.", e);
    }
  }

  @Override
  public void printData() {
    for (Movie movie : this.movies) {
      printField("\033[1;37m", "\nTitulo: " + movie.getTitle());
      printField("\033[1;37m", "Image: " + movie.getImage());
      Optional.ofNullable(movie.getImDbRating()).ifPresentOrElse(
          rating -> {
            printField("\033[0;105m", "Classificacao: " + rating);
            printField("\033[1;33m", "\u2605 ".repeat((int) Math.round(rating)));
          },
          () -> printField("\033[0;105m", "Sem classificacao"));
      Optional.ofNullable(movie.getMyRating()).ifPresent(
          rating -> printField("\033[1;31m", "Minha nota: " + rating));
    }
  }

  public void limitData(int max) {
    setMovies(this.movies.subList(0, max));
  }

  public void updateDataWithInput(InputStream inputRating) throws StickerApiException {
    try {
      List<Rating> listRating = new ObjectMapper().readValue(inputRating, new TypeReference<List<Rating>>() {
      });
      if (!listRating.isEmpty()) {
        this.movies = this.movies.stream()
            .map(movie -> {
              listRating.stream()
                  .filter(myRating -> myRating.getId().equals(movie.getId()))
                  .findFirst().ifPresent(rating -> movie.setMyRating(rating.getMyRating()));
              return movie;
            })
            .collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new StickerApiException("Erro ao inserir as notas personalizadas.", e);
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
    this.movies.stream().forEach(
        movie -> {
          try {
            System.out.print(movie.getTitle() + "... ");
            Double rating = Optional.ofNullable(movie.getImDbRating()).isPresent() ? movie.getImDbRating() : 0;
            String text = ratingText.floorEntry((int) Math.round(rating)).getValue();
            createSticker(Sticker.builder()
                .image(new URL(movie.getImage().replaceAll("\\._(.+).jpg$", ".jpg")).openStream())
                .targetWidth(1000)
                .targetHeight(1500)
                .text(text)
                .fontName("Impact")
                .fontSize(128)
                .isTop(ratingText.lastEntry().getValue().equals(text))
                .topImage("data/image/sticker/joinha.png")
                .outputPath("data/image/sticker/")
                .outputName(movie.getTitle()).build());
            System.out.println("Ok!");
          } catch (StickerApiException | IOException e) {
            System.out.println("Fail: " + e.getMessage());
          }
        });
    System.out.println("...Finalizado!");
  }

  /**
   * @param movie
   * @return The Poster object at the IMDB Poster Endpoint
   * @see Poster
   */
  private Poster getMoviePoster(Movie movie) throws StickerApiException {
    try {
      var mapper = new ObjectMapper();
      var url = String.format("%s%s/%s", Endpoint.POSTERS.getUrl(), this.key, movie.getId());
      Poster poster = mapper.treeToValue(
          mapper
              .readTree(jsonFromGet(url))
              .get("posters")
              .get(0),
          Poster.class);
      poster.setIdMovie(movie.getId());
      return poster;
    } catch (StickerApiException | IOException e) {
      throw new StickerApiException("Erro ao recuperar poster.", e);
    }
  }
}
