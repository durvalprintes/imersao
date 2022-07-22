package com.alura.model.imdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Poster {

  private String idMovie;
  private String id;
  private String link;
  private Double aspectRatio;
  private Integer width;
  private Integer length;

}
