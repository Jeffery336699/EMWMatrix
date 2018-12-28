package cc.emw.mobile.project.Util;

import java.util.HashMap;
import java.util.Map;
import cc.emw.mobile.R;

/**
 * 图标显示颜色类
 * @author jven.wu
 */
public class DisplayIcon {
	private static Map<Integer, Integer> shapeColorMap;
	private static Map<Integer, Integer> roundColorMap;
	private static Map<Integer, Integer> strokeColorMap;
	private static Map<Integer, String> priorityMap;
	private static Map<Integer, String> stateMap;
	private static Map<Integer, Integer> colorMap; 
	
	/**
	 * 带箭头图标
	 * @param index 颜色索引值
	 * @return
	 */
	public static int getShapeColor(int index){
		shapeColorMap = new HashMap<Integer, Integer>();
		shapeColorMap.put(1, R.drawable.shape_ico_blue);
		shapeColorMap.put(2, R.drawable.shape_ico_orange);
		shapeColorMap.put(3, R.drawable.shape_ico_red);
		shapeColorMap.put(0, R.drawable.shape_ico_red);
		shapeColorMap.put(4, R.drawable.shape_ico_red);
		shapeColorMap.put(5, R.drawable.shape_ico_red);
		shapeColorMap.put(6, R.drawable.shape_ico_red);
		shapeColorMap.put(7, R.drawable.shape_ico_red);
		return shapeColorMap.get(index);
	}
	
	/**
	 * 圆形图标
	 * @param index 颜色索引值
	 * @return
	 */
	public static int getRoundColor(int index){
		roundColorMap = new HashMap<Integer, Integer>();
		roundColorMap.put(0, R.drawable.round_icon_blue);
		roundColorMap.put(1, R.drawable.round_icon_orange);
		roundColorMap.put(2, R.drawable.round_icon_red);
		roundColorMap.put(3, R.drawable.round_icon_red);
		roundColorMap.put(4, R.drawable.round_icon_red);
		roundColorMap.put(5, R.drawable.round_icon_red);
		roundColorMap.put(6, R.drawable.round_icon_red);
		roundColorMap.put(7, R.drawable.round_icon_red);
		return roundColorMap.get(index);
	}

	/**
	 * 圆形描边
	 * @param index 颜色索引值
	 * @return
	 */
	public static int getStrokeColor(int index){
		strokeColorMap = new HashMap<Integer, Integer>();
		strokeColorMap.put(0, R.drawable.stroke_blue);
		strokeColorMap.put(1, R.drawable.stroke_orange);
		strokeColorMap.put(2, R.drawable.stroke_red);
		strokeColorMap.put(3, R.drawable.stroke_red);
		strokeColorMap.put(4, R.drawable.stroke_red);
		strokeColorMap.put(5, R.drawable.stroke_red);
		strokeColorMap.put(6, R.drawable.stroke_red);
		strokeColorMap.put(7, R.drawable.stroke_red);
		return strokeColorMap.get(index);
	}

	/**
	 * 颜色
	 * @param index 颜色索引值
	 * @return
	 */
	public static int getColor(int index){
		colorMap = new HashMap<Integer, Integer>();
		/*colorMap.put(0, R.color.red_4);
		colorMap.put(1, R.color.orange_1);
		colorMap.put(2, R.color.blue_22);
		colorMap.put(3, R.color.green_4);
		colorMap.put(4, R.color.purple_1);
		colorMap.put(5, R.color.blue_21);
		return colorMap.get(index);*/
		return R.color.red;
	}

	/**
	 * 紧急程度
	 * @param index
	 * @return
	 */
	public static String getPriority(int index){
		priorityMap = new HashMap<Integer, String>();
		priorityMap.put(0, "0");
		priorityMap.put(1, "普通");
		priorityMap.put(2, "紧急");
		priorityMap.put(3, "非常紧急");
		return priorityMap.get(index);
	}

	/**
	 * 状态
	 * @param index
	 * @return
	 */
	public static String getState(int index){
		priorityMap = new HashMap<Integer, String>();
		stateMap.put(0, "未开始");
		stateMap.put(1, "进行中");
		stateMap.put(2, "已完成");
		return stateMap.get(index);
	}
}
