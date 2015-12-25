package fys.core.web.controller;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fys.core.annotation.GetAction;
import fys.core.web.AppKeys;

public class RoutePolicyController extends GenericController {
	
	public RoutePolicyController(HttpServletRequest request, HttpServletResponse response){
		super(request, response);
		this.needLogin = false;
	}
	
	//账户记录
	@GetAction(urls="/routePolicy.do")
	public void accountOrdersViewAction() throws Exception {
		toView("/jsp/routepolicy.jsp");
	}
}
