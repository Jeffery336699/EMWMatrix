package cc.emw.mobile.me.entity;

import java.util.List;

import cc.emw.mobile.net.ApiEntity;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.me.entity
 * @data on 2018/8/31  15:32
 * @describe TODO
 */

public class PendingEvents {

    private ApiEntity.WeekInfo WeekInfo;
    private List<ApiEntity.UserFenPai> navigations;

    public PendingEvents(ApiEntity.WeekInfo weekInfo, List<ApiEntity.UserFenPai> navigations) {
        WeekInfo = weekInfo;
        this.navigations = navigations;
    }

    public ApiEntity.WeekInfo getWeekInfo() {
        return WeekInfo;
    }

    public void setWeekInfo(ApiEntity.WeekInfo WeekInfo) {
        this.WeekInfo = WeekInfo;
    }

    public List<ApiEntity.UserFenPai> getNavigations() {
        return navigations;
    }

    public void setNavigations(List<ApiEntity.UserFenPai> navigations) {
        this.navigations = navigations;
    }
}
