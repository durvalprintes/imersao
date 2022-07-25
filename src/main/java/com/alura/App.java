package com.alura;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.alura.exception.StickerApiException;
import com.alura.model.Endpoint;
import com.alura.service.StickerApi;
import com.alura.service.impl.ImdbApi;
import com.alura.service.impl.MarvelApi;

@SuppressWarnings("squid:S125")
public class App {

    private static StickerApi getInstance(Endpoint endpoint) {
        if (Endpoint.isImdb(endpoint)) {
            return new ImdbApi(endpoint, System.getProperty("imdb_key"));
        }
        if (Endpoint.isMarvel(endpoint)) {
            return new MarvelApi(endpoint,
                    System.getProperty("marvel_public_key"),
                    System.getProperty("marvel_private_key"));
        }
        throw new StickerApiException("Endpoint não mapeado a nenhuma implementacao da StickerAPI.");
    }

    public static void main(String[] args) {
        try {
            StickerApi api = getInstance(Endpoint.MOST_POPULAR_TV);
            api.limitData(10);
            api.updateDataWithInput(new FileInputStream(new File("data/rating.json")));
            api.printData();
            api.generateStickers();
        } catch (StickerApiException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
