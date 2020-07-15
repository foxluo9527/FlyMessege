package com.example.flymessagedome.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * 根据手机分辨率进行px和dp的转换工具类
 * 关闭/打开背景音乐
 */
public class DPTools {
	private static final double EARTH_RADIUS = 6378137.0;
	
	// 返回单位是米
	public static String getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(Lat1) * Math.cos(Lat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = Math.abs(s * EARTH_RADIUS);
		s = Math.round(s * 10000) / 10000.0;
        String distance = "";
		DecimalFormat df = new DecimalFormat("0.00");
        if (s>1000){
            double h = s*1.0/1000;
            distance = df.format(Math.round(h*100)/100.0)+"km";
        }else {
            distance = df.format(Math.round(s*100)/100.0)+"m";
        }
        
		return distance;
	}
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	
	public static String changeDistance(double distance){
		String disStr = "";
        DecimalFormat df = new DecimalFormat("0.00");
		if (distance>1000){
			double h = distance*1.0/1000;
			disStr = df.format(Math.round(h*100)/100.0)+"km";
		}else {
			disStr = df.format(Math.round(distance*100)/100.0)+"m";
		}
		return disStr;
	}
	
	public static String getMoney(Double money){
        if (money == null){
            return "0.00";
        }
		DecimalFormat format = new DecimalFormat("0.00");
		return format.format(Math.round(money*100)/100.0);
	}
	
	/**
	 * 获取屏幕的高度
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context){
		final float scale = context.getResources().getDisplayMetrics().heightPixels;
		return (int) scale;
	}

	/**
	 * 获取屏幕的宽度
	 * @param context
	 * @return
     */
	public static int getScreenWith(Context context){
		final float scale = context.getResources().getDisplayMetrics().widthPixels;
		return (int) scale;
	}

	/**
	 * dipתΪ px
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 *  px תΪ dip
     *  根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static float px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static float px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 获取状态栏高度
	 * @param context
	 * @return
     */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
				"android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**@param bMute 值为true时为关闭背景音乐。*/
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static void muteAudioFocus(Context context, boolean bMute) {
		if(context == null){
			Log.d("ANDROID_LAB", "context is null.");
			return;
		}
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO){
			// 2.1以下的版本不支持下面的API：requestAudioFocus和abandonAudioFocus
			Log.d("ANDROID_LAB", "Android 2.1 and below can not stop music");
			return;
		}
		boolean bool = false;
		AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		if(bMute){
			int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		}else{
			int result = am.abandonAudioFocus(null);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		}
		Log.d("ANDROID_LAB", "pauseMusic bMute="+bMute +" result="+bool);
		//return bool;
	}

}
