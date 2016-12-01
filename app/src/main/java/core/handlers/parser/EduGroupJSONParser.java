package core.handlers.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import core.model.EduGroup;
import core.model.Person;
import core.model.ReportingPeriod;
import core.model.Subject;
import core.utils.DateUtils;
import core.utils.StringUtils;

public class EduGroupJSONParser {

    //withSubjects - to avoid load subjects if no need, increase speed
    public static List<EduGroup> parseEduGroupList(String content, boolean withSubjects)
    {
        try {
            JSONArray arr = new JSONArray(content);
            List<EduGroup> list = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                EduGroup group = new EduGroup();

                group.setId(obj.getLong("id"));
//                private long[] parentIds;
//                group.setType(obj.getString("type"));
                group.setName(obj.getString("name"));
                if (!obj.isNull("fullName")) {
                    group.setFullName(StringUtils.removeExtraSpaces(obj.getString("fullName")));
                }
                if (!obj.isNull("parallel")) {
                    group.setParallel(obj.getInt("parallel"));
                }
                if (!obj.isNull("timetable")) {
                    group.setTimetable(obj.getLong("timetable"));
                }

                if (!obj.isNull("periodid")) {
                    group.setPeriodId(obj.getLong("periodid"));
                }

                // get subject list
                if (withSubjects) {
                    JSONArray subjArr = obj.getJSONArray("subjects");
                    List<Subject> subjectList = new ArrayList<>();
                    for (int j = 0; j < subjArr.length(); j++) {
                        JSONObject sObj = subjArr.getJSONObject(j);
                        Subject subject = new Subject();

                        subject.setId(sObj.getLong("id"));
                        subject.setName(sObj.getString("name"));

                        subjectList.add(subject);
                    }
                    group.setSubjects(subjectList);
                }

                list.add(group);
            }

            return list;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Person> parseEduGroupStudents(String content) {
        try {
            JSONArray arr = new JSONArray(content);
            List<Person> list = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Person person = new Person();

                person.setId(obj.getLong("id"));
                person.setFirstName(StringUtils.removeExtraSpaces(obj.getString("firstName")));
                person.setLastName(StringUtils.removeExtraSpaces(obj.getString("lastName")));
                person.setSex(obj.getString("sex"));
                if (!obj.isNull("middleName")) {
                    person.setMiddleName(obj.getString("middleName"));
                }

                list.add(person);
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO: check
    public static List<ReportingPeriod> parseReportingPeriods(String content) {
        try {
            JSONArray arr = new JSONArray(content);
            List<ReportingPeriod> list = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                ReportingPeriod period = new ReportingPeriod();

                Date start = DateUtils.deserialize(obj.getString("start"));
                Date finish = DateUtils.deserialize(obj.getString("finish"));

                //TODO: max difference beetween days is 192. Maybe, we need to move this check on the server side!
                int daysBeetween = DateUtils.calculateDifference(start, finish);
                if (daysBeetween > 0 && daysBeetween > 180) {
                    int substractDays = daysBeetween - 180;
                    finish = DateUtils.substractDays(finish, substractDays);
                }

                period.setId(obj.getLong("id"));
                period.setStart(start);
                period.setFinish(finish);
                period.setNumber(obj.getInt("number"));
                period.setType(obj.getString("type"));
                period.setName(StringUtils.removeExtraSpaces(obj.getString("name")));
                period.setYear(obj.getLong("year"));

                list.add(period);
            }

            return list;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}