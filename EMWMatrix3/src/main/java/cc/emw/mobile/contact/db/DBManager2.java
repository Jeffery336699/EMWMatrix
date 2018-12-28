package cc.emw.mobile.contact.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import cc.emw.mobile.chat.model.bean.DaoMaster;


/**
 * Created by tao.zhou on 2017/4/21.
 */

public class DBManager2 {
    private final static String dbName = "chat_db";
    private static DBManager2 mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager2(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager2 getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager2.class) {
                if (mInstance == null) {
                    mInstance = new DBManager2(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

//    /**
//     * 查询用户列表
//     */
//    public List<ChatHistoryBean> queryChatHistoryBeanList() {
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        ChatHistoryBeanDao userDao = daoSession.getChatHistoryBeanDao();
//        QueryBuilder<ChatHistoryBean> qb = userDao.queryBuilder();
//        List<ChatHistoryBean> list = qb.list();
//        return list;
//    }
//
//    /**
//     * 查询用户列表
//     */
//    public List<ChatHistoryBean> queryChatHistoryBeanList(int age) {
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        ChatHistoryBeanDao userDao = daoSession.getChatHistoryBeanDao();
//        QueryBuilder<ChatHistoryBean> qb = userDao.queryBuilder();
//        qb.where(ChatHistoryBeanDao.Properties.Age.gt(age)).orderAsc(ChatHistoryBeanDao.Properties.Age);
//        List<ChatHistoryBean> list = qb.list();
//        return list;
//    }
//
//    /**
//     * 插入一条记录
//     *
//     * @param user
//     */
//    public void insertChatHistoryBean(ChatHistoryBean user) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        ChatHistoryBeanDao userDao = daoSession.getChatHistoryBeanDao();
//        userDao.insert(user);
//    }
//
//    /**
//     * 插入用户集合
//     *
//     * @param users
//     */
//    public void insertChatHistoryBeanList(List<ChatHistoryBean> users) {
//        if (users == null || users.isEmpty()) {
//            return;
//        }
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        ChatHistoryBeanDao userDao = daoSession.getChatHistoryBeanDao();
//        userDao.insertInTx(users);
//    }
//
//    /**
//     * 删除一条记录
//     *
//     * @param user
//     */
//    public void deleteChatHistoryBean(ChatHistoryBean user) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        ChatHistoryBeanDao userDao = daoSession.getChatHistoryBeanDao();
//        userDao.delete(user);
//    }
//
//    /**
//     * 更新一条记录
//     *
//     * @param user
//     */
//    public void updateChatHistoryBean(ChatHistoryBean user) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        ChatHistoryBeanDao userDao = daoSession.getChatHistoryBeanDao();
//        userDao.update(user);
//    }
}
