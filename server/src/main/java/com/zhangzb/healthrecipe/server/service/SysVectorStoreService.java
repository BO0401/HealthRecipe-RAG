package com.zhangzb.healthrecipe.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangzb.healthrecipe.server.entity.SysVectorStore;

import java.util.List;

public interface SysVectorStoreService extends IService<SysVectorStore> {
    List<SysVectorStore> listAll();
}
