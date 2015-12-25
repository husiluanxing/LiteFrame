package fys.core.web;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IController {
//	String getCurrentUrlPrefix();
//	void setCurrentUrlPrefix(String urlPrefix);//获得这个controller对应的当前url前缀。一个controller可能对应多个前缀。比如forumController可以对应forum或f。f需要在application.xml中进行配置
	Object dispatchRequest( ) throws Exception;
	Object dispatchAjaxRequest( ) throws Exception;
	void release();
	void rollback() throws SQLException;
	void commit() throws SQLException;
	boolean needLogin();
	String getLoginUrl();
	void setModuleKey(String moduleKey);
	void clear();
	void setRequestResponse(HttpServletRequest request, HttpServletResponse response);
}
