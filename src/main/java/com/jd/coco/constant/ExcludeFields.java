package main.java.com.jd.coco.constant;

import java.util.ArrayList;
import java.util.List;

/*
* 定义不被解析参数的字段类型
* */
public class ExcludeFields {

    public static final List<String> EXCLUDE_FIELD_LIST = new ArrayList<>();
    static {
        EXCLUDE_FIELD_LIST.add("PsiType:Throwable");
        EXCLUDE_FIELD_LIST.add("PsiClass:Entry");
    }
}
