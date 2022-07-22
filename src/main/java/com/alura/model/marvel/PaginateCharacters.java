package com.alura.model.marvel;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaginateCharacters {
  private Integer offset;
  private Integer limit;
  private Integer total;
  private Integer count;
  private List<Character> characters;
}
