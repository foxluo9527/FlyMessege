package com.example.flymessagedome;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.bean.DaoMaster;
import com.example.flymessagedome.bean.DaoSession;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerAppComponent;
import com.example.flymessagedome.module.AppModule;
import com.example.flymessagedome.module.FlyMessageApiModule;
import com.example.flymessagedome.utils.SharedPreferencesUtil;

public class FlyMessageApplication extends MultiDexApplication {
    public static FlyMessageApplication instances;
    private DaoSession mDaoSession;
    private AppComponent appComponent;
    private HttpProxyCacheServer proxy;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        instances = this;
        setDatabase();
        initComponent();
        initPrefs();
        changeDefaultDarkModel(SharedPreferencesUtil.getInstance().getBoolean("withSystemDark", true));
    }

    public static FlyMessageApplication getInstances() {
        return instances;
    }

    public static void changeDefaultDarkModel(boolean withSystem) {
        if (withSystem) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static Context getAppContext() {
        return instances.getApplicationContext();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    private void initComponent() {
        appComponent = DaggerAppComponent.builder()
                .flyMessageApiModule(new FlyMessageApiModule())
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "fly-message-db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        FlyMessageApplication app = (FlyMessageApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy(context)) : app.proxy;
    }

    private HttpProxyCacheServer newProxy(Context context) {
        long size = SharedPreferencesUtil.getInstance().getLong("cacheCapacity", 0b10000000000000000000000000000000L);
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(size)       // 2 Gb for cache
                .build();
    }

    /**
     * 初始化SharedPreference
     */
    protected void initPrefs() {
        SharedPreferencesUtil.init(getApplicationContext(), getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
    }

}
