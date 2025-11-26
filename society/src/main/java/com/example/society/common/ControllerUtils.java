// ControllerUtils.java
package com.example.society.common;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 控制器工具类
 */
public class ControllerUtils {

    /**
     * 构建分页返回结果
     */
    public static <T> Result<PageResult<T>> buildPageResult(Page<T> page) {
        PageResult<T> pageResult = PageResult.of(
                page.getTotalElements(),
                page.getContent(),
                page.getNumber() + 1, // Spring Data Page 从0开始，转为从1开始
                page.getSize()
        );
        return Result.success(pageResult);
    }

    /**
     * 构建分页返回结果（自定义转换）
     */
    public static <T, R> Result<PageResult<R>> buildPageResult(Page<T> page, List<R> content) {
        PageResult<R> pageResult = PageResult.of(
                page.getTotalElements(),
                content,
                page.getNumber() + 1,
                page.getSize()
        );
        return Result.success(pageResult);
    }

    /**
     * 构建列表返回结果
     */
    public static <T> Result<List<T>> buildListResult(List<T> list) {
        return Result.success(list);
    }

    /**
     * 构建单个对象返回结果
     */
    public static <T> Result<T> buildSingleResult(T data) {
        return Result.success(data);
    }

    /**
     * 构建操作成功结果
     */
    public static Result<String> buildSuccessResult(String message) {
        return Result.success(message, null);
    }

    /**
     * 构建操作成功结果
     */
    public static Result<String> buildSuccessResult() {
        return Result.success();
    }
}