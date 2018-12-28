package cc.emw.mobile.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.map.route.RouteActivity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

public class LocationsPersonalListActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ArrayList<Locations> datas = new ArrayList<>();
    private DisplayImageOptions options;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        initView();
        getIntents();
    }

    private void initView() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head)
                .showImageForEmptyUri(R.drawable.cm_img_head)
                .showImageOnFail(R.drawable.cm_img_head)
                .cacheInMemory(true).cacheOnDisk(true).build();
        mListView = (ListView) findViewById(R.id.location_listview);
        ImageButton left = (ImageButton) findViewById(R.id.cm_header_btn_left);
        TextView title = (TextView) findViewById(R.id.cm_header_tv_title);
        left.setVisibility(View.VISIBLE);
        left.setOnClickListener(this);
        title.setText("定位人员列表");
        mListView.setOnItemClickListener(this);
    }

    private void getIntents() {
        String list = getIntent().getStringExtra("list");
        Type type = new TypeToken<ArrayList<Locations>>() {
        }.getType();
        ArrayList<Locations> data = new Gson().fromJson(list, type);
        for (Locations location : data) {
            if (!"".equals(location.getAxis())) {
                datas.add(location);
            }
        }
        mListView.setAdapter(new MyAdapter());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("px", "dianjile=" + position);
        Intent intent = new Intent(LocationsPersonalListActivity.this, RouteActivity.class);
        intent.putExtra("Axis", datas.get(position).getAxis());
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intents = new Intent(this, RouteActivity.class);
        startActivity(intents);
        finish();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size() == 0 ? 0 : datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position) == null ? null : datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(LocationsPersonalListActivity.this).inflate(R.layout.listitem_location_person, null);
                holder.imageView = (CircleImageView) convertView.findViewById(R.id.personnel_iv_head);
                holder.textView = (TextView) convertView.findViewById(R.id.personnel_tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(datas.get(position).getName());
            String uri = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode, datas.get(position).getImage());
            ImageLoader.getInstance().displayImage(uri, holder.imageView, options);
            return convertView;
        }
    }

    class ViewHolder {
        TextView textView;
        CircleImageView imageView;
    }
}
