//package cc.emw.mobile.contact.db;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//
//import org.greenrobot.greendao.query.QueryBuilder;
//
//import java.util.List;
//
//import cc.emw.mobile.chat.model.bean.HistoryMessage;
//import cc.emw.mobile.chat.db.DaoMaster;
//import cc.emw.mobile.chat.db.DaoSession;
//
///**
// * Created by tao.zhou on 2017/4/21.
// */
//
//public class DBManager {
//    private final static String dbName = "chat_db";
//    private static DBManager mInstance;
//    private DaoMaster.DevOpenHelper openHelper;
//    private Context context;
//
//    public DBManager(Context context) {
//        this.context = context;
//        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
//    }
//
//    /**
//     * 获取单例引用
//     *
//     * @param context
//     * @return
//     */
//    public static DBManager getInstance(Context context) {
//        if (mInstance == null) {
//            synchronized (DBManager.class) {
//                if (mInstance == null) {
//                    mInstance = new DBManager(context);
//                }
//            }
//        }
//        return mInstance;
//    }
//
//    /**
//     * 获取可读数据库
//     */
//    private SQLiteDatabase getReadableDatabase() {
//        if (openHelper == null) {
//            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
//        }
//        SQLiteDatabase db = openHelper.getReadableDatabase();
//        return db;
//    }
//
//    /**
//     * 获取可写数据库
//     */
//    private SQLiteDatabase getWritableDatabase() {
//        if (openHelper == null) {
//            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
//        }
//        SQLiteDatabase db = openHelper.getWritableDatabase();
//        return db;
//    }
//
//    /**
//     * 查询用户列表
//     */
//    public List<HistoryMessage> queryHistoryMessageList() {
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        HistoryMessageDao userDao = daoSession.getHistoryMessageDao();
//        QueryBuilder<HistoryMessage> qb = userDao.queryBuilder();
//        List<HistoryMessage> list = qb.list();
//        return list;
//    }
//
//    /**
//     * 查询用户列表
//     */
//    public List<HistoryMessage> queryHistoryMessageList(int age) {
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        HistoryMessageDao userDao = daoSession.getHistoryMessageDao();
//        QueryBuilder<HistoryMessage> qb = userDao.queryBuilder();
//        qb.where(HistoryMessageDao.Properties.Age.gt(age)).orderAsc(HistoryMessageDao.Properties.Age);
//        List<HistoryMessage> list = qb.list();
//        return list;
//    }
//
//    /**
//     * 插入一条记录
//     *
//     * @param user
//     */
//    public void insertHistoryMessage(HistoryMessage user) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        HistoryMessageDao userDao = daoSession.getHistoryMessageDao();
//        userDao.insert(user);
//    }
//
//    /**
//     * 插入用户集合
//     *
//     * @param users
//     */
//    public void insertHistoryMessageList(List<HistoryMessage> users) {
//        if (users == null || users.isEmpty()) {
//            return;
//        }
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        HistoryMessageDao userDao = daoSession.getHistoryMessageDao();
//        userDao.insertInTx(users);
//    }
//
//    /**
//     * 删除一条记录
//     *
//     * @param user
//     */
//    public void deleteHistoryMessage(HistoryMessage user) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        HistoryMessageDao userDao = daoSession.getHistoryMessageDao();
//        userDao.delete(user);
//    }
//
//    /**
//     * 更新一条记录
//     *
//     * @param user
//     */
//    public void updateHistoryMessage(HistoryMessage user) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        HistoryMessageDao userDao = daoSession.getHistoryMessageDao();
//        userDao.update(user);
//    }
//}
