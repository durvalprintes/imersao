package com.alura.model;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParamSticker {

  private InputStream image;
  private String topImage;
  private String text;
  private String outputPath;
  private String outputName;
  private String fontName;
  private Integer fontSize;
  private int targetWidth;
  private int targetHeight;

  @Builder.Default
  private int fontStyle = Font.BOLD;

  @Builder.Default
  private Color textColor = Color.BLACK;

  @Builder.Default
  private Float strokeNumber = 5F;

  @Builder.Default
  private Color strokeColor = Color.YELLOW;

  @Builder.Default
  private Integer heightPlus = 200;

  @Builder.Default
  private String outputFormat = "png";

  @Builder.Default
  private boolean isTop = false;
}
