package com.c88.affiliate.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.c88.affiliate.pojo.vo.MinProfitVO;

public class MinProfitHandler extends AbstractJsonTypeHandler<Object> {

    @Override
    protected Object parse(String json) {
        return JSON.parseArray(json, MinProfitVO.class);
    }

    @Override
    protected String toJson(Object obj) {
        return JSON.toJSONString(obj, new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty});
    }
}