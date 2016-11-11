---------------------------DDL----------------------------

drop view people_recommendation;
drop view view_record;
drop table people cascade constraints;
drop table People_Health_Supporter cascade constraints;
drop table Disease cascade constraints;
drop table Recommendation cascade constraints;
drop table Diagnosed_With cascade constraints;
drop table Measure cascade constraints;
drop table Disease_Specific_Reco cascade constraints;
drop table People_Specific_Reco cascade constraints;
drop table List_Of_Value cascade constraints;
drop table record cascade constraints;
drop table recommendation_measure cascade constraints;
drop table alert cascade constraints;

create table PEOPLE(
PID	VARCHAR2(60) PRIMARY KEY,	
DOB	Date	NOT NULL,
Address	VARCHAR2(120)	NOT NULL,
Gender	Char(1)	NOT NULL CHECK(Gender in ('F','M')), 
First_Name	VARCHAR2(60)	NOT NULL,
Last_Name	VARCHAR2(60)	NOT NULL,
Password	VARCHAR(60)	NOT NULL
);


create table People_Health_Supporter(
PID	VARCHAR2(60),
HID	VARCHAR2(60)
CONSTRAINT people_health_sup_hid_fk REFERENCES people(pid),	
IS_PRIMARY	CHAR(1)	NOT NULL CHECK (IS_PRIMARY in('N','Y')),
Authorization_Date	Date	NOT NULL,
Constraint people_heath_sup_pk primary key(pid,hid)
);


create table Disease(
D_NAME	VARCHAR2(60)	PRIMARY KEY,
Description	VARCHAR2(500)	
);


Create table Recommendation(
Recommendation_Name	Varchar(60)	PRIMARY KEY,
Description	Varchar(500),	
Is_General	Char(1)	NOT NULL CHECK (Is_general in ('N','Y'))
);


Create table Diagnosed_With(
PID	VARCHAR(60) constraint Diag_With_People_fk REFERENCES People(PID),
D_NAME	VARCHAR(60)	constraint Diag_With_Disease_fk REFERENCES Disease(D_Name),
DIAGNOSED_DATE	Date	NOT NULL,	
IS_ACTIVE	CHAR(1)	NOT NULL CHECK(IS_ACTIVE in ('N','Y')),
CONSTRAINT pk_Diag_with PRIMARY KEY (PID,D_NAME)
);


create table Measure(
Measure_Name varchar2(60) primary key,
Unit varchar2(60),
Upper_limit number,
Lower_limit number,
Frequency number,
Data_type varchar(60) Not null,
Number_of_Observations number,
Threshold number(4,2)
);

create table recommendation_measure (
recommendation_name varchar2(60),
measure_name varchar2(60),
constraint reco_measure_pk primary key(recommendation_name,measure_name),
constraint reco_measure_reco_fk foreign key(recommendation_name) references recommendation,
constraint reco_measure_measure_fk foreign key(measure_name) references measure
);

create table Disease_Specific_Reco(
Recommendation_Name varchar2(60),
Measure_Name varchar2(60),
D_NAME varchar2(60),
Upper_limit number,
Lower_limit number,
Frequency number,
Number_of_Observations number,
Threshold number(4,2),
CONSTRAINT pk_dis_spec PRIMARY KEY (Recommendation_Name,Measure_Name,D_NAME),
constraint dis_reco_measure_fk foreign key(recommendation_name,measure_name) references recommendation_measure,
CONSTRAINT fk_d_name_dsr FOREIGN KEY (D_NAME) REFERENCES Disease(D_Name)
);

create table People_Specific_Reco(
Recommendation_Name varchar2(60),
Measure_Name varchar2(60),
PID varchar2(60),
Upper_limit number,
Lower_limit number,
Frequency number,
Number_of_Observations number,
Threshold number(4,2),
CONSTRAINT pk_people_spec PRIMARY KEY (Recommendation_Name,Measure_Name,PID),
constraint ppl_reco_measure_fk foreign key(recommendation_name,measure_name) references recommendation_measure,
CONSTRAINT fk_pid_psr FOREIGN KEY (PID) REFERENCES People(PID)
);

create table List_Of_Value(
Lov_name varchar2(60),
Measure_Name varchar2(60),
Lov_value number not null,
Description varchar2(500),
CONSTRAINT pk_lov PRIMARY KEY (Lov_name, Measure_Name),
CONSTRAINT fk_meas_name_lov FOREIGN KEY (Measure_Name) REFERENCES Measure(Measure_Name)
);

create table record (
record_id number(10),
pid varchar2(60),
recommendation_name varchar2(60) not null,
measure_name varchar2(60) not null,
value varchar2(60),
recorded_date date not null,
created_date date not null,
created_by varchar2(60) not null,
constraint record_pk primary key(record_id,pid),
constraint record_reco_measure_fk foreign key(recommendation_name,measure_name) references recommendation_measure,
constraint record_people_fk foreign key(created_by) references people
);

create table alert (
alert_id number(10),
pid varchar2(60),
type varchar2(60) not null check(type in ('Low Activity','Outside Limit')),
recommendation_name varchar2(60) not null,
measure_name varchar2(60) not null,
record_id number(10),
is_active char(1) not null check(is_active in ('Y','N')),
is_viewed char(1) not null check(is_viewed in ('Y','N')),
cleared_date date,
cleared_by varchar2(60),
constraint alert_pk primary key(alert_id,pid),
constraint alert_record_fk foreign key(record_id, pid) references record(record_id, pid),
constraint alert_pid_fk foreign key(pid) references people,
constraint alert_reco_measure_fk foreign key(recommendation_name,measure_name) references recommendation_measure,
constraint alert_people_fk foreign key(cleared_by) references people,
constraint cannot_clear_LA_alert CHECK(not(pid=cleared_by and type='Low Activity')),
constraint clear_only_after_view CHECK(not(is_active='N' and is_viewed='N' and cleared_by is not null))
);

create view people_recommendation (PID,RECOMMENDATION_NAME,MEASURE_NAME,UPPER_LIMIT,LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD) as
select PID,RECOMMENDATION_NAME,MEASURE_NAME,UPPER_LIMIT,LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD from people_specific_reco
union
select PID,RECOMMENDATION_NAME,MEASURE_NAME,UPPER_LIMIT,LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD from DIAGNOSED_WITH join DISEASE_SPECIFIC_RECO on DIAGNOSED_WITH.D_NAME=DISEASE_SPECIFIC_RECO.D_NAME where (PID,RECOMMENDATION_NAME,MEASURE_NAME) not in (select PID,RECOMMENDATION_NAME,MEASURE_NAME from people_specific_reco)
union
select PEOPLE.PID,RECOMMENDATION.RECOMMENDATION_NAME,MEASURE.MEASURE_NAME,UPPER_LIMIT,LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD from 
PEOPLE, RECOMMENDATION join RECOMMENDATION_MEASURE on RECOMMENDATION.RECOMMENDATION_NAME=RECOMMENDATION_MEASURE.RECOMMENDATION_NAME join MEASURE on RECOMMENDATION_MEASURE.MEASURE_NAME=MEASURE.MEASURE_NAME 
where is_general='Y' and (PEOPLE.PID,RECOMMENDATION.RECOMMENDATION_NAME,MEASURE.MEASURE_NAME) not in (select PID,RECOMMENDATION_NAME,MEASURE_NAME from people_specific_reco union select PID,RECOMMENDATION_NAME,MEASURE_NAME from DIAGNOSED_WITH join DISEASE_SPECIFIC_RECO on DIAGNOSED_WITH.D_NAME=DISEASE_SPECIFIC_RECO.D_NAME where (PID,RECOMMENDATION_NAME,MEASURE_NAME) not in (select PID,RECOMMENDATION_NAME,MEASURE_NAME from people_specific_reco));

create view view_record (record_id,pid,recommendation_name,measure_name,value,recorded_date,created_date,created_by) as 
select record_id,pid,recommendation_name,measure_name,value,recorded_date,created_date,created_by from record where measure_name not in (select distinct measure_name from list_of_value)
union
select record_id,pid,recommendation_name,record.measure_name,lov_name,recorded_date,created_date,created_by from record join list_of_value on record.measure_name=list_of_value.measure_name and record.value=list_of_value.lov_value;


----------------------------------------Triggers--------------------------


create or replace trigger create_alert_id 
before insert 
on alert
for each row 
declare 
  alert_id number(10);
  max_alert_id number(10);
begin
  alert_id := 1;
  select MAX(alert_id) into max_alert_id from alert where pid=:NEW.pid;
  if max_alert_id is not null then
    alert_id := max_alert_id + 1;
  end if;
  :NEW.alert_id := alert_id;
end;
/

create or replace trigger create_record_id 
before insert 
on record
for each row 
declare 
  record_id number(10);
  max_record_id number(10);
begin
  record_id := 1;
  select MAX(record_id) into max_record_id from record where pid=:NEW.pid;
  if max_record_id is not null then
    record_id := max_record_id + 1;
  end if;
  :NEW.record_id := record_id;
end;
/

create or replace trigger authorize_to_clear_alert
before insert or update
on alert
for each row
declare
  var_pid varchar2(60);
  var_hid varchar2(60);
  hid_count number;
begin
  var_pid := :NEW.pid;
  if (:NEW.cleared_by is not null and :NEW.is_active='N') then 
    var_hid := :NEW.cleared_by;
    if (:NEW.is_viewed='N') then
      raise_application_error(-20001, 'Alerts can be cleared only after viewing them.');
    end if;
    select COUNT(*) into hid_count from people_health_supporter where pid=var_pid and hid=var_hid and TRUNC(authorization_date)<=TRUNC(sysdate);
    if (:NEW.type='Low Activity') then
      if (hid_count=0) then
        raise_application_error(-20001, 'Only Health Supporters can clear alerts for Low Activity.');
      end if;
    else 
      if (hid_count=0 and var_pid!=var_hid) then
        raise_application_error(-20001, 'Only Patient or Health Supporters can clear Outside Limit alerts.');
      end if;
    end if;
  end if;
end;
/

create or replace trigger verify_record_values
before insert or update
on record
for each row
declare
  var_pid varchar2(60);
  var_hid varchar2(60);
  var_recommendation_name varchar2(60);
  var_measure_name varchar2(60);
  query_count number;
  new_value varchar2(60);
begin
  -- :NEW.created_date := sysdate;
  var_pid := :NEW.pid;
  var_hid := :NEW.created_by;
  var_recommendation_name := :NEW.recommendation_name;
  var_measure_name := :NEW.measure_name;  
  select COUNT(*) into query_count from people_health_supporter where pid=var_pid and hid=var_hid and TRUNC(authorization_date)<=TRUNC(sysdate);
  if (query_count=0 and var_pid!=var_hid) then
    raise_application_error(-20001, 'Only Patient or Health Supporters can enter records.');
  end if;
  select COUNT(*) into query_count from people_recommendation where pid=var_pid and recommendation_name=var_recommendation_name and measure_name=var_measure_name;
  if (query_count=0) then
    raise_application_error(-20001, 'Can only add records for recommendations added for the patient.');
  end if;
  if (TRUNC(:NEW.recorded_date) > TRUNC(:NEW.created_date)) then
    raise_application_error(-20001, 'Recorded date cannot be larger than Current date.');
  end if;
  select COUNT(*) into query_count from list_of_value where measure_name=var_measure_name;
  if (query_count>0) then
    select COUNT(*) into query_count from list_of_value where measure_name=var_measure_name and lov_name=:NEW.value;
    if (query_count=1) then
      select lov_value into new_value from list_of_value where measure_name=var_measure_name and lov_name=:NEW.value;
      :NEW.value := new_value;
    else
      raise_application_error(-20001, 'Value not found in List of Values for the measure.');
    end if;
  end if;
end;
/

create or replace procedure generate_alerts is 
  cursor c is select * from people_recommendation;
  var_pid varchar2(60);
  var_recommendation_name varchar2(500);
  var_measure_name varchar2(500);
  var_upper_limit number;
  var_lower_limit number;
  var_frequency number;
  var_number_of_observations number;
  var_threshold number;
  max_record_id number;
  alert_count number;
  record_count number;
  latest_recorded_date date;
begin
  for row in c
  loop
    var_pid := row.pid;
    var_upper_limit := row.upper_limit;
    var_lower_limit := row.lower_limit;
    var_frequency := row.frequency;
    var_number_of_observations := row.number_of_observations;
    var_threshold := row.threshold;
    var_recommendation_name := row.recommendation_name;
    var_measure_name := row.measure_name;
    
    if (var_frequency is not null) then
      select COUNT(*),MAX(record_id),MAX(recorded_date) into record_count,max_record_id,latest_recorded_date from record where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid;
      if (record_count)>0 then
        if (TRUNC(latest_recorded_date)<TRUNC(sysdate-var_frequency)) then
          select COUNT(*) into alert_count from alert where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and record_id=max_record_id and type='Low Activity';
          if (alert_count<1) then
            update alert set is_active='N' where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and type='Low Activity';
            insert into alert (pid,record_id,type,recommendation_name,measure_name,is_active,is_viewed) values (var_pid,max_record_id,'Low Activity',var_recommendation_name,var_measure_name,'Y','N');
          end if;
        else
          update alert set is_active='N' where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and type='Low Activity';
        end if;
      else
        select COUNT(*) into alert_count from alert where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and record_id is null and type='Low Activity';
        if (alert_count<1) then
          insert into alert (pid,record_id,type,recommendation_name,measure_name,is_active,is_viewed) values (var_pid,max_record_id,'Low Activity',var_recommendation_name,var_measure_name,'Y','N');
        end if;
      end if;
    end if;
    if (var_number_of_observations is not null and var_threshold is not null) then
      if (var_upper_limit is not null and var_lower_limit is not null) then
        select COUNT(*),MAX(record_id) into record_count,max_record_id from (select * from (select * from record where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid order by recorded_date desc, record_id desc) where rownum<=var_number_of_observations) where value>var_upper_limit or value<var_lower_limit;
        if (record_count>=var_threshold) then
          select COUNT(*) into alert_count from alert where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and record_id=max_record_id and type='Outside Limit';
          if (alert_count<1) then
            update alert set is_active='N' where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and type='Outside Limit';
            insert into alert (pid,record_id,type,recommendation_name,measure_name,is_active,is_viewed) values (var_pid,max_record_id,'Outside Limit',var_recommendation_name,var_measure_name,'Y','N');
          end if;
        else
          update alert set is_active='N' where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and type='Outside Limit';
        end if;
      else if (var_upper_limit is not null) then
        select COUNT(*),MAX(record_id) into record_count,max_record_id from (select * from (select * from record where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid order by recorded_date desc, record_id desc) where rownum<=var_number_of_observations) where value>var_upper_limit;
        if (record_count>=var_threshold) then
          select COUNT(*) into alert_count from alert where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and record_id=max_record_id and type='Outside Limit';
          if (alert_count<1) then
            update alert set is_active='N' where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and type='Outside Limit';
            insert into alert (pid,record_id,type,recommendation_name,measure_name,is_active,is_viewed) values (var_pid,max_record_id,'Outside Limit',var_recommendation_name,var_measure_name,'Y','N');
          end if;
        else
          update alert set is_active='N' where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and type='Outside Limit';
        end if;
      else if (var_lower_limit is not null) then
        select COUNT(*),MAX(record_id) into record_count,max_record_id from (select * from (select * from record where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid order by recorded_date desc, record_id desc) where rownum<=var_number_of_observations) where value<var_lower_limit;
        if (record_count>=var_threshold) then
          select COUNT(*) into alert_count from alert where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and record_id=max_record_id and type='Outside Limit';
          if (alert_count<1) then
            update alert set is_active='N' where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and type='Outside Limit';
            insert into alert (pid,record_id,type,recommendation_name,measure_name,is_active,is_viewed) values (var_pid,max_record_id,'Outside Limit',var_recommendation_name,var_measure_name,'Y','N');
          end if;
        else
          update alert set is_active='N' where recommendation_name=var_recommendation_name and measure_name=var_measure_name and pid=var_pid and type='Outside Limit';
        end if;
      end if; end if; end if;
    end if;
  end loop;
end;
/

create or replace trigger run_generate_alerts 
after insert or update
on record 
begin
  generate_alerts();
end;
/

create or replace trigger check_diagnosis_HS
before insert
on diagnosed_with
for each row
declare
HS_count number;
begin
    select count(*) into HS_count from People_Health_Supporter where pid=:NEW.pid;
    if (HS_count = 0) then
      raise_application_error(-20001, 'Patient has no Health Supporter.');
    end if;
end;
/

create or replace trigger check_num_of_health_supporters
before insert
on people_health_supporter
for each row
declare
is_primary char(1);
HS_count number(10);

begin    
  
  if(:NEW.pid=:NEW.hid) then
    raise_application_error(-20001, 'Invalid. Cannot make patient its own health supporter');
  end if;

  select count(*) into HS_count from People_Health_Supporter where pid=:NEW.pid;
  if (HS_count = 2) then
    raise_application_error(-20001, 'Patient has already two health supporters added to the system');
  end if;
	
	if( HS_count = 1) then
		:NEW.is_primary:= 'N';
	end if;
	
	if (HS_count = 0) then
		:NEW.is_primary:= 'Y';
	end if;
	
end;
/


create or replace trigger check_dob
before insert or update on people
for each row
begin
if (TRUNC(:NEW.dob)>TRUNC(sysdate)) then
  raise_application_error(-20001, 'Birth Date cannot be greater than Current Date.');
end if;
end;
/


-------------------------------------DML-------------------------------------

insert into people(pid, dob, address, gender, first_name, last_name, password)
values('P1', '26-May-1984', '2500 Sacramento, Apt 903, Santa Cruz, CA-90021', 'M', 'Sheldon', 'Cooper', 'password');
insert into people(pid, dob, address, gender, first_name, last_name, password)
values('P2', '19-Apr-1989', '2500 Sacramento, Apt 904, Santa Cruz, CA-90021', 'M', 'Leonard', 'Hofstader', 'password');
insert into people(pid, dob, address, gender, first_name, last_name, password)
values('P3', '25-Dec-1990', '2500 Sacramento, Apt 904, Santa Cruz, CA-90021', 'F', 'Penny', 'Hofstader', 'password');
insert into people(pid, dob, address, gender, first_name, last_name, password)
values('P4', '15-Jun-1992', '2500 Sacramento, Apt 905, Santa Cruz, CA-90021', 'F', 'Amy', 'Farrahfowler', 'password');

insert into PEOPLE_HEALTH_SUPPORTER(pid, hid, is_primary, authorization_date)
values('P1', 'P2', 'Y', '21-oct-2016');
insert into PEOPLE_HEALTH_SUPPORTER(pid, hid, is_primary, authorization_date)
values('P1', 'P4', 'N', '21-oct-2016');
insert into PEOPLE_HEALTH_SUPPORTER(pid, hid, is_primary, authorization_date)
values('P2', 'P3', 'Y', '09-oct-2016');
insert into PEOPLE_HEALTH_SUPPORTER(pid, hid, is_primary, authorization_date)
values('P3', 'P4', 'Y', '21-oct-2016');

insert into DISEASE(D_NAME, description)
values('Heart Disease', 'Ailment of heart');
insert into DISEASE(D_NAME, description)
values('HIV', 'Human immunodeficiency virus');
insert into DISEASE(D_NAME, description)
values('COPD', 'Chronic obstructive pulmonary disease ');

insert into measure(measure_name, unit, upper_limit, lower_limit, frequency, data_type, number_of_observations, Threshold)
values('Weight', 'lbs', 200, 120, 7, 'number', 1,1);
insert into measure(measure_name, unit, upper_limit, lower_limit, frequency, data_type, number_of_observations, Threshold)
values('Systollic Blood Pressure', 'mmHg', null, null, null, 'number', 1,1);
insert into measure(measure_name, unit, upper_limit, lower_limit, frequency, data_type, number_of_observations, Threshold)
values('Dystollic Blood Pressure', 'mmHg', null, null, null, 'number', 1,1);
insert into measure(measure_name, unit, upper_limit, lower_limit, frequency, data_type, number_of_observations, Threshold)
values('Oxygen Saturation', 'percentage', null, null, null, 'number', 1,1);
insert into measure(measure_name, unit, upper_limit, lower_limit, frequency, data_type, number_of_observations, Threshold)
values('Pain', null, null, null, null, 'LOV', 1,1);
insert into measure(measure_name, unit, upper_limit, lower_limit, frequency, data_type, number_of_observations, Threshold)
values('Mood', null, null, null, null, 'LOV', 1,1);
insert into measure(measure_name, unit, upper_limit, lower_limit, frequency, data_type, number_of_observations, Threshold)
values('Temperature', 'F', null, null, null, 'number', 1,1);

insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('Happy', 'Mood', 0, 'Patient is in happy mood');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('Neutral', 'Mood', 1, 'Patient is in neutral mood');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('Sad', 'Mood', 2, 'Patient is in sad mood');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('0', 'Pain', 0, 'Patient is in Pain level 0');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('1', 'Pain', 1, 'Patient is in Pain level 1');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('2', 'Pain', 2, 'Patient is in Pain level 2');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('3', 'Pain', 3, 'Patient is in Pain level 3');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('4', 'Pain', 4, 'Patient is in Pain level 4');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('5', 'Pain', 5, 'Patient is in Pain level 5');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('6', 'Pain', 6, 'Patient is in Pain level 6');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('7', 'Pain', 7, 'Patient is in Pain level 7');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('8', 'Pain', 8, 'Patient is in Pain level 8');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('9', 'Pain', 9, 'Patient is in Pain level 9');
insert into LIST_OF_VALUE(LOV_NAME, MEASURE_NAME, LOV_value, description)
values('10', 'Pain', 10, 'Patient is in Pain level 10');

insert into RECOMMENDATION(Recommendation_Name,Description,Is_General)
values('Check Weight', 'Check Weight', 'N');
insert into RECOMMENDATION(Recommendation_Name,Description,Is_General)
values('Check Blood Pressure', 'Check Blood Pressure', 'N');
insert into RECOMMENDATION(Recommendation_Name,Description,Is_General)
values('Check O2 Saturation', 'Check O2 Saturation', 'N');
insert into RECOMMENDATION(Recommendation_Name,Description,Is_General)
values('Check Pain', 'Check Pain', 'N');
insert into RECOMMENDATION(Recommendation_Name,Description,Is_General)
values('Check Mood', 'Check Mood', 'N');
insert into RECOMMENDATION(Recommendation_Name,Description,Is_General)
values('Check Temperature', 'Check Temperature', 'N');

insert into RECOMMENDATION_MEASURE(RECOMMENDATION_NAME, MEASURE_NAME)
values('Check Weight', 'Weight');
insert into RECOMMENDATION_MEASURE(RECOMMENDATION_NAME, MEASURE_NAME)
values('Check Blood Pressure', 'Systollic Blood Pressure');
insert into RECOMMENDATION_MEASURE(RECOMMENDATION_NAME, MEASURE_NAME)
values('Check Blood Pressure', 'Dystollic Blood Pressure');
insert into RECOMMENDATION_MEASURE(RECOMMENDATION_NAME, MEASURE_NAME)
values('Check O2 Saturation', 'Oxygen Saturation');
insert into RECOMMENDATION_MEASURE(RECOMMENDATION_NAME, MEASURE_NAME)
values('Check Pain', 'Pain');
insert into RECOMMENDATION_MEASURE(RECOMMENDATION_NAME, MEASURE_NAME)
values('Check Mood', 'Mood');
insert into RECOMMENDATION_MEASURE(RECOMMENDATION_NAME, MEASURE_NAME)
values('Check Temperature', 'Temperature');

insert into PEOPLE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, PID, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Weight', 'Weight', 'P2', 190, 120, 7, 1, 1);
insert into PEOPLE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, PID, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Pain', 'Pain', 'P2', 5, 0, 1, 1, 1);
insert into PEOPLE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, PID, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Blood Pressure', 'Systollic Blood Pressure', 'P2', null, null, 1, 1, 1);
insert into PEOPLE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, PID, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Blood Pressure', 'Dystollic Blood Pressure', 'P2', null, null, 1, 1, 1);

insert into RECORD(PID, RECOMMENDATION_NAME, MEASURE_NAME, VALUE, RECORDED_DATE, CREATED_DATE, CREATED_BY)
VALUES('P2', 'Check Weight', 'Weight', 180, '10-oct-2016', '11-oct-2016', 'P2');
insert into RECORD(PID, RECOMMENDATION_NAME, MEASURE_NAME, VALUE, RECORDED_DATE, CREATED_DATE, CREATED_BY)
VALUES('P2', 'Check Weight', 'Weight', 195, '17-oct-2016', '17-oct-2016', 'P2');

insert into DISEASE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, D_NAME, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Weight', 'Weight', 'Heart Disease', 200, 120, 7, 1, 1);
insert into DISEASE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, D_NAME, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Blood Pressure', 'Systollic Blood Pressure', 'Heart Disease', 159, 140, 1, 1, 1);
insert into DISEASE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, D_NAME, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Blood Pressure', 'Dystollic Blood Pressure', 'Heart Disease', 99, 90, 1, 1, 1);
insert into DISEASE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, D_NAME, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Mood', 'Mood', 'Heart Disease', 3, 0, 7, 1, 1);

insert into DISEASE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, D_NAME, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Weight', 'Weight', 'HIV', 200, 120, 7, 1, 1);
insert into DISEASE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, D_NAME, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Blood Pressure', 'Systollic Blood Pressure', 'HIV', null, null, 1, 1, 1);
insert into DISEASE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, D_NAME, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Blood Pressure', 'Dystollic Blood Pressure', 'HIV', null, null, 1, 1, 1);
insert into DISEASE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, D_NAME, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Pain', 'Pain', 'HIV', 5, 0, 1, 1, 1);

insert into DISEASE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, D_NAME, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check O2 Saturation', 'Oxygen Saturation', 'COPD', 99, 90, 1, 1, 1);
insert into DISEASE_SPECIFIC_RECO(RECOMMENDATION_NAME, MEASURE_NAME, D_NAME, UPPER_LIMIT, LOWER_LIMIT,FREQUENCY,NUMBER_OF_OBSERVATIONS,THRESHOLD)
values('Check Temperature', 'Temperature', 'COPD', 100, 95, 1, 1, 1);

insert into Diagnosed_With(PID,D_Name,DIAGNOSED_DATE,IS_ACTIVE)
values('P1','Heart Disease','22-oct-2016','Y');
insert into Diagnosed_With(PID,D_Name,DIAGNOSED_DATE,IS_ACTIVE)
values('P2','HIV','10-oct-2016','Y');

commit;