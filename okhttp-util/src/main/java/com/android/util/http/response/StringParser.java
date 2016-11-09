package com.android.util.http.response;

import okhttp3.Response;

/**
 * 字符串解析
 * 
 * @author 张全
 */
public class StringParser implements DataParser {

	@Override
	public Object parseReponse(Response response) throws Exception {
		return response.body().string();
	}

}
