package fys.core.model;

import java.util.List;

public class MultiSelectionColumn extends Column {
	public List<Object> options;
	public String split; //存储在数据库表中的
	public MultiSelectionColumn(String name, String displayName, List<Object> options){
		super(name, displayName);
		this.options = options;
	}
}
