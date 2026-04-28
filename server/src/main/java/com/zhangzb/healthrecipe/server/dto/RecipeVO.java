package com.zhangzb.healthrecipe.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecipeVO {
    private Long id;
    private String name;
    private String coverImg;
    private Integer calories;
    private Integer cookTime;
    private String difficulty;
    private List<String> tags;
    private String description;
    private String steps;
}
