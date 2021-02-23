package main.java.com.jd.coco.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
/*
* 自定义的json工具类
* */
public class CustomJsonUtil {

    public static String prettyJson(JSONObject object) {
        String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);

        System.out.println(pretty);
        return pretty;
    }


}
