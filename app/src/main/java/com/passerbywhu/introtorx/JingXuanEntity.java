package com.passerbywhu.introtorx;

import java.io.Serializable;

public class JingXuanEntity implements Serializable {
    private int id; // 订阅源表主键id
    private String iconUrl; // 栏目图标地址
    private String topicName; // 栏目名称
    private String description; // 栏目描述

    public int getId() {
        return id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getDescription() {
        return description;
    }
}
