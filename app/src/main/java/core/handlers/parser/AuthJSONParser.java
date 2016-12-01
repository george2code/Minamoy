package core.handlers.parser;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import core.model.Authorization;
import core.model.EduGroup;
import core.model.Person;
import core.model.School;
import core.model.SchoolMembership;
import core.model.Subject;
import core.model.User;
import core.model.journal.PresenceStatus;
import core.model.journal.WorkType;
import core.utils.NumberUtils;
import core.utils.StringUtils;
import il.co.yomanim.mobileyomanim.R;


public class AuthJSONParser {
    private static String Tag = "AuthJSONParser";

    public static Authorization parseUser(String content) {
        try {
            JSONObject obj = new JSONObject(content);
            Authorization auth = new Authorization();
            auth.setAccessToken(obj.getString("accessToken"));
            auth.setRefreshToken(obj.getString("refreshToken"));
            auth.setPersonId(obj.getLong("personId"));
            auth.setUserFullName(obj.getString("userFullName"));
            //presences
            if (!obj.isNull("presences")) {
                JSONArray prArr = obj.getJSONArray("presences");
                List<PresenceStatus> presenceStatusList = new ArrayList<>();
                for(int i=0; i<prArr.length(); i++) {
                    JSONObject prObj = prArr.getJSONObject(i);
                    PresenceStatus presenceStatus = new PresenceStatus();
                    presenceStatus.setId(prObj.getLong("id"));
                    presenceStatus.setStatus(prObj.getString("status"));
                    presenceStatus.setShortName(prObj.getString("shortName"));
                    presenceStatus.setFullName(prObj.getString("fullName"));
                    presenceStatus.setColor(prObj.getString("color"));
                    presenceStatusList.add(presenceStatus);
                }
                auth.setPresences(presenceStatusList);
            }
            //work types
            if (!obj.isNull("workTypes")) {
                JSONArray wtArr = obj.getJSONArray("workTypes");
                List<WorkType> workTypeList = new ArrayList<>();
                for(int i=0; i<wtArr.length(); i++) {
                    JSONObject wtObj = wtArr.getJSONObject(i);
                    WorkType workType = new WorkType();
                    workType.setId(wtObj.getInt("id"));
                    workType.setTitle(wtObj.getString("title"));
                    workType.setAbbr(wtObj.getString("abbr"));
                    workTypeList.add(workType);
                }
                auth.setWorkTypes(workTypeList);
            }
            //result
            return auth;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static User parseProfileInfo(String content, boolean withSubjects) {
        try {
            JSONObject obj = new JSONObject(content);
            User user = new User();

            user.setPerson(obj.getLong("person"));

            if (!obj.isNull("schools")) {
                JSONArray schoolMembershipArr = obj.getJSONArray("schools");
                List<SchoolMembership> schoolMembershipList = new ArrayList<>();
                for (int i = 0; i < schoolMembershipArr.length(); i++) {
                    SchoolMembership membership = new SchoolMembership();
                    JSONObject arrObj = schoolMembershipArr.getJSONObject(i);

                    JSONObject schoolObj = arrObj.getJSONObject("school");
                    // get school
                    School school = new School();
                    school.setId(schoolObj.getLong("id"));
                    school.setName(schoolObj.getString("name"));
                    school.setFullName(schoolObj.getString("fullName"));
                    school.setAvatarSmall(schoolObj.getString("avatarSmall"));
                    school.setCity(schoolObj.getString("city"));
                    school.setIsClassicSystem(schoolObj.getBoolean("isclassicsystem"));
                    // set school to membership
                    membership.setSchool(school);

                    // get roles
                    try {
                        JSONArray rolesArr = arrObj.getJSONArray("roles");
                        String[] listRole = new String[rolesArr.length()];
                        for (int j = 0; j < rolesArr.length(); j++) {
                            listRole[j] = rolesArr.get(j).toString();
                        }
                        membership.setRoles(listRole);
                    } catch (Exception e) {
                        Log.e(Tag, e.getMessage());
                    }

                    //get edugroups
                    if (!arrObj.isNull("eduGroupMemberships")) {
                        JSONArray arrJsonGroups = arrObj.getJSONArray("eduGroupMemberships");
                        List<EduGroup> eduGroupList = new ArrayList<>();

                        for (int g=0; g<arrJsonGroups.length(); g++) {
                            JSONObject objGroupInside = arrJsonGroups.getJSONObject(i);

                                JSONObject objGroup = objGroupInside.getJSONObject("eduGroup");
                                EduGroup group = new EduGroup();

                                group.setId(objGroup.getLong("id"));
                                group.setName(objGroup.getString("name"));
                                if (!objGroup.isNull("fullName")) {
                                    group.setFullName(StringUtils.removeExtraSpaces(objGroup.getString("fullName")));
                                }
                                if (!objGroup.isNull("parallel")) {
                                    group.setParallel(objGroup.getInt("parallel"));
                                }
                                if (!objGroup.isNull("timetable")) {
                                    group.setTimetable(objGroup.getLong("timetable"));
                                }
                                if (!objGroup.isNull("periodid")) {
                                    group.setPeriodId(objGroup.getLong("periodid"));
                                }

                                // get subject list
                                if (withSubjects) {
                                    try {
                                        if (!objGroup.isNull("subjects")) {
                                            JSONArray subjArr = objGroup.getJSONArray("subjects");
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
                                    } catch (Exception e) {
                                        Log.e(Tag, e.getMessage());
                                    }
                                }
                                eduGroupList.add(group);

                        }

                        //setup groups to membership
                        membership.setEduGroups(eduGroupList);

                    }
                    // add membership to list
                    schoolMembershipList.add(membership);
                }
                //
                user.setSchoolMemberships(schoolMembershipList);
            }
            //response
            return user;
        } catch (JSONException e) {
            Log.e(Tag, e.getMessage());
            return null;
        }
    }


    public static User parserUsersMe(String content) {
        try {
            JSONObject obj = new JSONObject(content);
            User user = new User();
            user.setPerson(obj.getLong("id"));
            user.setAvatar(obj.getString("photoSmall"));
            user.setName("name");
            //result
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<Person> parseUserChilds(String content, Context context) {
        try {
            JSONArray jsonArray = new JSONArray(content);
            if (jsonArray.length() > 0) {
                List<Person> list = new ArrayList<>();
                for (int i=0; i<jsonArray.length(); i++) {
                    Person person = new Person();
                    JSONObject obj = jsonArray.getJSONObject(i);

                    person.setId(obj.getLong("id"));
//                    person.setUserId(obj.getLong("userId"));
                    person.setFirstName(StringUtils.removeExtraSpaces(obj.getString("firstName")));
                    person.setLastName(StringUtils.removeExtraSpaces(obj.getString("lastName")));
//                    person.setMiddleName(obj.getString("middleName"));
                    person.setSex(obj.getString("sex"));

                    //generate avatar
                    int avatarIndex = R.drawable.no_ava;
                    switch (person.getSex()) {
                        case "Female":
                            String girlAva = MessageFormat.format("child_ico_girl{0}", NumberUtils.GetRandomNumber(1, 3));
                            avatarIndex = context.getResources().getIdentifier(girlAva, "mipmap", context.getPackageName());
                            break;
                        case "Male":
                            String boyAva = MessageFormat.format("child_ico_boy{0}", NumberUtils.GetRandomNumber(1, 3));
                            avatarIndex = context.getResources().getIdentifier(boyAva, "mipmap", context.getPackageName());
                            break;
                        default:
                            break;
                    }
                    Uri imgUri = Uri.parse("android.resource://il.co.yomanim.mobileyomanim/" + avatarIndex);
                    person.setAvatarUrl(imgUri.toString());

                    if (!obj.isNull("class")) {
                        person.setClassName(obj.getString("class"));
                    }

                    list.add(person);
                }
                return list;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            Log.e(Tag, ex.getMessage());
        }

        return null;
    }
}