package cc.emw.mobile.chat.util;

import java.util.Comparator;

import cc.emw.mobile.chat.bean.HistoryMessage;

public class TimeCompare implements Comparator<HistoryMessage> {

	@Override
	public int compare(HistoryMessage arg0, HistoryMessage arg1) {

		return (int) (Integer.valueOf(arg1.getMessage().getCreateTime()) - Integer
				.valueOf(arg0.getMessage().getCreateTime()));
	}
}
