package org.example.tripplanner.pojo.entity;

import lombok.Data;
import org.example.tripplanner.pojo.common.Location;

/**
 * 酒店信息
 */
@Data
public class Hotel {
    /** 酒店名称 */
    private String name;
    /** 酒店地址，默认: {@code ""} */
    private String address = "";
    /** 酒店位置 */
    private Location location;
    /** 价格范围，默认: {@code ""} */
    private String priceRange = "";
    /** 评分，默认: {@code ""} */
    private String rating = "";
    /** 距离景点距离，默认: {@code ""} */
    private String distance = "";
    /** 酒店类型，默认: {@code ""} */
    private String type = "";
    /** 预估费用(元/晚)，默认: {@code 0} */
    private Integer estimatedCost = 0;
}