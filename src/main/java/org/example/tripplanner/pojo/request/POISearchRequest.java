package org.example.tripplanner.pojo.request;

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

    /**
     * 是否限制在城市范围内，非必填，默认: {@code true}
     * <p>注意：属性名必须为全小写 {@code citylimit}，
     * GET 查询参数按名字精确绑定(ServletModelAttributeMethodProcessor 不做驼峰匹配)。
     */
    private Boolean citylimit = true;
}
