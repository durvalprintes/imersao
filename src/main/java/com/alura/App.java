package com.alura;

import java.io.IOException;

import com.alura.model.Endpoint;
import com.alura.service.impl.ImdbService;

@SuppressWarnings("squid:S125")
public class App {

    public static void main(String[] args) {
        try {
            ImdbService api = new ImdbService(Endpoint.IMDB_MOST_POPULAR_TV, System.getProperty("key"));
            // api.updateMoviesWithMyRating(new FileInputStream(new
            // File("data/rating.json")));
            api.shrinkListMovies(5);
            // api.printMovies();
            api.generateStickerPosterImage("data/image/sticker/");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
