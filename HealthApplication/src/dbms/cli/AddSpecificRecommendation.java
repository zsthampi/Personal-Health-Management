package dbms.cli;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dbms.connection.DatabaseConnection;

public class AddSpecificRecommendation {
	public static void viewAddSpecificRecommendation(Scanner input) {
		try {
			System.out.println("0. Go Back to previous menu");
			System.out.println("Select the person you want to view/add for.");
			System.out.println("1. Self");

			// Give options to Pick a Patient
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_PATIENTS_FOR_HEALTHSUPPORTERS);
			preparedStmt.setString(1, UserProfile.getInstance().getPid());
			ResultSet rs = preparedStmt.executeQuery();

			int count = 1;
			List<String> pidList = new ArrayList<String>();
			pidList.add(UserProfile.getInstance().getPid());
			if (rs.next()) {
				do {
					System.out.println((++count) + ". " + rs.getString("PID"));
					pidList.add(rs.getString("PID"));
				} while (rs.next());
			}

			int choice = input.nextInt();
			switch (choice) {
			case 0:
				Login.homePage(input);
			default:
				addSpecificRecoOptions(pidList.get(choice - 1), input);
			}
		} catch (SQLException e) {
			System.out.println("UNSUCCESSFUL!");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error Occured!" + e.getMessage());
			Login.homePage(input);
		}
	}

	public static void addSpecificRecoOptions(String pid, Scanner input) {
		try {
			System.out.println("Enter 0 To Go back to previous Menu");
			System.out.println("Enter 1 For Adding New");
			System.out.println("Enter 2 For Viewing");

			int choice = input.nextInt();
			switch (choice) {
			case 0:
				viewAddSpecificRecommendation(input);
			case 1:
				addSpecificReco(pid, input);
			case 2:
				viewSpecificReco(pid, input);
			default:
				viewAddSpecificRecommendation(input);
			}

		} catch (Exception e) {
			System.out.println("Error Occured!" + e.getMessage());
			viewAddSpecificRecommendation(input);
		}

	}

	public static void addSpecificReco(String pid, Scanner input) {
		try {
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.INSERT_SPEC_RECO);
			input.nextLine();
			System.out.println("Enter Recommendation Name");
			preparedStmt.setString(1, input.nextLine());
			System.out.println("Enter MEASURE");
			preparedStmt.setString(2, input.nextLine());
			preparedStmt.setString(3, pid);
			System.out.println("Enter Upper Limit(Numeric)");
			preparedStmt.setInt(4, input.nextInt());
			System.out.println("Enter Lower Limit(Numeric)");
			preparedStmt.setInt(5, input.nextInt());
			System.out.println("Enter Frequency");
			preparedStmt.setInt(6, input.nextInt());
			preparedStmt.executeQuery();
			System.out.println("Added Successfully");
			viewAddSpecificRecommendation(input);
		} catch (SQLException e) {
			System.out.println("UNSUCCESSFUL!");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error Occured!" + e.getMessage());
			viewAddSpecificRecommendation(input);
		}

	}

	public static void viewSpecificReco(String pid, Scanner input) {
		try {
			int count = 0;

			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.VIEW_RECOMMENDATION);
			preparedStmt.setString(1, pid);
			ResultSet rs = preparedStmt.executeQuery();
			if (rs.next()) {
				System.out.println("PID | RECOMMENDATION_NAME | MEASURE_NAME | UPPER_LIMIT | LOWER_LIMIT | FREQUENCY");
				do {

					System.out.println(
							(++count) + ". " + rs.getString("PID") + " | " + rs.getString("RECOMMENDATION_NAME") + " | "
									+ rs.getString("MEASURE_NAME") + " | " + rs.getInt("UPPER_LIMIT") + " | "
									+ rs.getInt("LOWER_LIMIT") + " | " + rs.getInt("FREQUENCY"));
				} while (rs.next());
			} else {
				System.out.println("No Specific Recommendations were found!");
			}

			System.out.println("Enter 0 to go back");
			input.nextInt();
			viewAddSpecificRecommendation(input);

		} catch (SQLException e) {
			System.out.println("UNSUCCESSFUL!");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Error Occured!" + e.getMessage());
			viewAddSpecificRecommendation(input);
		}

	}
}
