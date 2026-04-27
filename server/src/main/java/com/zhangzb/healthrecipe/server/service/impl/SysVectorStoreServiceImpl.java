package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.SysVectorStore;
import com.zhangzb.healthrecipe.server.mapper.SysVectorStoreMapper;
import com.zhangzb.healthrecipe.server.service.SysVectorStoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysVectorStoreServiceImpl extends ServiceImpl<SysVectorStoreMapper, SysVectorStore> implements SysVectorStoreService {

    @Override
    public List<SysVectorStore> listAll() {
        return list(new LambdaQueryWrapper<SysVectorStore>().orderByDesc(SysVectorStore::getCreateTime));
    }
}
