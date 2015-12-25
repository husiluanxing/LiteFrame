package fys.core.model;

import java.util.List;

public class SingleSelectionColumn extends Column {
	public List<Object> options;
	
	public SingleSelectionColumn(String name, String displayName, List<Object> options){
		super(name, displayName);
		this.options = options;
	}
}
