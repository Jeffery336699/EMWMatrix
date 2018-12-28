package cc.emw.mobile.form.util;

import java.util.HashMap;
import java.util.Map;

import cc.emw.mobile.form.entity.CommonConsts;

/**
 * Created by chengyong.liu on 2016/7/2.
 */
public class CommonUtil {
    private static Map<String, Integer> typeMap;
    /**
     * 获取表单内容控件类型的int值
     * @param key 控件类型
     * @return
     */
    public static int getElementType(String key){
        typeMap = new HashMap<String, Integer>();
        typeMap.put("TextBox", CommonConsts.ElementType.TEXTBOX);
        typeMap.put("DateTimer", CommonConsts.ElementType.DATETIMER);
        typeMap.put("DropDownList", CommonConsts.ElementType.DROPDOWNLIST);
        typeMap.put("Searcher",CommonConsts.ElementType.SEARCHER);
        typeMap.put("RadioButton",CommonConsts.ElementType.RADIOBUTTON);
        typeMap.put("TimeSelector",CommonConsts.ElementType.TIMESELECTOR);
        typeMap.put("CheckBox", CommonConsts.ElementType.CHECKBOX);
        typeMap.put("Label",CommonConsts.ElementType.LABEL);
        int i = 0;
        try{
            i = typeMap.get(key);
        }catch(Exception e){
            e.printStackTrace();
        }
        return i;
    }
}
