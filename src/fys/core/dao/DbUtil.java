package fys.core.dao;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import fys.core.web.GlobalData;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DbUtil {
	private static DbUtil instance = null;
	private DataSource ds = null;
    
    private DbUtil( ){
    	try {
            Properties prop = new Properties();
//            String classPath = DbUtil.class.getClassLoader().getResource("../config").getPath(); 
//            String dbcpConfig = classPath + "dbcp.properties";
            String dbcpConfig = GlobalData.appRoot + "WEB-INF" + File.separator + "config"  + File.separator + "dbcp.properties";
            InputStream is = new FileInputStream(dbcpConfig);//GlobalData.application.getResourceAsStream("dbcp.properties");
            prop.load(is);
//            BasicDataSourceFactory factory = new BasicDataSourceFactory();
            ds = BasicDataSourceFactory.createDataSource(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static DbUtil getInstance(){
    	if(DbUtil.instance != null){
    		return DbUtil.instance;
    	}
    	DbUtil.instance = new DbUtil();
    	return DbUtil.instance; 
    }
    
    public Connection getConnection() throws SQLException{
        return ds.getConnection();
    }
    
    private void close(Connection conn, Statement st, ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(st != null){
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
}
