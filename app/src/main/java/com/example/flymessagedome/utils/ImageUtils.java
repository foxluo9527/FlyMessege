/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.flymessagedome.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author xule 图片存储获取等工具类F
 */
public class ImageUtils {

	private static final String[] STORE_IMAGES = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, };
	private static final String SD_PATH = "/sdcard/GSWX/pic/";
	private static final String IN_PATH = "/GSWX/pic/";
	/**
	 * 生成固定大小的二维码(不需网络权限)
	 *
	 * @param content 需要生成的内容
	 * @param width   二维码宽度
	 * @param height  二维码高度
	 * @return
	 */
	public static Bitmap generateBitmap(String content, int width, int height,Bitmap logoBitmap,float size) {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		Map<EncodeHintType, String> hints = new HashMap<>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		try {
			BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			int[] pixels = new int[width * height];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (encode.get(j, i)) {
						pixels[i * width + j] = 0x00000000;
					} else {
						pixels[i * width + j] = 0xffffffff;
					}
				}
			}
			if (logoBitmap==null)
				return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
			else
				return addLogo(Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565)
				,logoBitmap,size);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 解析二维码（使用解析RGB编码数据的方式）
	 *
	 * @param path
	 * @return
	 */
	public static Result decodeBarcodeRGB(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		Bitmap barcode = BitmapFactory.decodeFile(path, opts);
		Result result = decodeBarcodeRGB(barcode);
		barcode.recycle();
		barcode = null;
		return result;
	}

	/**
	 * 解析二维码 （使用解析RGB编码数据的方式）
	 *
	 * @param barcode
	 * @return
	 */
	public static Result decodeBarcodeRGB(Bitmap barcode) {
		int width = barcode.getWidth();
		int height = barcode.getHeight();
		int[] data = new int[width * height];
		barcode.getPixels(data, 0, width, 0, 0, width, height);
		RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		Result result = null;
		try {
			result = reader.decode(bitmap1);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		barcode.recycle();
		barcode = null;
		return result;
	}
	public static Bitmap getBitmapForSize(Bitmap bitmap, int x, int y) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 设置想要的大小
		int newWidth = x;
		int newHeight = y;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newBitMap = Bitmap.createBitmap(bitmap, 0, 0, width, height,matrix, true);
		return newBitMap;
	}
	/**
	 * 向二维码中间添加logo图片(图片合成)
	 *
	 * @param srcBitmap 原图片（生成的简单二维码图片）
	 * @param logoBitmap logo图片
	 * @param logoPercent 百分比 (用于调整logo图片在原图片中的显示大小, 取值范围[0,1] )
	 * @return
	 */
	private static Bitmap addLogo(Bitmap srcBitmap,  Bitmap logoBitmap, float logoPercent){
		if(srcBitmap == null){
			return null;
		}
		if(logoBitmap == null){
			return srcBitmap;
		}
		//传值不合法时使用0.2F
		if(logoPercent < 0F || logoPercent > 1F){
			logoPercent = 0.2F;
		}

		/** 1. 获取原图片和Logo图片各自的宽、高值 */
		int srcWidth = srcBitmap.getWidth();
		int srcHeight = srcBitmap.getHeight();
		int logoWidth = logoBitmap.getWidth();
		int logoHeight = logoBitmap.getHeight();

		/** 2. 计算画布缩放的宽高比 */
		float scaleWidth = srcWidth * logoPercent / logoWidth;
		float scaleHeight = srcHeight * logoPercent / logoHeight;

		/** 3. 使用Canvas绘制,合成图片 */
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(srcBitmap, 0, 0, null);
		canvas.scale(scaleWidth, scaleHeight, srcWidth/2, srcHeight/2);
		canvas.drawBitmap(logoBitmap, srcWidth/2 - logoWidth/2, srcHeight/2 - logoHeight/2, null);

		return bitmap;
	}
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 随机生产文件名
	 *
	 * @return
	 */
	private static String generateFileName() {
		return UUID.randomUUID().toString();
	}

	public static String getImagePathByUri(Context context, Uri uri) {
		// 查询，返回cursor
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		// 第一行第二列保存路径strRingPath
		cursor.moveToFirst();
		String strRingPath = cursor.getString(1);
		cursor.close();
		return strRingPath;
	}
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	public static void addImageToGallery(Context context, String filePath) {
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.MediaColumns.DATA, filePath);
		context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}

	public static File getAlbumStorageDir(String albumName) {
		// Get the directory for the user's public pictures directory.
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
		if (!file.mkdirs()) {
			Log.i(null, "Directory not created");
		}
		return file;
	}

	public static Bitmap getBitmapByUrl(String url){
		URL imageURL = null;
		Bitmap bitmap = null;
		Log.e("inuni","URL = "+url);
		try {
			imageURL = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) imageURL
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static Bitmap getBitmapByPath(String path , Context context) {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, DPTools.getScreenWith(context) * DPTools.getScreenHeight(context));
		opts.inJustDecodeBounds = false;
		try {
			bitmap = BitmapFactory.decodeFile(path, opts);
		} catch (OutOfMemoryError err) {
		}
		return bitmap;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128
				: (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 读取图片的旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return 图片的旋转角度
	 */
	public static int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 将图片按照某个角度进行旋转
	 * 
	 * @param bm
	 *            需要旋转的图片
	 * @param degree
	 *            旋转角度
	 * @return 旋转后的图片
	 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;

		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}

	public static final Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
	/**
	 * 保存bitmap到本地
	 *
	 * @param context
	 * @param mBitmap
	 * @return
	 */
	public static String saveBitmap(Context context, Bitmap mBitmap, int qulity) {
//		String savePath;
		File filePic;
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			savePath = SD_PATH;
//		} else {
//			savePath = context.getApplicationContext().getFilesDir()
//					.getAbsolutePath()
//					+ IN_PATH;
//		}
		try {
			filePic = new File(FileUtil.getPath() + generateFileName() + ".jpg");
			if (!filePic.exists()) {
				filePic.getParentFile().mkdirs();
				filePic.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(filePic);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, qulity, fos);
			fos.flush();
			fos.close();
//			ToastUtil.showMessage("保存成功");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return filePic.getAbsolutePath();
	}
	/**
	 * 保存bitmap到本地
	 *
	 * @param context
	 * @param mBitmap
	 * @return
	 */
	public static String saveImgBitmap(Context context, String fileName, Bitmap mBitmap, int qulity) {
//		String savePath;
		File filePic;
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			savePath = SD_PATH;
//		} else {
//			savePath = context.getApplicationContext().getFilesDir()
//					.getAbsolutePath()
//					+ IN_PATH;
//		}
		try {
			filePic = new File(FileUtil.getPath() + fileName);
			if (!filePic.exists()) {
				filePic.getParentFile().mkdirs();
				filePic.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(filePic);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, qulity, fos);
			fos.flush();
			fos.close();
//			ToastUtil.showMessage("保存成功");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return filePic.getAbsolutePath();
	}


	//Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img_public_no);
	public static void saveImageToGallery(Context context, Bitmap bmp, String fileName) {
		// 首先保存图片
//		File appDir = new File(Environment.getExternalStorageDirectory(), "GSWX");
//		if (!appDir.exists()) {
//			appDir.mkdir();
//		}
//		String fileName = System.currentTimeMillis() + ".jpg";
//		File file = new File(appDir, fileName);
		File file = null;
		try {
			file = new File(FileUtil.getPath() + fileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
//		try {
//			MediaStore.Images.Media.insertImage(context.getContentResolver(),
//					file.getAbsolutePath(), fileName, null);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
//		ToastUtil.showMessage("保存成功");
	}
}

