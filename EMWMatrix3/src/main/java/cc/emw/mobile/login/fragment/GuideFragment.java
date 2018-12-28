package cc.emw.mobile.login.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GuideFragment extends Fragment {

    final static String LAYOUT_ID = "layoutid";
    private View rootView;

    public static GuideFragment newInstance(int layoutId) {
        GuideFragment pane = new GuideFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(
                getArguments().getInt(LAYOUT_ID, -1), container, false);
        return rootView;
    }
}
