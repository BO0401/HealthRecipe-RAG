SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
CREATE TABLE sys_user (
  id            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  username      VARCHAR(50) NOT NULL COMMENT '账号',
  password      VARCHAR(100) NOT NULL COMMENT '密码',
  nickname      VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  avatar        VARCHAR(255) DEFAULT NULL COMMENT '头像',
  height_cm     DECIMAL(5,2) DEFAULT NULL COMMENT '身高(cm)',
  weight_kg     DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
  allergens     VARCHAR(255) DEFAULT NULL COMMENT '过敏原(逗号分隔)',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 食材表
-- ----------------------------
CREATE TABLE sys_ingredient (
  id            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  name          VARCHAR(100) NOT NULL COMMENT '食材名称',
  category      VARCHAR(50) DEFAULT NULL COMMENT '分类(蔬菜/肉类等)',
  unit          VARCHAR(20) DEFAULT 'g' COMMENT '默认单位',
  kcal_per_100g DECIMAL(8,2) DEFAULT NULL COMMENT '热量(kcal/100g)',
  protein_g     DECIMAL(8,2) DEFAULT NULL COMMENT '蛋白质(g/100g)',
  image_url     VARCHAR(255) DEFAULT NULL COMMENT '食材图片',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食材表';

-- ----------------------------
-- 3. 食谱表
-- ----------------------------
CREATE TABLE sys_recipe (
  id            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  name          VARCHAR(100) NOT NULL COMMENT '菜名',
  cover_img     VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  steps         TEXT COMMENT '制作步骤',
  cook_time     INT DEFAULT NULL COMMENT '烹饪时长(分钟)',
  difficulty    VARCHAR(20) DEFAULT NULL COMMENT '难度(简单/中等/困难)',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食谱表';

-- ----------------------------
-- 4. 食谱-食材关联表
-- ----------------------------
CREATE TABLE rel_recipe_ingredient (
  id            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  recipe_id     BIGINT NOT NULL COMMENT '食谱ID',
  ingredient_id BIGINT NOT NULL COMMENT '食材ID',
  amount        DECIMAL(10,2) NOT NULL COMMENT '用量(数值)',
  unit          VARCHAR(20) NOT NULL COMMENT '用量单位(g/ml/个)',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_recipe (recipe_id),
  KEY idx_ingredient (ingredient_id),
  CONSTRAINT fk_recipe_ing_recipe FOREIGN KEY (recipe_id) REFERENCES sys_recipe (id),
  CONSTRAINT fk_recipe_ing_ing FOREIGN KEY (ingredient_id) REFERENCES sys_ingredient (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食谱食材关联表';

-- ----------------------------
-- 5. 家庭库存表
-- ----------------------------
CREATE TABLE sys_inventory (
  id            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id       BIGINT NOT NULL COMMENT '用户ID',
  ingredient_id BIGINT NOT NULL COMMENT '食材ID',
  quantity      DECIMAL(10,2) NOT NULL COMMENT '当前数量',
  unit          VARCHAR(20) NOT NULL COMMENT '单位',
  expire_date   DATE DEFAULT NULL COMMENT '过期时间',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  KEY idx_ingredient (ingredient_id),
  CONSTRAINT fk_inventory_user FOREIGN KEY (user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_inventory_ing FOREIGN KEY (ingredient_id) REFERENCES sys_ingredient (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭库存表';

-- ----------------------------
-- 6. 采购清单表
-- ----------------------------
CREATE TABLE sys_shopping_list (
  id            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id       BIGINT NOT NULL COMMENT '用户ID',
  ingredient_name VARCHAR(100) NOT NULL COMMENT '食材名称(冗余字段，方便查看)',
  quantity      DECIMAL(10,2) NOT NULL COMMENT '采购数量',
  unit          VARCHAR(20) NOT NULL COMMENT '单位',
  status        TINYINT DEFAULT 0 COMMENT '状态(0未购 1已购)',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user (user_id),
  CONSTRAINT fk_shopping_user FOREIGN KEY (user_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购清单表';

-- ----------------------------
-- 7. 知识库表 (RAG用)
-- ----------------------------
CREATE TABLE sys_vector_store (
  id            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  content       TEXT NOT NULL COMMENT '文本内容',
  metadata      JSON DEFAULT NULL COMMENT '元数据(来源/标签等)',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库表';

SET FOREIGN_KEY_CHECKS = 1;