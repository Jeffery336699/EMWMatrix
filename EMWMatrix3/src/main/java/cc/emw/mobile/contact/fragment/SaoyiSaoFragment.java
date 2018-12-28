package cc.emw.mobile.contact.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.contact.PersonInfoActivity;
import cc.emw.mobile.contact.PersonInfoActivity2;
import cc.emw.mobile.sale.BeepManager;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_saoyi_sao)
public class SaoyiSaoFragment extends BaseFragment implements ZXingScannerView.ResultHandler {

    @ViewInject(R.id.content_frame)
    private ViewGroup contentFrame;

    private ZXingScannerView mScannerView;
    private BeepManager beepManager;

    public SaoyiSaoFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        beepManager = new BeepManager(getActivity());
        mScannerView = new ZXingScannerView(getActivity());
        contentFrame.addView(mScannerView);
    }

    @Override
    public void handleResult(Result rawResult) {
        beepManager.playBeepSoundAndVibrate();
        if (rawResult != null && rawResult.getText() != null) {
            try {
                Intent intent = new Intent(getActivity(), PersonInfoActivity2.class);
                intent.putExtra("start_anim", false);
                intent.putExtra(PersonInfoActivity2.PERSON_ID, Integer.valueOf(rawResult.getText()));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "扫描失败,请重试", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "扫描失败,请重试", Toast.LENGTH_SHORT).show();
        }
        getActivity().finish();
    }

}
