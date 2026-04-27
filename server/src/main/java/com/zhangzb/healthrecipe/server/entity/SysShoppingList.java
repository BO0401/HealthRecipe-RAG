package com.zhangzb.healthrecipe.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_shopping_list")
public class SysShoppingList {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String ingredientName;
    private java.math.BigDecimal quantity;
    private String unit;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
