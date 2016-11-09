package com.android.util.http.response;

import android.text.TextUtils;

import org.json.JSONArray;

import okhttp3.Response;

/**
 * JsonArray解析
 * 
 * @author 张全
 */
public abstract class JsonArrayParser implements DataParser {

	@Override
	public Object parseReponse(Response response) throws Exception {
		String data = response.body().string();
		if (TextUtils.isEmpty(data)) {
			throw new NullPointerException("response data===null");
		}
		return parseData(new JSONArray(data));
	}

	public abstract Object parseData(JSONArray dataArray) throws Exception;

}
