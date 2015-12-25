package fys.core.model;

import java.util.List;

public class ModuleItem {
	public String name;
	public String displayName;
	public String url = "";
	public boolean openInNewPage = false;
	public String imgClass = ""; //也可使用class来画图标
	public List<MenuItem> menus;
}
