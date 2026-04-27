package com.zhangzb.healthrecipe.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecipeQueryDTO {
    private String bodyType;
    private List<String> allergens;
    private String keyword;
}
