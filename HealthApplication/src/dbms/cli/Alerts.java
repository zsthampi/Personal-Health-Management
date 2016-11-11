package dbms.cli;

import java.util.Scanner;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dbms.connection.DatabaseConnection;

public class Alerts {
	static int count;
	private int id;
	private int alert_id;
	private String pid;
	private String recommendation_name;
	private String measure_name;
	private String type;
	private String is_viewed;
	private int upper_limit;
	private int lower_limit;
	private int frequency;
	private int number_of_observations;
	private int threshold;
	
	private Alerts(ResultSet rs) {
		try{
			this.id = ++count;
			this.alert_id = rs.getInt("alert_id");
			this.pid = rs.getString("pid");
			this.recommendation_name = rs.getString("recommendation_name");
			this.measure_name = rs.getString("measure_name");
			this.type = rs.getString("type");
			this.is_viewed = rs.getString("is_viewed");
			this.upper_limit = rs.getInt("upper_limit");
			this.lower_limit = rs.getInt("lower_limit");
			this.frequency = rs.getInt("frequency");
			this.number_of_observations = rs.getInt("number_of_observations");
			this.threshold = rs.getInt("threshold");
		} catch (SQLException e) {
			System.out.println("UNSUCCESSFUL! PLEASE SEE ERROR BELOW!");
			System.out.println(e.getMessage());
		}
		catch(Exception e){
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
	}
	
	private void clear() {
		try {
			PreparedStatement preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.CLEAR_ALERT);
			preparedStmt.setString(1, UserProfile.getInstance().getPid());
			preparedStmt.setInt(2, this.alert_id);
			preparedStmt.setString(3, this.pid);
			preparedStmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("UNSUCCESSFUL! PLEASE SEE ERROR BELOW!");
			System.out.println(e.getMessage());
		}
		catch(Exception e){
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
	}
	
	private void view() {
		try {
			if (this.is_viewed!="Y") {
				PreparedStatement preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.VIEW_ALERT);
				preparedStmt.setInt(1, this.alert_id);
				preparedStmt.setString(2, this.pid);
				preparedStmt.executeQuery();
			}
		} catch (SQLException e) {
			System.out.println("UNSUCCESSFUL! PLEASE SEE ERROR BELOW!");
			System.out.println(e.getMessage());
		}
		catch(Exception e){
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
	}
	
	private static ArrayList<Alerts> createAlertsObjects(ResultSet rs) {
		count = 0;
		ArrayList<Alerts> alerts = new ArrayList<Alerts>();
		try {
			while (rs.next()) {
				Alerts a = new Alerts(rs);
				alerts.add(a);
			}
	
		} catch (SQLException e) {
			e.printStackTrace();
			//return alerts;
		}
		catch(Exception e){
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
			//return null;
		}
		return alerts;
	}
	
	public static void viewAlerts(Scanner input) {
		try {
			while (true) {
				ResultSet rs;
				
				PreparedStatement preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.RUN_GENERATE_ALERTS);
				preparedStmt.executeQuery();
				
				preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.GET_ALERTS);
				String pid = UserProfile.getInstance().getPid();
				preparedStmt.setString(1,pid);
				preparedStmt.setString(2,pid);
				rs = preparedStmt.executeQuery();
				System.out.println("ALERTS");
				
				ArrayList<Alerts> alerts = createAlertsObjects(rs);
				int number_of_alerts = alerts.size();
				if (alerts.isEmpty()) {
					System.out.println("No alerts found.");
				} else {
					System.out.println("ID | PID | Type | Is Viewed | Recommendation Name | Measure Name | Upper Limit | Lower Limit | Frequency | No of Obs | Threshold");
					for (Alerts alert : alerts) {
						System.out.println(Integer.toString(alert.id)+". "+alert.pid+" | "+alert.type+" | "+alert.is_viewed+" | "+alert.recommendation_name+" | "+alert.measure_name+" | "+alert.upper_limit+" | "+alert.lower_limit+" | "+alert.frequency+" | "+alert.number_of_observations+" | "+alert.threshold);
					}
				}
				System.out.println("Select the alert ID for more options");
				System.out.println(Integer.toString(number_of_alerts+1)+". Go Back To Previous Menu");
				int choice = input.nextInt();
				if (choice==(number_of_alerts+1)) {
					Login.homePage(input);
				} else if (choice>=1 && choice<=number_of_alerts) {
					int row_number = choice-1;
					alerts.get(row_number).view();
					while (true) {
						System.out.println("1. Clear");
						System.out.println("2. Enter Data");
						System.out.println("3. Return to previous page");
						choice = input.nextInt();
						switch (choice) {
						case 1: {
							alerts.get(row_number).clear();
							viewAlerts(input);
							break;
						}
						case 2: {
							input.nextLine();
							Records.addRecord(input, alerts.get(row_number).pid, alerts.get(row_number).recommendation_name, alerts.get(row_number).measure_name);
							viewAlerts(input);
							break;
						}
						case 3: {
							viewAlerts(input);
							break;
						}
						default: {
							System.out.println("Invalid choice. Please try again.");
							continue;
						}}
					}
				} else {
					System.out.println("Invalid choice. Please try again.");
				}
			}
		} catch (SQLException e) {
			System.out.println("UNSUCCESSFUL! PLEASE SEE ERROR BELOW!");
			System.out.println(e.getMessage());
		}
		catch(Exception e){
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
	}
}
