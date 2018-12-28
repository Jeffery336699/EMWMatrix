package cc.emw.mobile.map;

import android.os.Bundle;
import android.util.Log;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;

public class NearByFoodActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_food);
        init();
    }

    private void init() {
//        PoiSearch.Query query = new PoiSearch.Query("", "050000", "0755");
        PoiSearch.Query query = new PoiSearch.Query("美食", "", "0755");
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查询页码
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        Log.d("zrjtsss",poiResult.toString());
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        Log.d("zrjtsss",poiItem.toString());
    }
}
