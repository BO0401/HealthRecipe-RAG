package com.zhangzb.healthrecipe.server.service;

import com.zhangzb.healthrecipe.server.entity.SysInventory;
import com.zhangzb.healthrecipe.server.mapper.SysInventoryMapper;
import com.zhangzb.healthrecipe.server.service.impl.SysInventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysInventoryServiceTest {

    @Mock
    private SysInventoryMapper inventoryMapper;

    @InjectMocks
    private SysInventoryServiceImpl inventoryService;

    @Test
    void listByUserId_shouldReturnUserInventory() {
        when(inventoryMapper.selectList(any())).thenReturn(Arrays.asList(new SysInventory(), new SysInventory()));

        List<SysInventory> result = inventoryService.listByUserId(1L);

        assertEquals(2, result.size());
        verify(inventoryMapper).selectList(any());
    }

    @Test
    void findExpiringSoon_shouldReturnItemsWithinDays() {
        SysInventory item1 = new SysInventory();
        item1.setExpireDate(LocalDate.now().plusDays(1));
        SysInventory item2 = new SysInventory();
        item2.setExpireDate(LocalDate.now().plusDays(5));

        when(inventoryMapper.selectList(any())).thenReturn(Arrays.asList(item1));

        List<SysInventory> result = inventoryService.findExpiringSoon(1L, 3);

        assertEquals(1, result.size());
        verify(inventoryMapper).selectList(any());
    }

    @Test
    void countByUserId_shouldReturnCount() {
        when(inventoryMapper.selectCount(any())).thenReturn(5L);

        long count = inventoryService.countByUserId(1L);

        assertEquals(5L, count);
        verify(inventoryMapper).selectCount(any());
    }
}
