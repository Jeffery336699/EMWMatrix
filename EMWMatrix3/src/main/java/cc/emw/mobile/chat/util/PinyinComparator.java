package cc.emw.mobile.chat.util;

import java.util.Comparator;

import cc.emw.mobile.entity.UserInfo;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<UserInfo> {

	public int compare(UserInfo o1, UserInfo o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}