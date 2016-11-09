package com.android.util.http.response;

import okhttp3.Response;

/**
 * 数据解析
 * 
 * @author 张全
 */
public interface DataParser {
	Object parseReponse(Response response) throws Exception;
}
