package main.java.com.jd.coco.util;

import com.alibaba.fastjson.JSON;
import com.google.gson.GsonBuilder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import main.java.com.jd.coco.exception.KnownException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static main.java.com.jd.coco.constant.ExcludeClass.EXCLUDE_CLASS_LIST;
import static main.java.com.jd.coco.constant.ExcludeFields.EXCLUDE_FIELD_LIST;

/*
* PSIclass转换成json
* */
public class ToJsonUtil {

    private static final Map<String, Object> normalTypes = new HashMap<>();
    private static final BigDecimal zero = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
    private static final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();

    static {

        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        normalTypes.put("Boolean", false);
        normalTypes.put("Float", zero);
        normalTypes.put("Double", zero);
        normalTypes.put("BigDecimal", zero);
        normalTypes.put("Number", 0);
        normalTypes.put("CharSequence", "");
        normalTypes.put("Date", dateTime);
        normalTypes.put("Temporal", now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        normalTypes.put("LocalDateTime", dateTime);
        normalTypes.put("LocalDate", now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        normalTypes.put("LocalTime", now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    /*
    * 解析PSICLASS返回json
    * */
    public static String parseJsonFromPsiClass(PsiClass psiClass,PsiClass psiInnerClass) {
        if(!EXCLUDE_CLASS_LIST.contains(psiClass.getName())){
            if(null != psiClass){
                Map<String, Object> kv = getFields(psiClass, psiInnerClass);
                String json = gsonBuilder.create().toJson(kv);
                return JSON.toJSON(json).toString();
            }
        }

        return "{}";

    }


    /*
    * 获取PSIclass的所有属性
    * */
    public static Map<String, Object> getFields(PsiClass psiClass, PsiClass psiInnnerClass) {
        Map<String, Object> map = new LinkedHashMap<>();

        if (psiClass == null) {
            return map;
        }

        for (PsiField field : psiClass.getAllFields()) {
        //for (PsiField field : psiClass.getFields()) {
            if(!field.getModifierList().hasModifierProperty(PsiModifier.STATIC) &&
                    !field.getModifierList().hasModifierProperty(PsiModifier.FINAL) &&
                    !field.getModifierList().hasModifierProperty(PsiModifier.PUBLIC)
            ){
                if(!EXCLUDE_FIELD_LIST.contains(field.getType().toString()) &&
                !field.getType().toString().contains("<K, V>")){
                    if(null != psiInnnerClass &&field.getType().toString().contains("PsiType:T")){
                        map.put(field.getName(), parseJsonFromPsiClass(psiInnnerClass, null));
                    }else{
                        map.put(field.getName(), typeResolve(field.getType(), 0));
                    }

                }
            }


        }

        return map;
    }


    private static Object typeResolve(PsiType type, int level) {
            level = ++level;

            if (type instanceof PsiPrimitiveType) {       //primitive Type

                return getDefaultValue(type);

            } else if (type instanceof PsiArrayType) {   //array type

                List<Object> list = new ArrayList<>();
                PsiType deepType = type.getDeepComponentType();
                list.add(typeResolve(deepType, level));
                return list;

            } else {    //reference Type

                Map<String, Object> map = new LinkedHashMap<>();

                PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);

                if (psiClass == null) {
                    return map;
                }

                if (psiClass.isEnum()) { // enum

                    for (PsiField field : psiClass.getFields()) {
                        if (field instanceof PsiEnumConstant) {
                            return field.getName();
                        }
                    }
                    return "";

                } else {

                    List<String> fieldTypeNames = new ArrayList<>();

                    PsiType[] types = type.getSuperTypes();

                    fieldTypeNames.add(type.getPresentableText());
                    fieldTypeNames.addAll(Arrays.stream(types).map(PsiType::getPresentableText).collect(Collectors.toList()));

                    if (fieldTypeNames.stream().anyMatch(s -> s.startsWith("Collection") || s.startsWith("Iterable"))) {// Iterable

                        List<Object> list = new ArrayList<>();
                        PsiType deepType = PsiUtil.extractIterableTypeParameter(type, false);
                        list.add(typeResolve(deepType, level));
                        return list;

                    } else { // Object

                        List<String> retain = new ArrayList<>(fieldTypeNames);
                        retain.retainAll(normalTypes.keySet());
                        if (!retain.isEmpty()) {
                            return normalTypes.get(retain.get(0));
                        } else {


                            if (level > 500) {
                                //System.out.println("This class reference level exceeds maximum limit or has nested references!"+psiClass);
                                throw new KnownException("This class reference level exceeds maximum limit or has nested references!");
                            }
                            if(!EXCLUDE_CLASS_LIST.contains(psiClass.getName())) {
                                for (PsiField field : psiClass.getAllFields()) {
                                    if (!field.getModifierList().hasModifierProperty(PsiModifier.STATIC) &&
                                            !field.getModifierList().hasModifierProperty(PsiModifier.FINAL) &&
                                            !field.getModifierList().hasModifierProperty(PsiModifier.PUBLIC)) {

                                        if(!EXCLUDE_FIELD_LIST.contains(field.getType().toString())&&
                                                !field.getType().toString().contains("<K, V>")){
                                            map.put(field.getName(), typeResolve(field.getType(), level));
                                        }
                                    }
                                }
                            }
                            return map;
                        }
                    }
                }
            }




    }

    public static Object getDefaultValue(PsiType type) {
        if (!(type instanceof PsiPrimitiveType)) return null;
        switch (type.getCanonicalText()) {
            case "boolean":
                return false;
            case "byte":
                return (byte) 0;
            case "char":
                return '\0';
            case "short":
                return (short) 0;
            case "int":
                return 0;
            case "long":
                return 0L;
            case "float":
                return zero;
            case "double":
                return zero;
            default:
                return null;
        }
    }



}
