package org.example.tripplanner.pojo.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 旅行计划
 */
@Data
public class TripPlan {
    /** 目的地城市 */
    private String city;
    /** 开始日期 */
    private String startDate;
    /** 结束日期 */
    private String endDate;
    /** 每日行程 */
    private List<DayPlan> days;
    /** 天气信息 */
    private List<WeatherInfo> weatherInfo = new ArrayList<>();
    /** 总体建议 */
    private String overallSuggestions;
    /** 预算信息 */
    private Budget budget;
}