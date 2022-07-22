package com.alura;

import java.io.IOException;

import com.alura.model.Endpoint;
import com.alura.model.Sticker;
import com.alura.service.impl.ImdbService;

@SuppressWarnings("squid:S125")
public class App {

    public static void main(String[] args) {
        try {
            ImdbService api = new ImdbService(Endpoint.IMDB_MOST_POPULAR_TV, System.getProperty("key"));
            api.shrinkListMovies(50);
            api.generateStickerImage(
                    Sticker.builder()
                            .outputPath("data/image/sticker/")
                            .targetWidth(1000)
                            .targetHeight(1500)
                            .fontName("Impact")
                            .fontSize(128)
                            .topImage("data/image/sticker/joinha.png")
                            .build());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
