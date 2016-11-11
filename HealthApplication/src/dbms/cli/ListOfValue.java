package dbms.cli;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbms.connection.DatabaseConnection;

public class ListOfValue {
	private String lovName;
	private String measureName;
	private int lovValue;
	private String description;

	public String getLovName() {
		return lovName;
	}

	public void setLovName(String lovName) {
		this.lovName = lovName;
	}

	public String getMeasureName() {
		return measureName;
	}

	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}

	public int getLovValue() {
		return lovValue;
	}

	public void setLovValue(int lovValue) {
		this.lovValue = lovValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static List<ListOfValue> getListOfValues(String measureName) throws SQLException {
		List<ListOfValue> lovList = new ArrayList<ListOfValue>();
		PreparedStatement preparedStmt = DatabaseConnection.getConnection()
				.prepareStatement(SqlQueries.GET_LIST_OF_VALUES);
		preparedStmt.setString(1, measureName);
		ResultSet rs = preparedStmt.executeQuery();
		while (rs.next()) {
			ListOfValue temp = new ListOfValue();
			temp.setDescription(rs.getString("DESCRIPTION"));
			temp.setMeasureName(rs.getString("MEASURE_NAME"));
			temp.setLovName(rs.getString("LOV_NAME"));
			temp.setLovValue(rs.getInt("LOV_VALUE"));
			lovList.add(temp);

		}
		return lovList;
	}

	public static String convertToLov(String measureName, int value) throws SQLException {
		List<ListOfValue> lovList = getListOfValues(measureName);
		for (ListOfValue temp : lovList) {
			if (temp.getLovValue() == value) {
				return temp.getLovName();
			}
		}
		return String.valueOf(value);
	}

}
