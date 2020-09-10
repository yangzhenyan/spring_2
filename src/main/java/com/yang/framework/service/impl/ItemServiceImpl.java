package com.yang.framework.service.impl;

import com.yang.annotation.YService;
import com.yang.framework.service.ItemService;

/**
 * @author yzy
 * @date 2020/8/26
 * @describe
 */
@YService("itemServiceImpl_1")
public class ItemServiceImpl implements ItemService {

    public Object getBean(String name) {
        return "bean：" + name;
    }
}
