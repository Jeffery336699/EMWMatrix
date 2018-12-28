package cc.emw.mobile.file;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import cc.emw.mobile.LogLongUtil;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ListDialog;

/**
 * 知识库
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_file)
public class FileActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderNoticeBtn; // 顶部条更多按钮

    private Dialog mLoadingDialog; //加载框

    private static final int CHOSE_FILE_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.file);
        mHeaderNoticeBtn.setImageResource(R.drawable.nav_btn_notice);
        mHeaderNoticeBtn.setVisibility(View.GONE);

        initAddDialog();

        findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileActivity.this, FileListActivity.class);
                intent.putExtra("file_type", 3);
                intent.putExtra("file_name", getString(R.string.file_myfile));
                startActivity(intent);
            }
        });
        findViewById(R.id.rl2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileActivity.this, FileListActivity.class);
                intent.putExtra("file_type", 4);
                intent.putExtra("file_name", getString(R.string.file_sharefile));
                startActivity(intent);
            }
        });
        findViewById(R.id.rl3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileActivity.this, FileListActivity.class);
                intent.putExtra("file_type", 6);
                intent.putExtra("file_name", getString(R.string.file_mysharefile));
                startActivity(intent);
            }
        });
        findViewById(R.id.rl4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileActivity.this, FileListActivity.class);
                intent.putExtra("file_type", 5);
                intent.putExtra("file_name", getString(R.string.file_cancelfile));
                startActivity(intent);
            }
        });

        findViewById(R.id.itv_file_add).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mAddDialog.show();
            }
        });
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
//				HelpUtil.hideSoftInput(this, mSearchEt);
                onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                Intent noticeIntent = new Intent(this, TestActivity.class);
                startActivity(noticeIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CHOSE_FILE_CODE:
                    String path = FileUtil.getPath(this, data.getData());
                    uploadFile(path);
                    break;
            }
        }
    }

    /**
     * 上传文件
     * @param path
     */
    private void uploadFile(String path) {
        LogLongUtil.e("Const","----FileActivity--------");
        RequestParam params = new RequestParam(Const.UPLOAD_FILE_URL);
        params.setMultipart(true);
        File file = new File(path);
        params.addBodyParameter("file_"+file.getName(), file);
        ArrayList<ApiEntity.Files> fileList = new ArrayList<ApiEntity.Files>();
        ApiEntity.Files noteFile = new ApiEntity.Files();
        noteFile.Name = file.getName();
        noteFile.Length = file.length();
        noteFile.Creator = PrefsUtil.readUserInfo().ID;
        noteFile.Url = file.getName();
        noteFile.ParentID = 0;
        fileList.add(noteFile);
        params.addBodyParameter("UploadData", new Gson().toJson(fileList));
        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips4);
        mLoadingDialog.show();
        Callback.Cancelable cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(FileActivity.this, R.string.filelist_upload_error);
            }
            @Override
            public void onFinished() {

            }
            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(FileActivity.this, R.string.filelist_upload_success, R.drawable.tishi_ico_gougou);
                } else {
                    ToastUtil.showToast(FileActivity.this, R.string.filelist_upload_error);
                }
            }
        });
    }

    private ListDialog mAddDialog;
    private void initAddDialog() {
        mAddDialog = new ListDialog(this, false);
        mAddDialog.addItem(R.string.filelist_more_upload, 1);
        mAddDialog.addItem(R.string.filelist_more_create, 2);
        mAddDialog.setOnItemSelectedListener(new ListDialog.OnItemSelectedListener() {
            @Override
            public void selected(View view, ListDialog.Item item, int position) {
                switch (item.id) {
                    case 1:
                        setStartAnim(false);
                        choseFileFromSystemFile();
                        setStartAnim(true);
                        break;
                    case 2:
                        Intent intent = new Intent(FileActivity.this, FileCreateActivity.class);
                        intent.putExtra("start_anim", false);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    /**
     * 从本地文件管理选取文件
     */
    private void choseFileFromSystemFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.filelist_choose_title)),
                    CHOSE_FILE_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            ToastUtil.showToast(this, R.string.filelist_choose_error);
        }
    }
}
