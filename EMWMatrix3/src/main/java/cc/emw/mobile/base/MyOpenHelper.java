package cc.emw.mobile.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

import cc.emw.mobile.chat.model.bean.ChatMsgBeanDao;
import cc.emw.mobile.chat.model.bean.DaoMaster;

/**
 * Created by sunny.du on 2017/5/10.
 */

public class MyOpenHelper extends DaoMaster.DevOpenHelper {

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.i("greenDAO","Upgrading schema from version " + oldVersion + " to " + newVersion + " by migrating all tables data");
        // 第二个参数为要升级的Dao文件.
        if (oldVersion<newVersion) {
            MigrationHelper.getInstance().migrate(db, ChatMsgBeanDao.class);
        }
    }
}