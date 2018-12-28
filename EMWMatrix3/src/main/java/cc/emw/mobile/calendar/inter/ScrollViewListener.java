package cc.emw.mobile.calendar.inter;

import cc.emw.mobile.calendar.view.ObservableScrollViewHor;
import cc.emw.mobile.calendar.view.ObservableScrollViewVer;

/**
 * Created by ${zrjt} on 2016/11/11.
 */
public interface ScrollViewListener {

    void onScrollChangedHor(ObservableScrollViewHor scrollView, int x, int y, int oldx, int oldy);

    void onScrollChangedVer(ObservableScrollViewVer scrollView, int x, int y, int oldx, int oldy);

}
