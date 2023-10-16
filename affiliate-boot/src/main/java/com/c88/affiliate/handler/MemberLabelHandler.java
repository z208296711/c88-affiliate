package com.c88.affiliate.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.c88.affiliate.pojo.vo.ActivityMemberVO;

import java.util.List;

public class MemberLabelHandler extends AbstractJsonTypeHandler<List<String>> {

    @Override
    protected List<String> parse(String json) {
        return JSON.parseArray(json,String.class);
    }

    @Override
    protected String toJson(List<String> obj) {
        return null;
    }
}