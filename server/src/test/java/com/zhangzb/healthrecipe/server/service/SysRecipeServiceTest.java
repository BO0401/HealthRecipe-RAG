package com.zhangzb.healthrecipe.server.service;

import com.zhangzb.healthrecipe.server.entity.SysRecipe;
import com.zhangzb.healthrecipe.server.mapper.SysRecipeMapper;
import com.zhangzb.healthrecipe.server.service.impl.SysRecipeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysRecipeServiceTest {

    @Mock
    private SysRecipeMapper recipeMapper;

    @InjectMocks
    private SysRecipeServiceImpl recipeService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recipeService, "baseMapper", recipeMapper);
    }

    @Test
    void searchByKeyword_shouldReturnMatchingRecipes() {
        SysRecipe recipe = new SysRecipe();
        recipe.setId(1L);
        recipe.setName("番茄炒蛋");
        when(recipeMapper.selectList(any())).thenReturn(Arrays.asList(recipe));

        List<SysRecipe> result = recipeService.searchByKeyword("番茄");

        assertEquals(1, result.size());
        assertEquals("番茄炒蛋", result.get(0).getName());
        verify(recipeMapper).selectList(any());
    }

    @Test
    void searchByKeyword_shouldReturnAll_whenKeywordNull() {
        when(recipeMapper.selectList(any())).thenReturn(Arrays.asList(new SysRecipe(), new SysRecipe()));

        List<SysRecipe> result = recipeService.searchByKeyword(null);

        assertEquals(2, result.size());
        verify(recipeMapper).selectList(any());
    }

    @Test
    void getRecipeDetail_shouldReturnRecipeWithIngredients() {
        SysRecipe recipe = new SysRecipe();
        recipe.setId(1L);
        recipe.setName("番茄炒蛋");
        when(recipeMapper.selectById(1L)).thenReturn(recipe);

        SysRecipe result = recipeService.getRecipeDetail(1L);

        assertNotNull(result);
        assertEquals("番茄炒蛋", result.getName());
        verify(recipeMapper).selectById(1L);
    }

    @Test
    void getRecipeDetail_shouldReturnNull_whenNotFound() {
        when(recipeMapper.selectById(999L)).thenReturn(null);

        SysRecipe result = recipeService.getRecipeDetail(999L);

        assertNull(result);
        verify(recipeMapper).selectById(999L);
    }
}
