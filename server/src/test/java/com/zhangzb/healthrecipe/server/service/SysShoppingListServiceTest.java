package com.zhangzb.healthrecipe.server.service;

import com.zhangzb.healthrecipe.server.entity.SysShoppingList;
import com.zhangzb.healthrecipe.server.mapper.SysShoppingListMapper;
import com.zhangzb.healthrecipe.server.service.impl.SysShoppingListServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysShoppingListServiceTest {

    @Mock
    private SysShoppingListMapper shoppingListMapper;

    @InjectMocks
    private SysShoppingListServiceImpl shoppingListService;

    @Test
    void listByUserId_shouldReturnUserItems() {
        when(shoppingListMapper.selectList(any())).thenReturn(Arrays.asList(new SysShoppingList()));

        List<SysShoppingList> result = shoppingListService.listByUserId(1L);

        assertEquals(1, result.size());
        verify(shoppingListMapper).selectList(any());
    }

    @Test
    void listByStatus_shouldFilterByStatus() {
        SysShoppingList pending = new SysShoppingList();
        pending.setStatus(0);
        SysShoppingList purchased = new SysShoppingList();
        purchased.setStatus(1);

        when(shoppingListMapper.selectList(any())).thenReturn(Arrays.asList(pending));

        List<SysShoppingList> result = shoppingListService.listByStatus(1L, 0);

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getStatus());
        verify(shoppingListMapper).selectList(any());
    }

    @Test
    void countPending_shouldReturnPendingCount() {
        when(shoppingListMapper.selectCount(any())).thenReturn(3L);

        long count = shoppingListService.countPending(1L);

        assertEquals(3L, count);
        verify(shoppingListMapper).selectCount(any());
    }

    @Test
    void clearPurchased_shouldDeletePurchasedItems() {
        when(shoppingListMapper.delete(any())).thenReturn(2);

        shoppingListService.clearPurchased(1L);

        verify(shoppingListMapper).delete(any());
    }
}
