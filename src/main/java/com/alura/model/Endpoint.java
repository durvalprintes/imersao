package com.alura.model;

public enum Endpoint {
  IMDB_TOP_250_MOVIES("https://imdb-api.com/en/API/Top250Movies/"),
  IMDB_MOST_POPULAR_MOVIES("https://imdb-api.com/en/API/MostPopularMovies/"),
  IMDB_TOP_250_TV("https://imdb-api.com/en/API/Top250TVs/"),
  IMDB_MOST_POPULAR_TV("https://imdb-api.com/en/API/MostPopularTVs/"),
  IMDB_POSTERS("https://imdb-api.com/en/API/Posters/");

  private String url;

  Endpoint(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

}
