package com.zhangzb.healthrecipe.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_ingredient")
public class SysIngredient {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private String unit;
    private java.math.BigDecimal kcalPer100g;
    private java.math.BigDecimal proteinG;
    private String imageUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
