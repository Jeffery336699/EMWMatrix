package cc.emw.mobile.task.factory;

import android.content.Context;
import com.bigkoo.pickerview.TimePickerView;
import cc.emw.mobile.R;


/**
 * Created by sunny.du on 2016/9/2.
 *
 * 时间控件工厂类
 */
public class TaskTimePickerViewFactory {
    public static TimePickerView createTimeSelector(Context context){
        TimePickerView timePickerView = new TimePickerView(context, TimePickerView.Type.ALL);// 时间选择器
        timePickerView.setTitle(context.getString(R.string.beg_time));
        timePickerView.setCancelable(true);
        return timePickerView;
    }
}
