package com.zhangzb.healthrecipe.server.service;

import com.zhangzb.healthrecipe.server.entity.SysIngredient;
import com.zhangzb.healthrecipe.server.mapper.SysIngredientMapper;
import com.zhangzb.healthrecipe.server.service.impl.SysIngredientServiceImpl;
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
class SysIngredientServiceTest {

    @Mock
    private SysIngredientMapper ingredientMapper;

    @InjectMocks
    private SysIngredientServiceImpl ingredientService;

    @Test
    void findByName_shouldReturnIngredient() {
        SysIngredient expected = new SysIngredient();
        expected.setName("番茄");
        when(ingredientMapper.selectOne(any())).thenReturn(expected);

        SysIngredient result = ingredientService.findByName("番茄");

        assertNotNull(result);
        assertEquals("番茄", result.getName());
        verify(ingredientMapper).selectOne(any());
    }

    @Test
    void findByName_shouldReturnNull_whenNotFound() {
        when(ingredientMapper.selectOne(any())).thenReturn(null);

        SysIngredient result = ingredientService.findByName("不存在的食材");

        assertNull(result);
        verify(ingredientMapper).selectOne(any());
    }

    @Test
    void getOrCreate_shouldReturnExisting() {
        SysIngredient existing = new SysIngredient();
        existing.setId(1L);
        existing.setName("番茄");
        when(ingredientMapper.selectOne(any())).thenReturn(existing);

        SysIngredient result = ingredientService.getOrCreate("番茄", "蔬菜", "g");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(ingredientMapper, never()).insert(any(SysIngredient.class));
    }

    @Test
    void getOrCreate_shouldCreateNew_whenNotExists() {
        when(ingredientMapper.selectOne(any())).thenReturn(null);
        when(ingredientMapper.insert(any(SysIngredient.class))).thenReturn(1);

        SysIngredient result = ingredientService.getOrCreate("新食材", "水果", "g");

        assertNotNull(result);
        assertEquals("新食材", result.getName());
        verify(ingredientMapper).insert(any(SysIngredient.class));
    }

    @Test
    void listByCategory_shouldReturnFiltered() {
        when(ingredientMapper.selectList(any())).thenReturn(Arrays.asList(new SysIngredient()));

        List<SysIngredient> result = ingredientService.listByCategory("蔬菜");

        assertEquals(1, result.size());
        verify(ingredientMapper).selectList(any());
    }
}
