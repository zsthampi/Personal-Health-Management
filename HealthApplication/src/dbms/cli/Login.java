package dbms.cli;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import dbms.connection.DatabaseConnection;

public class Login {

	public static String verifyLogin(Scanner input) {
		do {
			try {

				ResultSet rs;

				PreparedStatement preparedStmt = DatabaseConnection.getConnection()
						.prepareStatement(SqlQueries.LOGIN_VERIFY);
				System.out.println("Enter Your UserName:");
				String userName = input.next();
				System.out.println("Enter Your Password:");
				String password = input.next();
				preparedStmt.setString(1, userName);
				preparedStmt.setString(2, password);
				rs = preparedStmt.executeQuery();
				if (rs.next()) {
					System.out.println("Login Successful!");
					// Setting up the User Profile
					UserProfile.getInstance().setupProfile(rs);
					DatabaseConnection.close(preparedStmt);
					return userName;
				} else {
					System.out.println("Incorrect Credentials Please Try Again:");
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(Exception e){
				System.out.println("Enter correct value");
				System.out.println(e.getMessage());
			}
		} while (true);

	}

	/**
	 * Function to display options for Login Page/First Page Of the Application
	 **/
	public static void startPage(Scanner input) {
		System.out.println("Welcome to NC Health Application");

		while (true) {
			System.out.println("1. Create Account");
			System.out.println("2. Login");
			System.out.println("3. Exit");
			System.out.print("Choice : ");
			int choice = input.nextInt();
			switch (choice) {
			case 1:
				createAccount(input);
				homePage(input);
				break;
			case 2:
				verifyLogin(input);
				homePage(input);
				break;
			case 3:
				// closing the global connection and statement
				DatabaseConnection.closeConnection();
				DatabaseConnection.closeStatement();
				input.close();
				System.exit(0);
			default:
				System.out.println("Enter correct choice");
			}
		}

	}

	/**
	 * Function to display options for home page
	 **/
	public static void homePage(Scanner input) {

		System.out.println("Hello " + UserProfile.getInstance().getName());

		while (true) {
			System.out.println("1. Your Profile");
			System.out.println("2. Patients Profile");
			System.out.println("3. Diagnoses");
			System.out.println("4. Records");
			System.out.println("5. Alerts");
			System.out.println("6. Health Supporters");
			System.out.println("7. View/Add Specific Recommendation");
			System.out.println("8. Logout");
			System.out.print("Choice : ");
			int choice = input.nextInt();
			switch (choice) {
			case 1:
				HomePage.viewEditProfile(input);
				break;
			case 2:
				HomePage.viewPatientsProfile(input);
				break;
			case 3:
				Diagnosis.diagnosisHome(input);
				break;
			case 4:
				Records.viewRecordsPage(input);
				break;
			case 5:
				Alerts.viewAlerts(input);
				break;
			case 6:
				HealthSupporters.homePageHealthSupporters(input);
				break;
			case 7:
				AddSpecificRecommendation.viewAddSpecificRecommendation(input);
				break;
			case 8:
				UserProfile.deleteInstance();
				startPage(input);
				break;
			default:
				System.out.println("Enter correct choice");
			}
		}

	}

	public static void createAccount(Scanner input) {
		try {
		System.out.println("Enter User Name");
		UserProfile.getInstance().setPid(input.next());
		System.out.println("Enter Password");
		UserProfile.getInstance().setPassword(input.next());
		System.out.println("Enter First Name");
		UserProfile.getInstance().setFirstName(input.next());
		System.out.println("Enter Last Name");
		UserProfile.getInstance().setLastName(input.next());
		System.out.println("Enter Gender(M/F)");
		UserProfile.getInstance().setGender(input.next());
		System.out.println("Enter D.O.B (YYYY-MM-DD)");
		UserProfile.getInstance().setDob(Date.valueOf(input.next()));
		System.out.println("Enter Address");
		input.nextLine();
		UserProfile.getInstance().setAddress(input.nextLine());
		
			UserProfile.getInstance().insertInDatabase();
		} catch (Exception e) {
			System.out.println("Error in Account Creation. Please Try Again\n"+e.getMessage());
			startPage(input);
		}

	}

}
