package org.example.tripplanner.pojo.response;

import lombok.Data;
import org.example.tripplanner.pojo.entity.WeatherInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 天气查询响应
 */
@Data
public class WeatherResponse {
    /** 是否成功 */
    private Boolean success;
    /** 消息，默认: {@code ""} */
    private String message = "";
    /** 天气信息 */
    private List<WeatherInfo> data = new ArrayList<>();
}