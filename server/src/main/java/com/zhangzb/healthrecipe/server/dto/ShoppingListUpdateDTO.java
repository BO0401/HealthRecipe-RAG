package com.zhangzb.healthrecipe.server.dto;

import lombok.Data;

@Data
public class ShoppingListUpdateDTO {
    private Long id;
    private java.math.BigDecimal quantity;
    private String unit;
    private Integer status;
}
