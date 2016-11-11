package dbms.cli;

import java.sql.SQLException;
import java.util.Scanner;

import dbms.connection.DatabaseConnection;

public class HealthApplication {

	public static void main(String[] args) throws SQLException {

		Scanner input = new Scanner(System.in);
		DatabaseConnection.getConnection();
		Login.startPage(input);

	}

}
