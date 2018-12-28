package cc.emw.mobile.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.Templates;
import cc.emw.mobile.form.FormActivity;
import cc.emw.mobile.form.FormWebActivity;
import cc.emw.mobile.net.ApiEntity.Navigation;
import cc.emw.mobile.net.ApiEntity.Template;
import cc.emw.mobile.util.CircularAnim;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.IconTextView;
//
public class TemplatesAdapter extends BaseExpandableListAdapter {
    private List<Templates> templateList;
    private Context context;

    public TemplatesAdapter(Context context) {
        this.context = context;
        templateList = new ArrayList<>();
        initData();
    }

    private void initData() {
        /*Templates temps1 = new Templates();
        Template temp1 = new Template();
        temp1.ID = -1;
        temp1.AliasName = "Talker";
        List<Navigation> nav1 = new ArrayList<>();
        temps1.setTemplate(temp1);
        temps1.setNavigations(nav1);
        templateList.add(temps1);

        Templates temps2 = new Templates();
        Template temp2 = new Template();
        temp2.ID = -2;
        temp2.AliasName = "圈子";
        List<Navigation> nav2 = new ArrayList<>();
        temps2.setTemplate(temp2);
        temps2.setNavigations(nav2);
        templateList.add(temps2);

        Templates temps3 = new Templates();
        Template temp3 = new Template();
        temp3.ID = -3;
        temp3.AliasName = "Time Tracking";
        List<Navigation> nav3 = new ArrayList<>();
        temps3.setTemplate(temp3);
        temps3.setNavigations(nav3);
        templateList.add(temps3);

        Templates temps4 = new Templates();
        Template temp4 = new Template();
        temp4.ID = -4;
        temp4.AliasName = "扫码登录";
        List<Navigation> nav4 = new ArrayList<>();
        temps4.setTemplate(temp4);
        temps4.setNavigations(nav4);
        templateList.add(temps4);*/

        /*Templates temps5 = new Templates();
        Template temp5 = new Template();
        temp5.ID = -5;
        temp5.AliasName = "地图";
        List<Navigation> nav5 = new ArrayList<>();
        temps5.setTemplate(temp5);
        temps5.setNavigations(nav5);
        templateList.add(temps5);

        Templates temps6 = new Templates();
        Template temp6 = new Template();
        temp6.ID = -6;
        temp6.AliasName = "零售";
        List<Navigation> nav6 = new ArrayList<>();
        temps6.setTemplate(temp6);
        temps6.setNavigations(nav6);
        templateList.add(temps6);*/
    }
    //提供外面接口
    public void setData(List<Template> templates, List<Navigation> navigations) {
        templateList.clear();
        initData();
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
            cvh.childIconTv = (IconTextView) convertView.findViewById(R.id.tv_leftmenu_childicon);
            cvh.childNameTv = (TextView) convertView.findViewById(R.id.tv_leftmenu_childname);
            convertView.setTag(R.id.tag_first, cvh);
        } else {
            cvh = (ChildViewHolder) convertView.getTag(R.id.tag_first);
        }
        final Navigation nav = getChild(groupPosition, childPosition);
        cvh.childIconTv.setIconText(IconTextView.getIconCode(nav.ICON));
        if (!TextUtils.isEmpty(nav.AliasName)) {
            cvh.childNameTv.setText(nav.AliasName);
        } else {
            cvh.childNameTv.setText(nav.Name);
        }
        //每个item监听事件
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                if (PrefsUtil.isFormWeb()) {
                    //显示web列表
                    intent = new Intent(context, FormWebActivity.class);
                    intent.putExtra(FormWebActivity.HAS_PROGRESSBAR, false);
                    intent.putExtra(FormWebActivity.PAGE_ID, nav.PAGEID);
                } else {
                    //显示原生android界面
                    intent = new Intent(context, FormActivity.class);
                    intent.putExtra("nav_id", nav.PAGEID);
                    intent.putExtra("nav_name", nav.Name);
                }
                CircularAnim.fullActivity((Activity)context, v)
                        .colorOrImageRes(R.color.white)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                context.startActivity(intent);
                            }
                        });

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
            gvh.groupArrowTv = (IconTextView) convertView.findViewById(R.id.tv_leftmenu_grouparrow);
            convertView.setTag(R.id.tag_first, gvh);
        } else {
            gvh = (GroupViewHolder) convertView.getTag(R.id.tag_first);
        }

        Template temp = getGroup(groupPosition);
        if (!TextUtils.isEmpty(temp.AliasName)) {
            gvh.groupNameTv.setText(temp.AliasName);
        } else {
            gvh.groupNameTv.setText(temp.Name);
        }
        if (temp.ID > 0) {
            gvh.groupArrowTv.setIconText(isExpanded ? "eb6a" : "eb67");
            gvh.groupArrowTv.setVisibility(View.VISIBLE);
            convertView.setBackgroundResource(isExpanded ? R.color.main_menuitem_pressed : R.color.main_menu_bg);

        } else {
            gvh.groupArrowTv.setVisibility(View.GONE);
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
        //        ImageView groupArrowIv;
        IconTextView groupArrowTv;
    }

    public class ChildViewHolder {
        IconTextView childIconTv;
        TextView childNameTv;
    }
}
