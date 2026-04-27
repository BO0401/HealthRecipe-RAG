package com.zhangzb.healthrecipe.server.controller;

import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.InventoryCreateDTO;
import com.zhangzb.healthrecipe.server.dto.InventoryUpdateDTO;
import com.zhangzb.healthrecipe.server.dto.InventoryVO;
import com.zhangzb.healthrecipe.server.entity.SysIngredient;
import com.zhangzb.healthrecipe.server.entity.SysInventory;
import com.zhangzb.healthrecipe.server.service.SysIngredientService;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private SysInventoryService inventoryService;

    @Mock
    private SysIngredientService ingredientService;

    @InjectMocks
    private InventoryController inventoryController;

    @Test
    void list_shouldReturnInventory() {
        when(inventoryService.listByUserId(1L)).thenReturn(List.of(new SysInventory()));

        Result<List<InventoryVO>> result = inventoryController.list();

        assertTrue(result.getCode() == 200);
        assertEquals(1, result.getData().size());
    }

    @Test
    void add_shouldCreateInventory() {
        InventoryCreateDTO dto = new InventoryCreateDTO();
        dto.setIngredientName("番茄");
        dto.setQuantity(BigDecimal.TEN);
        dto.setUnit("个");
        dto.setExpireDate(LocalDate.now().plusDays(7));

        SysIngredient ingredient = new SysIngredient();
        ingredient.setId(1L);
        ingredient.setName("番茄");
        ingredient.setCategory("蔬菜");
        when(ingredientService.getOrCreate("番茄", null, "个")).thenReturn(ingredient);
        when(inventoryService.save(any())).thenReturn(true);

        Result<InventoryVO> result = inventoryController.add(dto);

        assertTrue(result.getCode() == 200);
        assertEquals("番茄", result.getData().getIngredientName());
        verify(inventoryService).save(any());
    }

    @Test
    void update_shouldModifyItem_whenExists() {
        SysInventory existing = new SysInventory();
        existing.setId(1L);
        existing.setQuantity(BigDecimal.ONE);
        when(inventoryService.getById(1L)).thenReturn(existing);

        InventoryUpdateDTO dto = new InventoryUpdateDTO();
        dto.setId(1L);
        dto.setQuantity(BigDecimal.TEN);

        Result<InventoryVO> result = inventoryController.update(dto);

        assertTrue(result.getCode() == 200);
        verify(inventoryService).updateById(any());
    }

    @Test
    void update_shouldReturn404_whenNotExists() {
        when(inventoryService.getById(999L)).thenReturn(null);

        InventoryUpdateDTO dto = new InventoryUpdateDTO();
        dto.setId(999L);

        Result<InventoryVO> result = inventoryController.update(dto);

        assertTrue(result.getCode() == 404);
        verify(inventoryService, never()).updateById(any());
    }

    @Test
    void remove_shouldReturnSuccess_whenExists() {
        when(inventoryService.removeById(1L)).thenReturn(true);

        Result<Void> result = inventoryController.remove(1L);

        assertTrue(result.getCode() == 200);
    }

    @Test
    void expiringSoon_shouldReturnItems() {
        when(inventoryService.findExpiringSoon(1L, 3)).thenReturn(List.of(new SysInventory()));

        Result<List<InventoryVO>> result = inventoryController.expiringSoon();

        assertTrue(result.getCode() == 200);
        assertEquals(1, result.getData().size());
    }
}
