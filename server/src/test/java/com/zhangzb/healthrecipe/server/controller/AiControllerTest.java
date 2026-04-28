package com.zhangzb.healthrecipe.server.controller;

import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.entity.SysVectorStore;
import com.zhangzb.healthrecipe.server.service.SysVectorStoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiControllerTest {

    @Mock
    private SysVectorStoreService vectorStoreService;

    @InjectMocks
    private AiController aiController;

    @Test
    void getDocuments_shouldReturnList() {
        SysVectorStore doc = new SysVectorStore();
        doc.setId(1L);
        when(vectorStoreService.listAll()).thenReturn(List.of(doc));

        Result<List<AiController.UploadedDocVO>> result = aiController.getDocuments();

        assertTrue(result.getCode() == 200);
        assertEquals(1, result.getData().size());
    }

    @Test
    void deleteDocument_shouldReturnSuccess_whenExists() {
        when(vectorStoreService.removeById(1L)).thenReturn(true);

        Result<Void> result = aiController.deleteDocument(1L);

        assertTrue(result.getCode() == 200);
        verify(vectorStoreService).removeById(1L);
    }

    @Test
    void deleteDocument_shouldReturn404_whenNotExists() {
        when(vectorStoreService.removeById(999L)).thenReturn(false);

        Result<Void> result = aiController.deleteDocument(999L);

        assertTrue(result.getCode() == 404);
        verify(vectorStoreService).removeById(999L);
    }
}
