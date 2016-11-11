package dbms.cli;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import dbms.connection.DatabaseConnection;

public class Records {
	public static void viewRecordsPage(Scanner input) {
		while (true) {
			System.out.println("1. View Records");
			System.out.println("2. Enter Records");
			System.out.println("3. Go Back To Previous Menu");
			int choice = input.nextInt();
			switch (choice) {
			case 1:
				printRecords(input);
				break;
			case 2:
				input.nextLine();
				addRecord(input);
				break;
			case 3:
				Login.homePage(input);
				break;
			default:
				System.out.println("Enter correct choice");
			}
		}
	}
	
	private static void addRecord(Scanner input) {
		System.out.println("Enter PID");
		String pid = input.nextLine();
		System.out.println("Enter Recommendation Name");
		String recommendation_name = input.nextLine();
		System.out.println("Enter Measure Name");
		String measure_name = input.nextLine();	
		addRecord(input,pid,recommendation_name,measure_name);
	}
	
	public static void addRecord(Scanner input,String pid, String recommendation_name, String measure_name) {
		try {
			System.out.println("Enter Record Date (YYYY-MM-DD)");
			Date recorded_date = Date.valueOf(input.nextLine());
			System.out.println("Enter Created Date (YYYY-MM-DD)");
			Date created_date = Date.valueOf(input.nextLine());
			System.out.println("Enter Value");
			String value = input.nextLine();
			String created_by = UserProfile.getInstance().getPid();
			
//			PreparedStatement preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.CHECK_IF_LIST_OF_VALUE);
//			preparedStmt.setString(1,measure_name);
//			rs = preparedStmt.executeQuery();
//			rs.next();
//			int count = rs.getInt(1);
//			if (count>0) {
//				// Code to convert value
//				preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.GET_LOV_VALUE);
//				preparedStmt.setString(1,measure_name);
//				preparedStmt.setString(2,value);
//				rs = preparedStmt.executeQuery();
//				if (rs.next()) {
//					value = rs.getString(1);
//				} else {
//					System.out.println("UNSUCCESSFUL - Value not found in List of Values for the measure.");
//					return;
//				}
//			}
			
			ResultSet rs;
			PreparedStatement preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.INSERT_RECORD);
			preparedStmt.setString(1,pid);
			preparedStmt.setString(2,recommendation_name);
			preparedStmt.setString(3,measure_name);
			preparedStmt.setString(4,value);
			preparedStmt.setDate(5,recorded_date);
			preparedStmt.setDate(6,created_date);
			preparedStmt.setString(7,created_by);
			rs = preparedStmt.executeQuery();
			
		} catch (SQLException e) {
			System.out.println("UNSUCCESSFUL! PLEASE SEE ERROR BELOW!");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error Occured! : Enter correct value");
			viewRecordsPage(input);
		}
	}
	
	private static void printRecords(Scanner input) {
		try {
			ResultSet rs;
			PreparedStatement preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.GET_RECORDS);
			String pid = UserProfile.getInstance().getPid();
			preparedStmt.setString(1,pid);
			preparedStmt.setString(2,pid);
			rs = preparedStmt.executeQuery();
			System.out.println(
				"PID | Record ID | Recorded Date | Recommendation Name | Measure Name | Value | Recorded Date | Created Date | Created By");
		
			while (rs.next()) {
				System.out.println(rs.getString("pid")+" | "+rs.getInt("record_id")+" | "+rs.getDate("recorded_date")+" | "+rs.getString("recommendation_name")+" | "+rs.getString("measure_name")+" | "+rs.getString("value")+" | "+rs.getDate("recorded_date")+" | "+rs.getDate("created_date")+" | "+rs.getString("created_by"));
			}
			while (true) {
				System.out.println("1. Go Back To Previous Menu");
				int choice = input.nextInt();
				switch (choice) {
				case 1:
					Records.viewRecordsPage(input);
					break;
				default:
					System.out.println("Enter correct choice");
				}
			}
		} catch (SQLException e) {
			System.out.println("UNSUCCESSFUL! PLEASE SEE ERROR BELOW!");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error Occured! : Enter correct value");
			viewRecordsPage(input);
		}

	}
}
