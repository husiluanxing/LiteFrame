package fys.core.model;

public class Cell {
	public Column column;
	public Object value;
	
	public Cell(Column column, Object value){
		this.column = column;
		this.value = value;
	}
}
