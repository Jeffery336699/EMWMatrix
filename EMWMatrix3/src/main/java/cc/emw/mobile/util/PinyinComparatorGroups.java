package cc.emw.mobile.util;

import java.util.Comparator;

import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;


public class PinyinComparatorGroups<T extends GroupInfo> implements Comparator<GroupInfo> {
	
	@Override
	public int compare(GroupInfo lhs, GroupInfo rhs) {
		if (lhs.getSortLetters().equals("@")
				|| rhs.getSortLetters().equals("#")) {
			return -1;
		} else if (lhs.getSortLetters().equals("#")
				|| rhs.getSortLetters().equals("@")) {
			return 1;
		} else {
			return lhs.getSortLetters().compareTo(rhs.getSortLetters());
		}
	}

}
