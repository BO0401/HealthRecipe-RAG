package com.zhangzb.healthrecipe.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFixer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseFixer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        fixInventoryTable();
        fixShoppingListTable();
        fixVectorStoreTable();
    }

    private void fixInventoryTable() {
        try {
            jdbcTemplate.execute("ALTER TABLE sys_inventory ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'");
            log.info("[DB Fix] Added column 'create_time' to sys_inventory");
        } catch (Exception e) {
            log.info("[DB Fix] Column 'create_time' already exists in sys_inventory: {}", e.getMessage());
        }
        try {
            jdbcTemplate.execute("ALTER TABLE sys_inventory ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'");
            log.info("[DB Fix] Added column 'update_time' to sys_inventory");
        } catch (Exception e) {
            log.info("[DB Fix] Column 'update_time' already exists in sys_inventory: {}", e.getMessage());
        }
    }

    private void fixShoppingListTable() {
        try {
            jdbcTemplate.execute("ALTER TABLE sys_shopping_list ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'");
            log.info("[DB Fix] Added column 'create_time' to sys_shopping_list");
        } catch (Exception e) {
            log.info("[DB Fix] Column 'create_time' already exists in sys_shopping_list: {}", e.getMessage());
        }
        try {
            jdbcTemplate.execute("ALTER TABLE sys_shopping_list ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'");
            log.info("[DB Fix] Added column 'update_time' to sys_shopping_list");
        } catch (Exception e) {
            log.info("[DB Fix] Column 'update_time' already exists in sys_shopping_list: {}", e.getMessage());
        }
    }

    private void fixVectorStoreTable() {
        try {
            jdbcTemplate.execute("ALTER TABLE sys_vector_store ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'");
            log.info("[DB Fix] Added column 'create_time' to sys_vector_store");
        } catch (Exception e) {
            log.info("[DB Fix] Column 'create_time' already exists in sys_vector_store: {}", e.getMessage());
        }
        try {
            jdbcTemplate.execute("ALTER TABLE sys_vector_store ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'");
            log.info("[DB Fix] Added column 'update_time' to sys_vector_store");
        } catch (Exception e) {
            log.info("[DB Fix] Column 'update_time' already exists in sys_vector_store: {}", e.getMessage());
        }
    }
}
