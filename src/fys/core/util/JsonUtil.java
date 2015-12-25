package fys.core.util;

import com.alibaba.fastjson.JSON;

public class JsonUtil {
	public static String toJson(Object data){
		return JSON.toJSONString(data);
	}
	
	public static  <T> T toObject(String jsonData, Class<T> objectClass){
		return JSON.parseObject(jsonData, objectClass);
	}
}
