package com.zhangzb.healthrecipe.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardAlertVO {
    private String id;
    private String title;
    private String dateText;
    private String type;
    private String tag;
}

