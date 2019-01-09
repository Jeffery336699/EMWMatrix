package cc.emw.mobile.contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tengyukun.alphatabs.AlphaTabsIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cc.emw.mobile.R;
import cc.emw.mobile.contact.fragment.GroupFragmentNew;

public class CiclerTalkFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.ivHead)
    ImageView ivHead;
    @BindView(R.id.tvCenterText)
    TextView tvCenterText;
    @BindView(R.id.ivNotice)
    ImageView ivNotice;
    @BindView(R.id.ivAite)
    ImageView ivAite;
    @BindView(R.id.alphaIndicator)
    AlphaTabsIndicator alphaIndicator;
    @BindView(R.id.vp)
    ViewPager mViewPger;

    private String mParam1;
    private String mParam2;
    private Unbinder bind;


    public CiclerTalkFragment() {
    }

    public static CiclerTalkFragment newInstance(String param1, String param2) {
        CiclerTalkFragment fragment = new CiclerTalkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cicler_talk, container, false);
        bind = ButterKnife.bind(this, view);
        initIndicator();
        return view;
    }

    private void initIndicator() {
        MainAdapter mainAdapter = new MainAdapter(getActivity().getSupportFragmentManager());
        mViewPger.setAdapter(mainAdapter);
        mViewPger.addOnPageChangeListener(mainAdapter);

        alphaIndicator.setViewPager(mViewPger);

        alphaIndicator.getTabView(0).showNumber(6);
        alphaIndicator.getTabView(1).showNumber(888);
        alphaIndicator.getTabView(2).showNumber(88);
        alphaIndicator.getTabView(3).showPoint();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }

    private class MainAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private List<Fragment> fragments = new ArrayList<>();
        private String[] titles = {"微信", "通讯录", "发现", "我"};

        public MainAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(TextFragment.newInstance(titles[0]));
            fragments.add(new GroupFragmentNew());
            fragments.add(TextFragment.newInstance(titles[2]));
            fragments.add(TextFragment.newInstance(titles[3]));
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
//            if (0 == position) {
//                alphaIndicator.getTabView(0).showNumber(alphaIndicator.getTabView(0).getBadgeNumber() - 1);
//            } else if (2 == position) {
//                alphaIndicator.getCurrentItemView().removeShow();
//            } else if (3 == position) {
//                alphaIndicator.removeAllBadge();
//            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
