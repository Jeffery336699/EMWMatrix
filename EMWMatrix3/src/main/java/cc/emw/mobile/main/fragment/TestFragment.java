package cc.emw.mobile.main.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.czt.mp3recorder.MP3Recorder;

import java.io.File;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.dynamic.PlanActivity;
import cc.emw.mobile.dynamic.ShareActivity;
import cc.emw.mobile.util.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {

    private MP3Recorder mRecorder;

    public TestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_test, container, false);
        TextView textView = (TextView) root.findViewById(R.id.test);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name = "voice_" + System.currentTimeMillis() + Math.round(Math.random() * 1000.0D) + ".mp3";
                    mRecorder = new MP3Recorder(new File(EMWApplication.audioPath, name));
                    mRecorder.setRecorderListener(new MP3Recorder.MP3RecorderListener() {
                        @Override
                        public void recorderCancel() {

                        }

                        @Override
                        public void recorderShort() {
                            ToastUtil.showToast(getActivity(), "录音时间小于1s，请重新录音！");
                        }

                        @Override
                        public void recorderTime(int second) {
                            //录制的秒数，每一秒调用一次
                        }

                        @Override
                        public void recorderPath(String path) {
//                            uploadAudios(path, FileUtil.getMediaLength(path));
                        }
                    });
                    mRecorder.start();
//                    mRecorder.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

}
