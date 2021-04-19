package com.example.flymessagedome.utils;

import android.os.Environment;

public class Constant {
    public static final String api_key="92fa8d59057fdd134be4cf4a4db4e9da";

    public static final String API_BASE_URL = "http://www.foxluo.cn/FlyMessage_war/";

    //申请权限的回调申请码
    //拍照
    public static final int REQUEST_CODE_PERMISSION_TAKE_PHOTO=1;
    //选择文件
    public static final int REQUEST_CODE_PERMISSION_CHOICE_FILE=2;
    //选择相册图片
    public static final int REQUEST_CODE_PERMISSION_CHOICE_PHOTO=3;
    //录音
    public static final int REQUEST_CODE_PERMISSION_RECORD_AUDIO=4;
    //展示图片
    public static final int REQUEST_CODE_SHOW_PHOTOS=7;
    //选择图片返回码
    public static final int RC_CHOOSE_PHOTO=5;
    //选择文件返回码
    public static final int RC_CHOOSE_FILE=6;

    public static final int RC_CHOOSE_HEAD_IMG=8;

    public static final int RC_CHOOSE_BG_IMG=9;
    //读取联系人
    public static final int READ_CONTRACT=10;
    //欢迎界面资源获取（one一个）
    public static final String ONE_API="http://api.tianapi.com/txapi/one/index";

    public static final String SOCKET_BASE_URL = "ws://www.foxluo.cn/FlyMessage_war/webSocketServer";

    public static final String FORGET_PASS_URL="http://www.foxluo.cn/changePass.html";

    public static final String REGISTER_URL="http://www.foxluo.cn/register.html";

    public static final String protocolUrl="http://www.foxluo.cn/protocol.html";

    //检查更新
    public static final String APP_UPDATE_API="http://www.foxluo.cn/myapp/AppUpdate?version=";

    public static final int SUCCESS = 200;

    public static final int FAILED = 500;

    public static final int NOT_LOGIN = 511;

    public static final int TOKEN_EXCEED = 512;

    public static final String IS_LOGIN = "isLogin";

    public static final String AUTO_LOGIN = "autoLogin";

    public static final String REMEMBER_ACCOUNT="rememberLoginAccount";

    public static final String U_NAME="loginUserName";

    public static final String U_PASS="loginUserPassword";

    public static final String LOGIN_TOKEN="loginToken";

    public static final String ON_LOGIN="正在登录";

    public static final String ON_SENDING_CODE="正在发送验证码";

    public static final String CODE_SEND_SUCCESS="验证码发送成功,请在短信查看";

    public static final String ONLINE_OPEN_OFFICE_FILE="https://view.officeapps.live.com/op/view.aspx?src=";

    public static final String DownloadPath= Environment.getExternalStorageDirectory()+"/FlyMessage_war/file/download/";

    public static final String RootPath=Environment.getExternalStorageDirectory()+"";

    //文件类型与文件后缀名的匹配表
    public static final String[][] MATCH_ARRAY={
            //{后缀名，    文件类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",      "image/bmp"},
            {".c",        "text/plain"},
            {".class",    "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",        "application/x-gzip"},
            {".h",        "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",        "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",        "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            {".xml",    "text/plain"},
            {".z",        "application/x-compress"},
            {".zip",    "application/zip"},
            {"",        "*/*"}
    };
}
