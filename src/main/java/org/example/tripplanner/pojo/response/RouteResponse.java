package org.example.tripplanner.pojo.response;

import lombok.Data;
import org.example.tripplanner.pojo.entity.RouteInfo;

/**
 * 路线规划响应
 */
@Data
public class RouteResponse {
    /** 是否成功 */
    private Boolean success;
    /** 消息，默认: {@code ""} */
    private String message = "";
    /** 路线信息 */
    private RouteInfo data;
}