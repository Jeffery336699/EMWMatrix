package cc.emw.mobile.main.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.project.view.SaleFormActivity;

import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;

/**
 * This class implements the main functionalities of the TableAdapter in
 * Mutuactivos.
 * 
 * 
 * @author Brais Gabín
 */
public abstract class SampleTableAdapter extends BaseTableAdapter {
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
	public SampleTableAdapter(Context context) {
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
		final View lineView = converView.findViewById(R.id.line);
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
		} else {
			final String rowID = getCellString(row, -2);
			if (column == -1) {
				checkBox.setChecked(false);
				for (String rid : selectIDs) {
					if (rowID.equals(rid)) {
						checkBox.setChecked(true);
						break;
					}
				}
				checkBox.setVisibility(View.VISIBLE);
				lineView.setVisibility(View.VISIBLE);
			} else {
				checkBox.setVisibility(View.GONE);
				lineView.setVisibility(View.INVISIBLE);
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
						Intent intent = new Intent(context, SaleFormActivity.class);
						intent.putExtra("page_id", String.valueOf(pageID));
						intent.putExtra("row_id", rowID);
						context.startActivity(intent);
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
