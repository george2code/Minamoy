package core.handlers.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import core.model.Subject;
import core.model.Task;
import core.model.journal.Lesson;
import core.model.journal.Work;
import core.utils.DateUtils;


public class LessonJSONParser
{
    public static List<Lesson> parseLessons(String content)
    {
        try {
            JSONArray arr = new JSONArray(content);
            List<Lesson> list = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Lesson lesson = new Lesson();

                lesson.setId(obj.getLong("id"));
                lesson.setTitle(obj.getString("title"));
                lesson.setDate(DateUtils.deserialize(obj.getString("date")));
                lesson.setNumber(obj.getInt("number"));


                if (!obj.isNull("place")) {
                    lesson.setPlace(obj.getString("place"));
                }
                if (!obj.isNull("start")) {
                    lesson.setStart(DateUtils.deserializeWithTime(obj.getString("start")));
                }
                if (!obj.isNull("finish")) {
                    lesson.setFinish(DateUtils.deserializeWithTime(obj.getString("finish")));
                }


                if (!obj.isNull("groupNames")) {
                    lesson.setGroupNames(URLDecoder.decode(obj.getString("groupNames")));
                }

                // Subject
                if (!obj.isNull("subject")) {
                    JSONObject subObj = obj.getJSONObject("subject");
                    Subject subject = new Subject();
                    subject.setId(subObj.getLong("id"));
                    subject.setName(subObj.getString("name"));

                    lesson.setSubject(subject);
                }

                // Works
                if (!obj.isNull("works"))
                {
                    JSONArray subjArr = obj.getJSONArray("works");
                    List<Work> workList = new ArrayList<>();
                    for (int j = 0; j < subjArr.length(); j++)
                    {
                        JSONObject wObj = subjArr.getJSONObject(j);
                        Work work = new Work();

                        work.setId(wObj.getLong("id"));
                        work.setType(wObj.getString("type"));
                        work.setMarkType(wObj.getString("markType"));
                        work.setMarkCount(wObj.getInt("markCount"));
                        work.setLesson(wObj.getLong("lesson"));
                        work.setDisplayInJournal(wObj.getBoolean("displayInJournal"));
                        work.setStatus(wObj.getString("status"));
                        work.setEduGroup(wObj.getLong("eduGroup"));
                        work.setText(wObj.getString("text"));
                        if (!wObj.isNull("periodNumber"))
                            work.setPeriodNumber(wObj.getInt("periodNumber"));
                        if (!wObj.isNull("periodType"))
                            work.setPeriodType(wObj.getString("periodType"));


                        //TODO: later back to tasks
                        if (!wObj.isNull("tasks"))
                        {
                            JSONArray taskArr = wObj.getJSONArray("tasks");
                            List<Task> taskList = new ArrayList<>();
                            for (int t=0; t < taskArr.length(); t++)
                            {
                                JSONObject taskObj = taskArr.getJSONObject(t);

                                if (!taskObj.getString("status").equals("New"))
                                {
                                    Task task = new Task();
                                    task.setId(taskObj.getLong("id"));
                                    task.setPerson(taskObj.getLong("person"));
                                    task.setWork(taskObj.getLong("work"));
                                    task.setStatus(taskObj.getString("status"));
                                    task.setTargetDate(DateUtils.deserialize(taskObj.getString("targetDate")));

                                    taskList.add(task);
                                }
                            }
                            work.setTasks(taskList);
                        }
                        // END tasks

                        workList.add(work);
                    }

                    lesson.setWorks(workList);
                }

                //Teachers
                if (!obj.isNull("teachers")) {
                    JSONArray teachersArr = obj.getJSONArray("teachers");
                    if (teachersArr.length() > 0) {
                        long[] tarr = new long[teachersArr.length()];
                        for (int j = 0; j < teachersArr.length(); j++) {
                            tarr[j] = teachersArr.getLong(j);
                        }

                        lesson.setTeachers(tarr);
                    }
                }


                list.add(lesson);
            }

            return list;

        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<Task> parseTasks(String content) {
        JSONArray taskArr;
        try {
            taskArr = new JSONArray(content);
            List<Task> taskList = new ArrayList<>();
            for (int t=0; t < taskArr.length(); t++)
            {
                JSONObject taskObj = taskArr.getJSONObject(t);

                Task task = new Task();
                task.setId(taskObj.getLong("id"));
                task.setPerson(taskObj.getLong("person"));
                if (!taskObj.isNull("personName")) {
                    task.setPersonName(taskObj.getString("personName"));
                }
                task.setWork(taskObj.getLong("work"));
                task.setStatus(taskObj.getString("status"));
                task.setTargetDate(DateUtils.deserialize(taskObj.getString("targetDate")));

                taskList.add(task);
            }

            return taskList;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}