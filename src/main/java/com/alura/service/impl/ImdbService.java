package com.alura.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.alura.model.Endpoint;
import com.alura.model.Movie;
import com.alura.model.Poster;
import com.alura.model.Rating;
import com.alura.model.Sticker;
import com.alura.service.WebService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("squid:S106")
public class ImdbService implements WebService {

  private String key;
  private List<Movie> movies;
  private final TreeMap<Integer, String> rangeRatingText = new TreeMap<>();

  public ImdbService(Endpoint endpoint, String key) throws IOException, InterruptedException {
    this.key = key;
    this.movies = this.getListImdb(endpoint);
  }

  private List<Movie> getListImdb(Endpoint endpoint) throws IOException, InterruptedException {
    var mapper = new ObjectMapper();
    return mapper.treeToValue(
        mapper.readTree(jsonFromGet(endpoint.getUrl() + this.key)).get("items"),
        mapper.getTypeFactory().constructCollectionType(
            List.class,
            Movie.class));
  }

  @Override
  public void print() {
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

  public void shrinkListMovies(int max) {
    setMovies(this.movies.subList(0, max));
  }

  public void updateMoviesWithMyRating(InputStream inputRating) throws IOException {
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
  }

  @Override
  public void generateStickerImage(Sticker param) {
    rangeRatingText.put(0, "AVALIAR");
    rangeRatingText.put(1, "PESSIMO");
    rangeRatingText.put(4, "RUIM");
    rangeRatingText.put(6, "PASSATEMPO");
    rangeRatingText.put(8, "TOP");

    System.out.println("Iniciando geração...");
    this.movies.stream().forEach(
        movie -> {
          try {
            System.out.print(movie.getTitle() + "... ");
            Double rating = Optional.ofNullable(movie.getImDbRating()).isPresent() ? movie.getImDbRating() : 0;
            String text = rangeRatingText.floorEntry((int) Math.round(rating)).getValue();

            param.setImage(new URL(movie.getImage().replaceAll("\\._(.+).jpg$", ".jpg")).openStream());
            param.setText(text);
            param.setTop(false);
            if (rangeRatingText.lastEntry().getValue().equals(text)) {
              param.setTop(true);
            }
            param.setOutputName(movie.getTitle());
            createSticker(param);
            System.out.println("OK!");
          } catch (IOException e) {
            System.out.println("Fail!");
          }
        });
    System.out.println("...Finalizado!");
  }

  /**
   * @param movie
   * @return The Poster object at the IMDB Poster Endpoint
   * @see Poster
   */
  private Poster getMoviePoster(Movie movie) {
    try {
      var mapper = new ObjectMapper();
      var url = String.format("%s%s/%s", Endpoint.IMDB_POSTERS.getUrl(), this.key, movie.getId());
      Poster poster = mapper.treeToValue(
          mapper
              .readTree(jsonFromGet(url))
              .get("posters")
              .get(0),
          Poster.class);
      poster.setIdMovie(movie.getId());
      return poster;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return null;
  }
}
