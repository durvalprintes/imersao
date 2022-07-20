package com.alura;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("squid:S106")
public class App {

    public static Movie updateMovie(Movie movie, Rating rating) {
        movie.setMyRating(rating.getMyRating());
        return movie;
    }

    public static List<Movie> findMyRating(List<Movie> listMovies) throws IOException {
        var mapper = new ObjectMapper();
        List<Rating> listRating = mapper.readValue(
                Paths.get("./data/rating.json").toFile(),
                new TypeReference<List<Rating>>() {
                });
        return listMovies.stream()
                .map(movie -> {
                    Movie updatedMovie = movie;
                    listRating.stream()
                            .filter(myRating -> myRating.getId().equals(movie.getId()))
                            .findFirst().ifPresent(rating -> updateMovie(updatedMovie, rating));
                    return updatedMovie;
                })
                .collect(Collectors.toList());
    }

    public static String getMovies(String api) throws IOException, InterruptedException {
        return HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder(URI.create(api)).GET().build(),
                        BodyHandlers.ofString())
                .body();
    }

    public static String customText(String custom, String text) {
        return custom + text + "\033[0m";
    }

    public static String countStar(Double rating) {
        StringBuilder star = new StringBuilder();
        for (int i = 0; i < Math.round(rating); i++) {
            star.append("\u2605 ");
        }
        return star.toString();
    }

    public static void printMovies(List<Movie> movies) {
        for (Movie movie : movies) {
            System.out.println("Titulo: " + customText("\033[1;37m", movie.getTitle()));
            System.out.println("Image: " + customText("\033[1;37m", movie.getImage()));
            Optional.ofNullable(movie.getImDbRating()).ifPresentOrElse(
                    rating -> {
                        System.out.println(customText("\033[0;105m", "Classificacao: " + rating));
                        System.out.println(customText("\033[1;33m", countStar(rating)));
                    },
                    () -> System.out.println(customText("\033[0;105m", "Sem Classificao")));
            Optional.ofNullable(movie.getMyRating()).ifPresent(
                    rating -> System.out.println(customText("\033[1;31m", "Minha nota: " + rating)));
        }
    }

    public static void main(String[] args) {
        try {
            PropertiesConfiguration property = new PropertiesConfiguration();
            property.load("app.properties");

            String url = property.getString("top.250.movies") + System.getProperty("key");

            var mapper = new ObjectMapper();
            List<Movie> movies = mapper.treeToValue(
                    mapper.readTree(getMovies(url)).get("items"),
                    mapper.getTypeFactory().constructCollectionType(
                            List.class,
                            Movie.class));

            printMovies(findMyRating(movies.stream().limit(5).collect(Collectors.toList())));

        } catch (IOException | ConfigurationException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
