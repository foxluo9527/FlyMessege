package com.example.flymessagedome.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.yalantis.ucrop.util.FileUtils.getDataColumn;

public class FileUtil {
	public static String path="";
	static{
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/FlyMessage";
		}else{
			path= Environment.getDataDirectory().getAbsolutePath()+"/FlyMessage";
		}
	}
	public static String getPath(){
		File file=new File(path);
		if(!file.exists()){
			file.mkdirs();
//			file.mkdir();
		}
		return path+"/";
	}
	/**
	 * 根据路径打开文件
	 * @param context 上下文
	 * @param path 文件路径
	 */
	public static void openFileByPath(Context context,String path) {
		if(context==null||path==null)
			return;
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		//文件的类型
		String type = "";
		for(int i =0;i<Constant.MATCH_ARRAY.length;i++){
			//判断文件的格式
			if(path.toString().contains(Constant.MATCH_ARRAY[i][0].toString())){
				type = Constant.MATCH_ARRAY[i][1];
				break;
			}
		}
		try {
			//设置intent的data和Type属性
			intent.setDataAndType(Uri.fromFile(new File(path)), type);
			//跳转
			context.startActivity(intent);
		} catch (Exception e) { //当系统没有携带文件打开软件，提示
			ToastUtils.showToast("无法打开该格式文件!");
			e.printStackTrace();
		}
	}
	/**
	 * 安装APK
	 */
	public static  void installApk(Context context, String downloadApk) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		File file = new File(downloadApk);
		LogUtil.d("HttpTool","安装路径=="+downloadApk);
		LogUtil.d("HttpTool","包名=="+context.getPackageName());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName()+".fileProvider", file);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
		} else {
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(file);
			intent.setDataAndType(uri, "application/vnd.android.package-archive");
		}
		context.startActivity(intent);

	}
	public static String getUriPath(Context context, Uri uri) {
		if(uri == null) {
			return null;
		}
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
			if("com.android.externalstorage.documents".equals(uri.getAuthority())) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if("com.android.providers.media.documents".equals(uri.getAuthority())) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = null;
				if("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {split[1]};
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} else if("content".equalsIgnoreCase(uri.getScheme())) {
			if("com.google.android.apps.photos.content".equals(uri.getAuthority())) {
				return uri.getLastPathSegment();
			}
			return getDataColumn(context, uri, null, null);
		} else if("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}
	public static boolean renameNewDirectory(String oldfolder, String newfolder )
	{
		File file=new File(oldfolder);
		boolean isOk = file.renameTo(new File(newfolder));
		return isOk;
		//		if(file.exists()){
		//			file.renameTo(new File(newfolder));
		//			return true;
		//		}else{
		//			return false;
		//		}
		//		if(fileIsExists(oldname))
		//		{
		//			File oldFile = new File(oldname);
		//			oldFile.renameTo(new File(newname));
		//			return true;
		//		}
		//		else
		//			return false;
	}

//	public static void saveMisocToLocal(AssetManager assetManager,String[] sounds2) {//将assets里的文件保存到本地
//		String filePath = FileUtil.getRecentChatComedyVideoPath();
//		for (int i = 0; i < sounds2.length; i++) {
//			if (sounds2[i].endsWith(".mp4")) {
//				//				Log.d("fff", "----sound-----"+sound);
//				File file = new File(filePath, sounds2[i]);
//				//				Log.d("fff", "--file--length-----"+file.length());
//				if (file.exists()) {
//					continue;
//				}
//				//				File tempFile = null;
//				//				try {
//				//					tempFile = File.createTempFile(sound, null, musicFile);
//				//				} catch (IOException e1) {
//				//					e1.printStackTrace();
//				//				}
//				//				Log.d("fff", "----getAbsolutePath-----"+file.getAbsolutePath());
//				//				Log.d("fff", "----getPath-----"+file.getPath());
//
//				InputStream is = null;
//				FileOutputStream fos = null;
//				try {
//					is = assetManager.open(sounds2[i]);
//					fos = new FileOutputStream(file);
//					byte[] buff = new byte[1024*4];
//					int len = 0;
//					while((len = is.read(buff)) != -1){
//						fos.write(buff, 0, len);
//						fos.flush();
//					}
//					is.close();
//					fos.close();
//					//					Log.d("fff", "----getAbsolutePath-----"+file.getAbsolutePath());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

//	public static void getComedyVideoFile(final List<VideoInfo> list, final File file) {// 遍历获得音乐文件
//
//		//		new Thread(new Runnable() {
//		//
//		//			@Override
//		//			public void run() {
//
//		file.listFiles(new FileFilter() {
//
//			@Override
//			public boolean accept(File file) {
//				// sdCard找到视频名称
//				String name = file.getName();
//
//				int i = name.indexOf('.');
//				if (i != -1) {
//					name = name.substring(i);
//					if (name.equalsIgnoreCase(".mp4") || name.equalsIgnoreCase(".flv")
//							) {
//						VideoInfo vi = new VideoInfo();
//						//								vi.setDisplayName(file.getName());
//						vi.setFilePath(file.getAbsolutePath());
//						long modified = file.lastModified();
//						vi.setModifyTime(modified);
//						String musicName = file.getName();
//						vi.setFilename(musicName);
//						vi.setVideoName(musicName);
//						int lastIndexOf = musicName.lastIndexOf(".");
//						String musicNa = musicName.substring(0, lastIndexOf);
//						String thumbImg = FileUtil.getRecentChatComedyVideoPath()+musicNa+".jpg";
//						if (new File(thumbImg).exists()) {
//							vi.setFilePicPath(thumbImg);
//						}else{
//							Bitmap mThumbBitmap=ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),Thumbnails.MINI_KIND);//用于获取视频的缩略图
//							//								vi.setB(mThumbBitmap);
//							FileUtil.saveBitmap(mThumbBitmap, FileUtil.getRecentChatComedyVideoPath(), musicNa+".jpg");
//							vi.setFilePicPath(thumbImg);
//						}
//						list.add(vi);
//						return true;
//					}
//				} else if (file.isDirectory()) {
//					getComedyVideoFile(list, file);
//				}
//				//						handler.sendEmptyMessage(MSG_QUERY_COMPLETE);
//				return false;
//			}
//		});
//		//			}
//		//		}).start();
//	}

	/**
	 * 将Editetext中的内容保存成.txt文本文件
	 * @param content
	 * @param filePath
	 * @param fileName
	 */
	public static void saveFileToText(String content, String filePath, String fileName){
		FileOutputStream fos = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				file.mkdirs();
			}
			file = new File(filePath+fileName);
			fos = new FileOutputStream(file, true);
			fos.write(content.getBytes());
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 读取文件，显示在textView上
	 * @param filePath
	 * @return
	 */
	public static String readFileFromTxt(String filePath){
		FileInputStream fis = null;
		byte[] buff = null;
		try {
			fis = new FileInputStream(new File(filePath));
			int len = fis.available();
			buff = new byte[len];
			fis.read(buff);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return new String(buff);
	}
	/**
	 * 读取文件，显示在textView上
	 * @param filePath
	 * @return
	 */
	public static String readFileFromPic(String filePath, long sizeL){
		InputStream is = null;
		byte[] buff = null;
		String text = "";
		try {
			is = new FileInputStream(new File(filePath));
			int len = (int) sizeL;
			buff = new byte[len];
			int read = is.read(buff);
//			is.read(buff, frameN * 1024, frameN * 1024 + da);
//			text = byte2hex(buff);
			text = byte2HexStr(buff);
//			Log.d("fff", "----------read----"+read);
//			Log.d("fff", "----------text----"+text);
		} catch (FileNotFoundException e) {
//			Log.d("fff", "----------1----"+e.toString());
			e.printStackTrace();
		} catch (IOException e) {
//			Log.d("fff", "---------2----"+e.toString());
			e.printStackTrace();
		}finally{
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
//					Log.d("fff", "----------3----"+e.toString());
					e.printStackTrace();
				}
			}
		}
		return text;
	}

	public static void saveBitmap(Bitmap bitmap, String localPath, String bitName){
		if (bitmap == null) {
			return;
		}
		File file = new File(localPath+bitName);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			if (bitmap != null) {
				if (bitmap.compress(CompressFormat.JPEG, 100, fos)) {
					fos.flush();
					fos.close();
				}
				bitmap.recycle();
				bitmap = null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int readTextEnglish(String s){//统计有多少字母
		int count = 0;    
		for(int i=0;i<s.length();i++){     
			char cs =s.charAt(i);  
			if((cs>='a'&& cs<='z') || ((cs>='A'&& cs<='Z')) || ((cs>='0'&& cs<='9')) ){  
				count++;  
			}  
		}
		return count;
	}
	public static String readTextEnglishContent(String s){//统计有效字母
		String con = "";
		for(int i=0;i<s.length();i++){     
			char cs =s.charAt(i);  
			if((cs>='a'&& cs<='z') || ((cs>='A'&& cs<='Z')) || ((cs>='0'&& cs<='9')) ){  
				con += cs;
			}  
		}
		return con;
	}

	public static String readTxtContent(String s){//统计有效汉子
		String content = "";
		String Reg="^[\u4e00-\u9fa5]{1}$";  //汉字的正规表达式
		for(int i=0;i<s.length();i++){   
			String b= Character.toString(s.charAt(i));
			if(b.matches(Reg)){
				content += b;
			}
		}
		return content;
	}

	public static String readTXTContent(String s){
		String con = "";
		String Reg="^[\u4e00-\u9fa5]{1}$";  //汉字的正规表达式
		for(int i=0;i<s.length();i++){     
			char cs =s.charAt(i);  
			String b= Character.toString(s.charAt(i));
			if(b.matches(Reg) || (cs>='a'&& cs<='z') || ((cs>='A'&& cs<='Z')) || ((cs>='0'&& cs<='9')) ){  
				con += cs;
			}  
		}
		return con;
	}

	public static int readTxtLength(String s){//统计有多少汉子
		int count =0;   
		String content = "";
		String Reg="^[\u4e00-\u9fa5]{1}$";  //汉字的正规表达式
		for(int i=0;i<s.length();i++){   
			String b= Character.toString(s.charAt(i));
			if(b.matches(Reg)){
				count++;   
				content += b;
			}
		}
		return count;
	}
	/**
	 * 判断文字有多少字节数
	 * @param s
	 * @return
	 */
	public static int getWordCount(String s)
	{   
		int length = 0;   
		for(int i = 0; i < s.length(); i++)   
		{   
			int ascii = Character.codePointAt(s, i);
			if(ascii >= 0 && ascii <=255)   
				length++;   
			else  
				length += 2;   
		}   
		return length;   
	} 

	public static String readTXTFile(String filePath){
		BufferedReader br = null;
		File file = new File(filePath);
		FileInputStream fis = null;
		StringBuffer sb = new StringBuffer();
		try {
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis));
			//			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				//				sb.append("\u3000\u3000"+line);
				//				sb.append(line);
				//				sb.append("\n\n");

				if(TextUtils.isEmpty(line)|| line.equals("") || line.equals(" ") || line.equals("  ") || line.equals("   ") || line.equals("    ") || line.equals("     "))
					continue;
				if (!TextUtils.isEmpty(line) && line.length() > 1) {
					while (line.substring(0, 1).equals(" ") || line.substring(0, 1).equals("\u3000")) {
						line = line.replace(line.substring(0, 1), "");
						if (TextUtils.isEmpty(line)) {
							break;
						}
					}
				}

				//				String head = "";
				//				if (!TextUtils.isEmpty(line) && line.length() > 4) {
				//					head = line.substring(0, 2);
				//					if (head.equals("  ") || head.equals("\u3000\u3000")) {
				//						line = line.replace(head, "");
				//					}
				//					head = line.substring(0, 2);
				//					while (line.substring(0, 1).equals(" ")) {
				//						line = line.replace(head, "");
				//					}
				//				}else{
				//					if (!TextUtils.isEmpty(line) && line.length() > 2) {
				//						head = line.substring(0, 2);
				//					}else{
				//						head = line;
				//					}
				//				}
				String str;
				//				if (head.equals("  ") || head.equals("\u3000\u3000")) {
				//					line = line.replace(head, "");
				//					str = line + "\n\n";
				//				}else{
				//					str = "\u3000\u3000" +line + "\n\n";
				str = line + "\n\n";
				//				}
				sb.append(str); 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

//	public static String readTXTFileForCount(String filePath,int count){
//		String content = "";
//		File file = new File(filePath);
//		FileInputStream fis = null;
//		try {
//			fis = new FileInputStream(file);
//			byte[] buffer = new byte[count];
//			fis.read(buffer);
//			//			content = EncodingUtils.getString(buffer, "GB2312");
//			content = EncodingUtils.getString(buffer, "UTF-8");
//
//			if(TextUtils.isEmpty(content)|| content.equals("") || content.equals(" ") || content.equals("  ") || content.equals("   ") || content.equals("    ") || content.equals("     ")){
//				fis.read(buffer, count, count*2);
//				//			content = EncodingUtils.getString(buffer, "GB2312");
//				content = EncodingUtils.getString(buffer, "UTF-8");
//			}
//			if (!TextUtils.isEmpty(content) && content.length() > 1) {
//				while (content.substring(0, 1).equals(" ") || content.substring(0, 1).equals("\u3000")) {
//					content = content.replace(content.substring(0, 1), "");
//					if (TextUtils.isEmpty(content)) {
//						break;
//					}
//				}
//			}
//
//			fis.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return content;
//	}
	public static String readTXTFileForCount(String filePath){
		String content = "";
		BufferedReader br = null;
		File file = new File(filePath);
		FileInputStream fis = null;
		StringBuffer sb = new StringBuffer();
		try {
			
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis));
			//			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			content = sb.toString();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String readHelpTXTFile(String filePath){
		BufferedReader br = null;
		File file = new File(filePath);
		FileInputStream fis = null;
		StringBuffer sb = new StringBuffer();
		try {
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis,"GB2312"));
			//			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (TextUtils.isEmpty(line) || line.equals("")) {
					continue;
				}
				sb.append(line);
				sb.append("\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public static void savaResImgToLocal(Context context, int id, String destPath, String name){
		//		String path = getRecentChatPath() + "chat_default.png";
		String path = destPath + name+".png";
		File file = new File(path);
		if (file.exists()) {
			return;
		}
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
		//		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			file.createNewFile();
			//			fos = context.openFileOutput("chat_tool_camera", Context.MODE_PRIVATE);
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(CompressFormat.PNG, 100, bos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	//保存图片到指定目录  并且在图库更新
	public static void saveImgeToGallery(Context context, Bitmap bitmap, String filename){
		//首先保存图片
		File dir = new File(getPath());
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir, filename);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), filename, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+file.getAbsolutePath())));
	}
	
	/** 
	 * 半角转换为全角 
	 *  解决文字，字母，标点排列，参吃不齐
	 * @param input 
	 * @return 
	 */  
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();  
		for (int i = 0; i < c.length; i++) {  
			if (c[i] == 12288) {  
				c[i] = (char) 32;  
				continue;  
			}  
			if (c[i] > 65280 && c[i] < 65375)  
				c[i] = (char) (c[i] - 65248);  
		}  
		return new String(c);
	}  
	/**
	 * 删除文件夹以及目录下的文件
	 * @param   filePath 被删除目录的文件路径
	 * @return  目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String filePath) {
		boolean flag = false;
		//如果filePath不以文件分隔符结尾，自动添加文件分隔符
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		File dirFile = new File(filePath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		File[] files = dirFile.listFiles();
		//遍历删除文件夹下的所有文件(包括子目录)
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				//删除子文件
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) break;
			} else {
				//删除子目录
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) break;
			}
		}
		if (!flag) return false;
		//删除当前空目录
		return dirFile.delete();
	}

	/**
	 * 删除文件，可以是文件或文件夹
	 *
	 * @param fileName
	 *            要删除的文件名
	 * @return 删除成功返回true，否则返回false
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			return false;
		} else {
			if (file.isFile())
				return deleteFile(fileName);
			else
				return deleteDirectory(fileName);
		}
	}

	/**
	 * 删除单个文件
	 * @param   filePath    被删除文件的文件名
	 * @return 文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFilePath(String filePath) {
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}

	/**
	 *  根据路径删除指定的目录或文件，无论存在与否
	 *@param filePath  要删除的目录或文件
	 *@return 删除成功返回 true，否则返回 false。
	 */
	public static boolean DeleteFolder(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return false;
		} else {
			if (file.isFile()) {
				// 为文件时调用删除文件方法
				return deleteFile(filePath);
			} else {
				// 为目录时调用删除目录方法
				return deleteDirectory(filePath);
			}
		}
	} 
	/**
	 * 获取SDCard中某个目录下图片路径集合
	 * @param strPath
	 * @return
	 */
	public List<String> getPictures(final String strPath) {
		List<String> list = new ArrayList<String>();
		File file = new File(strPath);
		File[] allfiles = file.listFiles();
		if (allfiles == null) { 
			return null; 
		} 
		for(int k = 0; k < allfiles.length; k++) { 
			final File fi = allfiles[k];
			if(fi.isFile()) { 
				int idx = fi.getPath().lastIndexOf("."); 
				if (idx <= 0) { 
					continue; 
				} 
				String suffix = fi.getPath().substring(idx);
				if (suffix.toLowerCase().equals(".jpg") || 
						suffix.toLowerCase().equals(".jpeg") || 
						suffix.toLowerCase().equals(".bmp") || 
						suffix.toLowerCase().equals(".png") || 
						suffix.toLowerCase().equals(".gif") ) { 
					list.add(fi.getPath()); 
				} 
			} 
		} 
		return list; 
	}
	/**
	 * 获取sd卡下的图片并显示
	 */
	public void getPicturesFromSdcard(){
		List<String> list = getPictures(Environment.getExternalStorageDirectory() + "");
		if (list != null) { 
			//			Log.d(TAG, "list.size = " + list.size()); 
			for (int i = 0; i < list.size(); i++) { 
				Bitmap bm = BitmapFactory.decodeFile(list.get(i));
				int top = 30; 
				if (i > 0) { 
					top += BitmapFactory.decodeFile(list.get(i - 1)).getHeight() + 2;
				} 
				Canvas canvas = new Canvas();
				Paint paint = new Paint();
				canvas.drawBitmap(bm, 0, top, paint); 
			} 
		} 
		else { 
			//			Log.d(TAG, "list is null!!!"); 
		}
	}

	public static String getWaterPhotoPath(){
		File file=new File(path+"/WaterPhoto/");
		if(!file.exists()){
			file.mkdirs();
		}
		return path+"/WaterPhoto/";
	}


	////////////////////////////////////////////////////////////
	private static String ANDROID_SECURE = "/mnt/sdcard/.android_secure";

	private static final String LOG_TAG = "Util";

	public static boolean isSDCardReady() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	// if path1 contains path2
//	public static boolean containsPath(String path1, String path2) {
//		String path = path2;
//		while (path != null) {
//			if (path.equalsIgnoreCase(path1))
//				return true;
//
//			if (path.equals(GlobalConsts.ROOT_PATH))
//				break;
//			path = new File(path).getParent();
//		}
//
//		return false;
//	}

	public static String makePath(String path1, String path2) {
		if (path1.endsWith(File.separator))
			return path1 + path2;

		return path1 + File.separator + path2;
	}

	public static String getSdDirectory() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	public static boolean isNormalFile(String fullName) {
		return !fullName.equals(ANDROID_SECURE);
	}

//	public static FileInfo GetFileInfo(String filePath) {
//		File lFile = new File(filePath);
//		if (!lFile.exists())
//			return null;
//
//		FileInfo lFileInfo = new FileInfo();
//		lFileInfo.canRead = lFile.canRead();
//		lFileInfo.canWrite = lFile.canWrite();
//		lFileInfo.isHidden = lFile.isHidden();
//		lFileInfo.fileName = FileUtil.getNameFromFilepath(filePath);
//		lFileInfo.ModifiedDate = lFile.lastModified();
//		lFileInfo.IsDir = lFile.isDirectory();
//		lFileInfo.filePath = filePath;
//		lFileInfo.fileSize = lFile.length();
//		return lFileInfo;
//	}

//	public static FileInfo GetFileInfo(File f, FilenameFilter filter, boolean showHidden) {
//		FileInfo lFileInfo = new FileInfo();
//		String filePath = f.getPath();
//		File lFile = new File(filePath);
//		lFileInfo.canRead = lFile.canRead();
//		lFileInfo.canWrite = lFile.canWrite();
//		lFileInfo.isHidden = lFile.isHidden();
//		lFileInfo.fileName = f.getName();
//		lFileInfo.ModifiedDate = lFile.lastModified();
//		lFileInfo.IsDir = lFile.isDirectory();
//		lFileInfo.filePath = filePath;
//		if (lFileInfo.IsDir) {
//			int lCount = 0;
//			File[] files = lFile.listFiles(filter);
//
//			// null means we cannot access this dir
//			if (files == null) {
//				return null;
//			}
//
//			for (File child : files) {
//				if ((!child.isHidden() || showHidden)
//						&& FileUtil.isNormalFile(child.getAbsolutePath())) {
//					lCount++;
//				}
//			}
//			lFileInfo.Count = lCount;
//
//		} else {
//
//			lFileInfo.fileSize = lFile.length();
//
//		}
//		return lFileInfo;
//	}

	/*
	 * 采用了新的办法获取APK图标，之前的失败是因为android中存在的一个BUG,通过
	 * appInfo.publicSourceDir = apkPath;来修正这个问题，详情参见:
	 * http://code.google.com/p/android/issues/detail?id=9151
	 */
	public static Drawable getApkIcon(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = apkPath;
			appInfo.publicSourceDir = apkPath;
			try {
				return appInfo.loadIcon(pm);
			} catch (OutOfMemoryError e) {
				Log.e(LOG_TAG, e.toString());
			}
		}
		return null;
	}

	public static String getExtFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(dotPosition + 1, filename.length());
		}
		return "";
	}

	public static String getNameFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(0, dotPosition);
		}
		return "";
	}

	public static String getPathFromFilepath(String filepath) {
		int pos = filepath.lastIndexOf('/');
		if (pos != -1) {
			return filepath.substring(0, pos);
		}
		return "";
	}

	public static String getNameFromFilepath(String filepath) {
		int pos = filepath.lastIndexOf('/');
		if (pos != -1) {
			return filepath.substring(pos + 1);
		}
		return "";
	}

	public static boolean fileIsExists(String filePath){
		try{
			File f=new File(filePath);
			if(!f.exists()){
				return false;
			}

		}catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void delay(int ms)
	{
		try {
			Thread.currentThread();
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}

	public static boolean renameFile(String oldname, String newname )
	{
		if(fileIsExists(oldname))
		{
			File oldFile = new File(oldname);
			oldFile.renameTo(new File(newname));
			return true;
		}
		else
			return false;
	}

	private Bitmap getLocalBitmap(String path){
		try {
			FileInputStream fis = new FileInputStream(path);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** 
	 * 复制单个文件 
	 * @param oldPath String 原文件路径 如：c:/fqf.txt 
	 * @param newPath String 复制后路径 如：f:/fqf.txt 
	 * @return boolean 
	 */ 
	public static void copyFilePic(String oldPath, String newPath) {
		try { 
			int bytesum = 0; 
			int byteread = 0; 
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时 
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444]; 
				int length; 
				while ( (byteread = inStream.read(buffer)) != -1) { 
					bytesum += byteread; //字节数 文件大小 
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread); 
				} 
				inStream.close(); 
			} 
		} 
		catch (Exception e) {
//			System.out.println("复制单个文件操作出错"); 
			e.printStackTrace(); 

		} 

	}

	// return new file path if successful, or return null
	public static String copyFile(String src, String dest) {
		File file = new File(src);
		if (!file.exists() || file.isDirectory()) {
			Log.v(LOG_TAG, "copyFile: file not exist or is directory, " + src);
			return null;
		}
		FileInputStream fi = null;
		FileOutputStream fo = null;
		try {
			fi = new FileInputStream(file);
			File destPlace = new File(dest);
			if (!destPlace.exists()) {
				if (!destPlace.mkdirs())
					return null;
			}

			String destPath = FileUtil.makePath(dest, file.getName());
			if(fileIsExists(destPath))
				deleteFile(destPath);
			File destFile = new File(destPath);
			//            int i = 1;
			//            while (destFile.exists()) {
			//                String destName = FileUtil.getNameFromFilename(file.getName()) + " " + i++ + "."
			//                        + FileUtil.getExtFromFilename(file.getName());
			//                destPath = FileUtil.makePath(dest, destName);
			//                destFile = new File(destPath);
			//            }

			if (!destFile.createNewFile())
				return null;

			fo = new FileOutputStream(destFile);
			int count = 102400;
			byte[] buffer = new byte[count];
			int read = 0;
			while ((read = fi.read(buffer, 0, count)) != -1) {
				fo.write(buffer, 0, read);
			}

			return destPath;
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "copyFile: file not found, " + src);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(LOG_TAG, "copyFile: " + e.toString());
		} finally {
			try {
				if (fi != null)
					fi.close();
				if (fo != null)
					fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	//将保存到本地的图片插入系统图库，并更新图库
	public static void fileToMediaStore(String filePath, String fileName, Context mContext)
	{
		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
					filePath, fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath+fileName)));
	}

//	public static void getVideoFile(final List<VideoInfo> list, final File file) {// 获得视频文件
//
//		//		new Thread(new Runnable() {
//		//
//		//			@Override
//		//			public void run() {
//
//		file.listFiles(new FileFilter() {
//
//			@Override
//			public boolean accept(File file) {
//				// sdCard找到视频名称
//				String name = file.getName();
//
//				int i = name.indexOf('.');
//				if (i != -1) {
//					name = name.substring(i);
//					if (name.equalsIgnoreCase(".mp4")
//							//									|| name.equalsIgnoreCase(".3gp")
//							//							|| name.equalsIgnoreCase(".wmv")
//							//							|| name.equalsIgnoreCase(".ts")
//							//							|| name.equalsIgnoreCase(".rmvb")
//							//									|| name.equalsIgnoreCase(".mov")
//							//							|| name.equalsIgnoreCase(".m4v")
//							//							|| name.equalsIgnoreCase(".avi")
//							//							|| name.equalsIgnoreCase(".m3u8")
//							//							|| name.equalsIgnoreCase(".3gpp")
//							//							|| name.equalsIgnoreCase(".3gpp2")
//							//							|| name.equalsIgnoreCase(".mkv")
//							//							|| name.equalsIgnoreCase(".flv")
//							//							|| name.equalsIgnoreCase(".divx")
//							//							|| name.equalsIgnoreCase(".f4v")
//							//							|| name.equalsIgnoreCase(".rm")
//							//							|| name.equalsIgnoreCase(".asf")
//							//							|| name.equalsIgnoreCase(".ram")
//							//							|| name.equalsIgnoreCase(".mpg")
//							//							|| name.equalsIgnoreCase(".v8")
//							//							|| name.equalsIgnoreCase(".swf")
//							//							|| name.equalsIgnoreCase(".m2v")
//							//							|| name.equalsIgnoreCase(".asx")
//							//							|| name.equalsIgnoreCase(".ra")
//							//							|| name.equalsIgnoreCase(".ndivx")
//							//							|| name.equalsIgnoreCase(".xvid")
//							) {
//						VideoInfo vi = new VideoInfo();
//						//								vi.setDisplayName(file.getName());
//						vi.setFilePath(file.getAbsolutePath());
//						long modified = file.lastModified();
//						vi.setModifyTime(modified);
//						Bitmap mThumbBitmap=ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),Thumbnails.MINI_KIND);//用于获取视频的缩略图
//						vi.setB(mThumbBitmap);
//						if (list != null && list.size() < 5) {
//							list.add(vi);
//						}else{
//							return false;
//						}
//						return true;
//					}
//				} else if (file.isDirectory()) {
//					getVideoFile(list, file);
//				}
//				//						handler.sendEmptyMessage(MSG_QUERY_COMPLETE);
//				return false;
//			}
//		});
		//			}
		//		}).start();
//	}
	public static void getPhotoFile(final List<String> list, final File file) {// 获得照片文件

		//		new Thread(new Runnable() {
		//			
		//			@Override
		//			public void run() {

		file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				// sdCard找到视频名称  
				String name = file.getName();

				int i = name.indexOf('.');  
				if (i != -1) {  
					name = name.substring(i);  
					if (name.equalsIgnoreCase(".jpg")  
							|| name.equalsIgnoreCase(".png")  
							//							|| name.equalsIgnoreCase(".wmv")  
							//							|| name.equalsIgnoreCase(".ts")  
							//							|| name.equalsIgnoreCase(".rmvb")  
							//									|| name.equalsIgnoreCase(".mov")  
							//							|| name.equalsIgnoreCase(".m4v")  
							//							|| name.equalsIgnoreCase(".avi")  
							//							|| name.equalsIgnoreCase(".m3u8")  
							//							|| name.equalsIgnoreCase(".3gpp")  
							//							|| name.equalsIgnoreCase(".3gpp2")  
							//							|| name.equalsIgnoreCase(".mkv")  
							//							|| name.equalsIgnoreCase(".flv")  
							//							|| name.equalsIgnoreCase(".divx")  
							//							|| name.equalsIgnoreCase(".f4v")  
							//							|| name.equalsIgnoreCase(".rm")  
							//							|| name.equalsIgnoreCase(".asf")  
							//							|| name.equalsIgnoreCase(".ram")  
							//							|| name.equalsIgnoreCase(".mpg")  
							//							|| name.equalsIgnoreCase(".v8")  
							//							|| name.equalsIgnoreCase(".swf")  
							//							|| name.equalsIgnoreCase(".m2v")  
							//							|| name.equalsIgnoreCase(".asx")  
							//							|| name.equalsIgnoreCase(".ra")  
							//							|| name.equalsIgnoreCase(".ndivx")  
							//							|| name.equalsIgnoreCase(".xvid")
							) {  
						String absolutePath = file.getAbsolutePath();
						if (list != null && list.size() < 20) {
							list.add(absolutePath);  
						}else{
							return false;
						}
						return true;  
					}  
				} else if (file.isDirectory()) {  
					getPhotoFile(list, file);  
				}  
				//						handler.sendEmptyMessage(MSG_QUERY_COMPLETE);
				return false;  
			}  
		});  
		//			}
		//		}).start();
	}

	private static boolean checkFilenameEndwith(String filename, String[] filesEndwith){
		for (String fileEndwith : filesEndwith) {
			if (!TextUtils.isEmpty(filename) && !TextUtils.isEmpty(fileEndwith) && filename.endsWith(fileEndwith)) {
				return true;
			}
		}
		return false;
	}

	public static void getAllBlogPhotoFile(final List<String> list, final File file, final String endName) {// 获得照片文件

		file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				// sdCard找到视频名称  
				String name = file.getName();
				boolean is0jpg = name.endsWith(endName);
				if (is0jpg) {
					String absolutePath = file.getAbsolutePath();
					if (list != null) {
						list.add(absolutePath);  
					}else{
						return false;
					}
					return true;  
				} else if (file.isDirectory()) {  
					getAllBlogPhotoFile(list, file,endName);  
				}  
				return false;  
			}  
		});  
	}

	public static void getAllUserHeadPicFile(final List<String> list, final File file, final String endName) {// 获得照片文件

		file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				// sdCard找到视频名称  
				String name = file.getName();
				boolean is0jpg = name.startsWith(endName);
				if (is0jpg) {
					if (name.length() < 15) {
						String absolutePath = file.getAbsolutePath();
						if (list != null) {
							list.add(absolutePath);  
						}else{
							return false;
						}
						return true;  
					}
				} else if (file.isDirectory()) {  
					getAllBlogPhotoFile(list, file,endName);  
				}  
				return false;  
			}  
		});  
	}

	// does not include sd card folder
	private static String[] SysFileDirs = new String[] {
		"miren_browser/imagecaches"
	};

//	public static boolean shouldShowFile(String path) {
////		return shouldShowFile(new File(path));
////	}

//	public static boolean shouldShowFile(File file) {
//		boolean show = Settings.instance().getShowDotAndHiddenFiles();
//		if (show)
//			return true;
//
//		if (file.isHidden())
//			return false;
//
//		if (file.getName().startsWith("."))
//			return false;
//
//		String sdFolder = getSdDirectory();
//		for (String s : SysFileDirs) {
//			if (file.getPath().startsWith(makePath(sdFolder, s)))
//				return false;
//		}
//
//		return true;
//	}

	/* public static ArrayList<FavoriteItem> getDefaultFavorites(Context context) {
        ArrayList<FavoriteItem> list = new ArrayList<FavoriteItem>();
        list.add(new FavoriteItem(context.getString(R.string.favorite_photo), makePath(getSdDirectory(), "DCIM/Camera")));
        list.add(new FavoriteItem(context.getString(R.string.favorite_sdcard), getSdDirectory()));
        //list.add(new FavoriteItem(context.getString(R.string.favorite_root), getSdDirectory()));
        list.add(new FavoriteItem(context.getString(R.string.favorite_screen_cap), makePath(getSdDirectory(), "MIUI/screen_cap")));
        list.add(new FavoriteItem(context.getString(R.string.favorite_ringtone), makePath(getSdDirectory(), "MIUI/ringtone")));
        return list;
    }*/

	public static String getFilePathUri(Context context, Uri uri){
		if (null == uri) {
			return null;
		}
		String data = "";
		String scheme = uri.getScheme();
		if (null == scheme) {
			data = uri.getPath();
		}else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		}else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri, new String[] {MediaStore.Images.ImageColumns.DATA}, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
						return data;
					}
				}
			}
			cursor.close();
		}
		return data;
	}

	public static boolean setText(View view, int id, String text) {
		TextView textView = (TextView) view.findViewById(id);
		if (textView == null)
			return false;

		textView.setText(text);
		return true;
	}

	public static boolean setText(View view, int id, int text) {
		TextView textView = (TextView) view.findViewById(id);
		if (textView == null)
			return false;

		textView.setText(text);
		return true;
	}

	// comma separated number
	public static String convertNumber(long number) {
		return String.format("%,d", number);
	}

	// storage, G M K B
	public static String convertStorage(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}

	public static class SDCardInfo {
		public long total;

		public long free;
	}

	public static SDCardInfo getSDCardInfo() {
		String sDcString = Environment.getExternalStorageState();

		if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
			File pathFile = Environment.getExternalStorageDirectory();

			try {
				android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

				// 获取SDCard上BLOCK总数
				long nTotalBlocks = statfs.getBlockCount();

				// 获取SDCard上每个block的SIZE
				long nBlocSize = statfs.getBlockSize();

				// 获取可供程序使用的Block的数量
				long nAvailaBlock = statfs.getAvailableBlocks();

				// 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
				long nFreeBlock = statfs.getFreeBlocks();

				SDCardInfo info = new SDCardInfo();
				// 计算SDCard 总容量大小MB
				info.total = nTotalBlocks * nBlocSize;

				// 计算 SDCard 剩余大小MB
				info.free = nAvailaBlock * nBlocSize;

				return info;
			} catch (IllegalArgumentException e) {
				Log.e(LOG_TAG, e.toString());
			}
		}

		return null;
	}

	/* public static void showNotification(Context context, Intent intent, String title, String body, int drawableId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(drawableId, body, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND;
        if (intent == null) {
            // FIXEME: category tab is disabled
            intent = new Intent(context, FileViewActivity.class);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        notification.setLatestEventInfo(context, title, body, contentIntent);
        manager.notify(drawableId, notification);
    }*/

	public static String formatDateString(Context context, long time) {
		DateFormat dateFormat = android.text.format.DateFormat
				.getDateFormat(context);
		DateFormat timeFormat = android.text.format.DateFormat
				.getTimeFormat(context);
		Date date = new Date(time);
		return dateFormat.format(date) + " " + timeFormat.format(date);
	}

	/*public static void updateActionModeTitle(ActionMode mode, Context context, int selectedNum) {
        if (mode != null) {
            mode.setTitle(context.getString(R.string.multi_select_title,selectedNum));
            if(selectedNum == 0){
                mode.finish();
            }
        }
    }
	 */
	public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
		{
			add("text/plain");
			add("text/plain");
			add("application/pdf");
			add("application/msword");
			add("application/vnd.ms-excel");
			add("application/vnd.ms-excel");
		}
	};

	public static boolean deleteFile(String path)
	{
		File file = new File(path);
		file.delete();
		return true;
	}

	public static String sZipFileMimeType = "application/zip";

	public static int CATEGORY_TAB_INDEX = 0;
	public static int SDCARD_TAB_INDEX = 1;

	/**
	 * 将接收的字符串转换成图片保存
	 * @param imgStr 二进制流转换的字符串
	 * @param imgPath 图片的保存路径
	 * @param imgName 图片的名称
	 * @return 
	 *      1：保存正常
	 *      0：保存失败
	 */
	public static int saveToImgByStr(String imgStr, String imgPath, String imgName){
		try {
			System.out.println("===imgStr.length()====>" + imgStr.length()
					+ "=====imgStr=====>" + imgStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int stateInt = 1;
		if(imgStr != null && imgStr.length() > 0){
			try {

				// 将字符串转换成二进制，用于显示图片  
				// 将上面生成的图片格式字符串 imgStr，还原成图片显示  
				byte[] imgByte = hex2byte( imgStr );  

				InputStream in = new ByteArrayInputStream(imgByte);

				File file=new File(imgPath,imgName);//可以是任何图片格式.jpg,.png等
				FileOutputStream fos=new FileOutputStream(file);

				byte[] b = new byte[1024];
				int nRead = 0;
				while ((nRead = in.read(b)) != -1) {
					fos.write(b, 0, nRead);
				}
				fos.flush();
				fos.close();
				in.close();

			} catch (Exception e) {
				stateInt = 0;
				e.printStackTrace();
			} finally {
			}
		}
		return stateInt;
	}

	/**
	 * 将二进制转换成图片保存
//	 * @param imgStr 二进制流转换的字符串
	 * @param imgPath 图片的保存路径
	 * @param imgName 图片的名称
	 * @return 
	 *      1：保存正常
	 *      0：保存失败
	 */
	public static int saveToImgByBytes(File imgFile, String imgPath, String imgName){

		int stateInt = 1;
		if(imgFile.length() > 0){
			try {
				File file=new File(imgPath,imgName);//可以是任何图片格式.jpg,.png等
				FileOutputStream fos=new FileOutputStream(file);

				FileInputStream fis = new FileInputStream(imgFile);

				byte[] b = new byte[1024];
				int nRead = 0;
				while ((nRead = fis.read(b)) != -1) {
					fos.write(b, 0, nRead);
				}
				fos.flush();
				fos.close();
				fis.close();

			} catch (Exception e) {
				stateInt = 0;
				e.printStackTrace();
			} finally {
			}
		}
		return stateInt;
	}

	/**
	 * 二进制转字符串
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) // 二进制转字符串
	{
		StringBuffer sb = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1) {
				sb.append("0" + stmp);
			} else {
				sb.append(stmp);
			}
		}
		return sb.toString();
	}

	/**
	 * 字符串转二进制
	 * @param str 要转换的字符串
	 * @return  转换后的二进制数组
	 */
	public static byte[] hex2byte(String str) { // 字符串转二进制
		if (str == null)
			return null;
		str = str.trim();
		int len = str.length();
		if (len == 0 || len % 2 == 1)
			return null;
		byte[] b = new byte[len / 2];
		try {
			for (int i = 0; i < str.length(); i += 2) {
				b[i / 2] = (byte) Integer
						.decode("0X" + str.substring(i, i + 2)).intValue();
			}
			return b;
		} catch (Exception e) {
			return null;
		}
	}
	
	/** 
     * 字节数组转换为十六进制字符串 
     *  
     * @param b 
     *            byte[] 需要转换的字节数组 
     * @return String 十六进制字符串 
     */  
    public static final String byte16hex(byte b[]) {
        if (b == null) {  
            throw new IllegalArgumentException(
                    "Argument b ( byte array ) is null! ");  
        }  
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {  
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {  
                hs = hs + "0" + stmp;  
            } else {  
                hs = hs + stmp;  
            }  
        }  
        return hs.toUpperCase();  
    }  
    
    /**    
     * 字符串转换成十六进制字符串   
//     * @param String str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]   
     */        
    public static String str2HexStr(String str)
    {        
        
        char[] chars = "0123456789ABCDEF".toCharArray();        
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();        
        int bit;        
              
        for (int i = 0; i < bs.length; i++)      
        {        
            bit = (bs[i] & 0x0f0) >> 4;        
            sb.append(chars[bit]);        
            bit = bs[i] & 0x0f;        
            sb.append(chars[bit]);      
            sb.append(' ');      
        }        
        return sb.toString().trim();        
    }      
          
    /**    
     * 十六进制转换字符串   
//     * @param String str Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串   
     */        
    public static String hexStr2Str(String hexStr)
    {        
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();        
        byte[] bytes = new byte[hexStr.length() / 2];        
        int n;        
        
        for (int i = 0; i < bytes.length; i++)      
        {        
            n = str.indexOf(hexs[2 * i]) * 16;        
            n += str.indexOf(hexs[2 * i + 1]);        
            bytes[i] = (byte) (n & 0xff);        
        }        
        return new String(bytes);
    }      
          
    /**   
     * bytes转换成十六进制字符串   
//     * @param byte[] b byte数组
     * @return String 每个Byte值之间空格分隔   
     */      
    public static String byte2HexStr(byte[] b)
    {      
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)      
        {      
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);      
        }      
        return sb.toString().toUpperCase().trim();      
    }
	
    /**   
     * bytes字符串转换为Byte值   
//     * @param String src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]   
     */      
    public static byte[] hexStr2Bytes(String src)
    {      
        int m=0,n=0;      
        int l=src.length()/2;      
        System.out.println(l);
        byte[] ret = new byte[l];      
        for (int i = 0; i < l; i++)      
        {      
            m=i*2+1;      
            n=m+1;      
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));
        }      
        return ret;      
    }      
        
    /**   
     * String的字符串转换成unicode的String   
//     * @param String strText 全角字符串
     * @return String 每个unicode之间无分隔符   
     * @throws Exception   
     */      
    public static String strToUnicode(String strText)
        throws Exception
    {      
        char c;      
        StringBuilder str = new StringBuilder();
        int intAsc;      
        String strHex;
        for (int i = 0; i < strText.length(); i++)      
        {      
            c = strText.charAt(i);      
            intAsc = (int) c;      
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)      
                str.append("\\u" + strHex);      
            else // 低位在前面补00      
                str.append("\\u00" + strHex);      
        }      
        return str.toString();      
    }      
          
    /**   
     * unicode的String转换成String的字符串   
//     * @param String hex 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串   
     */      
    public static String unicodeToString(String hex)
    {      
        int t = hex.length() / 6;      
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++)      
        {      
            String s = hex.substring(i * 6, (i + 1) * 6);
            // 高位需要补上00再转      
            String s1 = s.substring(2, 4) + "00";
            // 低位直接转      
            String s2 = s.substring(4);
            // 将16进制的string转为int      
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // 将int转换为字符      
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }      
        return str.toString();      
    } 
    
    /** 
     * 数字字符串转ASCII码字符串 
     *  
//     * @param String
     *            字符串 
     * @return ASCII字符串 
     */  
    public static String StringToAsciiString(String content) {
        String result = "";
        int max = content.length();  
        for (int i = 0; i < max; i++) {  
            char c = content.charAt(i);  
            String b = Integer.toHexString(c);
            result = result + b;  
        }  
        return result;  
    }  
    /** 
     * 十六进制转字符串 
     *  
     * @param hexString 
     *            十六进制字符串 
     * @param encodeType 
     *            编码类型4：Unicode，2：普通编码 
     * @return 字符串 
     */  
    public static String hexStringToString(String hexString, int encodeType) {
        String result = "";
        int max = hexString.length() / encodeType;  
        for (int i = 0; i < max; i++) {  
            char c = (char) hexStringToAlgorism(hexString  
                    .substring(i * encodeType, (i + 1) * encodeType));  
            result += c;  
        }  
        return result;  
    }  
    /** 
     * 十六进制字符串装十进制 
     *  
     * @param hex 
     *            十六进制字符串 
     * @return 十进制数值 
     */  
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();  
        int max = hex.length();  
        int result = 0;  
        for (int i = max; i > 0; i--) {  
            char c = hex.charAt(i - 1);  
            int algorism = 0;  
            if (c >= '0' && c <= '9') {  
                algorism = c - '0';  
            } else {  
                algorism = c - 55;  
            }  
            result += Math.pow(16, max - i) * algorism;
        }  
        return result;  
    }  
    /** 
     * 十六转二进制 
     *  
     * @param hex 
     *            十六进制字符串 
     * @return 二进制字符串 
     */  
    public static String hexStringToBinary(String hex) {
        hex = hex.toUpperCase();  
        String result = "";
        int max = hex.length();  
        for (int i = 0; i < max; i++) {  
            char c = hex.charAt(i);  
            switch (c) {  
            case '0':  
                result += "0000";  
                break;  
            case '1':  
                result += "0001";  
                break;  
            case '2':  
                result += "0010";  
                break;  
            case '3':  
                result += "0011";  
                break;  
            case '4':  
                result += "0100";  
                break;  
            case '5':  
                result += "0101";  
                break;  
            case '6':  
                result += "0110";  
                break;  
            case '7':  
                result += "0111";  
                break;  
            case '8':  
                result += "1000";  
                break;  
            case '9':  
                result += "1001";  
                break;  
            case 'A':  
                result += "1010";  
                break;  
            case 'B':  
                result += "1011";  
                break;  
            case 'C':  
                result += "1100";  
                break;  
            case 'D':  
                result += "1101";  
                break;  
            case 'E':  
                result += "1110";  
                break;  
            case 'F':  
                result += "1111";  
                break;  
            }  
        }  
        return result;  
    }  
    /** 
     * ASCII码字符串转数字字符串 
     *  
//     * @param String
     *            ASCII字符串 
     * @return 字符串 
     */  
    public static String AsciiStringToString(String content) {
        String result = "";
        int length = content.length() / 2;  
        for (int i = 0; i < length; i++) {  
            String c = content.substring(i * 2, i * 2 + 2);
            int a = hexStringToAlgorism(c);  
            char b = (char) a;  
            String d = String.valueOf(b);
            result += d;  
        }  
        return result;  
    }  
    /** 
     * 将十进制转换为指定长度的十六进制字符串 
     *  
     * @param algorism 
     *            int 十进制数字 
     * @param maxLength 
     *            int 转换后的十六进制字符串长度 
     * @return String 转换后的十六进制字符串 
     */  
    public static String algorismToHEXString(int algorism, int maxLength) {
        String result = "";
        result = Integer.toHexString(algorism);
  
        if (result.length() % 2 == 1) {  
            result = "0" + result;  
        }  
        return patchHexString(result.toUpperCase(), maxLength);  
    }  
    /** 
     * 字节数组转为普通字符串（ASCII对应的字符） 
     *  
     * @param bytearray 
     *            byte[] 
     * @return String 
     */  
    public static String bytetoString(byte[] bytearray) {
        String result = "";
        char temp;  
  
        int length = bytearray.length;  
        for (int i = 0; i < length; i++) {  
            temp = (char) bytearray[i];  
            result += temp;  
        }  
        return result;  
    }  
    /** 
     * 二进制字符串转十进制 
     *  
     * @param binary 
     *            二进制字符串 
     * @return 十进制数值 
     */  
    public static int binaryToAlgorism(String binary) {
        int max = binary.length();  
        int result = 0;  
        for (int i = max; i > 0; i--) {  
            char c = binary.charAt(i - 1);  
            int algorism = c - '0';  
            result += Math.pow(2, max - i) * algorism;
        }  
        return result;  
    }  
  
    /** 
     * 十进制转换为十六进制字符串 
     *  
     * @param algorism 
     *            int 十进制的数字 
     * @return String 对应的十六进制字符串 
     */  
    public static String algorismToHEXString(int algorism) {
        String result = "";
        result = Integer.toHexString(algorism);
  
        if (result.length() % 2 == 1) {  
            result = "0" + result;  
  
        }  
        result = result.toUpperCase();  
  
        return result;  
    }  
    /** 
     * HEX字符串前补0，主要用于长度位数不足。 
     *  
     * @param str 
     *            String 需要补充长度的十六进制字符串 
     * @param maxLength 
     *            int 补充后十六进制字符串的长度 
     * @return 补充结果 
     */  
    static public String patchHexString(String str, int maxLength) {
        String temp = "";
        for (int i = 0; i < maxLength - str.length(); i++) {  
            temp = "0" + temp;  
        }  
        str = (temp + str).substring(0, maxLength);  
        return str;  
    }  
    /** 
     * 将一个字符串转换为int 
     *  
     * @param s 
     *            String 要转换的字符串 
     * @param defaultInt 
     *            int 如果出现异常,默认返回的数字 
     * @param radix 
     *            int 要转换的字符串是什么进制的,如16 8 10. 
     * @return int 转换后的数字 
     */  
    public static int parseToInt(String s, int defaultInt, int radix) {
        int i = 0;  
        try {  
            i = Integer.parseInt(s, radix);
        } catch (NumberFormatException ex) {
            i = defaultInt;  
        }  
        return i;  
    }  
    /** 
     * 将一个十进制形式的数字字符串转换为int 
     *  
     * @param s 
     *            String 要转换的字符串 
     * @param defaultInt 
     *            int 如果出现异常,默认返回的数字 
     * @return int 转换后的数字 
     */  
    public static int parseToInt(String s, int defaultInt) {
        int i = 0;  
        try {  
            i = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            i = defaultInt;  
        }  
        return i;  
    }  
    /** 
     * 十六进制字符串转为Byte数组,每两个十六进制字符转为一个Byte 
     *  
     * @param hex 
     *            十六进制字符串 
     * @return byte 转换结果 
     */  
    public static byte[] hexStringToByte(String hex) {
        int max = hex.length() / 2;  
        byte[] bytes = new byte[max];  
        String binarys = hexStringToBinary(hex);
        for (int i = 0; i < max; i++) {  
            bytes[i] = (byte) binaryToAlgorism(binarys.substring(  
                    i * 8 + 1, (i + 1) * 8));  
            if (binarys.charAt(8 * i) == '1') {  
                bytes[i] = (byte) (0 - bytes[i]);  
            }  
        }  
        return bytes;  
    }  
    
    /**
	 * 将接收的字符串转换成图片保存
	 * @param imgStr 二进制流转换的字符串
	 * @param imgPath 图片的保存路径
//	 * @param imgName 图片的名称
	 * @return 
	 *      1：保存正常
	 *      0：保存失败
	 */
	public static int saveToImgByStr16(FileOutputStream fos, String imgStr, String imgPath){
		int stateInt = 1;
		if(!TextUtils.isEmpty(imgStr) && imgStr.length() > 0){
			try {

				// 将字符串转换成二进制，用于显示图片  
				// 将上面生成的图片格式字符串 imgStr，还原成图片显示  
//				byte[] imgByte = hexStringToByte( imgStr );  
				byte[] imgByte = hex2byte( imgStr );  

//				InputStream in = new ByteArrayInputStream(imgByte);

//				File file=new File(imgPath);//可以是任何图片格式.jpg,.png等
				
//				byte[] b = new byte[1024];
//				int nRead = imgByte.length;
//				while ((nRead = in.read(b)) != -1) {
					fos.write(imgByte);
//				}
				fos.flush();
			//	fos.close();
//				in.close();

			} catch (Exception e) {
				Log.d("ffff", "----FileUtil------saveToImgByStr16---"+e.toString());
				stateInt = 0;
				e.printStackTrace();
			} finally {
			}
		}
		return stateInt;
	}
	//获取当前ip地址
	public static String getIpAddressUtil(Context mContext){
		String ip = "";
		WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		if (manager.isWifiEnabled()) {
			WifiInfo wifiInfo = manager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			ip = (ipAddress & 0xFF) + "." +((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "." + ((ipAddress >> 24) & 0xFF);
		}
		return ip;
	}
	
	public static String getMobileIp() throws SocketException {
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface interface1 = en.nextElement();
			for (Enumeration<InetAddress> enumeration = interface1.getInetAddresses(); enumeration.hasMoreElements();) {
				InetAddress inetAddress = enumeration.nextElement();
				if (!inetAddress.isLoopbackAddress()) {
					return inetAddress.getHostAddress().toString();
				}
			}
		}
		return null;
	}
	
	// 功能：字符串半角转换为全角
	// 说明：半角空格为32,全角空格为12288.
//	 		 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
	// 输入参数：input -- 需要转换的字符串
	// 输出参数：无：
	// 返回值: 转换后的字符串
	public static String halfToFull(String input)
	{
	   boolean bIsAD = false;
		char[] c = input.toCharArray();
		for (int i = 0; i< c.length; i++)
		{
//			if (c[i] == 32) //半角空格
//			{
//				c[i] = (char) 12288;
//				continue;
//			}
	 
			//根据实际情况，过滤不需要转换的符号
			//if (c[i] == 46) //半角点号，不转换
			// continue;
			
			int v = c[i];
			if(( v> 46 && v < 59) || (v>=65&&v<=90)||(v>=97&&v<=122))
				bIsAD = true;
			else
				bIsAD = false;
			
			if (c[i]> 32 && c[i]< 127 && !bIsAD)	//其他符号都转换为全角
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	public static void update(Activity context,String path) {
		if (Build.VERSION.SDK_INT >= 26) {
			boolean b = context.getPackageManager().canRequestPackageInstalls();
			if (b) {
				installApk(context,path);//安装应用的逻辑(写自己的就可以)
			} else {
				//请求安装未知应用来源的权限
				Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
				context.startActivityForResult(intent, 10086);
			}
		} else {
			installApk(context,path);
		}
	}

	public static void openFile(Context mContext,File file) {
		try {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//设置intent的Action属性
			intent.setAction(Intent.ACTION_VIEW);
			//获取文件file的MIME类型
			String type = getMIMEType(file);
			//设置intent的data和Type属性。android 7.0以上crash,改用provider
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				Uri fileUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileProvider", file);//android 7.0以上
				intent.setDataAndType(fileUri, type);
				grantUriPermission(mContext, fileUri, intent);
			} else {
				intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
			}
			//跳转
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void grantUriPermission(Context context, Uri fileUri, Intent intent) {
		List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : resInfoList) {
			String packageName = resolveInfo.activityInfo.packageName;
			context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 *
	 * @param file
	 */
	private static String getMIMEType(File file) {

		String type = "*/*";
		String fName = file.getName();
		//获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "") return type;
		//在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	private static final String[][] MIME_MapTable = {
			//{后缀名， MIME类型}
			{".3gp", "video/3gpp"},
			{".apk", "application/vnd.android.package-archive"},
			{".asf", "video/x-ms-asf"},
			{".avi", "video/x-msvideo"},
			{".bin", "application/octet-stream"},
			{".bmp", "image/bmp"},
			{".c", "text/plain"},
			{".class", "application/octet-stream"},
			{".conf", "text/plain"},
			{".cpp", "text/plain"},
			{".doc", "application/msword"},
			{".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
			{".xls", "application/vnd.ms-excel"},
			{".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
			{".exe", "application/octet-stream"},
			{".gif", "image/gif"},
			{".gtar", "application/x-gtar"},
			{".gz", "application/x-gzip"},
			{".h", "text/plain"},
			{".htm", "text/html"},
			{".html", "text/html"},
			{".jar", "application/java-archive"},
			{".java", "text/plain"},
			{".jpeg", "image/jpeg"},
			{".jpg", "image/jpeg"},
			{".js", "application/x-javascript"},
			{".log", "text/plain"},
			{".m3u", "audio/x-mpegurl"},
			{".m4a", "audio/mp4a-latm"},
			{".m4b", "audio/mp4a-latm"},
			{".m4p", "audio/mp4a-latm"},
			{".m4u", "video/vnd.mpegurl"},
			{".m4v", "video/x-m4v"},
			{".mov", "video/quicktime"},
			{".mp2", "audio/x-mpeg"},
			{".mp3", "audio/x-mpeg"},
			{".mp4", "video/mp4"},
			{".mpc", "application/vnd.mpohun.certificate"},
			{".mpe", "video/mpeg"},
			{".mpeg", "video/mpeg"},
			{".mpg", "video/mpeg"},
			{".mpg4", "video/mp4"},
			{".mpga", "audio/mpeg"},
			{".msg", "application/vnd.ms-outlook"},
			{".ogg", "audio/ogg"},
			{".pdf", "application/pdf"},
			{".png", "image/png"},
			{".pps", "application/vnd.ms-powerpoint"},
			{".ppt", "application/vnd.ms-powerpoint"},
			{".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
			{".prop", "text/plain"},
			{".rc", "text/plain"},
			{".rmvb", "audio/x-pn-realaudio"},
			{".rtf", "application/rtf"},
			{".sh", "text/plain"},
			{".tar", "application/x-tar"},
			{".tgz", "application/x-compressed"},
			{".txt", "text/plain"},
			{".wav", "audio/x-wav"},
			{".wma", "audio/x-ms-wma"},
			{".wmv", "audio/x-ms-wmv"},
			{".wps", "application/vnd.ms-works"},
			{".xml", "text/plain"},
			{".z", "application/x-compress"},
			{".zip", "application/x-zip-compressed"},
			{"", "*/*"}
	};

}
