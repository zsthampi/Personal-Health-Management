package dbms.cli;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbms.connection.DatabaseConnection;

public class UserProfile {
	private String pid;
	private Date dob;
	private String address;
	private String gender;
	private String firstName;
	private String lastName;
	private String password;
	private static UserProfile userProfile;

	public static UserProfile getInstance() {
		if (userProfile == null) {
			userProfile = new UserProfile();
		}
		return userProfile;
	}

	public static UserProfile getNewInstance() {
		userProfile = new UserProfile();
		return userProfile;
	}

	public static void deleteInstance() {
		userProfile = null;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getName() {
		return getFirstName() + " " + getLastName();
	}

	public void setupProfile(ResultSet result) {
		try {
			setAddress(result.getString("ADDRESS"));
			setDob(result.getDate("DOB"));
			setFirstName(result.getString("FIRST_NAME"));
			setGender(result.getString("GENDER"));
			setLastName(result.getString("LAST_NAME"));
			setPid(result.getString("PID"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	public void updateInDatabase() throws Exception {

		PreparedStatement preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.UPDATE_PERSON);
		preparedStmt.setString(1, firstName);
		preparedStmt.setString(2, lastName);
		preparedStmt.setString(3, gender);
		preparedStmt.setDate(4, dob);
		preparedStmt.setString(5, address);
		preparedStmt.setString(6, pid);
		preparedStmt.executeUpdate();

	}

	public void insertInDatabase() throws SQLException {
		PreparedStatement preparedStmt = DatabaseConnection.getConnection().prepareStatement(SqlQueries.INSERT_PERSON);
		preparedStmt.setString(1, pid);
		preparedStmt.setDate(2, dob);
		preparedStmt.setString(3, address);
		preparedStmt.setString(4, gender);
		preparedStmt.setString(5, firstName);
		preparedStmt.setString(6, lastName);
		preparedStmt.setString(7, password);
		preparedStmt.executeUpdate();

	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
