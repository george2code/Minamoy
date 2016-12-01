package core.handlers.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import core.model.journal.TeacherComment;


public class CommentJSONParser {
    public static List<TeacherComment> parseTeacherComments(String content) {
        JSONArray taskArr;
        try {
            taskArr = new JSONArray(content);
            List<TeacherComment> personList = new ArrayList<>();
            for (int t=0; t < taskArr.length(); t++) {
                JSONObject taskObj = taskArr.getJSONObject(t);

                TeacherComment teacherComment = new TeacherComment();
                teacherComment.setId(taskObj.getLong("id"));
                teacherComment.setMark(taskObj.getInt("mark"));
                teacherComment.setMale(taskObj.getString("male"));
                teacherComment.setFemale(taskObj.getString("female"));

                personList.add(teacherComment);
            }

            return personList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}