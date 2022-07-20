package com.alura.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alura.model.Endpoint;
import com.alura.model.Movie;
import com.alura.model.Rating;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings({ "squid:S106", "squid:S2325" })
@Getter
@Setter
public class WebService {

  private List<Movie> movies;

  public WebService(Endpoint api, String key) throws IOException, InterruptedException {
    var mapper = new ObjectMapper();
    this.movies = mapper.treeToValue(
        mapper.readTree(jsonFromApi(api.getUrl() + key)).get("items"),
        mapper.getTypeFactory().constructCollectionType(
            List.class,
            Movie.class));
  }

  private String jsonFromApi(String api) throws IOException, InterruptedException {
    return HttpClient.newHttpClient()
        .send(HttpRequest.newBuilder(URI.create(api)).GET().build(),
            BodyHandlers.ofString())
        .body();
  }

  private void printField(String format, String field) {
    System.out.println(format + field + "\033[0m");
  }

  private String countStarRating(Double rating) {
    StringBuilder star = new StringBuilder();
    for (int i = 0; i < Math.round(rating); i++) {
      star.append("\u2605 ");
    }
    return star.toString();
  }

  public void printMovies() {
    for (Movie movie : this.movies) {
      printField("\033[1;37m", "\nTitulo: " + movie.getTitle());
      printField("\033[1;37m", "Image: " + movie.getImage());
      Optional.ofNullable(movie.getImDbRating()).ifPresentOrElse(
          rating -> {
            printField("\033[0;105m", "Classificacao: " + rating);
            printField("\033[1;33m", countStarRating(rating));
          },
          () -> printField("\033[0;105m", "Sem classificacao"));
      Optional.ofNullable(movie.getMyRating()).ifPresent(
          rating -> printField("\033[1;31m", "Minha nota: " + rating));
    }
  }

  public void updateWithMyRating(InputStream inputRating) throws IOException {
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
}
