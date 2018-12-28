package cc.emw.mobile.main.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.zf.iosdialog.widget.ActionSheetDialog.SheetItemColor;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.activity.ChatActivity;
import cc.emw.mobile.chat.util.DownLoadImage;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import uk.co.senab.photoview.PhotoView;

/**
 * 图片列表Fragment
 */
public class PhotoFragment extends Fragment implements OnLongClickListener {

	private PhotoView mPhotoView;
	private LinearLayout mLoadingLayout;

	private int position;
	private String imgUrl;
	private boolean isFormat; //true：图片名称；false：完整url
	private boolean isInit; // 是否可以开始加载数据
	private String url;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类

	public static PhotoFragment newInstance(int position, String imgUrl,
			boolean isFormat) {
		PhotoFragment f = new PhotoFragment();
		Bundle b = new Bundle();
		b.putInt("position", position);
		b.putString("imgUrl", imgUrl);
		b.putBoolean("isFormat", isFormat);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt("position");
		imgUrl = getArguments().getString("imgUrl");
		isFormat = getArguments().getBoolean("isFormat");
		isInit = true;

		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
		// .showStubImage(imageRes);
		// .showImageOnLoading(R.drawable.default_image_img)// 设置图片下载期间显示的图片
		// .showImageForEmptyUri(R.drawable.ic_empty) //
		// 设置图片Uri为空或是错误的时候显示的图片
		// .showImageOnFail(R.drawable.ic_error) //
		// 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_photo, null, false);
		mPhotoView = (PhotoView) root.findViewById(R.id.photoview);
		mLoadingLayout = (LinearLayout) root
				.findViewById(R.id.loading_dialog_linearLayout);
		mPhotoView.setOnLongClickListener(this);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		url = isFormat ? String.format(Const.DOWN_FILE_URL,
				PrefsUtil.readUserInfo().CompanyCode, imgUrl) : imgUrl;
		ImageLoader.getInstance().displayImage(url, mPhotoView, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						mLoadingLayout.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						mLoadingLayout.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						mLoadingLayout.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						mLoadingLayout.setVisibility(View.GONE);
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isInit) {// fragment可见且第一次进入时加载数据
			isInit = false;// 加载数据完成
			// requestData(offset, "");
		}
	}

	@Override
	public boolean onLongClick(View arg0) {
		ActionSheetDialog dialog = new ActionSheetDialog(getContext())
				.builder();
		dialog.addSheetItem("转发给好友", SheetItemColor.Blue,
				new OnSheetItemClickListener() {

					@Override
					public void onClick(int which) {
						Intent intent = new Intent(getActivity(),
								ContactSelectActivity.class);
						intent.putExtra(
								ContactSelectActivity.EXTRA_SELECT_TYPE,
								ContactSelectActivity.RADIO_SELECT);
						startActivityForResult(intent, 10);
					}
				});
		dialog.addSheetItem("收藏", SheetItemColor.Blue,
				new OnSheetItemClickListener() {

					@Override
					public void onClick(int which) {
						ToastUtil.showToast(getContext(), "收藏成功！",
								R.drawable.tishi_ico_gougou);
						DownLoadImage.saveImages(url, getContext());
					}
				});
		dialog.show();
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK && 10 == requestCode) {
			switch (requestCode) {
			case 10:
				UserInfo selectUser = (UserInfo) data
						.getSerializableExtra("select_user");
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra("SenderID", selectUser.ID);
				intent.putExtra("type", 1);
				intent.putExtra("name", selectUser.Name);
				intent.putExtra("url_image",
						url.replace(Const.BASE_URL, ""));
				startActivity(intent);
				getActivity().finish();
				break;
			}
		}
	}
}
