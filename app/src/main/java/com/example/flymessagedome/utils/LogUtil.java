package com.example.flymessagedome.utils;

import android.util.Log;

public class LogUtil {
	/**
	 * 控制日志是否打印
	 */
	private static boolean isPrint = true;

	public static boolean isPrint() {
		return isPrint;
	}

	public static void setIsPrint(boolean isPrint) {
		LogUtil.isPrint = isPrint;
	}

	public static void d(String tag, String msg){
		if (isPrint) {
			int segmentSize = 2001;//3 * 1024
			long length = msg.length();
			if (length <= segmentSize ) {// 长度小于等于限制直接打印
				Log.d(tag, msg);
			}else {
				while (msg.length() > segmentSize ) {// 循环分段打印日志
					String logContent = msg.substring(0, segmentSize );
					msg = msg.replace(logContent, "");
					Log.d(tag, logContent);
				}
				Log.d(tag, msg);// 打印剩余日志
			}
		}
	}
	
	public static void i(String tag, String msg){
		if (isPrint) {
			int segmentSize = 2001;//3 * 1024
			long length = msg.length();
			if (length <= segmentSize ) {// 长度小于等于限制直接打印
				Log.i(tag, msg);
			}else {
				while (msg.length() > segmentSize ) {// 循环分段打印日志
					String logContent = msg.substring(0, segmentSize );
					msg = msg.replace(logContent, "");
					Log.i(tag, logContent);
				}
				Log.i(tag, msg);// 打印剩余日志
			}
		}
	}
	
	public static void w(String tag, String msg) {
		if (isPrint) {
			int segmentSize = 2001;//3 * 1024
			long length = msg.length();
			if (length <= segmentSize ) {// 长度小于等于限制直接打印
				Log.w(tag, msg);
			}else {
				while (msg.length() > segmentSize ) {// 循环分段打印日志
					String logContent = msg.substring(0, segmentSize );
					msg = msg.replace(logContent, "");
					Log.w(tag, logContent);
				}
				Log.w(tag, msg);// 打印剩余日志
			}
		}
	}
//	private static final int LOG_MAXLENGTH = 3*1024;
	public static void e(String tag, String msg) {
		if (isPrint) {
			int segmentSize = 2001;//3 * 1024
			long length = msg.length();
			if (length <= segmentSize ) {// 长度小于等于限制直接打印
				Log.e(tag, msg);
			}else {
				while (msg.length() > segmentSize ) {// 循环分段打印日志
					String logContent = msg.substring(0, segmentSize );
					msg = msg.replace(logContent, "");
					Log.e(tag, logContent);
				}
				Log.e(tag, msg);// 打印剩余日志
			}
		}
//		//因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
//		//  把4*1024的MAX字节打印长度改为2001字符数
//		int max_str_length = 2001 - tag.length();
//		//大于4000时
//		while (msg.length() > max_str_length) {
//			Log.i(tag, msg.substring(0, max_str_length));
//			msg = msg.substring(max_str_length);
//		}
//		//剩余部分
//		Log.i(tag, msg);

//		if (isPrint) {
//			int strLength = msg.length();
//			int start = 0;
//			int end = LOG_MAXLENGTH;
//			for (int i = 0; i < 100; i++) {
//				if (strLength > end) {
//					Log.e(tag + i, msg.substring(start, end));
//					start = end;
//					end = end + LOG_MAXLENGTH;
//				} else {
//					Log.e(tag + i, msg.substring(start, strLength));
//					break;
//				}
//			}
//		}

	}
	
	public static void v(String tag, String msg) {
		if (isPrint) {
			int segmentSize = 2001;//3 * 1024
			long length = msg.length();
			if (length <= segmentSize ) {// 长度小于等于限制直接打印
				Log.v(tag, msg);
			}else {
				while (msg.length() > segmentSize ) {// 循环分段打印日志
					String logContent = msg.substring(0, segmentSize );
					msg = msg.replace(logContent, "");
					Log.v(tag, logContent);
				}
				Log.v(tag, msg);// 打印剩余日志
			}
		}
	}
}
