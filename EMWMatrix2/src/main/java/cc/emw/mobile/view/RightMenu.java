package cc.emw.mobile.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cc.emw.mobile.R;

public class RightMenu extends PopMenu {

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
		return new ArrayAdapter<Item>(context, R.layout.listitem_pop_menu,
				items) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = super.getView(position, convertView, parent);
				int resId = getItem(position).iconResId;
				if (resId != 0) {
					Drawable left = context.getResources().getDrawable(resId);
					left.setBounds(0, 0, left.getMinimumWidth(),
							left.getMinimumHeight());// 必须设置图片大小，否则不显示
					((TextView) convertView).setCompoundDrawables(left, null,
							null, null);
				}
				return convertView;
			}
		};
	}
}
