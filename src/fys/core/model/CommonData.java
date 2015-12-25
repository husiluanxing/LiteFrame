package fys.core.model;

public class CommonData {
	public String code;
	public String message;
	public String type;
	public Object data;
	
	public CommonData(String code){
		this.code = code;
		this.message = "";
		this.type = "";
		this.data = null;
	}
	public CommonData(String code, String message ){
		this.code = code;
		this.message = message;
		this.type = "";
		this.data = null;
	}
	
	public CommonData(String code, String message, Object data){
		this.code = code;
		this.message = message;
		this.type = data==null?"":data.getClass().getSimpleName();
		this.data = data;
	}
	
	public CommonData(String code, Object data){
		this.code = data==null? "null":"success";
		this.message = "";
		this.type = data==null?"":data.getClass().getSimpleName();
		this.data = data;
	}
	
	
	public CommonData(Exception ex){
		this.code = "error";
		this.message = ex.getMessage();
		this.type = "";
		this.data = null;
	}
}
