package com.zhangzb.healthrecipe.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_vector_store")
public class SysVectorStore {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private String metadata;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
