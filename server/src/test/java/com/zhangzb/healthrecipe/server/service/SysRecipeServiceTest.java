package com.zhangzb.healthrecipe.server.service;

import com.zhangzb.healthrecipe.server.entity.SysRecipe;
import com.zhangzb.healthrecipe.server.mapper.SysRecipeMapper;
import com.zhangzb.healthrecipe.server.service.impl.SysRecipeServiceImpl;
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
class SysRecipeServiceTest {

    @Mock
    private SysRecipeMapper recipeMapper;

    @InjectMocks
    private SysRecipeServiceImpl recipeService;

    @Test
    void searchByKeyword_shouldReturnAll_whenKeywordIsNull() {
        when(recipeMapper.selectList(any())).thenReturn(Arrays.asList(new SysRecipe(), new SysRecipe()));

        List<SysRecipe> result = recipeService.searchByKeyword(null);

        assertEquals(2, result.size());
        verify(recipeMapper).selectList(any());
    }

    @Test
    void searchByKeyword_shouldReturnAll_whenKeywordIsBlank() {
        when(recipeMapper.selectList(any())).thenReturn(Arrays.asList(new SysRecipe()));

        List<SysRecipe> result = recipeService.searchByKeyword("   ");

        assertEquals(1, result.size());
        verify(recipeMapper).selectList(any());
    }

    @Test
    void searchByKeyword_shouldFilterByName() {
        SysRecipe recipe1 = new SysRecipe();
        recipe1.setName("番茄炒蛋");
        SysRecipe recipe2 = new SysRecipe();
        recipe2.setName("红烧肉");

        when(recipeMapper.selectList(any())).thenReturn(Arrays.asList(recipe1, recipe2));

        List<SysRecipe> result = recipeService.searchByKeyword("番茄");

        assertEquals(2, result.size());
        verify(recipeMapper).selectList(any());
    }

    @Test
    void recommendByBodyType_shouldReturnLimitedResults() {
        when(recipeMapper.selectList(any())).thenReturn(Arrays.asList(new SysRecipe(), new SysRecipe()));

        List<SysRecipe> result = recipeService.recommendByBodyType("阳虚", null);

        assertEquals(2, result.size());
        verify(recipeMapper).selectList(any());
    }
}
