package com.example.backend.exception;

import com.example.backend.common.ApiResponse;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBizException(BizException ex) {
        log.warn("Business request failed: code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            TypeMismatchException.class,
            MultipartException.class
    })
    public ApiResponse<Void> handleBadRequest(Exception ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ApiResponse.error(400, "bad request");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ApiResponse<Void> handleUploadTooLarge(MaxUploadSizeExceededException ex) {
        log.warn("Upload file too large: {}", ex.getMessage());
        return ApiResponse.error(413, "file too large");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        // 中文注释：兜底异常必须记录堆栈，避免接口只返回 500 时无法定位数据库或序列化问题。
        log.error("Unhandled server error", ex);
        return ApiResponse.error(500, "internal server error");
    }
}
