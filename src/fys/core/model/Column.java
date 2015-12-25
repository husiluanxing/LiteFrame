package fys.core.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class Column {
	public String name;
	public String displayName;
	public int sqlType;
	public String type;
	public boolean editable = false;
	public boolean isMainKey = false;
	public boolean isForeignKey = false;
	public String foreignTable = ""; 
	public String referKey = "";
	public String referTextKey = "";
	
	public Column(String name, String displayName){
		this.name = name;
		this.displayName = displayName;
		this.type = Column.class.toString();
	}
	
	public Column(String name, String displayName, String foreignTable, String referKey, String referTextKey){
		this.name = name;
		this.displayName = displayName;
		this.type = Column.class.toString();
		this.isForeignKey = true;
		this.foreignTable = foreignTable;
		this.referKey = referKey;
		this.referTextKey = referTextKey;
	}
	
	public static List<Column> fromResultSet(ResultSet rs) throws SQLException{
		List<Column> columns = new LinkedList<Column>();
		ResultSetMetaData meta = rs.getMetaData();
		int colsize = meta.getColumnCount();
		for(int i=0; i<colsize; i++){
			Column col = new Column(meta.getColumnName(i+1), meta.getColumnLabel(i+1));
			col.sqlType = meta.getColumnType(i+1);
			columns.add(col);
		}
		return columns;
	}
}
