package com.zhangzb.healthrecipe.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProfileDataDTO {
    private UserProfile user;
    private List<InventoryItem> inventory;

    @Data
    public static class UserProfile {
        private java.math.BigDecimal height;
        private java.math.BigDecimal weight;
        private List<String> allergens;
    }

    @Data
    public static class InventoryItem {
        private String name;
        private java.math.BigDecimal quantity;
    }
}
