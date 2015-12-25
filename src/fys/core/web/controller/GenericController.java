package fys.core.web.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fys.core.annotation.Action;
import fys.core.annotation.AjaxGetAction;
import fys.core.annotation.AjaxPostAction;
import fys.core.annotation.GetAction;
import fys.core.annotation.PostAction;
import fys.core.config.AppConfig;
import fys.core.dao.DbUtil;
import fys.core.exception.PermissionException;
import fys.core.model.IUser;
import fys.core.web.AppKeys;
import fys.core.web.IController;

//import simpleWebFrame.database.DBConnectionPool;


public class GenericController implements IController {	
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected Connection conn;
	protected Boolean needLogin = false;
	protected Boolean needCheckRole = false;
	protected String loginUrl = "/login/login.do";
	protected String moduleKey = ""; //module prefix
	protected HashMap<String, String> parametersMap = null;
	public static HashMap<String, Method> methodAnnotationMap = new HashMap<String, Method>();
	public static HashMap<String, String> permissionMap = new HashMap<String, String>();
	
	
	protected HashMap<String, Object> appDataMap = new HashMap<String, Object>();
	
	protected GenericController(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
		request.setAttribute(AppKeys.AppDataMap, appDataMap);
		initMethodAnnotationMap();
	}
	
	public Connection getConnection() throws SQLException {
		if(this.conn != null){
			return this.conn;
		}
		
		this.conn = DbUtil.getInstance().getConnection();
		this.conn.setAutoCommit(false);
		return this.conn;
	}
	
	protected boolean isAjax(){
		String header = request.getHeader("X-Requested-With");
		return "XMLHttpRequest".equals(header) ? true : false;
	}
//	@Override
//	public String getCurrentUrlPrefix(){
//		return this.currentUrlPrefix;
//	}
	@Override
	public void setModuleKey(String urlPrefix){
		this.moduleKey = "/" + urlPrefix;
	}
	@Override
	public void setRequestResponse(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
	}

	@Override
	public Object dispatchRequest( )
	throws Exception {
		String httpMethod = request.getMethod();
		String servletPath = request.getServletPath();
		Method method = this.getActionMethod(httpMethod, servletPath, false);
		if(method != null){
			
			checkMethodRole(httpMethod, servletPath, false);
			if(method.getReturnType()== Void.class){
				method.invoke(this, new Object[0]);
				return null;
			}
			else{
				Object data = method.invoke(this, new Object[0]);
				return data == null ? AppConfig.returnValueIfNull : data;
			}
			
		}
		throw new Exception("request page not found.");
	}
	@Override
	public Object dispatchAjaxRequest( )
	throws Exception {
		String httpMethod = request.getMethod();
		String servletPath = request.getServletPath();
		Method method = this.getActionMethod(httpMethod, servletPath, true);
		if(method != null){

			checkMethodRole(httpMethod, servletPath, true);
			
			if(method.getReturnType()== Void.class){
				method.invoke(this, new Object[0]);
				return null;
			}
			else{
				Object data = method.invoke(this, new Object[0]);
				return data == null ? AppConfig.returnValueIfNull : data;
			}
		}
		throw new Exception("request page not found.");
	}
	@Override
	public void release(){
		if(this.conn != null){
			try{
				this.conn.close();
			}
			catch(SQLException sqlEx){
				
			}
		}
	}
	
	@Override
	public void commit() throws SQLException {
		if(this.conn != null){
			this.conn.commit();
			this.conn.setAutoCommit(true);
		}
		release();
		this.conn = null;
	}
	@Override
	public void rollback() throws SQLException {
		if(this.conn != null){
			this.conn.rollback();
		}
		release();
		this.conn = null;
	}
	
	@Override
	public void clear(){
		this.request = null;
		this.response = null;
	}
	
	
	public boolean needLogin(){
		return this.needLogin;
	}
	
	public String getLoginUrl(){
		return this.loginUrl;
	}
	
	public HashMap<String, Object> getAppDataMap(){
		return this.appDataMap;
	}
	
	public void setAppData(String key, Object value){
		this.appDataMap.put(key, value);
	}
	public Object getAppData(String key){
		return this.appDataMap.get(key);
	}
	
	public String getAppDataString(String key) throws Exception{
		if(this.appDataMap.get(key) == null){
			return "";
		}
		return this.appDataMap.get(key).toString(); //?
	}
	
	protected void initMethodAnnotationMap(){
		methodAnnotationMap = methodAnnotationMap==null? new HashMap<String, Method>() : methodAnnotationMap;
		if(!methodAnnotationMap.containsKey(this.getClass().getName())){
			Method[] methods = this.getClass().getMethods();			
			if(methods != null){
				for(Method method : methods){
					Annotation[] annotations = method.getDeclaredAnnotations();
					for(Annotation annotation : annotations){
						String[] roles = null;
						String key = "";
						if(annotation instanceof Action){
					    	Action actions = (Action) annotation;
//					    	String ajax = action.ajax() == false ? "" : "ajax:";
//					    	key = this.getClass().getName() + ":" + ajax + action.method() + ":" + action.url().toLowerCase();
//					    	methodAnnotationMap.put(key, method);
//					    	roles = action.roles();
//					    	setPermissionMap(key, roles);
					    	String ajax = actions.ajax() == false ? "" : "ajax:";
					    	String keyPrefix = this.getClass().getName() + ":" + ajax + actions.method() + ":";
					    	roles = actions.roles();
					    	for(String url : actions.urls()){
					    		key = keyPrefix + url.toLowerCase();
					    		methodAnnotationMap.put(key, method);
					    		setPermissionMap(key, roles);
					    	}
					    }
						else if(annotation instanceof GetAction){
					    	GetAction actions = (GetAction) annotation;
//					    	key = this.getClass().getName() + ":GET:" + action.url().toLowerCase();
//					    	methodAnnotationMap.put(key, method);
//					    	roles = action.roles();
//					    	setPermissionMap(key, roles);
					    	String keyPrefix = this.getClass().getName() + ":GET:";
					    	roles = actions.roles();
					    	for(String url : actions.urls()){
					    		key = keyPrefix + url.toLowerCase();
					    		methodAnnotationMap.put(key, method);
					    		setPermissionMap(key, roles);
					    	}
					    }
					    else if(annotation instanceof PostAction){
					    	PostAction actions = (PostAction) annotation;
//					    	key = this.getClass().getName() + ":POST:" + action.url().toLowerCase();
//					    	methodAnnotationMap.put(key, method);
//					    	roles = action.roles();
//					    	setPermissionMap(key, roles);
					    	String keyPrefix = this.getClass().getName() + ":POST:";
					    	roles = actions.roles();
					    	for(String url : actions.urls()){
					    		key = keyPrefix + url.toLowerCase();
					    		methodAnnotationMap.put(key, method);
					    		setPermissionMap(key, roles);
					    	}
					    }					    
					    else if(annotation instanceof AjaxGetAction){
					    	AjaxGetAction actions = (AjaxGetAction) annotation;
//					    	key = this.getClass().getName() + ":ajax:GET:" + action.url().toLowerCase();
//					    	methodAnnotationMap.put(key, method);
//					    	roles = action.roles();
//					    	setPermissionMap(key, roles);
					    	String keyPrefix = this.getClass().getName() + ":ajax:GET:";
					    	roles = actions.roles();
					    	for(String url : actions.urls()){
					    		key = keyPrefix + url.toLowerCase();
					    		methodAnnotationMap.put(key, method);
					    		setPermissionMap(key, roles);
					    	}
					    }
					    else if(annotation instanceof AjaxPostAction){
					    	AjaxPostAction actions = (AjaxPostAction) annotation;
//					    	key = this.getClass().getName() + ":ajax:POST:" + action.url().toLowerCase();
//					    	methodAnnotationMap.put(key, method);
//					    	roles = action.roles();
//					    	setPermissionMap(key, roles);
					    	String keyPrefix = this.getClass().getName() + ":ajax:POST:";
					    	roles = actions.roles();
					    	for(String url : actions.urls()){
					    		key = keyPrefix + url.toLowerCase();
					    		methodAnnotationMap.put(key, method);
					    		setPermissionMap(key, roles);
					    	}
					    }
					    else{
					    	continue;
					    }
					}
				}
			}
			//标记此类已被初始化
			methodAnnotationMap.put(this.getClass().getName(), null);
		}
	}
	
	private void setPermissionMap(String key, String[] roles){
		if(roles != null && roles.length > 0){
    		String roleStr = "";
    		for(String role : roles){
    			if(role != null && !role.equals("")){
    				roleStr += role + ", ";
    			}
    		}
    		if(!roleStr.equals("")){
    			permissionMap.put(key, roleStr);
    		}
    	}
	}
	
	public static void printRoutePolicy(){
		for(Entry<String, Method> entry: methodAnnotationMap.entrySet()){
			if(entry.getValue() != null){
				System.out.println(entry.getKey()+"   --->   " + entry.getValue().getName());
			}
		}
	}
	
//	protected void addRoutePolicy(String className, boolean isAjax, String httpMethod, String servletUrl){
//		String ajax = isAjax == false ? "" : "ajax:";
//    	String key = className + ":" + ajax + ":" + httpMethod + ":" + servletUrl;
//    	methodAnnotationMap.put(key, method);
//	}
	
	protected Method getActionMethod(String httpMethod, String url, boolean isAjax){
		String ajax = isAjax == false ? "" : "ajax:";
//		String key = "";//this.getClass().getName() + ":" + ajax +  httpMethod + ":" + url.toLowerCase();
//		if(needLogin && needCheckRole){
//			IUser user = getUser();
//			if(user != null){
//				String[] roles = user.getUserRoles();
//	    		if(roles != null){
//	    			for(String role : roles){
//	    				if(!role.equals("")){
//	    					key = this.getClass().getName() + ":" + ajax + httpMethod + ":role:" + role + ":" + url.toLowerCase();
//	    					if(methodAnnotationMap.containsKey(key)){
//	    						return methodAnnotationMap.get(key);
//	    					}
//	    				}
//	    			}
//	    		}
//			}
//			throw new PermissionException("Permission denied.");
//    	}
//		else{
//			key = this.getClass().getName() + ":" + ajax +  httpMethod + ":" + url.toLowerCase();
//			return methodAnnotationMap.get(key);
//		}
		String key = this.getClass().getName() + ":" + ajax +  httpMethod + ":" + url.toLowerCase();
		return methodAnnotationMap.get(key);
	}
	
	protected void checkMethodRole(String httpMethod, String url, boolean isAjax) throws PermissionException{
		String ajax = isAjax == false ? "" : "ajax:";
		String key = this.getClass().getName() + ":" + ajax +  httpMethod + ":" + url.toLowerCase();
		if(permissionMap.containsKey(key)){
			String roleStr = permissionMap.get(key);
			IUser user = (IUser)request.getSession().getAttribute(AppKeys.LoginUser);
			if(user != null){
				String[] userRoles = user.getUserRoles();
				for(String urole : userRoles){
					if(roleStr.indexOf(urole) >= 0){
						return;
					}
				}
			}
			throw new PermissionException("Permission denied.");
		}
	}
	
	protected Method getGetActionMethod(String url, boolean isAjax){
		String ajax = isAjax == false ? "" : "ajax:";
		String key = this.getClass().getName() + ":" + ajax + "GET:" + url.toLowerCase();
		return methodAnnotationMap.get(key);
	}
	
	protected Method getPostActionMethod(String url, boolean isAjax){
		String ajax = isAjax == false ? "" : "ajax:";
		String key = this.getClass().getName() + ":" + ajax + "POST:" + url.toLowerCase();
		return methodAnnotationMap.get(key);
	}
	
	protected IUser getUser(){
		return (IUser) request.getSession().getAttribute(AppKeys.LoginUser);
	}
	
	//用途 XXXDAO里都有对应的toXXX(HashMap<String, String>)方法，
	//那么这个方法可以将request的参数转换为hashmap，然后调用dao的对应方法就可以获得对象。
	protected HashMap<String, String> getParametersMap(){
		if(this.parametersMap != null){
			return this.parametersMap;
		}
		this.parametersMap = new HashMap<String, String>();
		Enumeration<String> names = request.getParameterNames();
		while(names.hasMoreElements()){
			String name = (String)names.nextElement();
			this.parametersMap.put(name, request.getParameter(name));
		}
		return this.parametersMap;
	}
	
	protected void toView(String page) throws Exception{
		request.getRequestDispatcher(page).forward(request, response);
	}
	
	protected void redirectTo(String url) throws Exception{
		response.sendRedirect(url);
	}
	
	protected void uploadFiles(){
//		String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
//		File file = new File(savePath);
//		//判断上传文件的保存目录是否存在
//		if (!file.exists() && !file.isDirectory()) {
//		System.out.println(savePath+"目录不存在，需要创建");
//		 26                     //创建目录
//		 27                     file.mkdir();
//		 28                 }
//		 29                 //消息提示
//		 30                 String message = "";
//		 31                 try{
//		 32                     //使用Apache文件上传组件处理文件上传步骤：
//		 33                     //1、创建一个DiskFileItemFactory工厂
//		 34                     DiskFileItemFactory factory = new DiskFileItemFactory();
//		 35                     //2、创建一个文件上传解析器
//		 36                     ServletFileUpload upload = new ServletFileUpload(factory);
//		 37                      //解决上传文件名的中文乱码
//		 38                     upload.setHeaderEncoding("UTF-8"); 
//		 39                     //3、判断提交上来的数据是否是上传表单的数据
//		 40                     if(!ServletFileUpload.isMultipartContent(request)){
//		 41                         //按照传统方式获取数据
//		 42                         return;
//		 43                     }
//		 44                     //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
//		 45                     List<FileItem> list = upload.parseRequest(request);
//		 46                     for(FileItem item : list){
//		 47                         //如果fileitem中封装的是普通输入项的数据
//		 48                         if(item.isFormField()){
//		 49                             String name = item.getFieldName();
//		 50                             //解决普通输入项的数据的中文乱码问题
//		 51                             String value = item.getString("UTF-8");
//		 52                             //value = new String(value.getBytes("iso8859-1"),"UTF-8");
//		 53                             System.out.println(name + "=" + value);
//		 54                         }else{//如果fileitem中封装的是上传文件
//		 55                             //得到上传的文件名称，
//		 56                             String filename = item.getName();
//		 57                             System.out.println(filename);
//		 58                             if(filename==null || filename.trim().equals("")){
//		 59                                 continue;
//		 60                             }
//		 61                             //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
//		 62                             //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
//		 63                             filename = filename.substring(filename.lastIndexOf("\\")+1);
//		 64                             //获取item中的上传文件的输入流
//		 65                             InputStream in = item.getInputStream();
//		 66                             //创建一个文件输出流
//		 67                             FileOutputStream out = new FileOutputStream(savePath + "\\" + filename);
//		 68                             //创建一个缓冲区
//		 69                             byte buffer[] = new byte[1024];
//		 70                             //判断输入流中的数据是否已经读完的标识
//		 71                             int len = 0;
//		 72                             //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
//		 73                             while((len=in.read(buffer))>0){
//		 74                                 //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
//		 75                                 out.write(buffer, 0, len);
//		 76                             }
//		 77                             //关闭输入流
//		 78                             in.close();
//		 79                             //关闭输出流
//		 80                             out.close();
//		 81                             //删除处理文件上传时生成的临时文件
//		 82                             item.delete();
//		 83                             message = "文件上传成功！";
//		 84                         }
//		 85                     }
//		 86                 }catch (Exception e) {
//		 87                     message= "文件上传失败！";
//		 88                     e.printStackTrace();
//		 89                     
//		 90                 }
//		 91                 request.setAttribute("message",message);
//		 92                 request.getRequestDispatcher("/message.jsp").forward(request, response);
	}
	
}
