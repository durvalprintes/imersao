package com.alura.model;

import lombok.Builder;

@Builder
public record ContentSticker(String title, String urlImage, String rating, String myRating) {

}
