package com.zhangzb.healthrecipe.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_recipe")
public class SysRecipe {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String coverImg;
    private String steps;
    private Integer cookTime;
    private String difficulty;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
