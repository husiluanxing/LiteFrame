package fys.core.model;

import java.util.LinkedList;

public class Row {
	public java.util.List<Cell> cells;
	public boolean editable;
	public boolean needSave;
	
	public Row(){
		this.cells = new LinkedList<Cell>();
		this.editable = false;
		this.needSave = false;
	}
	
	public Object getCellValue(int index){
		if(this.cells != null){
			return this.cells.get(index).value;
		}
		return null;
	}
}
