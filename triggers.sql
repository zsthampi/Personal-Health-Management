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
