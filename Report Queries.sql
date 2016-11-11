
--List the number of health supporters that were authorized in the month of September 2016 by patients suffering from heart disease.
--COMMENTS Question (Can repeats be included like same person was approved as a health supporter of 10 people)
select count(distinct hid) count from  PEOPLE_HEALTH_SUPPORTER where extract(year from AUTHORIZATION_DATE)='2016' and extract(month from AUTHORIZATION_DATE)='10' and pid in(select pid from DIAGNOSED_WITH where D_NAME like 'Heart Disease');

--Give the number of patients who were not complying with the recommended frequency of recording observations.
select COUNT(distinct pid) from ALERT where TYPE='Low Activity';

--List the health supporters who themselves are patients.
--COMMENTS patient is one who has specific reco or a health supporter or has been diagnosed with some disease
SELECT distinct hid FROM PEOPLE_HEALTH_SUPPORTER where hid in(select pid from PEOPLE_SPECIFIC_RECO union select pid from DIAGNOSED_WITH union select pid from PEOPLE_HEALTH_SUPPORTER);

--List the patients who are not ‘sick’.
--COMMENTS might include health supporters as well
select distinct pid from people where pid not in (select pid from DIAGNOSED_WITH);
--COMMENTS exclude health supporters
--COMMENTS patient is one who has specific reco or a health supporter or has been diagnosed with some disease
select distinct pid from people where pid not in (select pid from DIAGNOSED_WITH) and pid in (select pid from PEOPLE_SPECIFIC_RECO union select pid from PEOPLE_HEALTH_SUPPORTER);

--How many patients have different observation time and recording time (of the observation).
select COUNT(distinct pid) from RECORD where trunc(RECORDED_DATE)<>trunc(CREATED_DATE);
