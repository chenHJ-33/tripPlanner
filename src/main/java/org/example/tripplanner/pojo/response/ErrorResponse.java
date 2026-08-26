package org.example.tripplanner.pojo.response;

import lombok.Data;

/**
 * 错误响应
 */
@Data
public class ErrorResponse {
    /** 是否成功，默认: {@code false} */
    private Boolean success = false;
    /** 错误消息 */
    private String message;
    /** 错误代码 */
    private String errorCode;
}