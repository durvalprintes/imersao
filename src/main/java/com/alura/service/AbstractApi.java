package com.alura.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.alura.exception.StickerApiException;
import com.alura.model.ContentSticker;
import com.alura.model.RatingContent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("squid:S106")
public abstract class AbstractApi implements StickerApi {

  private List<ContentSticker> data;

  @Override
  public void limitData(int max) {
    if (max < data.size())
      this.data = this.data.subList(0, max);
  }

  private void printDataField(String format, String field) {
    System.out.println(format + field + "\033[0m");
  }

  @Override
  public void printData() {
    for (ContentSticker content : this.data) {
      printDataField("\033[1;37m", "\nTitulo: " + content.title());
      printDataField("\033[1;37m", "Image: " + content.urlImage());
      if (Optional.ofNullable(content.rating()).isPresent() && !content.rating().isBlank()) {
        printDataField("\033[0;105m", "Classificacao: " + content.rating());
        printDataField("\033[1;33m", "\u2605 ".repeat((int) Math.round(Double.valueOf(content.rating()))));
      } else {
        printDataField("\033[0;105m", "Sem classificacao");
      }
      Optional.ofNullable(content.myRating()).ifPresent(
          rating -> printDataField("\033[1;31m", "Minha nota: " + rating));
    }
  }

  @Override
  public void updateDataWithInput(InputStream inputRating) throws StickerApiException {
    try {
      List<RatingContent> jsonRating = new ObjectMapper().readValue(inputRating,
          new TypeReference<List<RatingContent>>() {
          });
      if (!jsonRating.isEmpty()) {
        setData(getData().stream()
            .map(content -> {
              Optional<RatingContent> personalRating = jsonRating.stream()
                  .filter(rating -> rating.title().equals(content.title()))
                  .findFirst();
              return personalRating.isPresent() ? new ContentSticker(content.title(), content.urlImage(),
                  content.rating(), personalRating.get().rating().toString()) : content;
            })
            .toList());
      }
    } catch (IOException e) {
      throw new StickerApiException("Erro ao inserir as notas personalizadas.", e);
    }
  }

}
