package com.yzy.sell.utils;

import org.springframework.data.domain.Sort;

/**
 * @description: 排序封装
 * @author: yzy
 * @create: 2018-04-07 16:17
 */
public class SortUtil {

    public static Sort basicSort() {
        return basicSort("desc", "id");
    }

    public static Sort basicSort(String orderType, String orderField) {
        Sort sort = new Sort(Sort.Direction.fromString(orderType), orderField);
        return sort;
    }
}
