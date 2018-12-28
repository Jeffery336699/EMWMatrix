package cc.emw.mobile.task.entity;

import java.util.Comparator;

/**
 * Created by chengyong.liu on 2016/7/26.
 */
public class LabelComparator<T extends UserLabelBean> implements Comparator<UserLabelBean> {
    @Override
    public int compare(UserLabelBean lhs, UserLabelBean rhs) {
        if (lhs.getSortLetters().equals("@")
                || rhs.getSortLetters().equals("#")) {
            return -1;
        } else if (lhs.getSortLetters().equals("#")
                || rhs.getSortLetters().equals("@")) {
            return 1;
        } else {
            return lhs.getSortLetters().compareTo(rhs.getSortLetters());
        }
    }
}
