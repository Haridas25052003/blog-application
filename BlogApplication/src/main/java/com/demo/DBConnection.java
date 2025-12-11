package com.demo;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	
	public static Connection createConnection() {
		
		Connection con=null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/dbblog","root","root123");
			
			System.out.println("database connected successfully");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return con;
	}

}
