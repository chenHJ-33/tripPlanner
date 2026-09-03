package org.example.tripplanner.service;

import org.example.tripplanner.pojo.response.POIDetailResponse;

import java.util.List;
import java.util.Map;

public interface UnsplashService {
    POIDetailResponse getPhotoURL(String name);

    /**
     * 搜索图片
     *
     * @param query   搜索关键词
     * @param perPage 每页数量
     * @return 图片列表，失败返回空列表
     */
    List<Map<String, Object>> searchPhotos(String query, int perPage);
}
