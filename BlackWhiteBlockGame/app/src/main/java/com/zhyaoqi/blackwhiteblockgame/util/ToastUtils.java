package com.zhyaoqi.blackwhiteblockgame.util;


import android.content.Context;
import android.widget.Toast;

/**
 * Toast统一管理类
 * 
 */
public class ToastUtils
{
	private static Toast toast;
	private ToastUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static void showShort(Context context, CharSequence message)
	{
		if (toast == null){
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		}else {
			toast.setText(message);
		}
		toast.show();
	}
	public static void showShort(Context context, int message){
		if (toast == null){
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		}else {
			toast.setText(message);
		}
		toast.show();
	}

	public static void showLong(Context context, CharSequence message)
	{
		if (toast == null){
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		}else {
			toast.setText(message);
		}
		toast.show();
	}
	public static void showLong(Context context, int message){
		if (toast == null){
			toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		}else {
			toast.setText(message);
		}
		toast.show();
	}

	public static void show(Context context, CharSequence message, int duration)
	{
		if (toast == null){
			toast = Toast.makeText(context, message, duration);
		}else {
			toast.setText(message);
		}
		toast.show();
	}
	public static void show(Context context, int message ,int duration){
		if (toast == null){
			toast = Toast.makeText(context, message, duration);
		}else {
			toast.setText(message);
		}
		toast.show();
	}

}