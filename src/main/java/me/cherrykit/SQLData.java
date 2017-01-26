package me.cherrykit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLData {

	private static Connection conn;
	
	public static void getConnection() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/jobs?useSSL=false", "sqluser", "sqluserpw");
			System.out.println("Connected to database");
		} catch (Exception e) {
			System.out.println("Error wile connecting: ");
		}
	}
	
	public static String[] getJob(String pname) {
		String[] results = new String[2];
		try {
			String query = "select * from job where playername = '" + pname + "'";
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery(query);
			if (rs.next()) {
				results[0] = rs.getString("jobtype");
				results[1] = rs.getString("blockamount");
			} else {
				results[0] = "0";
				results[1] = "0";
			}
			rs.close();
		} catch (Exception e) {
			System.out.println("getJob: " + e);
			results[0] = "0";
			results[1] = "0";
		}
		return results;
	}
	
	public static String getMoney(String pname) {
		try {
			String query = "select amount from money where playername = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, pname);
			ResultSet rs = ps.executeQuery();
			
			String result;
			if (rs.next()) {
				result = rs.getString("amount");
			} else {
				result = "0";
			}
			rs.close();
			return result;
			
		} catch (Exception e) {
			System.out.println(e);
			return "0";
		}
	}
	
	public static void setJob(String pname, String job, int amount) {
		//Player does not have a job
		if (getJob(pname)[0] == "0") {
			try {
				String query = "insert into job (playername, jobtype, blockamount) values (?,?,?)";
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, pname);
				ps.setString(2, job);
				ps.setInt(3, amount);
				ps.executeUpdate();
				
			} catch (Exception e) {
				System.out.println(e);
			}
			
		} else {
			try {
				String query = "update job set jobtype = ? where playername = '" + pname + "'";
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, job);
				ps.executeUpdate();
				
				query = "update job set blockamount = ? where playername = '" + pname + "'";
				ps = conn.prepareStatement(query);
				ps.setInt(1, amount);
				ps.executeUpdate();
				
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	public static void setMoney(String pname, double amount) {
		//Player in database?
		if (getMoney(pname) == "0") {
			try {
				String query = "insert into money (playername, amount) values (?,?)";
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, pname);
				ps.setDouble(2, amount);
				ps.executeUpdate();
				
			} catch (Exception e) {
				System.out.println(e);
			}
		} else {
			try {
				String query = "update money set amount = ? where playername = ?";
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setDouble(1, amount);
				ps.setString(2, pname);
				ps.executeUpdate();
				
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	public static void removeJob(String pname) {
		try {
			String query = "delete from job where playername = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, pname);
			ps.executeUpdate();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void closeConnection() {
		try {
			conn.close();
		} catch (Exception e) {
			System.out.println("Failed to disconnect: " + e);
		}
	}
	
}
