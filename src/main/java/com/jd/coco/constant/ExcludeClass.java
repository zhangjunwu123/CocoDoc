package main.java.com.jd.coco.constant;

import java.util.ArrayList;
import java.util.List;
/*
* 定义不被解析参数的类型
* */
public class ExcludeClass {
    public static final List<String> EXCLUDE_CLASS_LIST = new ArrayList<>();
    static {
        EXCLUDE_CLASS_LIST.add("DataAuthResult");
        EXCLUDE_CLASS_LIST.add("Throwable");
        EXCLUDE_CLASS_LIST.add("Byte");
        EXCLUDE_CLASS_LIST.add("Sort");
        EXCLUDE_CLASS_LIST.add("Map");
        EXCLUDE_CLASS_LIST.add("Entry");
    }
}
