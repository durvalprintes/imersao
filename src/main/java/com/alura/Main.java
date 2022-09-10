package com.alura;

import java.io.FileNotFoundException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.alura.config.StickerConfig;
import com.alura.exception.StickerApiException;
import com.alura.model.Endpoint;

public class Main {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                StickerConfig.class);) {
            applicationContext.getBean(StickerApp.class).callApi(Endpoint.MOST_POPULAR_TV);
        } catch (StickerApiException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
