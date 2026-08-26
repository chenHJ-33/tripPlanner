package org.example.tripplanner.pojo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * POI搜索请求
 */
@Data
public class POISearchRequest {
    
    /** 搜索关键词，必填，示例: {@code 故宫} */
    private String keywords;

    /** 城市，必填，示例: {@code 北京} */
    private String city;

    /** 是否限制在城市范围内，非必填，默认: {@code true} */
    @JsonProperty("citylimit")
    private Boolean cityLimit = true;
}