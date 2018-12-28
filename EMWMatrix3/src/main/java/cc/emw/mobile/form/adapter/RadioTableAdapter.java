package cc.emw.mobile.form.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;

import cc.emw.mobile.R;


/**
 * 单选
 */
public abstract class RadioTableAdapter extends BaseTableAdapter {
	private final Context context;
	private final LayoutInflater inflater;

	protected int selectID; //选的rid
	private String text =  ""; //选的rid对应的文本
	private int textIndex = -1; //显示文本的位置
	private int idIndex = -2; //行ID的位置

	/**
	 * Constructor
	 *
	 * @param context
	 *            The current context.
	 */
	public RadioTableAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setDefaultSelect(String rid, String text) {
		if (!TextUtils.isEmpty(rid) && TextUtils.isDigitsOnly(rid)) {
			selectID = Integer.valueOf(rid);
			this.text = text;
		}
	}
	
	public int getSelectID() {
		return selectID;
	}

	public void clearSelectID() {
		selectID = 0;
	}

	public String getText() {
		return text;
	}

	public void clearText() {
		text = "";
	}

	public void setTextIndex(int index) {
		textIndex = index;
	}

	public void setIdIndex(int index) {
		idIndex = index;
	}

	/**
	 * Returns the context associated with this array adapter. The context is
	 * used to create views from the resource passed to the constructor.
	 * 
	 * @return The Context associated with this adapter.
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Quick access to the LayoutInflater instance that this Adapter retreived
	 * from its Context.
	 * 
	 * @return The shared LayoutInflater.
	 */
	public LayoutInflater getInflater() {
		return inflater;
	}

	@Override
	public View getView(final int row, final int column, View converView, ViewGroup parent) {
		if (converView == null) {
			converView = inflater.inflate(getLayoutResource(row, column), parent, false);
		}
		final CheckBox checkBox = (CheckBox) converView.findViewById(android.R.id.checkbox);
		checkBox.setButtonDrawable(R.drawable.cm_radio_select);
		final View lineView = converView.findViewById(R.id.line);
		setText(converView, getCellString(row, column));
		if (row == -1) {
			checkBox.setVisibility(column == -1 ? View.INVISIBLE : View.GONE);
//			lineView.setVisibility(column == -1 ? View.VISIBLE : View.INVISIBLE);
			converView.setOnClickListener(null);
		} else {
			final String rowID = getCellString(row, idIndex);
			if (column == -1) {
				checkBox.setChecked(false);
				if (rowID.equals(String.valueOf(selectID))) {
					checkBox.setChecked(true);
				}
				checkBox.setVisibility(View.VISIBLE);
//				lineView.setVisibility(View.VISIBLE);
			} else {
				checkBox.setVisibility(View.GONE);
//				lineView.setVisibility(View.INVISIBLE);
			}
			
			converView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (TextUtils.isDigitsOnly(rowID) ) {
						if (Integer.valueOf(rowID) == selectID) {
							selectID = 0;
							text ="";
						} else {
							selectID = Integer.valueOf(rowID);
							text = getCellString(row, textIndex);
						}
						notifyDataSetChanged();
					}
				}
			});
		}
		return converView;
	}

	/**
	 * Sets the text to the view.
	 * 
	 * @param view
	 * @param text
	 */
	private void setText(View view, String text) {
		((TextView) view.findViewById(android.R.id.text1)).setText(text);
	}

	/**
	 * @param row
	 *            the title of the row of this header. If the column is -1
	 *            returns the title of the row header.
	 * @param column
	 *            the title of the column of this header. If the column is -1
	 *            returns the title of the column header.
	 * @return the string for the cell [row, column]
	 */
	public abstract String getCellString(int row, int column);

	public abstract int getLayoutResource(int row, int column);
}
