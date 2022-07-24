package com.alura.model;

import java.util.EnumSet;

public enum Endpoint {
  TOP_250_MOVIES("https://imdb-api.com/en/API/Top250Movies/"),
  MOST_POPULAR_MOVIES("https://imdb-api.com/en/API/MostPopularMovies/"),
  TOP_250_TV("https://imdb-api.com/en/API/Top250TVs/"),
  MOST_POPULAR_TV("https://imdb-api.com/en/API/MostPopularTVs/"),
  POSTERS("https://imdb-api.com/en/API/Posters/"),
  CHARACTERS("https://gateway.marvel.com:443/v1/public/characters"),
  COMICS("https://gateway.marvel.com:443/v1/public/comics");

  private static final EnumSet<Endpoint> IMDB = EnumSet.of(TOP_250_MOVIES, MOST_POPULAR_MOVIES, TOP_250_TV,
      MOST_POPULAR_TV);

  private static final EnumSet<Endpoint> MARVEL = EnumSet.of(CHARACTERS);

  private String url;

  Endpoint(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public static boolean isImdb(Endpoint endpoint) {
    return IMDB.contains(endpoint);
  }

  public static boolean isMarvel(Endpoint endpoint) {
    return MARVEL.contains(endpoint);
  }

}
