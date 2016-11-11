package dbms.cli;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import dbms.connection.DatabaseConnection;

public class Diagnosis {

	static int count;
	private static Scanner scan;
	static ArrayList<String> hasdiseases;
	static ArrayList<String> hasnotactivediseases;

	public static void diagnosisHome(Scanner input) {
		scan = input;
		while (true) {
			System.out.println("Diagnosis Menu");
			System.out.println("1. View");
			System.out.println("2. Edit");
			System.out.println("3. Enter diagnosis for new disease");
			System.out.println("4. Delete");
			System.out.println("5. Back");
			System.out.print("Choice : ");
			int choice = scan.nextInt();
			switch (choice) {
			case 1:
				viewDiagnosis();
				break;
			case 2:
				editDiagnosis();
				break;
			case 3:
				newDiagnosis();
				break;
			case 4:
				deleteDiagnosis();
				break;
			case 5:
				Login.homePage(scan);
				break;
			default:
				System.out.println("Enter correct choice!");
			}
		}
	}

	public static void viewDiagnosis() {

		try {
			ResultSet rs;
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_DIAGNOSIS);
			String pid = find_pid(); // pid from user
			if (pid == null)
				System.out.println("Choose a correct pid");
			else {
				preparedStmt.setString(1, pid);
				rs = preparedStmt.executeQuery();
				hasdiseases = new ArrayList<String>();
				count = 0;
				if (rs != null) {
					if (!rs.next()) {
						System.out.println("Patient " + pid + " is well");
					} else {
						System.out.println("The patient is diagnosed with the following diseases:");
						do {
							System.out.println((++count) + ". " + rs.getString("PID") + " " + rs.getString("D_NAME")
									+ " " + rs.getDate("DIAGNOSED_DATE") + " " + rs.getString("IS_ACTIVE"));
							hasdiseases.add(rs.getString("D_NAME"));
						} while (rs.next());
					}
				} else {
					System.out.println("Patient " + pid + " is well");
				}
			}
		} catch (SQLException e) {
			System.out.println("Cannot View Diagnosis");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
	}

	public static void find_dis_for_patients(String pid) {
		try {
			hasdiseases = new ArrayList<String>();
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_DIAGNOSIS);
			ResultSet rs;
			preparedStmt.setString(1, pid);
			rs = preparedStmt.executeQuery();
			count = 0;
			if (rs != null) {
				if (!rs.next()) {
					System.out.println("Patient " + pid + " is well");
				} else {
					System.out.println("The patient is diagnosed with the following diseases:");
					System.out.println(" ID | DISEASE NAME | DIAGNOSED DATE | IS ACTIVE(Y/N) ");
					do {
						System.out.println((++count) + ". " + rs.getString("PID") + " " + rs.getString("D_NAME") + " "
								+ rs.getDate("DIAGNOSED_DATE") + " " + rs.getString("IS_ACTIVE"));
						hasdiseases.add(rs.getString("D_NAME"));
					} while (rs.next());
				}
			}

		} catch (SQLException e) {
			System.out.println("Error in diagnosis records");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
	}

	public static void find_notact_dis_for_patients(String pid) {
		try {
			hasnotactivediseases = new ArrayList<String>();
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_NOTACTIVE_DIAGNOSIS);
			ResultSet rs;
			preparedStmt.setString(1, pid);
			rs = preparedStmt.executeQuery();
			count = 0;
			if (rs != null) {
				if (!rs.next()) {
					System.out.println("Patient " + pid + " has not inactive records.");
				} else {
					System.out.println("The patient is diagnosed with the following diseases:");
					System.out.println(" ID | DISEASE NAME | DIAGNOSED DATE | IS ACTIVE(Y/N) ");
					do {
						if (rs.getString("IS_ACTIVE").compareTo("N") == 0) {
							System.out.println((++count) + ". " + rs.getString("PID") + " " + rs.getString("D_NAME")
									+ " " + rs.getDate("DIAGNOSED_DATE") + " " + rs.getString("IS_ACTIVE"));
							hasnotactivediseases.add(rs.getString("D_NAME"));
						}
					} while (rs.next());
				}
			}

		} catch (SQLException e) {
			System.out.println("Error in diagnosis records");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
	}

	public static void editDiagnosis() {
		// viewDiagnosis();
		String pid = find_pid();
		find_dis_for_patients(pid);
		if (pid != null && !hasdiseases.isEmpty()) {

			System.out.print("Enter record number to update: ");
			int record = scan.nextInt();
			if (record > hasdiseases.size() || record <= 0) {
				System.out.println("Record number chosen does not exist");
			} else if (hasdiseases != null && !hasdiseases.isEmpty()) {
				System.out.println("Edit Diagnosis Menu");
				System.out.println("1. Change Diagnosis Date");
				System.out.println("2. Change Diagnosis Activity");
				System.out.println("3. Back");
				System.out.print("Choice : ");
				int choice = scan.nextInt();

				switch (choice) {
				case 1:
					try {
						PreparedStatement preparedStmt = DatabaseConnection.getConnection()
								.prepareStatement(SqlQueries.UPDATE_DIAGNOSED_DATE);
						System.out.print("Enter new diagnosis date: ");
						Date diagdate = Date.valueOf(scan.next());
						preparedStmt.setDate(1, diagdate);
						preparedStmt.setString(2, pid);
						preparedStmt.setString(3, hasdiseases.get(record - 1));
						int rows = preparedStmt.executeUpdate();
						System.out.println(rows + " rows updated");
					} catch (SQLException e) {
						System.out.println("Cannot edit Diagnosis for diagnosis date");
						System.out.println(e.getMessage());
					} catch (Exception e) {
						System.out.println("Enter correct value");
					}
					break;
				case 2:
					try {
						PreparedStatement preparedStmt = DatabaseConnection.getConnection()
								.prepareStatement(SqlQueries.UPDATE_DIAGNOSED_ACTIVITY);
						System.out.print("Enter new activity flag(Y/N): ");
						String activity = scan.next();
						if (activity.compareTo("Y") == 0 || activity.compareTo("N") == 0) {// get_new_activity(pid,
																							// hasdiseases.get(record-1));
							preparedStmt.setString(1, activity);
							preparedStmt.setString(2, pid);
							preparedStmt.setString(3, hasdiseases.get(record - 1));
							int rows = preparedStmt.executeUpdate();
							System.out.println(rows + " rows updated");
						} else {
							System.out.println("Incorrect activity value");
						}
					} catch (SQLException e) {
						System.out.println("Cannot Update Diagnosis on activity");
						System.out.println(e.getMessage());
					} catch (Exception e) {
						System.out.println("Enter correct value");
					}
					break;
				case 3:
					diagnosisHome(scan);
					break;
				default:
					System.out.println("Enter correct choice!");
				}
			} else
				System.out.println("No record to edit");
		} else if (pid == null) {
			System.out.println("Choose a correct pid");
		} else {
			System.out.println("No record to edit.");
		}
	}

	public static void newDiagnosis() {
		try {
			String pid = find_pid();
			if (pid == null)
				System.out.println("Choose a correct pid");
			else if (pid == UserProfile.getInstance().getPid()) {
				PreparedStatement preparedStmt;
				/*
				 * PreparedStatement preparedStmt =
				 * DatabaseConnection.getConnection().prepareStatement(
				 * SqlQueries.GET_HEALTHSUPPORTERS); preparedStmt.setString(1,
				 * UserProfile.getInstance().getPid()); rs =
				 * preparedStmt.executeQuery();
				 * 
				 * if(!rs.next()){ HealthSupporters.addSupporters(scan); }
				 */
				preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.INSERT_DIAGNOSIS);
				String dis = check_disease();
				System.out.print("Enter new diagnosis date(YYYY-MM-DD): ");
				Date diagdate = Date.valueOf(scan.next());
				preparedStmt.setString(1, pid);
				preparedStmt.setString(2, dis);
				preparedStmt.setDate(3, diagdate);
				int rows = preparedStmt.executeUpdate();
				System.out.println(rows + " rows updated");

			} else {
				PreparedStatement preparedStmt = DatabaseConnection.getConnection()
						.prepareStatement(SqlQueries.INSERT_DIAGNOSIS);
				String dis = check_disease();
				System.out.print("Enter new diagnosis date(YYYY-MM-DD): ");
				Date diagdate = Date.valueOf(scan.next());
				preparedStmt.setString(1, pid);
				preparedStmt.setString(2, dis);
				preparedStmt.setDate(3, diagdate);
				int rows = preparedStmt.executeUpdate();
				System.out.println(rows + " rows updated");
			}
		} catch (SQLException e) {
			System.out.println("Cannot Insert Diagnosis");
			if (e.getErrorCode() == 20001) {
				System.out.println("You do not have any health supporters added, Redirecting to Health Supporter Menu");
				HealthSupporters.addHS(scan);
			}

			else
				System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Check values before inserting");
		}
	}

	public static void deleteDiagnosis() {
		// viewDiagnosis();
		String pid = find_pid();
		find_notact_dis_for_patients(pid);
		if (pid != null && !hasnotactivediseases.isEmpty()) {

			System.out.print("Enter record number to delete: ");
			int record = scan.nextInt();
			if (record > hasnotactivediseases.size() || record <= 0) {
				System.out.println("Record number chosen does not exist");
			} else if (hasnotactivediseases != null && !hasnotactivediseases.isEmpty()) {
				try {
					PreparedStatement preparedStmt = DatabaseConnection.getConnection()
							.prepareStatement(SqlQueries.DELETE_DIAGNOSES);
					preparedStmt.setString(1, pid);
					preparedStmt.setString(2, hasnotactivediseases.get(record - 1));
					int rows = preparedStmt.executeUpdate();
					System.out.println(rows + " rows deleted");
				} catch (SQLException e) {
					System.out.println("Cannot delete Diagnosis");
					System.out.println(e.getMessage());
				} catch (Exception e) {
					System.out.println("Enter correct value");
				}
			} else
				System.out.println(
						"No record to delete. If record is present, please change the active flag to N before attempting to delete.");
		} else if (pid == null) {
			System.out.println("Choose a correct pid.");
		} else {
			System.out.println(
					"No record to delete. If record is present, please change the active flag to N before attempting to delete.");
		}
	}

	public static String check_disease() {
		ArrayList<String> diseases = new ArrayList<String>();
		count = 0;
		try {
			ResultSet rs;
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_DISEASES);
			rs = preparedStmt.executeQuery();
			if (rs != null) {
				count = 0;
				while (rs.next()) {
					System.out.println((++count) + ". " + rs.getString("D_NAME"));
					diseases.add(rs.getString("D_NAME"));
				}
				System.out.print("Choose Disease id to record: ");
				int id = scan.nextInt();
				if (id <= diseases.size())
					return diseases.get(id - 1);
				else {
					System.out.println("Enter a correct disease.");
					return null;
				}
			} else {
				System.out.println("There is no disease in the database table");
			}
		} catch (SQLException e) {
			System.out.println("Cannot View Disease");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
		return null;
	}

	public static String find_pid() {
		ResultSet rs;
		ArrayList<String> people = new ArrayList<String>();
		int id = 0;
		try {
			PreparedStatement preparedStmt = DatabaseConnection.getConnection()
					.prepareStatement(SqlQueries.GET_PATIENTS_FOR_HEALTHSUPPORTERS);
			preparedStmt.setString(1, UserProfile.getInstance().getPid());
			rs = preparedStmt.executeQuery();
			count = 0;
			if (rs != null) {
				while (rs.next()) {
					System.out.println((++count) + ". " + rs.getString(2));
					people.add(rs.getString(1));
				}
			}
			System.out.println((++count) + ". Self");

			System.out.print("Choose pid or self: ");
			id = scan.nextInt();
			if (id == count) {
				return UserProfile.getInstance().getPid();
			} else if (id < count && id > 0) {
				return people.get(id - 1);
			}
		} catch (SQLException e) {
			System.out.println("Cannot View pid");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("Enter correct value");
			System.out.println(e.getMessage());
		}
		return null;
	}
}
