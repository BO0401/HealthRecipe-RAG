package com.zhangzb.healthrecipe.server.dto;

import lombok.Data;

@Data
public class ShoppingListCreateDTO {
    private String ingredientName;
    private java.math.BigDecimal quantity;
    private String unit;
}
