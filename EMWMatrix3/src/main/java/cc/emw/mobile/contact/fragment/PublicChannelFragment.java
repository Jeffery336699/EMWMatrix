package cc.emw.mobile.contact.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;

public class PublicChannelFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.public_channel_fragment, container, false);
        return view;
    }
}