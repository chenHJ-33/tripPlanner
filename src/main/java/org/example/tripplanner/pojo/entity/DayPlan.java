package org.example.tripplanner.pojo.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 单日行程
 */
@Data
public class DayPlan {
    /** 日期 YYYY-MM-DD */
    private String date;
    /** 第几天(从0开始) */
    private Integer dayIndex;
    /** 当日行程描述 */
    private String description;
    /** 交通方式 */
    private String transportation;
    /** 住宿 */
    private String accommodation;
    /** 推荐酒店 */
    private Hotel hotel;
    /** 景点列表 */
    private List<Attraction> attractions = new ArrayList<>();
    /** 餐饮列表 */
    private List<Meal> meals = new ArrayList<>();
}