package cc.emw.mobile.util;

import java.util.Comparator;

import cc.emw.mobile.net.ApiEntity.UserInfo;


public class PersonnelComparator<T extends UserInfo> implements
		Comparator<UserInfo> {

	@Override
	public int compare(UserInfo lhs, UserInfo rhs) {
		if (lhs.DeptID == 0)
			lhs.DeptID = Integer.MAX_VALUE - 1000;
		if (rhs.DeptID == 0)
			rhs.DeptID = Integer.MAX_VALUE - 1000;
		return lhs.DeptID - rhs.DeptID;
	}

}
