# 健康食谱管理与食材采购系统

基于 RAG 技术的智能饮食管理平台，打通从健康决策到食材采购的完整链路。

## 项目简介

本系统通过个性化推荐、智能问答与自动化执行，帮助用户解决日常饮食管理中的痛点：

- **智能食谱推荐** — 根据个人健康档案推荐合适的三餐组合
- **AI 营养助手** — 基于 RAG 技术提供精准的膳食建议
- **一键采购清单** — 选定食谱后自动生成采购清单，智能比对库存去重

## 快速启动

### 后端

```bash
cd server
mvn spring-boot:run
```

### 前端

```bash
cd front
npm install
npm run dev
```

### 数据库

确保本地 MySQL 服务运行中，并创建 `health_recipe` 数据库。Flyway 会自动执行初始化脚本。

## 技术栈

- **前端**: Vue 3 + Element Plus + Pinia + ECharts
- **后端**: Spring Boot 3 + MyBatis-Plus + Flyway
- **数据库**: MySQL 8.0 + Redis
- **AI 服务**: DeepSeek API + 本地向量库

---

欢迎加入，一起打造更健康的饮食方式！
