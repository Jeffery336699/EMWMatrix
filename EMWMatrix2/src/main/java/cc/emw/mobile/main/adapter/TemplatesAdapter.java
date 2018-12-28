package cc.emw.mobile.main.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.bean.Templates;
import cc.emw.mobile.main.SaleOrderActivity;
import cc.emw.mobile.net.ApiEntity.Navigation;
import cc.emw.mobile.net.ApiEntity.Template;

public class TemplatesAdapter extends BaseExpandableListAdapter {
    private List<Templates> templateList;
    private Context context;

    public TemplatesAdapter(Context context) {
        this.context = context;
        templateList = new ArrayList<Templates>();
        Templates temps1 = new Templates();
        Template temp1 = new Template();
        temp1.ID = -1;
        temp1.Name = "Talker";
        List<Navigation> nav1 = new ArrayList<Navigation>();
        temps1.setTemplate(temp1);
        temps1.setNavigations(nav1);
        templateList.add(temps1);

        Templates temps2 = new Templates();
        Template temp2 = new Template();
        temp2.ID = -2;
        temp2.Name = "工作台";
        List<Navigation> nav2 = new ArrayList<Navigation>();
        temps2.setTemplate(temp2);
        temps2.setNavigations(nav2);
        templateList.add(temps2);

        Templates temps3 = new Templates();
        Template temp3 = new Template();
        temp3.ID = -3;
        temp3.Name = "地图";
        List<Navigation> nav3 = new ArrayList<Navigation>();
        temps3.setTemplate(temp3);
        temps3.setNavigations(nav3);
        templateList.add(temps3);

        Templates temps4 = new Templates();
        Template temp4 = new Template();
        temp4.ID = -4;
        temp4.Name = "零售";
        List<Navigation> nav4 = new ArrayList<>();
        temps4.setTemplate(temp4);
        temps4.setNavigations(nav4);
        templateList.add(temps4);
    }

    public void setData(List<Template> templates, List<Navigation> navigations) {
        //组织数据
        for (Template template : templates) {
            Templates t = new Templates();
            List<Navigation> nav = new ArrayList<Navigation>();
            for (Navigation navigation : navigations) {
                //有pageid，且url为空字符串才加入
                if (template.ID == navigation.TemplateID && navigation.PAGEID > 0 && TextUtils.isEmpty(navigation.URL)) {
                    nav.add(navigation);
                }
            }
            t.setTemplate(template);
            t.setNavigations(nav);
            templateList.add(t);
        }
        //不显示没有子项的分组
        Iterator<Templates> it = templateList.iterator();
        while (it.hasNext()) {
            Templates temps = it.next();
            if (temps.getNavigations().size() == 0 && temps.getTemplate().ID > 0) {
                it.remove();//注意此处不能用list.remove(it.next());
            }
        }
    }

    @Override
    public Navigation getChild(int groupPosition, int childPosition) {
        return templateList.get(groupPosition).getNavigations()
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        ChildViewHolder cvh;
        if (convertView == null) {
            cvh = new ChildViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_leftmenu_child, null);
            cvh.childIconTv = (TextView) convertView.findViewById(R.id.tv_leftmenu_childicon);
            cvh.childNameTv = (TextView) convertView.findViewById(R.id.tv_leftmenu_childname);
            convertView.setTag(R.id.tag_first, cvh);
        } else {
            cvh = (ChildViewHolder) convertView.getTag(R.id.tag_first);
        }
        final Navigation nav = getChild(groupPosition, childPosition);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "iconfont/fontello.ttf");
        cvh.childIconTv.setTypeface(typeface);
        cvh.childIconTv.setText(Html.fromHtml(EMWApplication.getIcon(nav.ICON))); // "&#xe809;"

        cvh.childNameTv.setText(nav.Name);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SaleOrderActivity.class);
                intent.putExtra("nav_id", nav.PAGEID);
                intent.putExtra("nav_name", nav.Name);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return templateList.get(groupPosition).getNavigations().size();
    }

    @Override
    public Template getGroup(int groupPosition) {
        return templateList.get(groupPosition).getTemplate();
    }

    @Override
    public int getGroupCount() {
        return templateList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder gvh;
        if (convertView == null) {
            gvh = new GroupViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_leftmenu_group, null);
            gvh.groupNameTv = (TextView) convertView.findViewById(R.id.tv_leftmenu_groupname);
            gvh.groupArrowIv = (ImageView) convertView.findViewById(R.id.iv_leftmenu_grouparrow);
            convertView.setTag(R.id.tag_first, gvh);
        } else {
            gvh = (GroupViewHolder) convertView.getTag(R.id.tag_first);
        }

        Template temp = getGroup(groupPosition);
        gvh.groupNameTv.setText(temp.Name);
        if (temp.ID > 0) {
            gvh.groupArrowIv
                    .setImageResource(isExpanded ? R.drawable.list_up_jiantou
                            : R.drawable.list_down_jiantou);
            gvh.groupArrowIv.setVisibility(View.VISIBLE);
            convertView.setBackgroundResource(isExpanded ? R.color.main_menuitem_pressed : R.color.main_menu_bg);
        } else {
            gvh.groupArrowIv.setVisibility(View.GONE);
            convertView.setBackgroundResource(R.drawable.main_menu_item_bg1);
        }

        convertView.setTag(R.id.tag_second, temp);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupViewHolder {
        TextView groupNameTv;
        ImageView groupArrowIv;
    }

    public class ChildViewHolder {
        TextView childIconTv;
        TextView childNameTv;
    }
}
