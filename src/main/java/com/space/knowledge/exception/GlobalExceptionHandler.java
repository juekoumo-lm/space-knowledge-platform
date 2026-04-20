package com.space.knowledge.exception;

import com.space.knowledge.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        logger.warn("参数错误 [{}]: {}", request.getRequestURI(), e.getMessage());
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getDefaultMessage()).append("; ");
        }
        logger.warn("数据验证失败 [{}]: {}", request.getRequestURI(), errorMessage.toString());
        return Result.fail(errorMessage.toString());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        logger.warn("状态错误 [{}]: {}", request.getRequestURI(), e.getMessage());
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        logger.warn("文件过大 [{}]: {}", request.getRequestURI(), e.getMessage());
        return Result.fail("文件大小超过限制（最大10MB）");
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleIOException(IOException e, HttpServletRequest request) {
        logger.error("IO错误 [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        return Result.fail("文件处理失败，请稍后重试");
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleSQLException(SQLException e, HttpServletRequest request) {
        logger.error("数据库错误 [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        return Result.fail("数据库操作失败，请稍后重试");
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.error("运行时错误 [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        return Result.fail("系统运行时错误，请稍后重试");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        logger.error("系统错误 [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        // 生产环境返回通用错误信息，开发环境可以返回详细错误信息
        return Result.fail("系统繁忙，请稍后重试");
    }
}