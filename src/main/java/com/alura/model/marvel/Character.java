package com.alura.model.marvel;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Character {
  private Integer id;
  private String name;
  private String description;
  private String thumbnailPath;
  private String thumbnailExtension;
  private String resourceURI;
}
