package com.alura.model;

import java.awt.Color;
import java.awt.Font;
import java.util.TreeMap;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Sticker {

  private String output;
  private TreeMap<Integer, String> rangeText;
  private String font;
  private Integer size;

  @Builder.Default
  private int style = Font.BOLD;

  @Builder.Default
  private Color colorText = Color.BLACK;

  @Builder.Default
  private Float stroke = 5F;

  @Builder.Default
  private Color colorStroke = Color.YELLOW;

  @Builder.Default
  private Integer heightPlus = 200;

  @Builder.Default
  private String format = "png";

}
