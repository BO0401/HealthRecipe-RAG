package com.zhangzb.healthrecipe.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rel_recipe_ingredient")
public class RelRecipeIngredient {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recipeId;
    private Long ingredientId;
    private java.math.BigDecimal amount;
    private String unit;
    private LocalDateTime createTime;
}
