package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.emw.mobile.R;
import cc.emw.mobile.project.entities.Elements2;
import cc.emw.mobile.project.entities.SelectItems;

/**
 * Created by jven.wu on 2016/5/25.
 */
public class FormMultiSelectAdapter extends BaseAdapter {
    public static final String TAG = "FormMultiSelectAdapter";
    private Context mContext;
    private ArrayList<SelectItems> selectItems;
    private Elements2 element;
    private Map<Integer,Boolean> selMap;

    public FormMultiSelectAdapter(Context mContext) {
        this.mContext = mContext;
        selectItems = new ArrayList<>();
    }

    public void setSelectItems(Elements2 elem) {
        this.element = elem;
        this.selectItems.clear();
        this.selectItems.addAll(elem.SelectItems);
        selMap = new HashMap<>();
        String[] strings = elem.Value.split(",");
        for(int i = 0;i<selectItems.size();i++){
            selMap.put(i,false);
            for(int j = 0;j<strings.length;j++){
                String val = elem.SelectItems.get(i).Value;
                if(val.equals(strings[j])){
                    selMap.put(i,true);
                    break;
                }
            }
        }
    }

    public String getRetItems() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i<selectItems.size();i++){
            if(selMap.get(i)){
                stringBuilder.append(selectItems.get(i).Value);
                stringBuilder.append(",");
            }
        }
        String retString = stringBuilder.toString();
        if(!TextUtils.isEmpty(retString)) {
            retString = retString.substring(0, retString.lastIndexOf(","));
        }
        return retString;
    }

    @Override
    public int getCount() {
        return selectItems == null ? 0 :selectItems.size();
    }

    @Override
    public Object getItem(int position) {
        return selectItems == null ? null : selectItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView == null){
            vh = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.listitem_project_relativetask,parent,false);
            vh.name = (TextView)convertView
                    .findViewById(R.id.tv_summarytask_name);
            vh.description = (TextView)convertView
                    .findViewById(R.id.tv_summarytask_description);
            vh.selImg = (ImageView)convertView
                    .findViewById(R.id.iv_relativetask_select);
            vh.arrowImg = (ImageView)convertView
                    .findViewById(R.id.iv_summarytask_arrow);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder)convertView.getTag();
        }
        SelectItems item = selectItems.get(position);
        vh.name.setText(item.Text);
        vh.description.setVisibility(View.GONE);
        vh.arrowImg.setVisibility(View.GONE);
        vh.selImg.setImageResource(selMap.get(position) ? R.drawable.ico_duoxuan
                : R.drawable.cm_multi_select_nor);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selMap.put(position,selMap.get(position) ? false : true);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
    class ViewHolder{
        TextView name;
        TextView description;
        ImageView arrowImg;
        ImageView selImg;
    }
}
