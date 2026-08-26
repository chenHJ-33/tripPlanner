package org.example.tripplanner.pojo.entity;

import lombok.Data;
import org.example.tripplanner.pojo.common.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * 景点信息
 */
@Data
public class Attraction {
    /** 景点名称 */
    private String name;
    /** 地址 */
    private String address;
    /** 经纬度坐标 */
    private Location location;
    /** 建议游览时间(分钟) */
    private Integer visitDuration;
    /** 景点描述 */
    private String description;
    /** 景点类别，默认: {@code 景点} */
    private String category = "景点";
    /** 评分 */
    private Double rating;
    /** 景点图片URL列表 */
    private List<String> photos = new ArrayList<>();
    /** POI ID，默认: {@code ""} */
    private String poiId = "";
    /** 图片URL */
    private String imageUrl;
    /** 门票价格(元)，默认: {@code 0} */
    private Integer ticketPrice = 0;
}
