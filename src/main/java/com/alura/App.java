package com.alura;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.alura.model.Endpoint;
import com.alura.service.StickerApi;
import com.alura.service.impl.ImdbApi;
import com.alura.service.impl.MarvelApi;

@SuppressWarnings("squid:S125")
public class App {

    public static void main(String[] args) {
        try {
            StickerApi imdb = new ImdbApi(Endpoint.IMDB_MOST_POPULAR_TV,
                    System.getProperty("imdb_key"));
            imdb.shrinkList(0, 1);
            imdb.generateStickerImage();

            StickerApi marvel = new MarvelApi(Endpoint.MARVEL_COMIC_CHARACTERS,
                    System.getProperty("marvel_public_key"),
                    System.getProperty("marvel_private_key"));
            marvel.print();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
