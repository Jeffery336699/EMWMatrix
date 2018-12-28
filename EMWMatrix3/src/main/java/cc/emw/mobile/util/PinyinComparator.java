package cc.emw.mobile.util;

import java.util.Comparator;

import cc.emw.mobile.entity.UserInfo;


public class PinyinComparator<T extends UserInfo> implements Comparator<UserInfo> {
	
	@Override
	public int compare(UserInfo lhs, UserInfo rhs) {
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
