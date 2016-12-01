package core.handlers;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import core.helpers.url.UrlBehaviourHelper;
import core.helpers.url.UrlGradeGroupsHelper;
import core.helpers.url.UrlHelper;
import core.helpers.url.UrlLessonHelper;
import core.helpers.url.UrlMarkHelper;
import core.helpers.url.UrlPresenceHelper;
import core.helpers.url.UrlTimetableHelper;
import core.helpers.url.UrlUserHelper;
import core.model.LoginData;
import core.model.Task;
import core.model.journal.LessonActivity;
import core.model.journal.Mark;


public class HttpManager {

    //region User
    public static String login(Context context, String username, String password) {
        String url = UrlUserHelper.loginUrl(context, username, password);
        return HttpHandler.getData(url);
    }

    public static String login(Context context, LoginData loginData) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(loginData);
            String url = UrlUserHelper.loginUrl(context);
            return HttpHandler.postData(url, json, null);
        } catch (Exception e) {
            Log.e("login", e.getMessage());
        }
        return null;
    }

    public static String getUserSchoolMemberships(Context context, String token) {
        String url = UrlUserHelper.getUserSchoolMemberships(context, token);
        return HttpHandler.getData(url);
    }

    public static String getUsersMe(Context context, String token) {
        String url = UrlUserHelper.getUsersMe(context, token);
        return HttpHandler.getData(url);
    }

    public static String getUserChildList(Context context, String token, long personId) {
        String url = UrlUserHelper.getUserChildListUrl(context, token, personId);
        return HttpHandler.getData(url);
    }
    //endregion


    //region Grades
    public static String getGradesByTeacherAndSchool(Context context, String token, long personId, long schoolId) {
        String url = UrlGradeGroupsHelper.getGradesByTeacherAndSchool(context, token, personId, schoolId);
        return HttpHandler.getData(url);
    }

    public static String getGradesByStudent(Context context, String token, long personId, long schoolId) {
        String url = UrlGradeGroupsHelper.getGradesByStudent(context, token, personId, schoolId);
        return HttpHandler.getData(url);
    }

    public static String getSubjectByGradeForTeacher(Context context, String token, long gradeId, long personId) {
        String url = UrlGradeGroupsHelper.getSubjectByGradeForTeacher(context, token, gradeId, personId);
        return HttpHandler.getData(url);
    }

    public static String getSubjectByGradeForStudent(Context context, String token, long gradeId) {
        String url = UrlGradeGroupsHelper.getSubjectByGradeForStudent(context, token, gradeId);
        return HttpHandler.getData(url);
    }

    public static String getSubjectGroupsByStudentAndSchool(Context context, String token, long personId, long schoolId) {
        String url = UrlGradeGroupsHelper.getSubjectGroupsByStudentAndSchool(context, token, personId, schoolId);
        return HttpHandler.getData(url);
    }
    //endregion


    //todo: remove
    public static String getEduGroupStudents(Context context, String token, long eduGroupId) {
        String url = UrlHelper.getEduGroupStudents(context, token, eduGroupId);
        return HttpHandler.getData(url);
    }


    public static String getStudents(Context context, String token, long eduGroupId) {
        String url = UrlHelper.getStudents(context, token, eduGroupId);
        return HttpHandler.getData(url);
    }


    //todo: remove
    public static String getEduGroupReportingPeriods(Context context, String token, long eduGroupId) {
        String url = UrlHelper.getEduGroupReportingPeriods(context, token, eduGroupId);
        return HttpHandler.getData(url);
    }

    public static String getReportingPeriods(Context context, String token, long schoolId) {
        String url = UrlHelper.getReportingPeriods(context, token, schoolId);
        return HttpHandler.getData(url);
    }



    public static String getLessonsByEduGroupAndPeriod(Context context, String token, long eduGroupId, String from, String to) {
        String url = UrlLessonHelper.getLessonsByEduGroupAndPeriod(context, token, eduGroupId, from, to);
        return HttpHandler.getData(url);
    }

    public static String getLessonsByPersonAndPeriod(Context context, String token, long personId, String from, String to) {
        String url = UrlLessonHelper.getLessonsByPersonAndPeriod(context, token, personId, from, to);
        return HttpHandler.getData(url);
    }


    //region for tasks (homework)
    public static String getLessonsWithoutTasksGetByGroupAndPeriod(Context context, String token,
                                                        long eduGroupId, String from, String to) {
        String url = UrlLessonHelper.getLessonsWithoutTasksGetByGroupAndPeriod(
                context, token, eduGroupId, from, to);
        return HttpHandler.getData(url);
    }

    public static String getLessonsWithoutTasksGetByGroupAndPeriodAndPerson(Context context, String token,
                                            long eduGroupId, long personId, String from, String to) {
        String url = UrlLessonHelper.getLessonsWithoutTasksGetByGroupAndPeriodAndPerson(
                context, token, eduGroupId, personId, from, to);
        return HttpHandler.getData(url);
    }

    public static String getTasksByLessonAndWork(Context context, String token, long workId) {
        String url = UrlLessonHelper.getTasksByLessonAndWork(context, token, workId);
        return HttpHandler.getData(url);
    }

    public static String getHomeworkMarks(Context context, String token, long workId) {
        String url = UrlLessonHelper.getHomeworkMarks(context, token, workId);
        return HttpHandler.getData(url);
    }

    public static String getWorkTasksAndMarks(Context context, String token, long workId) {
        String url = UrlLessonHelper.getWorkTasksAndMarks(context, token, workId);
        return HttpHandler.getData(url);
    }

    public static String getTasksByWork(Context context, String token, long workId) {
        String url = UrlLessonHelper.getTasksByWork(context, token, workId);
        return HttpHandler.getData(url);
    }



    // Задание в статусе "Выдано" (New): Статус можно сменить на "Выполнено" (Closed) или на
    // статус "Отменено" (Cancelled). Для этого необходимо выполнить метод POST: tasks/ в теле
    // передать объект Task с новым статусом.
    public static String changeHomeworkTaskStatus(Context context, String token, Task task) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(task);
            String url = UrlHelper.changeHomeworkTaskStatus(context, token);
            return HttpHandler.postData(url, json, token);
        } catch (Exception e) {
            Log.e("changeHWTaskStatus", e.getMessage());
        }
        return null;
    }





    //--
    public static String getTeacherHomeworksMulticlass(Context context, String token, long groupId, String from, String to, String status) {
        String url = UrlLessonHelper.getTeacherHomeworksMulticlass(context, token, groupId, from, to, status);
        return HttpHandler.getData(url);
    }

    public static String getTeacherHomeworksClassical(Context context, String token, long gradeId, long subjectId, long personId, String from, String to, String status) {
        String url = UrlLessonHelper.getTeacherHomeworksClassical(context, token, gradeId, subjectId, personId, from, to, status);
        return HttpHandler.getData(url);
    }

    public static String getStudentHomeworks(Context context, String token, long personId, String from, String to) {
        String url = UrlLessonHelper.getStudentHomeworks(context, token, personId, from, to);
        return HttpHandler.getData(url);
    }
    //endregion


    // for timetable
    public static String getSchoolTimetable(Context context, String token, long schoolId) {
        String url = UrlTimetableHelper.getSchoolTimetable(context, token, schoolId);
        return HttpHandler.getData(url);
    }

    public static String getTimetableStudentLessons(Context context, String token, long person, String from, String to) {
        String url = UrlTimetableHelper.getTimetableStudentLessons(context, token, person, from, to);
        return HttpHandler.getData(url);
    }

    public static String getTimetableTeacherLessons(Context context, String token, long school, long person, String from, String to) {
        String url = UrlTimetableHelper.getTimetableTeacherLessons(context, token, school, person, from, to);
        return HttpHandler.getData(url);
    }


    //Marks
    public static String getMarksByEduGroupAndPeriod(Context context, String token, long eduGroupId, String from, String to) {
        String url = UrlMarkHelper.getMarksByEduGroupAndPeriod(context, token, eduGroupId, from, to);
        return HttpHandler.getData(url);
    }

    public static String getMarksByPersonSchoolAndPeriod(Context context, String token, long schoolId, long personId, String from, String to) {
        String url = UrlMarkHelper.getMarksByPersonSchoolAndPeriod(context, token, schoolId, personId, from, to);
        return HttpHandler.getData(url);
    }


    //setup homework mark
    //todo: test
    public static String deleteMark(Context context, String token, long markId) {
        String url = UrlMarkHelper.deleteMark(context, token, markId);
        return HttpHandler.deleteData(url, token);
    }

    public static String putMark(Context context, String token, Mark mark) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(mark);
            String url = UrlMarkHelper.putMark(context, token);
            return HttpHandler.putData(url, json, token);
        } catch (Exception e) {
            Log.e("putMark", e.getMessage());
        }
        return null;
    }
    public static String setMark(Context context, String token, Mark mark) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(mark);
            String url = UrlMarkHelper.setMark(context, token);
            return HttpHandler.postData(url, json, token);
        } catch (Exception e) {
            Log.e("setMark", e.getMessage());
        }
        return null;
    }



    //todo: new
    public static String getFinalMarksTeacherMulti(Context context, String token, long subjectGroupId, long periodId) {
        String url = UrlMarkHelper.getFinalMarksTeacherMulti(context, token, subjectGroupId, periodId);
        Log.e("FINAL_MARKS", url);
        return HttpHandler.getData(url);
    }



    public static String setFinalMarkTeacherMulti(Context context, String token, long personId, long subjectGroupId, long periodId, String markValue) {
        String url = UrlMarkHelper.setFinalMarkTeacherMulti(context, token, personId, subjectGroupId, periodId, markValue);
        return HttpHandler.postData(url, null, null);
    }


    //teacher comments
    public static String getTeacherCommentMarks(Context context, String token, long subjectGroupId, int periodId) {
        String url = UrlMarkHelper.getTeacherCommentMarks(context, token, subjectGroupId, periodId);
        Log.e("COMMENT_MARKS", url);
        return HttpHandler.getData(url);
    }


    public static String getTeacherCommentsBySchool(Context context, String token, long school) {
        String url = UrlMarkHelper.getTeacherCommentsBySchool(context, token, school);
        return HttpHandler.getData(url);
    }



    //Behaviour
    public static String getBehaviourForTeacher(Context context, String token, long groupId, String from, String to) {
        String url = UrlBehaviourHelper.getBehaviourForTeacher(context, token, groupId, from, to);
        return HttpHandler.getData(url);
    }

    public static String getBehaviourForStudent(Context context, String token, long personId, String from, String to) {
        String url = UrlBehaviourHelper.getBehaviourForStudent(context, token, personId, from, to);
        return HttpHandler.getData(url);
    }

    public static String getBehaviourStatusList(Context context, String token, long schoolId) {
        String url = UrlBehaviourHelper.getBehaviourStatusList(context, token, schoolId);
        return HttpHandler.getData(url);
    }

    //TODO: check new
    public static String getPresenceStatuses(Context context, String token) {
        String url = UrlPresenceHelper.getPresenceStatuses(context, token);
        return HttpHandler.getData(url);
    }

    //Presence
    public static String getPresenceByGroupAndPeriod(Context context, String token, long groupId, String from, String to) {
        String url = UrlPresenceHelper.getPresenceByGroupAndPeriod(context, token, groupId, from, to);
        return HttpHandler.getData(url);
    }

    public static String getPresenceBySchoolPersonAndPeriod(Context context, String token, long schoolId, long personId, String from, String to) {
        String url = UrlPresenceHelper.getPresenceBySchoolPersonAndPeriod(context, token, schoolId, personId, from, to);
        return HttpHandler.getData(url);
    }


    //set mark
    public static String postMark(Context context, LessonActivity lessonActivity, String token) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(lessonActivity);
            String url = UrlHelper.getSetMark(context, token);
            return HttpHandler.postData(url, json, token);
        } catch (Exception e) {
            Log.e("postMark", e.getMessage());
        }
        return null;
    }
}