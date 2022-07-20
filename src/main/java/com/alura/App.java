package com.alura;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.alura.model.Endpoint;
import com.alura.service.WebService;

public class App {

    public static void main(String[] args) {
        try {
            WebService imdb = new WebService(Endpoint.IMDB_MOST_POPULAR_TV, System.getProperty("key"));
            imdb.updateWithMyRating(new FileInputStream(new File("data/rating.json")));
            imdb.printMovies();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
