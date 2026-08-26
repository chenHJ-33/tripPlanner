package org.example.tripplanner.pojo.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 旅行规划请求
 */
@Data
public class TripRequest {

    /** 目的地城市，必填，示例: {@code 北京} */
    private String city;

    /** 开始日期，必填，格式: YYYY-MM-DD，示例: {@code 2025-06-01} */
    private String startDate;

    /** 结束日期，必填，格式: YYYY-MM-DD，示例: {@code 2025-06-03} */
    private String endDate;

    /** 旅行天数，必填，取值范围: 1-30，示例: {@code 3} */
    private Integer travelDays;

    /** 交通方式，必填，示例: {@code 公共交通} */
    private String transportation;

    /** 住宿偏好，必填，示例: {@code 经济型酒店} */
    private String accommodation;

    /** 旅行偏好标签，非必填，示例: {@code ["历史文化", "美食"]} */
    private List<String> preferences = new ArrayList<>();

    /** 额外要求，非必填，示例: {@code 希望多安排一些博物馆} */
    private String freeTextInput = "";
}