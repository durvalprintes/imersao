package com.alura;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alura.exception.StickerApiException;
import com.alura.model.Endpoint;
import com.alura.service.StickerApi;

@Component
public class StickerApp {

    @Autowired
    private List<StickerApi> listApi;

    public void callApi(Endpoint endpoint) throws StickerApiException, FileNotFoundException {
        StickerApi api = listApi.stream()
                .filter(stickerApi -> stickerApi.accept(endpoint)).findAny()
                .orElseThrow(() -> new StickerApiException(
                        "Endpoint não mapeado a nenhuma implementação da StickerAPI."));
        api.consume(endpoint);
        api.limitData(10);
        api.updateDataWithInput(new FileInputStream(new File("data/rating.json")));
        api.printData();
        api.generateStickers();
    }
}
