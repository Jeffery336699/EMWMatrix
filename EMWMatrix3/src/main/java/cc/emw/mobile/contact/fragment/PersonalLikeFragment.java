package cc.emw.mobile.contact.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.adapter.PersonalLikeAdapter;
import cc.emw.mobile.contact.bean.PersonalLikeBean;

public class PersonalLikeFragment extends BaseFragment {

    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private List<String> mListm;
    private List<PersonalLikeBean> mList1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initRecycleView();
        mListm = new ArrayList<>();
        mListm.add("1");
        mListm.add("2");
        mListm.add("3");
        mListm.add("4");
        mListm.add("5");
        initData();
        return view;
    }

    private void initData() {


        mList1 = new ArrayList<>();
        mList1.add(0,new PersonalLikeBean(1,"公共(2)"));
        mList1.add(1,new PersonalLikeBean(2,mListm));
        mList1.add(2,new PersonalLikeBean(1,"闲聊(3)"));
        mList1.add(3,new PersonalLikeBean(2,mListm));
        mList1.add(4,new PersonalLikeBean(1,"茶水(4)"));
        mList1.add(5,new PersonalLikeBean(2,mListm));

        Log.d("junjun", mList1.toString());

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(new PersonalLikeAdapter(mList1));
            }
        });

    }

    private void initRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
