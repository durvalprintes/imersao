package com.alura.model.imdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {

  private String id;
  private Long rank;
  private String title;
  private String fullTitle;
  private int year;
  private String image;
  private String crew;
  private Double imDbRating;
  private Long imDbRatingCount;
  private Double myRating;

}
