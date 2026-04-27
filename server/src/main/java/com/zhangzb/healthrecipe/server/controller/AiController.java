package com.zhangzb.healthrecipe.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.entity.SysVectorStore;
import com.zhangzb.healthrecipe.server.service.SysVectorStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private SysVectorStoreService vectorStoreService;

    @PostMapping("/document/upload")
    public Result<UploadedDocVO> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String content = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
            ).lines().collect(Collectors.joining("\n"));

            SysVectorStore doc = new SysVectorStore();
            doc.setContent(content);
            doc.setMetadata("{\"name\":\"" + file.getOriginalFilename() + "\",\"size\":" + file.getSize() + "}");
            doc.setCreateTime(LocalDateTime.now());
            doc.setUpdateTime(LocalDateTime.now());
            vectorStoreService.save(doc);

            UploadedDocVO vo = new UploadedDocVO();
            vo.setId(doc.getId().toString());
            vo.setName(file.getOriginalFilename());
            vo.setSize(file.getSize());
            vo.setUploadTime(doc.getCreateTime().toString());

            return Result.success(vo);
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/documents")
    public Result<List<UploadedDocVO>> getDocuments() {
        List<SysVectorStore> list = vectorStoreService.list(
                new LambdaQueryWrapper<SysVectorStore>().orderByDesc(SysVectorStore::getCreateTime)
        );
        List<UploadedDocVO> vos = list.stream().map(doc -> {
            UploadedDocVO vo = new UploadedDocVO();
            vo.setId(doc.getId().toString());
            vo.setName("document_" + doc.getId());
            vo.setSize(0);
            vo.setUploadTime(doc.getCreateTime() != null ? doc.getCreateTime().toString() : "");
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @DeleteMapping("/document/{id}")
    public Result<Void> deleteDocument(@PathVariable Long id) {
        if (!vectorStoreService.removeById(id)) {
            return Result.error(404, "文档不存在");
        }
        return Result.success();
    }

    static class UploadedDocVO {
        private String id;
        private String name;
        private long size;
        private String uploadTime;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public String getUploadTime() { return uploadTime; }
        public void setUploadTime(String uploadTime) { this.uploadTime = uploadTime; }
    }
}
