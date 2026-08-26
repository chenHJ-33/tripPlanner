package org.example.tripplanner.pojo.response;

import lombok.Data;
import org.example.tripplanner.pojo.entity.POIInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * POI搜索响应
 */
@Data
public class POISearchResponse {
    /** 是否成功 */
    private Boolean success;
    /** 消息，默认: {@code ""} */
    private String message = "";
    /** POI列表 */
    private List<POIInfo> data = new ArrayList<>();
}