package com.l000phone.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;

public class ImageUtils {

	/**
	 * 对sd上的图片进行二次采样
	 * @param path
	 * @param maxWidth
	 * @param maxHeight
     * @return
     */
	public static Bitmap getBitmap(String path,int maxWidth,int maxHeight){
		if(! new File(path).exists()){
			Log.e("info", "path->"+path+",指定的图片不存在");
			return null;
		}
		
		//准备一次采样
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true; //只采样图片边缘数据,获得图片概要的信息
		options.inSampleSize=1; //设置采样比例为原始大小
		
		//一次采样,获取原始的图片大小
		BitmapFactory.decodeFile(path, options);
		
		//一次采样之后，根据图片的大小和实际显示图片控件的大小来计算图片压缩比例
		int hScale=options.outHeight/maxHeight; //计算图片高度的压缩比例
		int wScale=options.outWidth/maxWidth; //计算图片宽度的压缩比例
		
		options.inSampleSize=Math.max(hScale,wScale); //获取最小的压缩比(失真)
		
		options.inJustDecodeBounds=false;//二次采样图片的全部数据
		//二次采样，返回压缩之后的图片
		Bitmap bitmap=BitmapFactory.decodeFile(path, options);
		
		return bitmap;
	}
	
}
