package fys.core.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import fys.core.config.AppConfig;
import fys.core.exception.PermissionException;
import fys.core.util.ControllerUtil;


public class GeneralServlet extends HttpServlet {
	private static HashMap<String, Class> moduleMap = new HashMap<String, Class>();
//	static {
//		moduleMap.put("api/user", UserController.class);
//		moduleMap.put("login", LoginController.class);
//		moduleMap.put("index", IndexController.class);
//		moduleMap.put("routepolicy", RoutePolicyController.class);
//	}
	
	public final void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            GlobalData.application = this.getServletContext();
            GlobalData.appRoot = this.getServletContext().getRealPath("/");
            AppConfig.getInstance().init(GlobalData.appRoot);
//            AppLogger.getInstance().init(e);
//            this.entryInit();
        } catch (Exception var3) {
            var3.printStackTrace();
            System.exit(-1);
        }

    }
	
//	public static void registerDispatchHandler(String moduleKey, Class handlerClass)throws Exception{
//		String key = moduleKey.toLowerCase();
//		if(moduleMap.containsKey(key)){
//			throw new Exception("module key " + moduleKey + "has been regiestered. Please use other key for module url.");
//		}
//		else{
//			moduleMap.put(key, handlerClass);
//		}
//	}
//	public static void registerDispatchHandler(String[] moduleKeys, Class handlerClass)throws Exception{
//		for(String moduleKey : moduleKeys){
//			registerDispatchHandler(moduleKey, handlerClass);
//		}
//	}
	
	public boolean isAjax(HttpServletRequest request){
		String header = request.getHeader("X-Requested-With");
		return "XMLHttpRequest".equals(header) ? true : false;
	}
	
	public static Boolean checkLogin(HttpServletRequest request){
		if(request.getSession().getAttribute(AppKeys.LoginUser) != null){
			return true;
		}
		else {
			//todo: check cookies to find whether it's logged in.
			
			//todo: if it's logged in, record in session then return true; else return false;
			//request.getSession().getAttribute("LoginUser", userInfo);
			return false;
		}
		//return false, tell caller it should be forward to login page.
//		return false;
	}
	
	private String getModuleKey(String servletPath){
		if(servletPath.startsWith("/")){
			servletPath = servletPath.substring(1);	
			int i = 0;
			while(i < servletPath.length()){
				if(servletPath.charAt(i)=='/' || servletPath.charAt(i) == '.'){
					break;
				}
				i++;
			}
			return servletPath.substring(0, i);
		}
		else{
			return "";
		}
	}
	
	private String getParametersStr(String servletPath){
		if(servletPath.indexOf('?') > 0){
			return servletPath.substring(servletPath.indexOf('?'));
		}
		return "";
	}
	
	private Class getHandlerClassFromConfig(String servletPath){
		for(Entry<String, String> entry: AppConfig.getInstance().modules.entrySet()){
			if(servletPath.equals("/" + entry.getKey()) ||
					servletPath.startsWith("/" + entry.getKey() + "/") ||
					servletPath.startsWith("/" + entry.getKey() + ".")){
				return getHandlerClassByName(entry.getValue());
			}
		}
		return null;
	}
	
	private Class getHandlerClassByName(String module) {
		try{
			return Class.forName(AppConfig.controllerPackage +  "." + module.substring(0, 1).toUpperCase() + module.substring(1) + "Controller");
		}
		catch(Exception ex){
			
		}
		try{
			return Class.forName("core.web.controller." + module.substring(0, 1).toUpperCase() + module.substring(1) + "Controller");
		}
		catch(Exception ex){
		}
		try{
			return Class.forName(module);
		}
		catch(Exception ex){
			return null;
		}
	}
			
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String servletPath = request.getServletPath();
		String moduleKey = getModuleKey(servletPath);
		
		boolean isAjax = isAjax(request);
		
		Class handlerClass = null;
		if( moduleMap.containsKey(moduleKey) ){
			handlerClass = moduleMap.get(moduleKey);
		}
		/*
		 * 暂时不支持控制器类对应多个Url前缀，灵活性太高，反而会使得程序结构不清晰。
		 */
//		handlerClass = this.getHandlerClassFromConfig(servletPath);
		if(handlerClass==null && AppConfig.findControllerByName){			
			handlerClass = getHandlerClassByName(moduleKey);
		}

		if(handlerClass != null){
			moduleMap.put(moduleKey, handlerClass);
			IController handler = null;
			try{
				handler = ControllerUtil.getHandlerInstance(handlerClass, request, response); // (IController) handlerClass.getConstructor(parameterTypes).newInstance();
				handler.setModuleKey(moduleKey);
				request.setAttribute(AppKeys.JspPageHandler, handler);
				//检查模块是否需要登录
				if(handler.needLogin()){
					//检查是否登录，如果未登录，则跳转到指定的登录页面
					if(checkLogin(request) ==  false){
						String loginUrl = handler.getLoginUrl() == null ? AppConfig.loginUrl : handler.getLoginUrl();
						loginUrl += "?returnAddress=" + URLEncoder.encode(request.getRequestURI(), AppConfig.appEncoding); 
						response.sendRedirect(loginUrl);
						return;
					}
				}
				try{
					//普通请求
					if(!isAjax){
						Object data = handler.dispatchRequest( );
						reponseJsonData(response, data);
					}
					else{ //异步ajax请求
						Object data = handler.dispatchAjaxRequest( );
						reponseJsonData(response, data);					
					}
					handler.commit();
					return;
				}
				catch(PermissionException pex){
					handler.rollback();
					request.getRequestDispatcher(AppConfig.errorPermissionPage).forward(request, response);
				}
				catch(Exception ex){
					handler.rollback();
					response.sendRedirect(AppConfig.error404page);
				}
			}
			catch(Exception ex){ // 内部错误
				ex.printStackTrace();
				response.sendRedirect(AppConfig.error500page);
			}
			finally{
				ControllerUtil.returnToPool(handler);
			}
		}
		else{
			response.sendRedirect(AppConfig.error404page);
		}
	}

	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
	throws ServletException, IOException {
		doGet(arg0, arg1);
	}
	
	protected void reponseJsonData(HttpServletResponse response, Object data)
	throws IOException {
		if(data != null){
			response.setCharacterEncoding(AppConfig.appEncoding);
			PrintWriter out = response.getWriter();
			out.print(getJsonData(data));
			out.flush();
			out.close();
		}
		return;
	}
	
	private void printError(HttpServletResponse response, Exception ex)throws IOException{
		if(ex != null){
			PrintWriter out = response.getWriter();
			for(StackTraceElement ele : ex.getStackTrace()){
				out.print(ele.toString());
				out.println();
			}
			out.flush();
			out.close();
		}
	}
	
	private String getJsonData(Object data){
		if(data.getClass() == Object.class){
			return data.toString();
		}
		else if(data.getClass() != String.class){
			return JSON.toJSONString(data);
		}
		else{
			return (String)data;
		}
	}
	
	private String getXMLData(Object data){
		return "";
	}
}
