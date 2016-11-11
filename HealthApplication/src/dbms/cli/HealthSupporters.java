/* author : bhavya bansal*/
package dbms.cli;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import dbms.connection.DatabaseConnection;

public class HealthSupporters {

	public static void homePageHealthSupporters(Scanner input) {
		System.out.println("Hello " + UserProfile.getInstance().getName());
		System.out.println("Please select the option you want to go with.");

		while (true) {
			System.out.println("1. Add Health Supporter");
			System.out.println("2. Remove Health Supporter(s)");
			System.out.println("3. View Health Supporter(s)");
			System.out.println("4. Back to home page");
			System.out.print("Choice : ");
			int choice = input.nextInt();
			switch (choice) {
			case 1:
				addHS(input);
				break;
			case 2:
				removeHealthSupporter(input);
				break;
			case 3:
				printHealthSupporters(input);
				break;
			case 4:
				Login.homePage(input);
				break;
			default:
				System.out.println("Enter Correct Choice");
			}

		}
	}

	public static void printHealthSupporters(Scanner input) {
		try {
			ResultSet rs;
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_HEALTHSUPPORTERS);
			preparedStmt.setString(1, UserProfile.getInstance().getPid());
			rs = preparedStmt.executeQuery();
			if (!rs.next()) {
				System.out.println("No Health Supporters found.");
			} else {
				System.out.println("HID | Authorization Date | is_primary");
				do {
					System.out.println(rs.getString("hid") + " | " + rs.getDate("authorization_date") + " | "
							+ rs.getString("is_primary"));
				} while (rs.next());
			}
		} catch (SQLException e) {
			System.out.println("ERROR OCCURED!");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("ERROR OCCURED!");
			homePageHealthSupporters(input);
		}
	}

	public static void addSupporters(Scanner input) {
		String errorMessage = null;
		try {
			ResultSet rs;
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_HEALTHSUPPORTERS);

			preparedStmt.setString(1, UserProfile.getInstance().getPid());
			rs = preparedStmt.executeQuery();
			int count = 0;
			count = countRows(rs);
			if (count >= 2) {
				System.out.println(
						"You can not add more than 2 health supporters. Please delete one of the health supporter and then add a new one.");
				System.out.println("Redirecting back to the Health Supporter Menu");

			} else if (count == 1) {
				// can add one secondary health supporter
				System.out.println(
						"You have already a primary health supporter. You can add a secondary health supporter in the system!");
				System.out.println("Do you want to continue adding a secondary health supporter? Choice: (Y/N)");
				String responseFromUser = input.next();
				if (responseFromUser.equals("Y")) {

					System.out.println("Enter User Id of the health supporter you wish to add");
					String userNameOfHealthSupporter = input.next();
					if (checkUserExistence(userNameOfHealthSupporter)) {

						// secondary health supporter can be added

						java.sql.Date authDate = new java.sql.Date(new java.util.Date().getTime());
						addHealthSupporter(UserProfile.getInstance().getPid(), userNameOfHealthSupporter, "N",
								authDate);

					} else {
						System.out.println(
								"Given health supporter does not exist in the system. Please ask them to sign up first and then please retry adding a health supporter.");
						System.out.println("Redirecting back to the Health Supporter Menu");
					}
				}

				else if (responseFromUser.equals("N")) {
					System.out.println("Redirecting back to the Health Supporter Menu");

				} else {
					System.out.println("Please enter a valid choice and try again!");
					System.out.println("Redirecting back to the Health Supporter Menu");
				}
			} else {

				System.out.println("You currently do not have any health supporters added to the system.");
				System.out.println("You can add two health supporters, one primary and one secondary");
				System.out.println("Please enter below details to add primary health supporter");
				System.out.println("Enter user Id of the health supporter you wish to add");
				String healthSupporterId = input.next();
				if (checkUserExistence(healthSupporterId)) {
					java.sql.Date authDate = new java.sql.Date(new java.util.Date().getTime());
					addHealthSupporter(UserProfile.getInstance().getPid(), healthSupporterId, "Y", authDate);
				} else {
					System.out.println(
							"Given health supporter does not exist in the system. Please ask them to sign up first and then please retry adding a health supporter.");
					System.out.println("Redirecting back to the Health Supporter Menu");
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			errorMessage = e.getMessage();

		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}

	}

	public static int countRows(ResultSet rs) throws SQLException {
		int count = 0;

		while (rs.next()) {
			++count;

		}
		return count;
	}

	public static boolean checkUserExistence(String userName) throws SQLException {
		boolean result = false;
		ResultSet rs2;
		PreparedStatement preparedStmt2 = DatabaseConnection.getConnection().prepareStatement(SqlQueries.USER_VERIFY);

		preparedStmt2.setString(1, userName);
		rs2 = preparedStmt2.executeQuery();
		if (rs2.next()) {
			// health supporter exist and can be added to the system
			result = true;
			return result;

		}

		return false;
	}

	public static void addHealthSupporter(String pid, String hid, String is_primary, java.sql.Date authDate) {
		String errorMessage = null;
		try {
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.INSERT_HEALTH_SUPPORTER);

			preparedStmt.setString(1, pid);
			preparedStmt.setString(2, hid);
			// preparedStmt.setString(3, is_primary);
			preparedStmt.setDate(3, authDate);

			preparedStmt.executeUpdate();
			if (is_primary.equals("N")) {
				System.out.println("Secondary Health Supporter added");
			} else {
				System.out.println("Primary Health Supporter added");
			}
			System.out.println("Redirecting back to the Health Supporter Menu");

		} catch (SQLException e) {
			errorMessage = e.getMessage();
			if (errorMessage.toLowerCase().contains(ErrorMessages.UNIQUE_CONSTRAINT.toString().toLowerCase())) {
				System.out.println("Given health supporter is already added as your primary health supporter");
				System.out.println("You can not add two health supporters with same userName");
				System.out.println("Redirecting back to the Health Supporter Menu");
			} else
				System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
	}

	public static void removeHealthSupporter(Scanner input) {
		try {
			ResultSet rs;
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_HEALTHSUPPORTERS);

			preparedStmt.setString(1, UserProfile.getInstance().getPid());
			rs = preparedStmt.executeQuery();
			int count = 0;
			count = countRows(rs);
			if (count == 0) {
				System.out.println("You do not have any health supporters added into the system");
				System.out.println("Returning back to the main menu");
			} else if (count == 1) {
				System.out.println("You have a primary health supporter added to the system");
				// check if user is having any diagnosed or not
				// if user is diagnosed with a disease, then he is required to
				// have atleast one health supporter
				// hence user can not delete the only health supporter present
				// in the system
				// if user is not diagnosed with any disease, then he can remove
				// the health supporter

				boolean result = checkUserDiagnoses(UserProfile.getInstance().getPid());
				if (result == false) {
					// user is not diagnosed with any disease and hence can
					// delete the health supporter from the system
					System.out.println("Do you want to continue deleting the primary health supporter?");
					System.out.println("Choice: (Y/N)");
					String response = input.next();
					if (response.equals("Y")) {
						deletePrimaryHealthSupporter();
					} else if (response.equals("N")) {
						System.out.println("Returning back to the main menu");
					} else {
						System.out.println("Please enter a valid choice and try again");
						System.out.println("Returning back to the main menu");
					}

				}

				else {
					System.out
							.println("You are diagnosed with a disease and hence required to have a health supporter.");
					System.out.println("Please add a secondary and then try removing the current health supporter");
					System.out.println("Returning back to the main menu");
				}
			} else {
				System.out.println("Please enter which health supporter you want to remove");
				System.out.println("Choice: (primary/secondary)");
				String response = input.next();
				if (response.equals("primary")) {
					// remove primary and make secondary as primary
					deletePrimaryHealthSupporter();
					System.out.println("secondary health supporter marked as primary now");
					System.out.println("Returning back to main menu");
				} else if (response.equals("secondary")) {

					deleteSecondaryHealthSupporter();
					System.out.println("Returning back to main menu");
				}

				else {
					System.out.println("Please enter a valid choice and try again");
					System.out.println("Returning back to main menu");
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}

	}

	public static boolean checkUserDiagnoses(String userName) throws SQLException {
		boolean result = false;
		ResultSet rs2;
		PreparedStatement preparedStmt2 = DatabaseConnection.getConnection()
				.prepareStatement(SqlQueries.USER_DIAGNOSES_VERIFICATION);

		preparedStmt2.setString(1, userName);
		rs2 = preparedStmt2.executeQuery();
		if (rs2.next()) {
			// health supporter exist and can be added to the system
			result = true;
			return result;

		}

		return false;
	}

	public static void deletePrimaryHealthSupporter() {
		try {
			ResultSet rs2;
			PreparedStatement preparedStmt2 = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.DELETE_PRIMARY_HEALTH_SUPPORTER);
			preparedStmt2.setString(1, UserProfile.getInstance().getPid());
			preparedStmt2.setString(2, "Y");
			preparedStmt2.executeUpdate();
			System.out.println("Primary Health Supporter is Deleted");

			ResultSet rs3;
			PreparedStatement preparedStmt3 = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.MAKE_SECONDARY_HEALTH_SUPPORTER_AS_PRIMARY);

			preparedStmt3.setString(1, UserProfile.getInstance().getPid());

			preparedStmt3.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
	}

	public static void deleteSecondaryHealthSupporter() {
		try {
			ResultSet rs;
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.DELETE_SECONDARY_HEALTH_SUPPORTER);

			preparedStmt.setString(1, UserProfile.getInstance().getPid());
			preparedStmt.setString(2, "N");
			preparedStmt.executeUpdate();
			System.out.println("Secondary Health Supporter is Deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
	}

	public static void addHS(Scanner input) {
		try {
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.INSERT_HEALTH_SUPPORTER);

			System.out.println("Enter health supporter ID");
			String HSID = input.next();
			System.out.println("Enter authorization date (YYYY-MM-DD)");
			Date auth_date = Date.valueOf(input.next());
			preparedStmt.setString(1, UserProfile.getInstance().getPid());
			preparedStmt.setString(2, HSID);
			preparedStmt.setDate(3, auth_date);
			preparedStmt.executeUpdate();
			System.out.println("Success: Health Supporter Added Successfully");

		}

		catch (SQLException e) {
			if (e.getMessage() == null) {
				System.out
						.println("Error: Invalid values entered. Can not add health supporter with given information");
			} else if (e.getMessage().contains("ORA-02291")) {
				System.out.println(
						"Error: Health Supporter not registered with the system. Please ask them to sign up first.");
			} else if (e.getMessage().contains("authorization date")) {
				System.out.println("Error: Invalid authorization date. Authorization date can not be in the past");
			} else if (e.getMessage().contains("Patient has already")) {
				System.out.println("Error: Patient has already two health supporters added to the system");
			}

			else if (e.getMessage().contains("own health supporter")) {
				System.out.println("Error: Cannot make patient its own health supporter");
			} else {

				System.out.println("Error: Health Supporter already exist in the system.");
			}

		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
		
		System.out.println("Returning back to main menu");
		homePageHealthSupporters(input);
	}
}
