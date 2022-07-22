package com.alura;

import java.io.IOException;

import com.alura.model.Endpoint;
import com.alura.service.StickerApi;
import com.alura.service.impl.ImdbApi;

@SuppressWarnings("squid:S125")
public class App {

    public static void main(String[] args) {
        try {
            StickerApi api = new ImdbApi(Endpoint.IMDB_MOST_POPULAR_TV, System.getProperty("key"));
            api.shrinkList(20, 40);
            api.generateStickerImage();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
