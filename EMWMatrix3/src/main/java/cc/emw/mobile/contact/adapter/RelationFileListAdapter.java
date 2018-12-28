package cc.emw.mobile.contact.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.file.FilePreviewActivity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.NotificationUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;

/**
 * 知识库列表Adapter
 *
 * @author shaobo.zhuang
 */
public class RelationFileListAdapter extends BaseAdapter implements OnClickListener {

    private Context mContext;
    private ArrayList<Files> mDataList;
    private int lastPosition = -1;
    private Dialog mLoadingDialog; //加载框
    private Handler handler;
    private boolean isExpand; //是否可以展开显示操作条
    private static SparseIntArray mProgressMap;
    private int power;

    public RelationFileListAdapter(Context context,
                                   ArrayList<Files> dataList) {
        this(context, dataList, null, true);
    }

    public RelationFileListAdapter(Context context,
                                   ArrayList<Files> dataList, int power) {
        this(context, dataList, null, true);
        this.power = power;
    }

    public RelationFileListAdapter(Context context,
                                   ArrayList<Files> dataList, boolean isExpand) {
        this(context, dataList, null, isExpand);
    }

    public RelationFileListAdapter(Context context,
                                   ArrayList<Files> dataList, Handler handler) {
        this(context, dataList, handler, true);
    }

    public RelationFileListAdapter(Context context,
                                   ArrayList<Files> dataList, Handler handler, boolean isExpand) {
        this.mContext = context;
        this.mDataList = dataList;
        this.handler = handler;
        this.isExpand = isExpand;
        this.mProgressMap = new SparseIntArray();
    }

    public void setProgressMap(int fid, int progress) {
        mProgressMap.put(fid, progress);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_relation_filelist, null);
            vh.iconIv = (ImageView) convertView.findViewById(R.id.iv_filelist_icon);
            vh.nameTv = (TextView) convertView.findViewById(R.id.tv_filelist_name);
            vh.timeTv = (TextView) convertView.findViewById(R.id.tv_filelist_time);
            vh.sizeTv = (TextView) convertView.findViewById(R.id.tv_filelist_size);
            vh.projectTv = (TextView) convertView.findViewById(R.id.tv_filelist_project);
            vh.watchLayout = (LinearLayout) convertView.findViewById(R.id.rl_filelist_open);
            vh.existItv = (IconTextView) convertView.findViewById(R.id.itv_filelist_exist);
            vh.downLayout = (IconTextView) convertView.findViewById(R.id.rl_filelist_download);
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) convertView.getTag(R.id.tag_first);
        }
        final Files file = mDataList.get(position);
        if (file.Name == null || file.Name.equals("")) {
            file.Name = file.ThumbFileName;
        }

        vh.nameTv.setText(file.Name);
        vh.timeTv.setText(file.UpdateTime);
        vh.sizeTv.setText(FileUtil.getReadableFileSize(file.Length));
        if (file.Type == 1) {
            vh.iconIv.setImageResource(R.drawable.list_ico_folder);
            vh.sizeTv.setVisibility(View.GONE);
            vh.projectTv.setVisibility(View.GONE);
            vh.downLayout.setVisibility(View.GONE);
            vh.existItv.setVisibility(View.GONE);
        } else {
            vh.iconIv.setImageResource(FileUtil.getResIconId(file.Name));
            vh.sizeTv.setVisibility(View.VISIBLE);
            vh.projectTv.setVisibility(View.GONE);
            vh.existItv.setVisibility(FileUtil.hasFile(EMWApplication.filePath + file.Url, file.Length) ? View.VISIBLE : View.GONE);
        }
        vh.downLayout.setTag(file);
        vh.downLayout.setOnClickListener(this);
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Files noteFile = (Files) v.getTag(R.id.tag_second);
                String path = EMWApplication.filePath + FileUtil.getFileName(noteFile.Url);
                if (FileUtil.hasFile(path, noteFile.Length)) {
                    FileUtil.openFile(mContext, path);
                } else if (noteFile.Url.contains(".doc") || noteFile.Url.contains(".docx") || noteFile.Url.contains(".xls") || noteFile.Url.contains(".xlsx")) {
                    Intent previewIntent = new Intent(mContext, FilePreviewActivity.class);
                    previewIntent.putExtra(FilePreviewActivity.EXTENSION, noteFile.Name);
                    previewIntent.putExtra(FilePreviewActivity.FILE_ID, noteFile.ID);
                    previewIntent.putExtra(FilePreviewActivity.CREATOR, noteFile.Creator);
                    mContext.startActivity(previewIntent);
                } else {
                    ToastUtil.showToast(mContext, "请先下载该文件");
                }
            }
        });
        convertView.setTag(R.id.tag_second, file);
        return convertView;
    }

    static class ViewHolder {
        ImageView iconIv;
        TextView nameTv;
        TextView timeTv;
        TextView sizeTv;
        TextView projectTv;
        LinearLayout watchLayout;
        IconTextView downLayout;
        IconTextView existItv;
    }

    @Override
    public void onClick(View v) {
        final Files noteFile = (Files) v.getTag();
        switch (v.getId()) {
            case R.id.rl_filelist_download: //下载
                if (noteFile.Url != null) { // 通过服务下载文件
                    if (!FileUtil.hasFile(EMWApplication.filePath + FileUtil.getFileName(noteFile.Url), noteFile.Length)) {
                        lastPosition = -1;
                        String name = "";
                        name = getExtensionName(noteFile.Url);
                        String fileUrl = HelpUtil.getFileURL(noteFile);
                        if (name.equals("jpg") || name.equals("png") || name.equals("gif") || name.equals("jpeg")) {
                            fileUrl = HelpUtil.getFileImage(noteFile);
                        } else if (name.equals("avi") || name.equals("3gp") || name.equals("mp3") || name.equals("mp4")) {
                            fileUrl = HelpUtil.getFileVideo(noteFile);
                        }
                        NotificationUtil.notificationForDLAPK(mContext, fileUrl, EMWApplication.filePath, noteFile.ID, true);
                    } else {
                        ToastUtil.showToast(mContext, R.string.download_fileexist_tips);
                    }
                }
                break;
        }
    }

    /**
     * Java文件操作 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }


}
