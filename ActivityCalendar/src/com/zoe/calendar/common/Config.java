package com.zoe.calendar.common;

import android.content.Context;

import com.rarnu.utils.ConfigUtils;

public class Config {

	private static final String KEY_CITY = "key_city";
	private static final String KEY_CITY_PINYIN = "key_city_pinyin";
	private static final String KEY_LAST_TIMESTAMP = "key_last_timestamp";
	private static final String KEY_SETTING_TYPE = "key_setting_type_%d";

	public static String getCity(Context context) {
		return ConfigUtils.getStringConfig(context, KEY_CITY, "");
	}

	public static void setCity(Context context, String city) {
		ConfigUtils.setStringConfig(context, KEY_CITY, city);
	}

	public static long getLastTimestamp(Context context) {
		return ConfigUtils.getLongConfig(context, KEY_LAST_TIMESTAMP, 0L);
	}

	public static void setLastTimestamp(Context context, long timestamp) {
		ConfigUtils.setLongConfig(context, KEY_LAST_TIMESTAMP, timestamp);
	}

	public static String getCityPinyin(Context context) {
		return ConfigUtils.getStringConfig(context, KEY_CITY_PINYIN, "");
	}

	public static void setCityPinyin(Context context, String pinyin) {
		ConfigUtils.setStringConfig(context, KEY_CITY_PINYIN, pinyin);
	}

	public static boolean getSettingType(Context context, int index) {
		return ConfigUtils.getBooleanConfig(context,
				String.format(KEY_SETTING_TYPE, index), true);
	}

	public static void setSettingType(Context context, int index, boolean value) {
		ConfigUtils.setBooleanConfig(context,
				String.format(KEY_SETTING_TYPE, index), value);
	}
}
