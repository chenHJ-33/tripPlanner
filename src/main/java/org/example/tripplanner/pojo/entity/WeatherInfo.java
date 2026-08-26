package org.example.tripplanner.pojo.entity;

import lombok.Data;

/**
 * 天气信息
 */
@Data
public class WeatherInfo {
    /** 日期 YYYY-MM-DD */
    private String date;
    /** 白天天气，默认: {@code ""} */
    private String dayWeather = "";
    /** 夜间天气，默认: {@code ""} */
    private String nightWeather = "";
    /** 白天温度，默认: {@code 0} (已移除°C等单位) */
    private Integer dayTemp = 0;
    /** 夜间温度，默认: {@code 0} (已移除°C等单位) */
    private Integer nightTemp = 0;
    /** 风向，默认: {@code ""} */
    private String windDirection = "";
    /** 风力，默认: {@code ""} */
    private String windPower = "";
}