package il.co.yomanim.mobileyomanim.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import core.enums.RoleType;
import core.handlers.HttpManager;
import core.handlers.SessionManager;
import core.handlers.parser.LessonJSONParser;
import core.model.journal.Lesson;
import core.model.journal.Work;
import core.utils.ConfigUtils;
import core.utils.DateUtils;
import core.utils.FontUtils;
import core.view.HomeworkView;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.activities.MainActivity;
import il.co.yomanim.mobileyomanim.compound.CompoundFilter;
import il.co.yomanim.mobileyomanim.compound.FilterItem;
import il.co.yomanim.mobileyomanim.grids.HomeworkTable;
import il.co.yomanim.mobileyomanim.params.HomeworkTaskParams;


public class HomeworkFragment extends Fragment {
    //region Fields
    SessionManager session;
    List<Object> tasks;
    List<HomeworkView> listView;
    ProgressBar pb;
    TabHost tabs;
    TextView labelEmtpy;
    FrameLayout layout;
    long personId;
    long schoolId;
    String token;
    RoleType roleType;
    Typeface fontRobotoMedium;
    //endregion

    public HomeworkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (FrameLayout)inflater.inflate(R.layout.fragment_homework, container, false);

        //hide toolbar filter buttons
        ((MainActivity)getActivity()).hideToolbarFilters();

        fontRobotoMedium = FontUtils.getRobotoMedium(layout.getContext().getAssets());

        labelEmtpy = (TextView) layout.findViewById(R.id.label_homework_empty);
        labelEmtpy.setVisibility(View.INVISIBLE);

        pb = (ProgressBar) layout.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        // Session class instance
        session = new SessionManager(layout.getContext());
        schoolId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_SCHOOL_ID));
        token = session.getUserDetails().get(SessionManager.KEY_TOKEN);
        roleType = RoleType.valueOf(session.getUserDetails().get(SessionManager.KEY_ROLE));
        personId = (roleType != RoleType.Teacher) ?
                Long.parseLong(session.getUserDetails().get(SessionManager.KEY_CHILD_PROFILE_ID)) :
                Long.parseLong(session.getUserDetails().get(SessionManager.KEY_PROFILE_ID));


        // Custom filter panel
        CompoundFilter object = new CompoundFilter(getActivity(), layout, ConfigUtils.FILTER_MODE_DEFAULT);
        object.groupSelectedIndex = session.getFilterSelectedIndex(SessionManager.KEY_FILTER_GROUP_INDEX);
        object.periodSelectedIndex = session.getFilterSelectedIndex(SessionManager.KEY_FILTER_PERIOD_INDEX);
        object.weekSelectedIndex = session.getFilterSelectedIndex(SessionManager.KEY_FILTER_WEEK_INDEX);
        object.setCustomObjectListener(new CompoundFilter.MyCustomObjectListener() {
            @Override
            public void onObjectReady(FilterItem filterItem) {
                Log.e("HOME", filterItem.getFrom() + " === " + filterItem.getTo() + " === " + filterItem.getGroupId());
                // Code to handle object ready
                HomeworkTaskParams homeworkTaskParams = new HomeworkTaskParams(filterItem, roleType);
                HomeworkLoaderTask task = new HomeworkLoaderTask();
                task.execute(homeworkTaskParams);
            }
        });
        object.Init();


        //Init tab host
        tabs = (TabHost) layout.findViewById(R.id.tabHost);
        tabs.setup();

        this.setNewTab(tabs, "all", getString(R.string.homework_status_all), R.id.tab1);
        this.setNewTab(tabs, "sent", getString(R.string.homework_status_sent), R.id.tab2);
        this.setNewTab(tabs, "closed", getString(R.string.homework_status_closed), R.id.tab3); //archive

        tabs.setCurrentTab(0);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equalsIgnoreCase("Sent")) {
                    if (listView != null && listView.size() > 0) {
                        List<HomeworkView> slist = new ArrayList<>();
                        for (HomeworkView hv : listView)
                            if (hv.getWorkStatus().equalsIgnoreCase("Sent"))
                                slist.add(hv);

                        if (slist.size() > 0) {
                            HomeworkTable homeworkTable = new HomeworkTable(HomeworkFragment.this, R.id.grid_homework_sent, slist, token, roleType);
                            homeworkTable.Render();
                            labelEmtpy.setVisibility(View.INVISIBLE);
                        } else {
                            labelEmtpy.setVisibility(View.VISIBLE);
                        }
                    }
                }

                if (tabId.equalsIgnoreCase("Closed")) {
                    if (listView != null && listView.size() > 0) {
                        List<HomeworkView> clist = new ArrayList<>();
                        for (HomeworkView hv : listView)
                            if (hv.getWorkStatus().equalsIgnoreCase("Closed"))
                                clist.add(hv);

                        if (clist.size() > 0) {
                            HomeworkTable homeworkTable = new HomeworkTable(HomeworkFragment.this, R.id.grid_homework_archive, clist, token, roleType);
                            homeworkTable.Render();
                            labelEmtpy.setVisibility(View.INVISIBLE);
                        } else {
                            labelEmtpy.setVisibility(View.VISIBLE);
                        }
                    }
                }

                if (tabId.equalsIgnoreCase("All")) {
                    if (listView != null && listView.size() > 0) {
                        HomeworkTable homeworkTable = new HomeworkTable(HomeworkFragment.this,
                                R.id.grid_homework_all, listView, token, roleType);
                        homeworkTable.Render();
                        labelEmtpy.setVisibility(View.INVISIBLE);
                    }
                    else
                        labelEmtpy.setVisibility(View.VISIBLE);
                }
            }
        });


        return layout;
    }



    //region Tab install
    private void setNewTab(TabHost tabHost, String tag, String title, int contentID ){
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(getTabIndicator(tabHost.getContext(), title)); // new function to inject our own tab layout
        tabSpec.setContent(contentID);
        tabHost.addTab(tabSpec);
    }

    private View getTabIndicator(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setTypeface(fontRobotoMedium);
        tv.setText(title);
        return view;
    }
    //endregion



    //region Homework task -----------------------------------------
    private class HomeworkLoaderTask extends AsyncTask<HomeworkTaskParams, Void, List<Lesson>> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);

            //Clear all grids
            HomeworkTable.ClearGrid(getActivity(), R.id.grid_homework_all);
            HomeworkTable.ClearGrid(getActivity(), R.id.grid_homework_sent);
            HomeworkTable.ClearGrid(getActivity(), R.id.grid_homework_archive);

            labelEmtpy.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Lesson> doInBackground(HomeworkTaskParams... params) {
            String content = null;

            try {
                if (params[0].role == RoleType.Teacher) {
                    content = (session.isSchoolClassicalSystem()) ?
                            HttpManager.getTeacherHomeworksClassical(getActivity(),
                                    token,
                                    params[0].filterItem.getGradeId(),
                                    params[0].filterItem.getSubjectId(),
                                    params[0].filterItem.getPersonId(),
                                    DateUtils.dateToString(params[0].filterItem.getFrom()),
                                    DateUtils.dateToString(params[0].filterItem.getTo()),
                                    "") :
                            HttpManager.getTeacherHomeworksMulticlass(getActivity(),
                                    token,
                                    params[0].filterItem.getGroupId(),
                                    DateUtils.dateToString(params[0].filterItem.getFrom()),
                                    DateUtils.dateToString(params[0].filterItem.getTo()),
                                    "");
                } else {
                    content = HttpManager.getStudentHomeworks(getActivity(),
                            token,
                            params[0].filterItem.getPersonId(),
                            DateUtils.dateToString(params[0].filterItem.getFrom()),
                            DateUtils.dateToString(params[0].filterItem.getTo()));
                }
                Log.e("HOME", "==>" + content);
            } catch (Exception e) {
                Log.e("HOME", e.getMessage());
            }

            if (content != null) {
                return LessonJSONParser.parseLessons(content);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final List<Lesson> result) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }
            if (result == null) {
                labelEmtpy.setVisibility(View.VISIBLE);
            } else {
                // linq, get all works list
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                List<HomeworkView> list = new ArrayList<>();

                for (Lesson lesson : result) {
                    long lessonId = lesson.getId();
                    String title = lesson.getTitle();
                    int number = lesson.getNumber();
                    String date = simpleDateFormat.format(lesson.getDate());
                    String subjectName = (lesson.getSubject() != null) ? lesson.getSubject().getName() : "-";

                    for (Work work : lesson.getWorks()) {
                        if (work.getStatus().equals("New"))
                            continue;

                        boolean isNew = true;

                        for (HomeworkView hv : list) {
                            if (hv.getWorkId() == work.getId()) {
                                isNew = false;
                                break;
                            }
                        }

                        if (isNew) {
                            HomeworkView view = new HomeworkView();
                            // lesson
                            view.setLessonId(lessonId);
                            view.setLessonTitle(title);
                            view.setLessonNumber(number);
                            view.setLessonDate(date);
                            // work
                            view.setWorkId(work.getId());
                            view.setWorkText(work.getText());
                            view.setWorkStatus(work.getStatus());
                            view.setMarkType(work.getMarkType());
                            // subject
                            view.setSubjectName(subjectName);
                            // task list
                            view.setTasks(work.getTasks());

                            list.add(view);
                        }
                    }
                }

                if (list.size() > 0) {
                    listView = list;
                    // render homework table
                    HomeworkTable homeworkTable = new HomeworkTable(HomeworkFragment.this,
                            R.id.grid_homework_all, list, token, roleType);
                    homeworkTable.Render();
                    labelEmtpy.setVisibility(View.INVISIBLE);
                    tabs.setCurrentTab(0);
                }
                else {
                    labelEmtpy.setVisibility(View.VISIBLE);
                }
            }
        }
    } // END: Homework Loader Task
    //endregion
}