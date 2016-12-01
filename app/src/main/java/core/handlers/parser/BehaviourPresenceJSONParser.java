package core.handlers.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import core.model.journal.Behaviour;
import core.model.journal.BehaviourStatus;
import core.model.journal.LessonLogEntry;
import core.model.journal.PresenceStatus;
import core.model.journal.Presence;

public class BehaviourPresenceJSONParser {

    public static List<BehaviourStatus> parseBeharviourStatus(String content) {
        try {
            if (content != null && content.length() > 1) {
                String[] pairs = content.replace("{", "").replace("}", "").split(",");
                if (pairs.length > 0) {
                    List<BehaviourStatus> list = new ArrayList<>();
                    for (String pair : pairs) {
                        if (pair.contains(":")) {
                            String[] items = pair.split(":");
                            long id = Long.parseLong(items[0].replace("\"", ""));
                            String state = items[1].replace("\"", "").replace("\n", "");
                            //todo: fix
                            list.add(new BehaviourStatus(id, state));
                        }
                    }
                    return list;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static List<String> parsePresencesStatus(String content) {
        try {
            if (content != null && content.length() > 1) {
                JSONArray arr = new JSONArray(content);
                List<String> list = new ArrayList<>();
                for(int i=0; i<arr.length(); i++) {
                    list.add(arr.get(i).toString());
                }
                return list;
//                return content.replace("{", "").replace("}", "").replace("[", "").replace("]", "").replace("\"", "").replace("\n", "").split(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<Behaviour> parseBehaviour(String content) {
        JSONArray taskArr;
        try {
            taskArr = new JSONArray(content);
            List<Behaviour> list = new ArrayList<>();
            for (int t=0; t < taskArr.length(); t++)
            {
                JSONObject taskObj = taskArr.getJSONObject(t);

                Behaviour task = new Behaviour();
                task.setPersonId(taskObj.getLong("person"));
                task.setLessonId(taskObj.getLong("lesson"));

                if (!taskObj.isNull("behaviours")){
                    JSONArray arrBehaviours = taskObj.getJSONArray("behaviours");
                    if (arrBehaviours.length() > 0) {
                        List<Long> behList = new ArrayList<>();
                        for (int i=0; i < arrBehaviours.length(); i++) {
                            long behItem = arrBehaviours.getLong(i);
                            behList.add(behItem);
                        }
                        task.setBehaviours(behList);
                    }
                }

                if (!taskObj.isNull("lessonlogentry")) {
                    JSONObject logObject = taskObj.getJSONObject("lessonlogentry");
                    LessonLogEntry lessonLogEntry = new LessonLogEntry();
                    lessonLogEntry.setPerson(logObject.getLong("person"));
                    lessonLogEntry.setLesson(logObject.getLong("lesson"));
                    lessonLogEntry.setComment(logObject.getString("comment"));
                    lessonLogEntry.setStatus(logObject.getString("status"));

                    task.setLessonlogentry(lessonLogEntry);
                }


                //todo: old code, get rid
                if (!taskObj.isNull("behaviour"))
                    task.setBehaviourId(taskObj.getLong("behaviour"));
                if (!taskObj.isNull("comment"))
                    task.setComment(taskObj.getString("comment"));
                if (!taskObj.isNull("justified"))
                    task.setJustified(taskObj.getBoolean("justified"));
                //end old code...


                list.add(task);
            }

            return list;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<Presence> parsePresences(String content) {
        JSONArray taskArr;
        try {
            taskArr = new JSONArray(content);
            List<Presence> personList = new ArrayList<>();
            for (int t=0; t < taskArr.length(); t++)
            {
                JSONObject taskObj = taskArr.getJSONObject(t);

                Presence task = new Presence();
                task.setPerson(taskObj.getLong("person"));
                task.setLesson(taskObj.getLong("lesson"));
                if (!taskObj.isNull("comment")) {
                    task.setComment(taskObj.getString("comment"));
                }
                task.setStatus(taskObj.getString("status"));

                personList.add(task);
            }

            return personList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}