package cc.emw.mobile.contact.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Hashtable;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_business_card)
public class BusinessCardFragment extends BaseFragment {

    @ViewInject(R.id.iv_qr_code)
    private ImageView mImgQRCode;
    @ViewInject(R.id.civ_user_img)
    private CircleImageView mImgHead;
    @ViewInject(R.id.tv_user_name)
    private TextView mUserName;
    @ViewInject(R.id.tv_user_depart)
    private TextView mUserDepart;

    public BusinessCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserName.setText(TextUtils.isEmpty(PrefsUtil.readUserInfo().Name) ? "未知" : PrefsUtil.readUserInfo().Name);
        mUserDepart.setText(TextUtils.isEmpty(PrefsUtil.readUserInfo().DeptName) ? "暂无部门" : PrefsUtil.readUserInfo().DeptName);
        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode, PrefsUtil.readUserInfo().Image);
        Picasso.with(getActivity())
                .load(uri)
                .resize(DisplayUtil.dip2px(getActivity(), 40), DisplayUtil.dip2px(getActivity(), 40))
                .centerCrop()
                .config(Bitmap.Config.ALPHA_8)
                .placeholder(R.drawable.cm_img_head)
                .error(R.drawable.cm_img_head)
                .into(mImgHead);
        createQRImage(PrefsUtil.readUserInfo().ID + "");
    }

    public void createQRImage(String url) {
        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            int QR_WIDTH = DisplayUtil.dip2px(getActivity(), 200);
            int QR_HEIGHT = DisplayUtil.dip2px(getActivity(), 211);
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            mImgQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
