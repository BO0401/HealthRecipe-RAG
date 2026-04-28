package com.zhangzb.healthrecipe.server.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InventoryCreateDTO {
    private String ingredientName;
    private java.math.BigDecimal quantity;
    private String unit;
    private LocalDate expireDate;
}
