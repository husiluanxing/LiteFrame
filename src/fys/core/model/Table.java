package fys.core.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Table {
	public ResultSet rs;
	public String name;
	public String displayName;
	public String description;
	public List<Column> columns;
	public List<Row> rows;
	public boolean editable;
	public boolean needSave;
	public String action;
	public String method;
	//Integer代表哪一列（此列必须是外键列）， HashMap<Object, Object>存储了对应的外键表的这列的所有值和显示值的映射关系。
	public HashMap<Integer, HashMap<Object, Object>> colValTextMap = null;
	
	public Table(String name, String displayName){
		this.name = name;
		this.displayName = displayName;
		this.columns = new LinkedList<Column>();
		this.rows = new LinkedList<Row>();
		this.editable = false;
		this.needSave = false;
		this.action = "#";
		this.method = "GET";
	}
	
	public static Table fromResultSet(String tableName, String displayName, ResultSet rs) throws SQLException{
		Table t = new Table(tableName, displayName);
		t.rs = rs;
		t.columns = Column.fromResultSet(rs);
		
		int colsize = rs.getMetaData().getColumnCount();
		while(rs.next()){
			Row row = new Row();
			for(int i = 0; i < colsize; i++){
				Cell cell = new Cell(t.columns.get(i), rs.getObject(i+1));
				row.cells.add(cell);
			}
			t.rows.add(row);
		}
		return t;
	}
	
	public int getColumnIndex(String columnName){
		if(this.columns != null){
			for(int i=0; i<this.columns.size(); i++){
				if(this.columns.get(i).name.equals(columnName)){
					return i;
				}
			}
		}
		return -1;
	}
	
	public List<Object> getColumnValues(int index){
		List<Object> values = new LinkedList<Object>();
		if(this.rows != null){
			for(Row row : this.rows){
				if(row.getCellValue(index) != null){
					values.add(row.getCellValue(index));
				}
			}
		}
		return values;
	}
}
