package org.example.tripplanner.pojo.response;

import lombok.Data;

import java.util.Map;

@Data
public class POIDetailResponse {
    private boolean success;
    private String message;
    private Map<String,Object> data;
}
