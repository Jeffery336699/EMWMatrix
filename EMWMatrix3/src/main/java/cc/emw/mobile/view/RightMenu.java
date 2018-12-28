package cc.emw.mobile.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.form.entity.GridControl;

public class RightMenu extends PopMenu {

	private ArrayList<GridControl.ToolInfo> toolList;
	public void setToolList(ArrayList<GridControl.ToolInfo> toolList) {
		this.toolList = toolList;
	}

	public RightMenu(Context context) {
		super(context);
	}

	@Override
	protected ListView findListView(View view) {
        return (ListView) view.findViewById(R.id.menu_listview);
	}

	@Override
	protected View onCreateView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.pop_menu,
				null);
		return view;
	}

	@Override
	protected ArrayAdapter<Item> onCreateAdapter(final Context context,
			ArrayList<Item> items) {
		return new ArrayAdapter<Item>(context, R.layout.listitem_pop_menu,R.id.text1,
				items) {

			@Override
			public View getView(int position, View convertView, final ViewGroup parent) {
				convertView = super.getView(position, convertView, parent);
				int resId = getItem(position).iconResId;
                String iconCode = getItem(position).iconCode;
                if(!"".equals(iconCode)){
                    IconTextView iconTextView = (IconTextView)convertView.findViewById(R.id.icon1);
                    iconTextView.setIconText(iconCode);
					iconTextView.setVisibility(View.VISIBLE);
                }
				if (resId != 0) {
					Drawable left = context.getResources().getDrawable(resId);
					left.setBounds(0, 0, left.getMinimumWidth(),
							left.getMinimumHeight());// 必须设置图片大小，否则不显示
					((TextView) convertView).setCompoundDrawables(left, null,
							null, null);
				}
				boolean hasExpand = getItem(position).hasExpand;
				convertView.findViewById(R.id.arrow1).setVisibility(hasExpand? View.VISIBLE:View.GONE);
				LinearLayout groupLayout = (LinearLayout) convertView.findViewById(R.id.group1);
				final ListView listView = (ExListView)convertView.findViewById(R.id.child1);
				if (hasExpand && toolList != null && toolList.size() > 0) {
					groupLayout.setEnabled(true);
					groupLayout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							listView.setVisibility(listView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
						}
					});
//					listView.setVisibility(View.VISIBLE);
					ArrayList<GridControl.ToolInfo> childList = new ArrayList<>();
					for (GridControl.ToolInfo tool : toolList) {
						if (getItem(position).toolId.equals(tool.ParentTool)) {
							childList.add(tool);
						}
					}
					ArrayAdapter<GridControl.ToolInfo> adapter = new ArrayAdapter<GridControl.ToolInfo>(context, R.layout.listitem_pop_menu_child, R.id.childtext1,
							childList) {

						@Override
						public View getView(int position, View convertView, ViewGroup parent) {
							convertView = super.getView(position, convertView, parent);
							String iconCode = IconTextView.getIconCode(getItem(position).Icon);
							if(!"".equals(iconCode)){
								IconTextView iconTextView = (IconTextView)convertView.findViewById(R.id.childicon1);
								iconTextView.setIconText(iconCode);
								iconTextView.setVisibility(View.VISIBLE);
							}
							return convertView;
						}
					};
					listView.setAdapter(adapter);
				} else {
					groupLayout.setEnabled(false);
					listView.setVisibility(View.GONE);
				}
				return convertView;
			}
		};
	}
}
