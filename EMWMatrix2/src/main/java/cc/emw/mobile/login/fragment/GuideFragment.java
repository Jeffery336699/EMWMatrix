package cc.emw.mobile.login.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import cc.emw.mobile.R;

public class GuideFragment extends Fragment {

    final static String LAYOUT_ID = "layoutid";

    private int mLayoutId;
    private Button btnLogin;
    private View rootView;
    private ImageView imageFaXian;
    private ImageView imageLianJie;
    private ImageView imageXieTong;
    private ImageView blue;
    private ImageView green;
    private ImageView orenge;

    public static GuideFragment newInstance(int layoutId) {
        GuideFragment pane = new GuideFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = getArguments().getInt(LAYOUT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                getArguments().getInt(LAYOUT_ID, -1), container, false);
        btnLogin = (Button) rootView.findViewById(R.id.btn_into_login);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });
        init();

        stopAnim(mLayoutId);
        showAnim(mLayoutId);

		/*
         * if (mLayoutId == R.layout.fragment_guide4) {
		 * rootView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { endTutorial(); } }); }
		 */

        return rootView;
    }

    private void init() {
        imageFaXian = (ImageView) rootView
                .findViewById(R.id.guide_faxian);
        imageLianJie = (ImageView) rootView
                .findViewById(R.id.guide_lianjie);
        imageXieTong = (ImageView) rootView
                .findViewById(R.id.guide_xietong);
        blue = (ImageView) rootView
                .findViewById(R.id.guide3_blue);
        green = (ImageView) rootView
                .findViewById(R.id.guide3_green);
        orenge = (ImageView) rootView
                .findViewById(R.id.guide3_orenge);
    }

    /**
     * 开始显示动画
     *
     * @param id
     */
    public void showAnim(int id) {
        switch (id) {
            case R.layout.fragment_guide1:

                imageFaXian.setVisibility(View.GONE);
                imageLianJie.setVisibility(View.GONE);
                imageXieTong.setVisibility(View.GONE);

                final Animation anim1 = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.guide1_faxian_anim);
                final Animation anim2 = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.guide1_lianjie_anim);
                final Animation anim3 = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.guide1_xietong_anim);

                imageFaXian.setAnimation(anim1);
                anim1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        imageFaXian.setVisibility(View.VISIBLE);
                        imageLianJie.clearAnimation();
                        imageXieTong.clearAnimation();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageLianJie.setAnimation(anim2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                anim2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        imageLianJie.setVisibility(View.VISIBLE);
                        imageXieTong.clearAnimation();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageXieTong.setAnimation(anim3);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                anim3.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        imageXieTong.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                break;
            case R.layout.fragment_guide2:
                ImageView imageGouTong = (ImageView) rootView
                        .findViewById(R.id.guide_contact_img);
                Animation anim4 = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.guide2_goutong_anim);
                imageGouTong.setAnimation(anim4);
                break;
            case R.layout.fragment_guide3:

                green.setVisibility(View.GONE);
                orenge.setVisibility(View.GONE);
                blue.setVisibility(View.GONE);

                final Animation animBlue = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.guide3_blue_anim);
                final Animation animGreen = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.guide3_green_anim);
                final Animation animOrenge = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.guide3_orenge_anim);
                green.setAnimation(animGreen);
                animGreen.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        green.setVisibility(View.VISIBLE);
                        orenge.clearAnimation();
                        blue.clearAnimation();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        orenge.setAnimation(animOrenge);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                animOrenge.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        orenge.setVisibility(View.VISIBLE);
                        blue.clearAnimation();
                        green.clearAnimation();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        blue.setAnimation(animBlue);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                animBlue.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        blue.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
            case R.layout.fragment_guide4:
                ImageView calImg = (ImageView) rootView
                        .findViewById(R.id.guide4_calendar_img);
                Animation calAnim = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.guide4_calendar_anim);
                calImg.setAnimation(calAnim);
                break;
            default:
                break;
        }
    }

    /**
     * 结束显示动画
     *
     * @param id
     */
    public void stopAnim(int id) {
        switch (id) {
            case R.layout.fragment_guide1:
                imageFaXian.setVisibility(View.GONE);
                imageLianJie.setVisibility(View.GONE);
                imageXieTong.setVisibility(View.GONE);
                imageFaXian.clearAnimation();
                imageLianJie.clearAnimation();
                imageXieTong.clearAnimation();
                break;
            case R.layout.fragment_guide2:

                break;
            case R.layout.fragment_guide3:
                green.setVisibility(View.GONE);
                orenge.setVisibility(View.GONE);
                blue.setVisibility(View.GONE);
                green.clearAnimation();
                orenge.clearAnimation();
                blue.clearAnimation();
                break;
            case R.layout.fragment_guide4:

                break;
            default:
                break;
        }
    }

    private void endTutorial() {
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.abc_fade_in,
                R.anim.abc_fade_out);
    }
}