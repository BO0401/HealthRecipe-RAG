package com.zhangzb.healthrecipe.server.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryVO {
    private Long id;
    private Long userId;
    private Long ingredientId;
    private String ingredientName;
    private String category;
    private java.math.BigDecimal quantity;
    private String unit;
    private LocalDate expireDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
