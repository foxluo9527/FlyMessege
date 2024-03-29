package com.example.flymessagedome;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;

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
import com.example.flymessagedome.service.MessageService;
import com.example.flymessagedome.service.UploadService;
import com.example.flymessagedome.ui.activity.MainActivity;
import com.example.flymessagedome.utils.AppUtils;
import com.example.flymessagedome.utils.LocationService;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

public class FlyMessageApplication extends MultiDexApplication {
    public static FlyMessageApplication instances;
    private DaoSession mDaoSession;
    private AppComponent appComponent;
    private HttpProxyCacheServer proxy;
    public UploadService uploadService;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        instances = this;
        AppUtils.init(instances);
        setDatabase();
        initComponent();
        initPrefs();
        initBugly();
        LocationService.get().init(this);
        changeDefaultDarkModel(SharedPreferencesUtil.getInstance().getBoolean("withSystemDark", true));
        try {
            if (uploadService == null) {
                Intent intent = new Intent(getApplicationContext(), UploadService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startService(intent);
                getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            instances.uploadService = ((UploadService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /**
     * 初始化腾讯bug管理平台
     */
    private void initBugly() {
        /**
         * true表示初始化时自动检查升级;
         * false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
         */
        Beta.autoCheckUpgrade = false;
        /**
         * 设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
         */
        Beta.upgradeCheckPeriod = 60 * 1000;

        /**
         * 设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
         */
        Beta.initDelay = 1 * 1000;

        /**
         * 设置通知栏大图标，largeIconId为项目中的图片资源;
         */
        Beta.largeIconId = R.drawable.icon;

        /**
         * 设置状态栏小图标，smallIconId为项目中的图片资源Id;
         */
        Beta.smallIconId = R.drawable.icon;

        Bugly.init(getApplicationContext(), "cf62b927ee", true);
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
