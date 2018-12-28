package cc.emw.mobile.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zf.iosdialog.widget.AlertDialog;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.dynamic.DynamicDiscussActivity;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;

/**
 * 评论带文件
 */
public class DiscussFileLayout extends RelativeLayout {

    protected View view;
    protected LinearLayout mFileLayout;
    protected Context context;

    private ArrayList<UserNote.UserNoteFile> fileList; //文件列表数据

    public DiscussFileLayout(Context context) {
        this(context, null);
    }

    public DiscussFileLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        fileList = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_discuss_file, this);
        mFileLayout = (LinearLayout) view.findViewById(R.id.ll_sharefile_item);
        Button addBtn = (Button) view.findViewById(R.id.btn_sharefile_add);
        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgIntent = new Intent(context, FileSelectActivity.class);
                imgIntent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                imgIntent.putExtra("click_pos_y", location[1]);
                ((Activity)context).startActivityForResult(imgIntent, DynamicDiscussActivity.CHOSE_FILE_CODE);
            }
        });
    }

    public void setData(ArrayList<ApiEntity.Files> fileList) {
        if (fileList != null && fileList.size() > 0) {
            for (int i = 0; i < fileList.size(); i++) {
                UserNote.UserNoteFile file = HelpUtil.files2UserNoteFile(fileList.get(i));
                addFileItem(file);
            }
        }
    }

    public ArrayList<UserNote.UserNoteFile> getData() {
        return fileList;
    }

    public void clearData() {
        if (fileList != null) {
            fileList.clear();
            mFileLayout.removeAllViews();
        }

    }

    /**
     * 显示选择的文件
     * @param file
     */
    private void addFileItem(final UserNote.UserNoteFile file) {
        final View childView = LayoutInflater.from(context).inflate(R.layout.share_tab_file_item, null);
//    	ImageButton delBtn = (ImageButton) childView.findViewById(R.id.btn_sharefile_del);
        IconTextView delTv = (IconTextView) childView.findViewById(R.id.tv_sharefile_del);
        ImageView iconIv = (ImageView) childView.findViewById(R.id.iv_sharefile_icon);
        TextView nameTv = (TextView) childView.findViewById(R.id.tv_sharefile_name);
        TextView timeTv = (TextView) childView.findViewById(R.id.tv_sharefile_time);
        TextView sizeTv = (TextView) childView.findViewById(R.id.tv_sharefile_size);
//        TextView projectTv = (TextView) childView.findViewById(R.id.tv_sharefile_project);

        delTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog(context).builder().setMsg(context.getString(R.string.deletefile_tips))
                        .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(context.getString(R.string.confirm), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFileLayout.removeView(childView);
                                fileList.remove(file);
                            }
                        })
                        .setNegativeButton(context.getString(R.string.cancel), new OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        }).show();

            }
        });
        iconIv.setImageResource(FileUtil.getResIconId(file.FileName));
        nameTv.setText(file.FileName);
        timeTv.setText("");
        sizeTv.setText(FileUtil.getReadableFileSize(file.Length));
        mFileLayout.addView(childView);
        fileList.add(file);
    }
}
