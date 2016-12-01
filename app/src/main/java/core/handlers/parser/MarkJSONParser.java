package core.handlers.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import core.model.journal.Behaviour;
import core.model.journal.JournalFinalMark;
import core.model.journal.LessonActivity;
import core.model.journal.Mark;
import core.model.journal.Presence;
import core.utils.DateUtils;
import il.co.yomanim.mobileyomanim.items.JournalItem;

public class MarkJSONParser
{
    public static List<Mark> parseMarks(String content) {
        try {
            JSONArray arr = new JSONArray(content);
            List<Mark> list = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Mark mark = new Mark();

                mark.setId(obj.getLong("id"));
                mark.setType(obj.getString("type"));
                mark.setValue(obj.getString("value"));
                mark.setPerson(obj.getLong("person"));
                mark.setWork(obj.getLong("work"));
                mark.setLesson(obj.getLong("lesson"));
                mark.setNumber(obj.getInt("number"));
                mark.setDate(DateUtils.deserialize(obj.getString("date")));
                mark.setWorkType(obj.getString("workType"));

                list.add(mark);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static JournalFinalMark parseFinalMark(String content) {
        try {
            JSONObject obj = new JSONObject(content);
            JournalFinalMark mark = new JournalFinalMark();

            mark.setPerson(obj.getLong("person"));
            mark.setSubject(obj.getLong("subject"));
            if (!obj.isNull("sabjectname"))
                mark.setSabjectname(obj.getString("sabjectname"));

            if (!obj.isNull("avgmark"))
                mark.setAvgmark(obj.getDouble("avgmark"));
            if (!obj.isNull("markid"))
                mark.setMarkid(obj.getLong("markid"));
            if (!obj.isNull("mark"))
                mark.setMark(obj.getInt("mark"));
            if (!obj.isNull("marktype"))
                mark.setMarktype(obj.getString("marktype"));

            if (!obj.isNull("commentid"))
                mark.setCommentid(obj.getLong("commentid"));
            if (!obj.isNull("comment"))
                mark.setComment(obj.getString("comment"));

            return mark;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }


    public static List<JournalFinalMark> parseFinalMarks(String content) {
        try {
            JSONArray array = new JSONArray(content);
            List<JournalFinalMark> list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                JournalFinalMark mark = new JournalFinalMark();

//                {"person":1000000009294,"subject":10561,"sabjectname":"","avgmark":null,"markid":1915,"mark":78,"marktype":"Mark100","commentid":null,"comment":null}
                mark.setPerson(obj.getLong("person"));
                mark.setSubject(obj.getLong("subject"));
                if (!obj.isNull("sabjectname"))
                    mark.setSabjectname(obj.getString("sabjectname"));

                if (!obj.isNull("avgmark"))
                    mark.setAvgmark(obj.getDouble("avgmark"));
                if (!obj.isNull("markid"))
                    mark.setMarkid(obj.getLong("markid"));
                if (!obj.isNull("mark"))
                    mark.setMark(obj.getInt("mark"));
                if (!obj.isNull("marktype"))
                    mark.setMarktype(obj.getString("marktype"));

                if (!obj.isNull("commentid"))
                    mark.setCommentid(obj.getLong("commentid"));
                if (!obj.isNull("comment"))
                    mark.setComment(obj.getString("comment"));

                list.add(mark);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static LessonActivity parseLessonActivity(String content) {
        try {
            JSONObject obj = new JSONObject(content);
            LessonActivity lessonActivity = new LessonActivity();

            if (!obj.isNull("person")) {
                lessonActivity.setPerson(obj.getLong("person"));
            }
            if (!obj.isNull("lesson")) {
                lessonActivity.setPerson(obj.getLong("lesson"));
            }
            if (!obj.isNull("mark")) {
                JSONObject objMark = obj.getJSONObject("mark");
                Mark mark = new Mark();
                mark.setPerson(objMark.getLong("person"));
                mark.setLesson(objMark.getLong("lesson"));
                mark.setValue(objMark.getString("value"));
                // set mark
                lessonActivity.setResource(mark);
            }
            if (!obj.isNull("behaviors")) {
                JSONArray behaviorArr = obj.getJSONArray("behaviors");
                List<Behaviour> behaviourList = new ArrayList<>();
                for(int i=0; i<behaviorArr.length(); i++) {
                    JSONObject objBehavior = behaviorArr.getJSONObject(i);
                    Behaviour behaviour = new Behaviour();
                    behaviour.setPersonId(objBehavior.getLong("person"));
                    behaviour.setLessonId(objBehavior.getLong("lesson"));
                    behaviour.setBehaviourId(objBehavior.getLong("behaviour"));
                    behaviourList.add(behaviour);
                }
                //set behaviors
                if (behaviourList.size() > 0) {
                    lessonActivity.setBehaviors(behaviourList);
                }
            }
            if (!obj.isNull("lessonlogentry")) {
                JSONObject objPresence = obj.getJSONObject("lessonlogentry");
                Presence presence = new Presence();
                presence.setPerson(objPresence.getLong("person"));
                presence.setLesson(objPresence.getLong("lesson"));
                if (!objPresence.isNull("comment")) {
                    presence.setComment(objPresence.getString("comment"));
                }
                presence.setStatus(objPresence.getString("status"));
                //set presence
                lessonActivity.setLessonLogEntry(presence);
            }

            return lessonActivity;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}