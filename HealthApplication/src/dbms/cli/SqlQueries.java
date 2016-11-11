package dbms.cli;

public final class SqlQueries {

	static final String LOGIN_VERIFY = "Select * from people where pid = ? and password = ?";

	static final String UPDATE_PERSON = "Update PEOPLE SET " + "First_Name=?, Last_Name=?,Gender=?,DOB=?, Address=? "
			+ "where pid=?";
	static final String INSERT_PERSON = "insert into PEOPLE(PID, DOB, Address, Gender, First_Name, Last_Name,Password) "
			+ "values(?,?,?, ?,?, ?, ?)";

	static final String VALID_RECOMMENDATIONS = "select RM.RECOMMENDATION_NAME from RECOMMENDATION_MEASURE RM"
			+ " where RM.RECOMMENDATION_NAME in (select REC.RECOMMENDATION_NAME from RECOMMENDATION REC where REC.IS_GENERAL='Y') "
			+ "UNION select PSR.RECOMMENDATION_NAME from PEOPLE_SPECIFIC_RECO PSR where PSR.PID= ? "
			+ "UNION select DSR.RECOMMENDATION_NAME from DISEASE_SPECIFIC_RECO DSR "
			+ "where DSR.D_NAME in (select DIAGNOSED_WITH.D_NAME from DIAGNOSED_WITH where DIAGNOSED_WITH.PID= ? )";

	static final String PERSON_RECORDS = "select * from record where pid=? order by CREATED_DATE DESC";

	static final String GET_RECORDS = "select * from view_record where pid in (select ? from dual union select pid from PEOPLE_HEALTH_SUPPORTER where hid=? and TRUNC(sysdate)>=TRUNC(authorization_date)) order by pid,record_id desc";
	static final String INSERT_RECORD = "insert into RECORD(PID, RECOMMENDATION_NAME, MEASURE_NAME, VALUE, RECORDED_DATE, CREATED_DATE, CREATED_BY) "
			+ "VALUES(?, ?, ?, ?, ?, ?, ?)";
	static final String CHECK_IF_LIST_OF_VALUE = "select COUNT(*) from list_of_value where measure_name=?";
	static final String GET_LOV_VALUE = "select lov_value from list_of_value where measure_name=? and lov_name=?";
	
	static final String VALID_MEASURES = "select * from RECOMMENDATION_MEASURE,MEASURE where MEASURE.MEASURE_NAME=RECOMMENDATION_MEASURE.MEASURE_NAME AND RECOMMENDATION_MEASURE.RECOMMENDATION_NAME=?";

	static final String GET_ALERTS = "select * from alert left outer join people_recommendation on alert.pid=people_recommendation.pid and alert.recommendation_name=people_recommendation.recommendation_name and alert.measure_name=people_recommendation.measure_name where alert.pid in (select ? from dual union select pid from PEOPLE_HEALTH_SUPPORTER where hid=? and TRUNC(sysdate)>=TRUNC(authorization_date)) and is_active='Y' order by alert.pid,alert_id desc";
	static final String CLEAR_ALERT = "update alert set is_active='N',cleared_by=?,cleared_date=sysdate where alert_id=? and pid=?";
	static final String VIEW_ALERT = "update alert set is_viewed='Y' where alert_id=? and pid=?";
	
	static final String GET_HEALTHSUPPORTERS = "Select * from People_Health_Supporter where pid = ?";
	
	static final String USER_VERIFY = 	"Select * from people where pid = ?";
	
	static final String INSERT_HEALTH_SUPPORTER = "insert into People_Health_Supporter(PID,HID,AUTHORIZATION_DATE)" 
													+ "values(?,?,?)";
	
	static final String DELETE_PRIMARY_HEALTH_SUPPORTER = "delete from People_Health_Supporter where pid = ? and is_primary = ?";
	
	
	static final String MAKE_SECONDARY_HEALTH_SUPPORTER_AS_PRIMARY = "update People_Health_Supporter set is_primary='Y' where pid=?";
	
	static final String DELETE_SECONDARY_HEALTH_SUPPORTER = "delete from People_Health_Supporter where pid = ? and is_primary = ?";
	
	static final String USER_DIAGNOSES_VERIFICATION = 	"Select * from DIAGNOSED_WITH where pid = ?";

	static final String RUN_GENERATE_ALERTS = "begin generate_alerts(); end;";

	static final String GET_DIAGNOSIS = "select * from DIAGNOSED_WITH where pid = ? ";
	static final String GET_NOTACTIVE_DIAGNOSIS = "select * from DIAGNOSED_WITH where pid = ? and IS_ACTIVE='N'";
	static final String GET_DIAGNOSIS_BY_DISEASE = "select * from DIAGNOSED_WITH where pid = ? and D_NAME=?";
	static final String UPDATE_DIAGNOSED_DATE = "update DIAGNOSED_WITH set DIAGNOSED_DATE = ? where pid =? and D_NAME=?";
	static final String UPDATE_DIAGNOSED_ACTIVITY = "update DIAGNOSED_WITH set IS_ACTIVE = ? where pid =? and D_NAME=?";
	static final String INSERT_DIAGNOSIS = "insert into Diagnosed_With(PID,D_Name,DIAGNOSED_DATE,IS_ACTIVE) values(?,?,?,'Y')";
	static final String GET_DISEASES = "select D_NAME from DISEASE";
	static final String GET_ACTIVITY_OF_DISEASE = "select is_active from DIAGNOSED_WITH where pid = ? and D_name = ?";
	static final String GET_PATIENTS_FOR_HEALTHSUPPORTERS = "select pep.pid, first_name from people pep, PEOPLE_HEALTH_SUPPORTER phs where pep.pid =PHS.PID and HID = ? and TRUNC(authorization_date)<=TRUNC(sysdate)";
	static final String DELETE_DIAGNOSES = "DELETE FROM DIAGNOSED_WITH where pid =? and D_NAME=?";
	
	static final String Get_HealthSupporters = "Select * from People_Health_Supporter where pid = ?";

	static final String GET_LIST_OF_VALUES = "select * from LIST_OF_VALUE where MEASURE_NAME=?";
	
	static final String INSERT_SPEC_RECO = "insert into PEOPLE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, PID, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD) values(?, ?, ?, ?, ?, ?,1, 1)";
	static final String VIEW_RECOMMENDATION = "Select * from PEOPLE_SPECIFIC_RECO where PID =? ";


}
