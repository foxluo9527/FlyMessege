/* 
 * Copyright 2014 ShangDao.Ltd  All rights reserved.
 * SiChuan ShangDao.Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @CommUtil.java  2014-2-25 下午5:46:24 - jiangyue
 * @author jiangyue
 * @version 1.0
 */

package com.example.flymessagedome.utils;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.flymessagedome.FlyMessageApplication;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1.判断是否联网
 * 2.验证邮箱地址是否正确
 * 3.判断身份证号码
 * 4.获取保留的小数位数
 * 5.根据日期获得星期
 * 6.判断是正整数还是小数
 * 7.验证账户
 * 8.验证手机号码
 * 9.电话号码验证
 * 10.检测密码
 * 11.判断网络连接
 * 12.验证邮政编码
 * 13.打电话
 * 14.检验是否存在非法字符
 * 15.EditText竖直方向是否可以滚动
 * 16.获取当月的 天数
 * 17.根据年 月 获取对应的月份 天数
 * 18.根据日期 找到对应日期的 星期
 * 19.设置时间格式
 * 20.设置时间格式，转成成时间搓
 * 21.传入时间搓输出时间类型
 * 22.获取当前年月日
 * 23.获取当前年月
 * 24.获取应用详情页面intent
 * 25.是否连接wifi
 * 26.设置电话号码344格式
 * 27.打开软键盘
 * 28.关闭软键盘
 */
public class CommUtil {
	public static String getAppVersion(String packageName,Context context) throws PackageManager.NameNotFoundException {
		PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
		String appVersion = packageInfo.versionName;
		return appVersion;
	}
	/**
	 * 启动到应用商店app详情界面
	 *com.android.vending     Google Play
	 com.tencent.android.qqdownloader    应用宝
	 com.qihoo.appstore   360手机助手
	 com.baidu.appsearch 百度手机助
	 com.xiaomi.market  小米应用商店
	 com.wandoujia.phoenix2 豌豆荚
	 com.huawei.appmarket  华为应用市场
	 com.taobao.appcenter  淘宝手机助手
	 com.hiapk.marketpho 安卓市场
	 cn.goapk.market 安智市场
	 * @param appPkg  目标App的包名
	 * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败
	 */
	public void launchAppDetail(Activity context, String appPkg, String marketPkg) {
		try {
			if (TextUtils.isEmpty(appPkg)) return;
			Uri uri = Uri.parse("market://details?id=" + appPkg);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if (!TextUtils.isEmpty(marketPkg)) {
				intent.setPackage(marketPkg);
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取应用详情页面intent
	 *
	 * @return
	 */
	public static Intent getAppDetailSettingIntent() {
		Intent localIntent = new Intent();
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= 9) {
			localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			localIntent.setData(Uri.fromParts("package", FlyMessageApplication.getInstances().getPackageName(), null));
		} else if (Build.VERSION.SDK_INT <= 8) {
			localIntent.setAction(Intent.ACTION_VIEW);
			localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
			localIntent.putExtra("com.android.settings.ApplicationPkgName", FlyMessageApplication.getInstances().getPackageName());
		}
		return localIntent;
	}

	/** 判断是否联网 */
	public static boolean isNetWork(Context context) {

		boolean isNetWork = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			isNetWork = true;
		} else {
			isNetWork = false;
		}
		return isNetWork;
	}

	/**是否连接WIFI*/
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}

		return false;
	}

	//打开软键盘
	public static void openKeyBord(MultiAutoCompleteTextView editText, Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	//关闭软键盘
	public static void closeKeybord(MultiAutoCompleteTextView editText, Context context)
	{
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * 验证邮箱地址是否正确
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[_-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 判断身份证号码
	 * @param str
	 * @return
	 */
	public static boolean checkPersonID(String str){
		String isIDCard1="^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";//判断15位身份证号码
		String isIDCard2="^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$";//判断18位身份证号码
		return str.matches(isIDCard1) || str.matches(isIDCard2);
	}
	/**
	 * 获取保留的小数位数
	 * @param str
	 * @return
	 *   String str="86.64466666";
	 *   86.64=86.64=86.64=86.64  
	 */
	public static String getBigDecimal(String str){
		BigDecimal bd = new BigDecimal(Double.parseDouble(str));
//	        System.out.println(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//	        System.out.println("=================");
		DecimalFormat df = new DecimalFormat("#.00");
//	         System.out.println(df.format(Double.parseDouble(str)));
//	         System.out.println("=================");
//	         System.out.println(String.format("%.2f", Double.parseDouble(str)));
//	         System.out.println("=================");
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
//	         System.out.println(nf.format(Double.parseDouble(str)));
		return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"";
	}
	/**
	 * 根据日期获得星期
	 * @param date
	 * @return
	 */
	public static String getWeekOfDate(Date date) {
		String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
				"星期六" };
		String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysName[intWeek];
	}

	/**
	 * 判断是正整数还是小数
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str){
		String num = "^([0-9]*)([.]?)([0-9]+)";
		String num1 = "^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$";
		return str.matches(num);
	}

	/**
	 * 验证账户
	 * @param account
	 * @return
	 */
	public static boolean checkAccount(String account) {
		boolean flag = false;
		try {
			String check = "^[a-zA-Z0-9@.]{1,24}+$";
			Pattern p = Pattern.compile(check);
			Matcher m = p.matcher(account);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static boolean isMobileNO(String mobile){
		if (mobile.length() != 11)
		{
			return false;
		}else{
			/**
			 * 移动号段正则表达式
			 */
			String pat1 = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
			/**
			 * 联通号段正则表达式
			 */
			String pat2  = "^((13[0-2])|(145)|(15[5-6])|(176)|(18[5,6]))\\d{8}|(1709)\\d{7}$";
			/**
			 * 电信号段正则表达式
			 */
			String pat3  = "^((133)|(153)|(177)|(18[0,1,9])|(149))\\d{8}$";
			/**
			 * 虚拟运营商正则表达式
			 */
			String pat4 = "^((170))\\d{8}|(1718)|(1719)\\d{7}$";

			Pattern pattern1 = Pattern.compile(pat1);
			Matcher match1 = pattern1.matcher(mobile);
			boolean isMatch1 = match1.matches();
			if(isMatch1){
				return true;
			}
			Pattern pattern2 = Pattern.compile(pat2);
			Matcher match2 = pattern2.matcher(mobile);
			boolean isMatch2 = match2.matches();
			if(isMatch2){
				return true;
			}
			Pattern pattern3 = Pattern.compile(pat3);
			Matcher match3 = pattern3.matcher(mobile);
			boolean isMatch3 = match3.matches();
			if(isMatch3){
				return true;
			}
			Pattern pattern4 = Pattern.compile(pat4);
			Matcher match4 = pattern4.matcher(mobile);
			boolean isMatch4 = match4.matches();
			if(isMatch4){
				return true;
			}
			return false;
		}
	}

	/**
	 * 设置手机号码344格式
	 * @param str 输入框内容
	 * @param editText 输入框
	 */
	public static void setPhone344(String str, EditText editText){
		String contents = str;
		int length = contents.length();
		if(length == 4){
			if(contents.substring(3).equals(new String(" "))){ //
				contents = contents.substring(0, 3);
				editText.setText(contents);
				editText.setSelection(contents.length());
			}else{ // +
				contents = contents.substring(0, 3) + " " + contents.substring(3);
				editText.setText(contents);
				editText.setSelection(contents.length());
			}
		}
		else if(length == 9){
			if(contents.substring(8).equals(new String(" "))){ //
				contents = contents.substring(0, 8);
				editText.setText(contents);
				editText.setSelection(contents.length());
			}else{// +
				contents = contents.substring(0, 8) + " " + contents.substring(8);
				editText.setText(contents);
				editText.setSelection(contents.length());
			}
		}
	}

	/**
	 * 座机号码验证
	 *
	 * @param  str
	 * @return 验证通过返回true
	 */
	public static boolean isPhone(String str) {
		Pattern p1 = null,p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
		if(str.length() >9)
		{   m = p1.matcher(str);
			b = m.matches();
		}else{
			m = p2.matcher(str);
			b = m.matches();
		}
		return b;
	}
	/**
	 * 检测密码
	 * 字母或者数字
	 * @param pwd
	 * @return
	 */
	public static boolean checkPwd(String pwd) {
		boolean flag = false;
		try {
			String check = "^[a-zA-Z0-9]{8,16}+$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(pwd);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	/**
	 * 检测密码
	 *字母和数字
	 * @param pwd
	 * @return
	 */
	public static boolean checkPwdN(String pwd) {
		boolean flag = false;
		try {
			String check = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(pwd);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	/**
	 * 检测密码强度
	 *   能匹配的组合为：数字+字母，数字+特殊字符，字母+特殊字符，数字+字母+特殊字符组合，而且不能是纯数字，纯字母，纯特殊字符
	 *   ^(?![\d]+$)(?![a-zA-Z]+$)(?![^\da-zA-Z]+$).{6,20}$  含有特殊字符
	 *   上面的正则里所说的特殊字符是除了数字，字母之外的所有字符，如果要限定特殊字符，例如，特殊字符的范围为 !#$%^&*  ，那么可以这么改
	 *   ^(?![\d]+$)(?![a-zA-Z]+$)(?![!#$%^&*]+$)[\da-zA-Z!#$%^&*]{6,20}$
	 * @param pwd
	 * @return
	 */
	public static void checkPwdStrong(String pwd, TextView pwd_weak, TextView pwd_in, TextView pwd_strong) {
		//输入框为0
		if (pwd.length() == 0)
		{
			pwd_weak.setBackgroundColor(Color.rgb(205,205,205));
			pwd_in.setBackgroundColor(Color.rgb(205,205,205));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}

		//输入的纯数字为弱
		if (pwd.matches ("^[0-9]+$"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(205,205,205));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的纯小写字母为弱
		else if (pwd.matches ("^[a-z]+$"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(205,205,205));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的纯大写字母为弱
		else if (pwd.matches ("^[A-Z]+$"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(205,205,205));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的大写字母和数字，输入的字符小于7个密码为弱
		else if (pwd.matches ("^[A-Z0-9]{1,7}"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(205,205,205));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的大写字母和数字，输入的字符大于7个密码为中
		else if (pwd.matches ("^[A-Z0-9]{8,16}"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(255,184,77));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的小写字母和数字，输入的字符小于7个密码为弱
		else if (pwd.matches ("^[a-z0-9]{1,7}"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(205,205,205));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的小写字母和数字，输入的字符大于7个密码为中
		else if (pwd.matches ("^[a-z0-9]{8,16}"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(255,184,77));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的大写字母和小写字母，输入的字符小于7个密码为弱
		else if (pwd.matches ("^[A-Za-z]{1,7}"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(205,205,205));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的大写字母和小写字母，输入的字符大于7个密码为中
		else if (pwd.matches ("^[A-Za-z]{8,16}"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(255,184,77));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的大写字母和小写字母和数字，输入的字符小于5个个密码为弱
		else if (pwd.matches ("^[A-Za-z0-9]{1,5}"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(205,205,205));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的大写字母和小写字母和数字，输入的字符大于6个个密码为中
		else if (pwd.matches ("^[A-Za-z0-9]{6,7}"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(255,184,77));
			pwd_strong.setBackgroundColor(Color.rgb(205,205,205));
		}
		//输入的大写字母和小写字母和数字，输入的字符大于8个密码为强
		else if (pwd.matches ("^[A-Za-z0-9]{8,16}"))
		{
			pwd_weak.setBackgroundColor(Color.rgb(255,129,128));
			pwd_in.setBackgroundColor(Color.rgb(255,184,77));
			pwd_strong.setBackgroundColor(Color.rgb(113,198,14));
		}

	}
	/**
	 * 检测密码强度
	 *
	 * @param pwd
	 * @return
	 */
	public static int checkPwdStrongB(String pwd) {
		// 0 弱 1 中 2 强
		int strong = 0;
		//输入框为0
		if (pwd.length() == 0)
		{
			strong = 0;
		}

		//输入的纯数字为弱
		if (pwd.matches ("^[0-9]+$"))
		{
			strong = 0;
		}
		//输入的纯小写字母为弱
		else if (pwd.matches ("^[a-z]+$"))
		{
			strong = 0;
		}
		//输入的纯大写字母为弱
		else if (pwd.matches ("^[A-Z]+$"))
		{
			strong = 0;
		}
		//输入的大写字母和数字，输入的字符小于7个密码为弱
		else if (pwd.matches ("^[A-Z0-9]{1,5}"))
		{
			return 0;
		}
		//输入的大写字母和数字，输入的字符大于7个密码为中
		else if (pwd.matches ("^[A-Z0-9]{6,16}"))
		{
			strong = 1;
		}
		//输入的小写字母和数字，输入的字符小于7个密码为弱
		else if (pwd.matches ("^[a-z0-9]{1,5}"))
		{
			strong = 0;
		}
		//输入的小写字母和数字，输入的字符大于7个密码为中
		else if (pwd.matches ("^[a-z0-9]{6,16}"))
		{
			strong = 1;
		}
		//输入的大写字母和小写字母，输入的字符小于7个密码为弱
		else if (pwd.matches ("^[A-Za-z]{1,5}"))
		{
			strong = 0;
		}
		//输入的大写字母和小写字母，输入的字符大于7个密码为中
		else if (pwd.matches ("^[A-Za-z]{6,16}"))
		{
			strong = 1;
		}
		//输入的大写字母和小写字母和数字，输入的字符小于5个个密码为弱
		else if (pwd.matches ("^[A-Za-z0-9]{1,5}"))
		{
			strong = 0;
		}
		//输入的大写字母和小写字母和数字，输入的字符大于6个个密码为中
		else if (pwd.matches ("^[A-Za-z0-9]{6,8}"))
		{
			strong = 1;
		}
		//输入的大写字母和小写字母和数字，输入的字符大于8个密码为强
		else if (pwd.matches ("^[A-Za-z0-9]{9,16}"))
		{
			strong = 2;
		}
		return strong;
	}

	/**
	 * 判断网络连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
		} else {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].isAvailable()&&info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	//验证邮政编码
	public static boolean checkPost(String post){
		if(post.matches("[1-9]\\d{5}(?!\\d)")){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 打电话
	 * @param context
	 * @param tel
	 */
	public static void callPhone(Context context, String tel) {
		if (TextUtils.isEmpty(tel)) {
			tel = "";
		}
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
		context.startActivity(intent);
	}
	/**
	 * 检验是否存在非法字符
	 */
	public static boolean IllegalChar(String args) {
		if(args.matches("[a-zA-Z0-9_\u4e00-\u9fa5]*")){
			//不是非法字符
			return false;
		}
		return true;
	}

	/**
	 * EditText竖直方向是否可以滚动
	 * @param editText 需要判断的EditText
	 * @return true：可以滚动  false：不可以滚动
	 */
	public static boolean canVerticalScroll(EditText editText) {
		//滚动的距离
		int scrollY = editText.getScrollY();
		//控件内容的总高度
		int scrollRange = editText.getLayout().getHeight();
		//控件实际显示的高度
		int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
		//控件内容总高度与实际显示高度的差值
		int scrollDifference = scrollRange - scrollExtent;

		if(scrollDifference == 0) {
			return false;
		}

		return (scrollY > 0) || (scrollY < scrollDifference - 1);
	}


	/**
	 * 获取当月的 天数
	 * */
	public static int getCurrentMonthDay() {

		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 根据年 月 获取对应的月份 天数
	 * */
	public static int getDaysByYearMonth(int year, int month) {

		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 根据日期 找到对应日期的 星期
	 */
	public static String getDayOfWeekByDate(String date) {
		String dayOfweek = "-1";
		try {
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");

			Date myDate = myFormatter.parse(date);
			SimpleDateFormat formatter = new SimpleDateFormat("E");
			String str = formatter.format(myDate);
			dayOfweek = str;

		} catch (Exception e) {
			System.out.println("错误!");
		}
		return dayOfweek;
	}
	/**
	 *设置时间格式
	 */
	public static String getDateType(String date) {
		String retu=null;
		try {
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");

			Date myDate = myFormatter.parse(date);
			retu=myFormatter.format(myDate);
			return retu;
		} catch (Exception e) {
			System.out.println("错误!");
		}
		return retu;
	}
	/**
	 *设置时间格式
	 */
	public static String getDateType(String date, String type) {
		String retu=null;
		try {
			SimpleDateFormat myFormatter = new SimpleDateFormat(type);

			Date myDate = myFormatter.parse(date);
			retu=myFormatter.format(myDate);
			return retu;
		} catch (Exception e) {
			System.out.println("错误!");
		}
		return retu;
	}
	/**
	 *设置时间格式，转成成时间搓
	 */
	public static Long getDateTime(String date, String type) {
		Long retu=null;
		try {
			SimpleDateFormat myFormatter = new SimpleDateFormat(type);

			Date myDate = myFormatter.parse(date);
			retu=myDate.getTime();
			return retu;
		} catch (Exception e) {
			System.out.println("错误!");
		}
		return retu;
	}
	/**
	 * 传入时间搓输出时间类型
	 * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分"）
	 * @param time
	 * @param type
	 * @return
	 */
	public static String SetTimeType(String time, String type){

		String retu=null;
		String date2=null;
		try {

			Log.i("type", type);
			if(type.substring(0, 2).equals("hh")){
				date2= new SimpleDateFormat("HH:mm").format(new Date(Long.parseLong(time)));
			}else{
				date2= new SimpleDateFormat(type).format(new Date(Long.parseLong(time)));
			}
			Date date=new Date(Long.valueOf(time));
			SimpleDateFormat myFormatter = new SimpleDateFormat(type);

			retu=myFormatter.format(date);
			Log.i("retu", retu);
			return date2;

		} catch (Exception e) {
			System.out.println("错误!");
		}
		return retu;
	}
	/*
     * 获取当前年月日
     */
	public static String getCurDate(){

		Calendar c= Calendar.getInstance(Locale.CHINA);
		int year=c.get(Calendar.YEAR);
		int month=c.get(Calendar.MONTH);
		int day=c.get(Calendar.DAY_OF_MONTH);
		return year+"-"+(month+1)+"-"+day;
	}
	/*
     * 获取当前年月
     * 2016年9月
     */
	public static String getCurYearMonth(){

		Calendar c= Calendar.getInstance(Locale.CHINA);
		int year=c.get(Calendar.YEAR);
		int month=c.get(Calendar.MONTH);
		int day=c.get(Calendar.DAY_OF_MONTH);
		return year+"年"+(month+1)+"月";
	}
	/*
     * 获取当前年月
     * 2016-9
     */
	public static String getCurYearMonthadd(){

		Calendar c= Calendar.getInstance(Locale.CHINA);
		int year=c.get(Calendar.YEAR);
		int month=c.get(Calendar.MONTH);
		int day=c.get(Calendar.DAY_OF_MONTH);
		return year+"-"+(month+1);
	}
	//setScale(2,BigDecimal.ROUND_DOWN);//直接删除多余的小数位  11.116约=11.11
	//setScale(2,BigDecimal.ROUND_UP);//临近位非零，则直接进位；临近位为零，不进位。11.114约=11.12
	//double d = 3.1415926;   String result = String .format("%.2f");
	public static String getDecimalFormat(double qrofit){
		DecimalFormat df = new DecimalFormat("0.00");//    #.## 表示有0就显示0,没有0就不显示   0.00表示,有没有0都会显示
//      BigDecimal decimal = new BigDecimal(qrofit);
//      decimal.setScale(2,BigDecimal.ROUND_HALF_UP);
		return df.format(qrofit);
	}
	public static String getDecimalFormatOne(double qrofit){
		DecimalFormat df = new DecimalFormat("0.0");//    #.## 表示有0就显示0,没有0就不显示   0.00表示,有没有0都会显示
//      BigDecimal decimal = new BigDecimal(qrofit);
//      decimal.setScale(2,BigDecimal.ROUND_HALF_UP);
		return df.format(qrofit);
	}

	public static void setSystemLook(Activity activity){
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings","com.android.settings.ChooseLockGeneric");
		intent.setComponent(cm);
		activity.startActivityForResult(intent, 0);
	}
	public static void setSystemLook(Context context, Activity activity){
//		Intent intent = new Intent(context, ChooseLockGeneric.class);
//		activity.startActivityForResult(intent, 0);
	}

	public static void startRemoveSystemLockActivity(Activity activity) {
		String phoneName = Build.BRAND;
		if (phoneName.toLowerCase().contains("xiaomi")) {
			Intent intent = new Intent("/");
			ComponentName cm = new ComponentName(
					"com.android.settings",
					"com.android.settings.DevelopmentSettings");
			intent.setComponent(cm);
			activity.startActivityForResult(intent, 0);
		} else if (phoneName.toLowerCase().contains("lge")) {
			Intent intent = new Intent("/");
			ComponentName cm = new ComponentName(
					"com.android.settings",
					"com.android.settings.lockscreen.ChooseLockGeneric");
			intent.setComponent(cm);
			activity.startActivityForResult(intent, 0);
		} else {
			Intent intent = new Intent("/");
			ComponentName cm = new ComponentName(
					"com.android.settings",
					"com.android.settings.ChooseLockGeneric");
			intent.setComponent(cm);
			activity.startActivityForResult(intent, 0);
		}
	}

	/**
	 *跳转到系统解锁页面
	 */

//	public final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 0;
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	public static boolean showAuthenticationScreen(Context context, Activity activity) {
		KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		if (mKeyguardManager.isKeyguardSecure()) {
			Intent intent = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
			}
			if (intent != null) {
				activity.startActivityForResult(intent, 0);
			}else {
				return false;
			}
			return true;
		}else {
			return false;
		}
	}

	public static void setSubName(String name,TextView textView){
		if (!TextUtils.isEmpty(name)){
			textView.setVisibility(View.VISIBLE);
			if (name.length() >2){
				textView.setText(name.substring(name.length()-2));
			}else if (name.length() >1){
				textView.setText(name.substring(name.length()-1));
			}else {
				textView.setText(name);
			}
		}else {
			textView.setVisibility(View.GONE);
		}
	}

	public static String setSubName(String name){
		if (!TextUtils.isEmpty(name)){
			if (name.length() >2){
				return name.substring(name.length()-2);
			}else if (name.length() >1){
				return name.substring(name.length()-1);
			}else {
				return name;
			}
		}
		return "";
	}

}
