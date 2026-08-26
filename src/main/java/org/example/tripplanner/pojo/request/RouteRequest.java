package org.example.tripplanner.pojo.request;

import lombok.Data;

/**
 * 路线规划请求
 */
@Data
public class RouteRequest {
    
    /** 起点地址，必填，示例: {@code 北京市朝阳区阜通东大街6号} */
    private String originAddress;

    /** 终点地址，必填，示例: {@code 北京市海淀区上地十街10号} */
    private String destinationAddress;

    /** 起点城市，非必填 */
    private String originCity;

    /** 终点城市，非必填 */
    private String destinationCity;

    /** 路线类型，非必填，可选值: walking/driving/transit，默认: {@code walking} */
    private String routeType = "walking";
}