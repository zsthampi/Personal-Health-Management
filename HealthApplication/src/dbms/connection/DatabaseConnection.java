package dbms.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseConnection {

	static final String jdbcURL = "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	static Connection conn = null;
	static Statement stmt = null;

	public static Connection getConnection() {
		if (conn == null) {

			try {

				Class.forName("oracle.jdbc.driver.OracleDriver");
				Scanner scan = new Scanner(System.in);
				System.out.println("Enter Database UserName - UnityId");
				String user = scan.next();
				System.out.println("Enter Database Password - StudentId");
				String passwd = scan.next();

				conn = DriverManager.getConnection(jdbcURL, user, passwd);

				stmt = conn.createStatement();
				System.out.println("~~~~DATABASE CONNECTION SUCCESSFUL~~~");
				System.out.println("\n\n");

			} catch (Throwable oops) {
				System.out.println(oops.getMessage());
				getConnection();
			}

		}
		return conn;
	}

	public static Statement getStatement() {
		if (stmt == null) {
			getConnection();
		}
		return stmt;
	}

	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Throwable whatever) {
			}
		}
	}

	public static void close(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (Throwable whatever) {
			}
		}
	}

	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Throwable whatever) {
			}
		}
	}

	public static void closeStatement() {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Throwable whatever) {
			}
		}
	}

	static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Throwable whatever) {
			}
		}
	}
}
