package com.example.flymessagedome;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
import com.mob.MobSDK;
import com.mob.OperationCallback;

import java.util.Date;

import static com.yalantis.ucrop.UCropFragment.TAG;

public class FlyMessageApplication extends MultiDexApplication {
    public static FlyMessageApplication instances;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private AppComponent appComponent;
    private HttpProxyCacheServer proxy;
    public static int STATUS_HEIGHT = 0;//状态栏高度

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        instances = this;
        setDatabase();
        initComponent();
        initPrefs();
        submitPrivacyGrantResult(true);
        changeDefaultDarkModel(SharedPreferencesUtil.getInstance().getBoolean("withSystemDark",true));
    }

    public static FlyMessageApplication getInstances(){
        return instances;
    }

    public static void changeDefaultDarkModel(boolean withSystem){
        if (withSystem){
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM );
        }else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO );
        }
    }

    public static Context getAppContext(){
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
    public AppComponent getAppComponent(){
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
        mHelper = new DaoMaster.DevOpenHelper(this, "fly-message-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        FlyMessageApplication app = (FlyMessageApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy(context)) : app.proxy;
    }

    private HttpProxyCacheServer newProxy(Context context) {
        long size=SharedPreferencesUtil.getInstance().getLong("cacheCapacity",0b10000000000000000000000000000000l);
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

    /**
     * 提交隐私协议
     * @param granted
     */
    private void submitPrivacyGrantResult(boolean granted) {
        MobSDK.submitPolicyGrantResult(granted, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void data) {
                Log.d(TAG, "隐私协议授权结果提交：成功");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "隐私协议授权结果提交：失败");
            }
        });
    }
}
