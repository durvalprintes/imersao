package com.alura;

import java.io.IOException;
import java.util.TreeMap;

import com.alura.model.Endpoint;
import com.alura.model.Sticker;
import com.alura.service.impl.ImdbService;

@SuppressWarnings("squid:S125")
public class App {

    public static void main(String[] args) {
        try {
            ImdbService api = new ImdbService(Endpoint.IMDB_MOST_POPULAR_TV, System.getProperty("key"));
            // api.updateMoviesWithMyRating(new FileInputStream(new
            // File("data/rating.json")));
            TreeMap<Integer, String> range = new TreeMap<>();
            range.put(0, "AVALIAR");
            range.put(1, "PESSIMO");
            range.put(4, "RUIM");
            range.put(6, "PASSATEMPO");
            range.put(8, "TOP");

            api.shrinkListMovies(50);
            api.generateStickerPosterImage(
                    Sticker.builder()
                            .output("data/image/sticker/")
                            .font("Impact")
                            .size(111)
                            .rangeText(range).build());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
