package com.example.flymessagedome.ui.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.flymessagedome.utils.ImageUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;
import com.example.flymessagedome.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class WebActivity extends Activity {
    String url;
    WebView webView;
    TextView title;
    String titleString="加载中...";
    Context context;
    int fontSize = 2;
    WebSettings settings;
    SharedPreferencesUtil shared;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        onConfigView();
        initData();
    }

    protected void onConfigView() {
        context=WebActivity.this;
        webView = (WebView) findViewById(R.id.web_view);
        title=findViewById(R.id.title);
    }

    protected void initData() {
        SharedPreferencesUtil.init(this,"Music", Context.MODE_PRIVATE);
        shared = SharedPreferencesUtil.getInstance();
        fontSize=shared.getInt("webFontSize",2);
        url = this.getIntent().getStringExtra("URLString");
        initSetting();
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;
                if (url.startsWith("http:") || url.startsWith("https:") ){
                    view.loadUrl(url);
                    return false;
                }else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        //startActivity(intent);
                    } catch (Exception e) {
//                    ToastUtils.showShort("暂无应用打开此链接");
                    }
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                titleString=view.getTitle();
                title.setText(titleString);
                if (popView!=null){
                    ((TextView)popView.findViewById(R.id.web_title)).setText(titleString);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); //表示等待证书响应
                // handler.cancel(); //表示挂起连接，为默认方式
                // handler.handleMessage(null); //可做其他处理
//                super.onReceivedSslError(view, handler, error);
            }

        });
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = webView.getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                switch (type) {
                    case WebView.HitTestResult.EDIT_TEXT_TYPE: // 选中的文字类型
                        break;
                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE: // &emsp;地图类型
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // 带有链接的图片类型
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                        String url = result.getExtra();
                        showSelectImgPopup(url);
                        return true;
                    case WebView.HitTestResult.UNKNOWN_TYPE: //未知
                        break;
                }
                return false;
            }
        });
        webView.loadUrl(url);
        settings = webView.getSettings();
        settings.setSupportZoom(true);
        switch (fontSize){
            case 0:
                settings.setTextZoom(80);
                break;
            case 1:
                settings.setTextZoom(90);
                break;
            case 2:
                settings.setTextZoom(100);
                break;
            case 3:
                settings.setTextZoom(110);
                break;
            case 4:
                settings.setTextZoom(120);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null){
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
//            ((LinearLayout)webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }

    private void initSetting(){
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //支持插件
//        webSettings.setPluginsEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(true); //隐藏原生的缩放控件
        //设置WebView缓存
        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        // LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        // LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        //其他细节操作
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

    }
    public void doClick(View view) {
        switch (view.getId()){
            case R.id.back_space:
                finish();
                break;
            case R.id.more:
                showMorePopup();
                break;
        }
    }
    View popView;
    private void showMorePopup(){
        popView = View.inflate(context,R.layout.web_more_popup,null);
        TextView title=popView.findViewById(R.id.web_title);
        title.setText(titleString);
        ImageView dismiss=popView.findViewById(R.id.dismiss);
        ImageView exit=popView.findViewById(R.id.exit);
        ImageView share=popView.findViewById(R.id.share);
        View copyView=popView.findViewById(R.id.copy_view);
        View refreshView=popView.findViewById(R.id.refresh_view);
        View chromeView=popView.findViewById(R.id.chrome_view);
        View findView=popView.findViewById(R.id.find_view);
        View fontView=popView.findViewById(R.id.font_view);
        //获取屏幕宽高
        int weight = ((Activity)context).getResources().getDisplayMetrics().widthPixels;
        int height = dip2px(160,context);

//        int height = getResources().getDisplayMetrics().heightPixels/2;
        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popView=null;
                WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.copy_view:
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(webView.getUrl());
                        Toast.makeText(context,"链接已复制到剪切板",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.refresh_view:
                        titleString="加载中...";
                        title.setText(titleString);
                        WebActivity.this.title.setText(titleString);
                        webView.loadUrl(url);
                        break;
                    case R.id.chrome_view:
                        Intent intent= new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                        break;
                    case R.id.exit:
                        popupWindow.dismiss();
                        finish();
                        break;
                    case R.id.share:
                        popupWindow.dismiss();
                        OnekeyShare oks = new OnekeyShare();
                        // title标题，微信、QQ和QQ空间等平台使用
                        oks.setTitle(getString(R.string.share));
                        // titleUrl QQ和QQ空间跳转链接
                        oks.setTitleUrl(url);
                        // text是分享文本，所有平台都需要这个字段
                        oks.setText(titleString);
                        // imagePath是图片的本地路径，确保SDcard下面存在此张图片
                        oks.setImagePath("");
                        // url在微信、Facebook等平台中使用
                        oks.setUrl(url);
                        // 启动分享GUI
                        oks.show(context);
                        break;
                    case R.id.find_view:
//                        webView.showFindDialog(" ",true);
                        break;
                    case R.id.font_view:
                        showFontSizePopup();
                        break;
                }
                popupWindow.dismiss();
            }
        };
        refreshView.setOnClickListener(listener);
        copyView.setOnClickListener(listener);
        chromeView.setOnClickListener(listener);
        dismiss.setOnClickListener(listener);
        exit.setOnClickListener(listener);
        share.setOnClickListener(listener);
        findView.setOnClickListener(listener);
        fontView.setOnClickListener(listener);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    private void showSelectImgPopup(String imgUrl){
        View popView = View.inflate(context,R.layout.web_select_img_popup,null);

        View shareView=popView.findViewById(R.id.share);
        View saveView=popView.findViewById(R.id.save);
        View cancelView=popView.findViewById(R.id.cancel);
        //获取屏幕宽高
        int weight = ((Activity)context).getResources().getDisplayMetrics().widthPixels;
        int height = dip2px(161,context);

//        int height = getResources().getDisplayMetrics().heightPixels/2;
        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.save:
                        Glide.with(context)
                                .load(imgUrl)
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        Bitmap srcBitmap= ImageUtils.drawableToBitmap(resource);
                                        new Thread(){
                                            @Override
                                            public void run() {
                                                Bitmap bitmap = srcBitmap;
                                                String filePath = Environment.getExternalStorageDirectory()+"/FlyMessage/downloadImg/"+ UUID.randomUUID() +".jpg";
                                                File file = new File(filePath);
                                                FileOutputStream fos = null;
                                                try {
                                                    if (!file.exists()) {
                                                        // 先得到文件的上级目录，并创建上级目录，在创建文件
                                                        file.getParentFile().mkdirs();
                                                        file.createNewFile();
                                                    }
                                                    fos = new FileOutputStream(filePath );
                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                                    fos.close();
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }.start();
                                        Toast.makeText(context,"图片保存至"+Environment.getExternalStorageDirectory()+"/FlyMessage/downloadImg文件夹",Toast.LENGTH_SHORT).show();
                                    }
                                });
                        break;
                    case R.id.share:
                        popupWindow.dismiss();
                        OnekeyShare oks = new OnekeyShare();
                        // title标题，微信、QQ和QQ空间等平台使用
                        oks.setTitle(getString(R.string.share));
                        // titleUrl QQ和QQ空间跳转链接
                        oks.setTitleUrl(imgUrl);
                        // text是分享文本，所有平台都需要这个字段
                        oks.setText("分享图片");
                        // imagePath是图片的本地路径，确保SDcard下面存在此张图片
                        oks.setImagePath("");
                        // url在微信、Facebook等平台中使用
                        oks.setUrl(url);
                        // 启动分享GUI
                        oks.show(context);
                        break;
                }
                popupWindow.dismiss();
            }
        };
        shareView.setOnClickListener(listener);
        saveView.setOnClickListener(listener);
        cancelView.setOnClickListener(listener);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    private void showFontSizePopup(){
        View popView = View.inflate(context,R.layout.web_font_size_popup,null);
        fontSize=shared.getInt("webFontSize",2);
        RadioButton mini=popView.findViewById(R.id.mini);
        RadioButton small=popView.findViewById(R.id.small);
        RadioButton normal=popView.findViewById(R.id.normal);
        RadioButton big=popView.findViewById(R.id.big);
        RadioButton large=popView.findViewById(R.id.large);
        switch (fontSize){
            case 0:
                mini.setChecked(true);
                break;
            case 1:
                small.setChecked(true);
                break;
            case 2:
                normal.setChecked(true);
                break;
            case 3:
                big.setChecked(true);
                break;
            case 4:
                large.setChecked(true);
                break;
        }
        RadioButton.OnCheckedChangeListener listener=new RadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (fontSize){
                    case 0:
                        mini.setChecked(false);
                        break;
                    case 1:
                        small.setChecked(false);
                        break;
                    case 2:
                        normal.setChecked(false);
                        break;
                    case 3:
                        big.setChecked(false);
                        break;
                    case 4:
                        large.setChecked(false);
                        break;
                }
                switch (buttonView.getId()){
                    case R.id.mini:
                        fontSize=0;
                        settings.setTextZoom(80);
                        break;
                    case R.id.small:
                        fontSize=1;
                        settings.setTextZoom(90);
                        break;
                    case R.id.normal:
                        fontSize=2;
                        settings.setTextZoom(100);
                        break;
                    case R.id.big:
                        fontSize=3;
                        settings.setTextZoom(110);
                        break;
                    case R.id.large:
                        fontSize=4;
                        settings.setTextZoom(120);
                        break;
                }
                shared.putInt("webFontSize",fontSize);
            }
        };
        mini.setOnCheckedChangeListener(listener);
        small.setOnCheckedChangeListener(listener);
        normal.setOnCheckedChangeListener(listener);
        big.setOnCheckedChangeListener(listener);
        large.setOnCheckedChangeListener(listener);
        //获取屏幕宽高
        int weight = ((Activity)context).getResources().getDisplayMetrics().widthPixels;
        int height = dip2px(80,context);

//        int height = getResources().getDisplayMetrics().heightPixels/2;
        PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
                lp.alpha = 1.0f;
                ((Activity)context).getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)context).getWindow().setAttributes(lp);

        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
    }
    public static void displayToGallery(Context context, File photoFile) {
        if (photoFile == null || !photoFile.exists()) {
            return;
        }
        String photoPath = photoFile.getAbsolutePath();
        String photoName = photoFile.getName();
        // 把文件插入到系统图库
        try {
            ContentResolver contentResolver = context.getContentResolver();
            MediaStore.Images.Media.insertImage(contentResolver, photoPath, photoName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + photoPath)));
//        Looper.prepare();
//        Toast.makeText(context,"图片保存至"+photoFile.getPath(),Toast.LENGTH_SHORT).show();
    }

    public static int dip2px(float dp, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        int px = (int) (dp * density + 0.5f);
        return px;
    }
}
