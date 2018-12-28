package cc.emw.mobile.form.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.form.FormDetailActivity;


/**
 * 多选
 */
public abstract class MultiTableAdapter extends BaseTableAdapter {
	private final Context context;
	private final LayoutInflater inflater;
	
	protected ArrayList<String> selectIDs; //复选的rid集
	protected boolean isFirstChecked; //是否复选第一行第一列
	private int pageID;
	
	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context.
	 */
	public MultiTableAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		selectIDs = new ArrayList<String>();
	}
	
	public void setPageID(int pageID) {
		this.pageID = pageID;
	}

	public ArrayList<String> getSelectIDs() {
		return selectIDs;
	}
	
	public void setFirstChecked(boolean isFirstChecked) {
		this.isFirstChecked = isFirstChecked;
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
		final TextView textView = (TextView) converView.findViewById(android.R.id.text1);
		final View lineView = converView.findViewById(R.id.line);
		textView.setEnabled(false);
		setText(converView, getCellString(row, column));
		if (row == -1) {
			if (column == -1 && !TextUtils.isEmpty(getCellString(row, column))) {
				checkBox.setChecked(isFirstChecked);
				checkBox.setVisibility(View.VISIBLE);
				converView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						isFirstChecked = !checkBox.isChecked();
						checkBox.setChecked(isFirstChecked);
						onClickFirstRowCol(isFirstChecked);
					}
				});
			} else {
				checkBox.setVisibility(View.GONE);
				converView.setOnClickListener(null);
			}
			if (textView.getText().toString().lastIndexOf("*") > 0) {
				SpannableString spanStr = new SpannableString(textView.getText());
				ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
				spanStr.setSpan(colorSpan, textView.length() - 1, textView.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				/*RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.2f);
				spanStr.setSpan(sizeSpan, textView.length() - 1, textView.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);*/
				textView.setText(spanStr);
			}
		} else {
			final String rowID = getCellString(row, -2);
			if (column == -1) {
				checkBox.setChecked(false);
				if (selectIDs.contains(rowID)) {
					checkBox.setChecked(true);
				}
				checkBox.setVisibility(View.VISIBLE);
//				lineView.setVisibility(View.VISIBLE);
				textView.setEnabled(true);
				textView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (pageID != 0) {
							Intent intent = new Intent(context, FormDetailActivity.class);
							intent.putExtra("page_id", String.valueOf(pageID));
							intent.putExtra("row_id", rowID);
							intent.putExtra("start_anim", false);
							context.startActivity(intent);
						} else {
							onClickCell(row, column);
						}
					}
				});
			} else {
				checkBox.setVisibility(View.GONE);
//				lineView.setVisibility(View.INVISIBLE);
			}
			
			converView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (column == -1) {
						boolean curSelect = !checkBox.isChecked();
						checkBox.setChecked(curSelect);
						if (curSelect) {
							selectIDs.add(rowID);
						} else {
							selectIDs.remove(rowID);
						}
					} else if (pageID != 0) {
						Intent intent = new Intent(context, FormDetailActivity.class);
						intent.putExtra("page_id", String.valueOf(pageID));
						intent.putExtra("row_id", rowID);
						intent.putExtra("start_anim", false);
						context.startActivity(intent);
					} else {
						onClickCell(row, column);
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
	
	public void onClickFirstRowCol(boolean checked) {
		
	}
	public void onClickCell(int row, int column) {

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
