package fys.core.model;

import java.util.List;

public class RangeColumn extends Column {
	public Object min;
	public Object max;
	public RangeColumn(String name, String displayName, Object min, Object max){
		super(name, displayName);
		this.min = min;
		this.max = max;
	}
}
