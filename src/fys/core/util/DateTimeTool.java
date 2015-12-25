package fys.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeTool {
	public static Date strToDate(String dateStr) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	    Date date = sdf.parse(dateStr);  
	    return date;
	}
	
	public static Date strToDateTime(String dateStr) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    Date date = sdf.parse(dateStr);  
	    return date;
	    
	    //http://blog.sina.com.cn/s/blog_6a0cd5e5010109sw.html
//	    Timestamp timeStamp = new Timestamp(date.getTime()); //可直接得到"yyyy-MM-dd HH:mm:ss"的字符串
	    	
	}
	
	public static String dateToStr(Date date){
		return (new SimpleDateFormat("yyyy-MM-dd")).format(date);
	}
	
	public static String dateTimeToStr(Date date){
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
	}
	
	public static String getPreviousDateStr(String dateStr) throws Exception{
		Date today = strToDate(dateStr);
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}
	
}
