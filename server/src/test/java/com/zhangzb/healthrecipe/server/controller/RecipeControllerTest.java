package com.zhangzb.healthrecipe.server.controller;

import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.RecipeCreateDTO;
import com.zhangzb.healthrecipe.server.dto.RecipeVO;
import com.zhangzb.healthrecipe.server.entity.SysRecipe;
import com.zhangzb.healthrecipe.server.service.RelRecipeIngredientService;
import com.zhangzb.healthrecipe.server.service.SysRecipeService;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private SysRecipeService recipeService;

    @Mock
    private RelRecipeIngredientService relRecipeIngredientService;

    @Mock
    private SysShoppingListService shoppingListService;

    @InjectMocks
    private RecipeController recipeController;

    @Test
    void getById_shouldReturnRecipe_whenExists() {
        SysRecipe recipe = new SysRecipe();
        recipe.setId(1L);
        recipe.setName("番茄炒蛋");
        when(recipeService.getById(1L)).thenReturn(recipe);

        Result<RecipeVO> result = recipeController.getById(1L);

        assertTrue(result.getCode() == 200);
        assertNotNull(result.getData());
        assertEquals("番茄炒蛋", result.getData().getName());
    }

    @Test
    void getById_shouldReturn404_whenNotExists() {
        when(recipeService.getById(999L)).thenReturn(null);

        Result<RecipeVO> result = recipeController.getById(999L);

        assertTrue(result.getCode() == 404);
        assertNull(result.getData());
    }

    @Test
    void remove_shouldReturnSuccess_whenExists() {
        when(recipeService.removeById(1L)).thenReturn(true);

        Result<Void> result = recipeController.remove(1L);

        assertTrue(result.getCode() == 200);
        verify(recipeService).removeById(1L);
    }

    @Test
    void remove_shouldReturn404_whenNotExists() {
        when(recipeService.removeById(999L)).thenReturn(false);

        Result<Void> result = recipeController.remove(999L);

        assertTrue(result.getCode() == 404);
        verify(recipeService).removeById(999L);
    }

    @Test
    void create_shouldReturnNewRecipe() {
        RecipeCreateDTO dto = new RecipeCreateDTO();
        dto.setName("新食谱");
        dto.setCookTime(30);
        dto.setDifficulty("简单");
        dto.setSteps("步骤1");
        when(recipeService.save(any())).thenReturn(true);

        Result<RecipeVO> result = recipeController.create(dto);

        assertTrue(result.getCode() == 200);
        assertNotNull(result.getData());
        assertEquals("新食谱", result.getData().getName());
        verify(recipeService).save(any());
    }

    @Test
    void list_shouldReturnRecipeList() {
        SysRecipe recipe = new SysRecipe();
        recipe.setId(1L);
        recipe.setName("番茄炒蛋");
        when(recipeService.searchByKeyword(null)).thenReturn(List.of(recipe));

        Result<List<RecipeVO>> result = recipeController.list(null);

        assertTrue(result.getCode() == 200);
        assertFalse(result.getData().isEmpty());
        assertEquals(1, result.getData().size());
    }
}
