package dbms.cli;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dbms.connection.DatabaseConnection;

public class HomePage {

	public static void viewPatientsProfile(Scanner input) {
		try {
			// Give options to Pick a Patient
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_PATIENTS_FOR_HEALTHSUPPORTERS);
			preparedStmt.setString(1, UserProfile.getInstance().getPid());
			ResultSet rs = preparedStmt.executeQuery();
			int count = 0;
			List<String> pidList = new ArrayList<String>();
			if (rs.next()) {
				System.out.println("Select a patient from a below list:");
				System.out.println("0. To go back to the previous menu");
				do {
					System.out.println((++count) + ". " + rs.getString("PID"));
					pidList.add(rs.getString("PID"));
				} while (rs.next());
				int choice = input.nextInt();
				if (choice == 0) {
					Login.homePage(input);
				} else {
					if (choice < 0 || choice > pidList.size()) {
						System.out.println("Invalid Choice: Select a valid Menu option");
						viewPatientsProfile(input);
					} else {
						ResultSet rs2;
						PreparedStatement preparedStmt2 = DatabaseConnection.getConnection()
								.prepareStatement(SqlQueries.USER_VERIFY);

						preparedStmt2.setString(1, pidList.get(choice - 1));
						rs2 = preparedStmt2.executeQuery();
						rs2.next();
						System.out.println("1. First Name - " + rs2.getString("FIRST_NAME"));
						System.out.println("2. Last Name - " + rs2.getString("LAST_NAME"));
						System.out.println("3. Gender - " + rs2.getString("GENDER"));
						System.out.println("4. D.O.B. - " + rs2.getDate("DOB"));
						System.out.println("Enter 0 to go back to the previous menu");
						choice = input.nextInt();
						Login.homePage(input);

					}
				}

			} else {
				System.out.println("You are not health supporter of anyone");
				System.out.println("Enter 0 to go back to the previous menu");
				input.nextInt();
				Login.homePage(input);

			}

		} catch (SQLException e) {
			System.out.println("Error Occured :\n" + e.getMessage());
			viewPatientsProfile(input);
		} catch (Exception e) {
			System.out.println("Error Occured :\n" + e.getMessage());
			viewPatientsProfile(input);
		}

	}

	public static void viewEditProfile(Scanner input) {
		String s = "";
		try {
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_DIAGNOSIS);
			String pid = UserProfile.getInstance().getPid();
			preparedStmt.setString(1, pid);
			ResultSet rs = preparedStmt.executeQuery();
			ArrayList<String> hasdiseases = new ArrayList<String>();
			while (rs.next()) {
				hasdiseases.add(rs.getString("D_NAME"));
			}
			if (hasdiseases.isEmpty())
				s = "Well";
			else
				s = "Sick";
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		try {
			while (true) {
				System.out.println("1. First Name - " + UserProfile.getInstance().getFirstName());
				System.out.println("2. Last Name - " + UserProfile.getInstance().getLastName());
				System.out.println("3. Gender - " + UserProfile.getInstance().getGender());
				System.out.println("4. D.O.B. - " + UserProfile.getInstance().getDob());
				System.out.println("5. Patient Category - " + s);
				System.out.println("6. Go Back To Previous Menu");
				System.out.println("Choice(Select Number to Edit) : ");
				int choice = input.nextInt();
				switch (choice) {
				case 1:
					System.out.println("Enter New First Name");
					UserProfile.getInstance().setFirstName(input.next());
					UserProfile.getInstance().updateInDatabase();
					break;
				case 2:
					System.out.println("Enter New Last Name");
					UserProfile.getInstance().setLastName(input.next());
					UserProfile.getInstance().updateInDatabase();
					break;
				case 3:
					System.out.println("Enter New Gender(M/F)");
					String gender = input.next();
					if (gender.compareTo("M") == 0 || gender.compareTo("F") == 0) {
						UserProfile.getInstance().setGender(gender);
						UserProfile.getInstance().updateInDatabase();
					} else {
						System.out.println("Enter correct gender!!!");
					}
					break;
				case 4:
					System.out.println("Enter New D.O.B (YYYY-MM-DD)");
					try {
						UserProfile.getInstance().setDob(Date.valueOf(input.next()));
						UserProfile.getInstance().updateInDatabase();
					} catch (Exception e) {
						System.out.println("Enter date in correct format");
					}
					break;
				case 5:
					System.out.println("Field cannot be editted");
					break;
				case 6:
					Login.homePage(input);

				default:
					System.out.println("Enter correct choice");
				}
			}
		} catch (SQLException e) {
			System.out.println("Error Occured :\n" + e.getMessage());
			viewEditProfile(input);

		} catch (Exception e) {
			System.out.println("Error Occured :\n" + e.getMessage());
			viewEditProfile(input);

		}

	}

}
