package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.SysInventory;
import com.zhangzb.healthrecipe.server.mapper.SysInventoryMapper;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import org.springframework.stereotype.Service;

@Service
public class SysInventoryServiceImpl extends ServiceImpl<SysInventoryMapper, SysInventory> implements SysInventoryService {
}
