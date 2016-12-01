package core.handlers.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import core.model.EducationalMemebership;
import core.model.School;
import core.model.SchoolMembership;


public class SchoolJSONParser {

    private static final String INDEX_SCHOOL = "school";
    private static final String INDEX_ROLES = "roles";
    private static final String INDEX_EDU_MEMBERSHIP = "eduGroupMemberships";


    public static EducationalMemebership parseEducationalMembership(String content)
    {
        EducationalMemebership educationalMemebership = new EducationalMemebership();

        try {
            JSONObject obj = new JSONObject(content);
            educationalMemebership.setPerson(obj.getLong("person"));

            JSONArray schoolMembershipArr = obj.getJSONArray("schools");
            List<SchoolMembership> list = new ArrayList<>();

            for (int i = 0; i < schoolMembershipArr.length(); i++)
            {
                SchoolMembership membership = new SchoolMembership();
                JSONObject arrObj = schoolMembershipArr.getJSONObject(i);

                // get school
                JSONObject schoolObj = arrObj.getJSONObject(INDEX_SCHOOL);
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
                    JSONArray rolesArr = arrObj.getJSONArray(INDEX_ROLES);
                    String[] listRole = new String[rolesArr.length()];
                    for (int j = 0; j < rolesArr.length(); j++) {
                        listRole[j] = rolesArr.get(j).toString();
                    }
                    membership.setRoles(listRole);
                } catch(Exception e) {
                    e.printStackTrace();
                }

                // add membership to list
                list.add(membership);
            }

            educationalMemebership.setSchools(list);
            return educationalMemebership;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static long[] parseSchoolIdList(String content) {
        try {
            JSONArray arr = new JSONArray(content);
            long[] schoolIdList = new long[arr.length()];

            for (int i = 0; i < arr.length(); i++) {
                schoolIdList[i] = arr.getLong(i);
            }

            return schoolIdList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static School parseSchool(String content) {
        try {
            JSONObject obj = new JSONObject(content);
            School school = new School();

            school.setId(obj.getLong("id"));
            school.setName(obj.getString("name"));
            school.setFullName(obj.getString("fullName"));
            school.setAvatarSmall(obj.getString("avatarSmall"));
            school.setCity(obj.getString("city"));

            return school;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}