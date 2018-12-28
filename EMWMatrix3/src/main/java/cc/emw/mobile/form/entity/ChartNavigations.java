package cc.emw.mobile.form.entity;

/**
 * Created by chengyong.liu on 2016/5/25.
 */

import java.io.Serializable;

/**
 * 用于图表查询的实体
 */
public class ChartNavigations implements Serializable {
    public int ChartType;
    public int CountType;
    public boolean IsShowTitle;
    public String LegendLocation;
    public String Name;
    public String Text;
    public String ID;
    public String Value;//对应请求的数据中XYT的X值
}
