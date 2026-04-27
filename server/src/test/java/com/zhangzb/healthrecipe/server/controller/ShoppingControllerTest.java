package com.zhangzb.healthrecipe.server.controller;

import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.ShoppingListCreateDTO;
import com.zhangzb.healthrecipe.server.dto.ShoppingListUpdateDTO;
import com.zhangzb.healthrecipe.server.entity.SysShoppingList;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingControllerTest {

    @Mock
    private SysShoppingListService shoppingListService;

    @InjectMocks
    private ShoppingController shoppingController;

    @Test
    void list_shouldReturnShoppingList() {
        when(shoppingListService.listByUserId(1L)).thenReturn(List.of(new SysShoppingList()));

        Result<List<SysShoppingList>> result = shoppingController.list();

        assertTrue(result.getCode() == 200);
        assertEquals(1, result.getData().size());
    }

    @Test
    void add_shouldCreateItem() {
        ShoppingListCreateDTO dto = new ShoppingListCreateDTO();
        dto.setIngredientName("番茄");
        dto.setQuantity(BigDecimal.ONE);
        dto.setUnit("个");
        when(shoppingListService.save(any())).thenReturn(true);

        Result<SysShoppingList> result = shoppingController.add(dto);

        assertTrue(result.getCode() == 200);
        assertEquals("番茄", result.getData().getIngredientName());
        assertEquals(0, result.getData().getStatus());
    }

    @Test
    void update_shouldModifyItem_whenExists() {
        SysShoppingList existing = new SysShoppingList();
        existing.setId(1L);
        existing.setStatus(0);
        when(shoppingListService.getById(1L)).thenReturn(existing);

        ShoppingListUpdateDTO dto = new ShoppingListUpdateDTO();
        dto.setId(1L);
        dto.setStatus(1);

        Result<SysShoppingList> result = shoppingController.update(dto);

        assertTrue(result.getCode() == 200);
        assertEquals(1, result.getData().getStatus());
        verify(shoppingListService).updateById(any());
    }

    @Test
    void update_shouldReturn404_whenNotExists() {
        when(shoppingListService.getById(999L)).thenReturn(null);

        ShoppingListUpdateDTO dto = new ShoppingListUpdateDTO();
        dto.setId(999L);

        Result<SysShoppingList> result = shoppingController.update(dto);

        assertTrue(result.getCode() == 404);
        verify(shoppingListService, never()).updateById(any());
    }

    @Test
    void remove_shouldReturnSuccess_whenExists() {
        when(shoppingListService.removeById(1L)).thenReturn(true);

        Result<Void> result = shoppingController.remove(1L);

        assertTrue(result.getCode() == 200);
    }

    @Test
    void clearPurchased_shouldCallService() {
        doNothing().when(shoppingListService).clearPurchased(1L);

        Result<Void> result = shoppingController.clearPurchased();

        assertTrue(result.getCode() == 200);
        verify(shoppingListService).clearPurchased(1L);
    }

    @Test
    void generate_shouldReturnItems() {
        when(shoppingListService.listByIds(any())).thenReturn(List.of(new SysShoppingList()));

        Result<List<SysShoppingList>> result = shoppingController.generate(Map.of("recipeIds", List.of(1L, 2L)));

        assertTrue(result.getCode() == 200);
        assertEquals(1, result.getData().size());
    }
}
