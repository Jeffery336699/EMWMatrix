package cc.emw.mobile.task.factory;

import android.content.Context;

import com.bigkoo.pickerview.OptionsPickerView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.task.constant.TaskConstant;

/**
 * Created by sunny.du on 2016/9/2.
 * 任务紧急程度工厂类
 */
public class TaskOptionsPickerViewFactory {
    public static OptionsPickerView<String> createOptionsPickerView(Context context){
        ArrayList<String> sorts = new ArrayList<>();
        sorts.add(TaskConstant.TaskEmergencyState.NORMAL);
        sorts.add(TaskConstant.TaskEmergencyState.EMERGENCY);
        sorts.add(TaskConstant.TaskEmergencyState.VERY_EMERGENCY);
        OptionsPickerView<String> optionsPickerView = new OptionsPickerView<>(context);
        optionsPickerView.setPicker(sorts);
        optionsPickerView.setTitle(context.getString(R.string.task_modify_emergenct));
        optionsPickerView.setCancelable(true);
        optionsPickerView.setCyclic(false);// 无限循环
        return optionsPickerView;
    }
}
