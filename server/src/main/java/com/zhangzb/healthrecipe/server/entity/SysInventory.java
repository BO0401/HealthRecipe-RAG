package com.zhangzb.healthrecipe.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sys_inventory")
public class SysInventory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long ingredientId;
    private java.math.BigDecimal quantity;
    private String unit;
    private LocalDate expireDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
