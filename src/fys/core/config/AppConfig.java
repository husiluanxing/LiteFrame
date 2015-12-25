package fys.core.config;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AppConfig {
	public String applicationRoot;

	public static boolean findControllerByName = true;
	public static String controllerPackage = "dfw.ht.controller";
	public static String loginUrl = "/login/login.do";
	public static String error404page = "/error_404.html";
	public static String error500page = "/error_500.html";
	public static String errorPermissionPage = "/error_permission.html";
	public static String appEncoding = "UTF-8";
	public static String returnValueIfNull = "null";
	public static int maxBufferControllerForOneMoudle = 1000;

	public HashMap<String, String> modules = new HashMap<String, String>();
	public HashMap<String, String> parameters = new HashMap<String, String>();
	public HashMap<String, String> constants = new HashMap<String, String>();
	private static AppConfig config = new AppConfig();

	private AppConfig() {
	}

	public static AppConfig getInstance() {
		return config;
	}

	// 如果不存在则返回null
	public String getModule(String moduleName) {
		return modules.get(moduleName);
	}

	// 如果不存在则返回空字符串
	public String getParameter(String parameter) {
		return parameters.containsKey(parameter) ? parameters.get(parameter)
				: "";
	}

	// 如果不存在则返回空字符串
	public String getConstant(String constantName) {
		return parameters.containsKey(constantName) ? parameters
				.get(constantName) : "";
	}

	public void init(String applicationRoot) throws Exception {
		this.applicationRoot = applicationRoot;
		// String parameterFilePath = applicationRoot + File.separator +
		// "WEB-INF"
		// + File.separator + "config" + File.separator
		// + "config.properties";
		// String messageFilePath = applicationRoot + File.separator + "WEB-INF"
		// + File.separator + "config" + File.separator
		// + "message.properties";
		String appConfigFile = applicationRoot + File.separator + "WEB-INF"
				+ File.separator + "config" + File.separator
				+ "application.xml";

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		builderFactory.setValidating(false);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document doc = builder.parse(appConfigFile);
		if(doc != null){
			/*
			 * 暂时不支持控制器类对应多个Url前缀，灵活性太高，反而会使得程序结构不清晰。
			 */
//			this.initModules(doc);
			this.initParameters(doc);
			this.initConstants(doc);
		}
	}
	

	private void initModules(Document doc) {
		this.modules.clear();
		NodeList moduleNodeList = doc.getElementsByTagName("modules");
		if(moduleNodeList != null && moduleNodeList.getLength() > 0){
			Element ele = (Element) moduleNodeList.item(0);
			org.w3c.dom.NodeList nodes = ele.getElementsByTagName("module");
			for (int i = 0; i < nodes.getLength(); ++i) {
				org.w3c.dom.Node node = nodes.item(i);
				Element element = (Element) node;
				String urlPrefix = element.getAttribute("urlPrefix").trim();
				String controller = element.getAttribute("controller").trim();
				if (!urlPrefix.equals("") && !controller.equals("")) {
					modules.put(urlPrefix, controller);
				}
			}
		}
	}
	private void initParameters(Document doc) {
		this.parameters.clear();
		NodeList moduleNodeList = doc.getElementsByTagName("parameters");
		if(moduleNodeList != null && moduleNodeList.getLength() > 0){
			Element ele = (Element) moduleNodeList.item(0);
			org.w3c.dom.NodeList nodes = ele.getElementsByTagName("parameter");
			for (int i = 0; i < nodes.getLength(); ++i) {
				Node node = nodes.item(i);
				Element element = (Element) node;
				String parameterName = element.getAttribute("name").trim();
				String parameterValue = element.getAttribute("value").trim();
				if (!parameterName.equals("") && !parameterName.equals("")) {
					modules.put(parameterName, parameterValue);
				}
			}
		}
	}
	private void initConstants(Document doc) {
		this.parameters.clear();
		NodeList moduleNodeList = doc.getElementsByTagName("constants");
		if(moduleNodeList != null && moduleNodeList.getLength() > 0){
			Element ele = (Element) moduleNodeList.item(0);
			org.w3c.dom.NodeList nodes = ele.getElementsByTagName("constant");
			for (int i = 0; i < nodes.getLength(); ++i) {
				Node node = nodes.item(i);
				Element element = (Element) node;
				String constantName = element.getAttribute("name").trim();
				String constantValue = element.getAttribute("value").trim();
				if (!constantName.equals("") && !constantValue.equals("")) {
					modules.put(constantName, constantValue);
				}
			}
		}
	}

	public void reload() throws Exception {
		this.init(this.applicationRoot);
	}
}
