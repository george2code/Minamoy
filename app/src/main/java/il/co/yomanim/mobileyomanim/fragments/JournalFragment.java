package il.co.yomanim.mobileyomanim.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import core.enums.RoleType;
import core.handlers.HttpManager;
import core.handlers.SessionManager;
import core.handlers.parser.BehaviourPresenceJSONParser;
import core.handlers.parser.CommentJSONParser;
import core.handlers.parser.EduGroupJSONParser;
import core.handlers.parser.LessonJSONParser;
import core.handlers.parser.MarkJSONParser;
import core.helpers.ConstantsHelper;
import core.model.Person;
import core.model.journal.Behaviour;
import core.model.journal.BehaviourStatus;
import core.model.journal.PresenceStatus;
import core.model.journal.JournalFinalMark;
import core.model.journal.Lesson;
import core.model.journal.Mark;
import core.model.journal.Presence;
import core.model.journal.TeacherComment;
import core.utils.ConfigUtils;
import core.utils.DateUtils;
import core.utils.FontUtils;
import core.utils.StringUtils;
import core.view.JournalView;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.activities.MainActivity;
import il.co.yomanim.mobileyomanim.compound.CompoundClassicalFilter;
import il.co.yomanim.mobileyomanim.compound.CompoundFilter;
import il.co.yomanim.mobileyomanim.compound.FilterItem;
import il.co.yomanim.mobileyomanim.grids.BehaviourJournal;
import il.co.yomanim.mobileyomanim.grids.FinalJournal;
import il.co.yomanim.mobileyomanim.grids.GroupJournal;
import il.co.yomanim.mobileyomanim.grids.StudentJournal;
import il.co.yomanim.mobileyomanim.params.GroupTaskParams;
import il.co.yomanim.mobileyomanim.params.JournalTaskParams;


public class JournalFragment extends Fragment {

    //region Fields
    TabHost tabs;
    TextView labelEmtpy;
    FrameLayout layout;
    ProgressBar pb;
    SessionManager session;
    JournalView journalView;
    List<Object> tasks;
    List<BehaviourStatus> _listBehaviour;

    private CompoundFilter _multiClassFilter;
    private FilterItem _currentFilterItem;

    long _personId;
    long _schoolId;
    String _token;
    RoleType _roleType;
    //endregion

    public JournalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (FrameLayout)inflater.inflate(R.layout.fragment_journal, container, false);

        //hide toolbar filter buttons
        ((MainActivity)getActivity()).hideToolbarFilters();

        labelEmtpy = (TextView) layout.findViewById(R.id.textViewEmpty);
        labelEmtpy.setVisibility(View.VISIBLE);

        pb = (ProgressBar) layout.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        // Session class instance
        session = new SessionManager(layout.getContext());

        //Load groups, subjects and periods
        _roleType = RoleType.valueOf(session.getUserDetails().get(SessionManager.KEY_ROLE));

        _personId = (_roleType == RoleType.Parent) ?
                Long.parseLong(session.getUserDetails().get(SessionManager.KEY_CHILD_PROFILE_ID)) :
                Long.parseLong(session.getUserDetails().get(SessionManager.KEY_PROFILE_ID));
        _schoolId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_SCHOOL_ID));
        _token = session.getUserDetails().get(SessionManager.KEY_TOKEN);


        //TODO: get school behaviour list
        BehaviourListTask behaviourListTask = new BehaviourListTask();
        behaviourListTask.execute(new GroupTaskParams(_token, _personId, _schoolId));


        //Init tabs
        tabs = (TabHost) layout.findViewById(R.id.tabHostJournal);
        tabs.setup();
        this.setNewTab(tabs, "marks", getString(R.string.journal_marks), R.id.tab1);
        this.setNewTab(tabs, "behaviour", "Behaviour", R.id.tab2);
        this.setNewTab(tabs, "final", getString(R.string.journal_final), R.id.tab3);
        tabs.setCurrentTab(0);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
                RenderJournal(tabs.getCurrentTab(), true);
            }
        });



        //Load filters
        if (session.isSchoolClassicalSystem()) {
            // Custom Classical filter panel
            CompoundClassicalFilter object = new CompoundClassicalFilter(getActivity(), layout, false);
            object.setCustomObjectListener(new CompoundClassicalFilter.MyCustomObjectListener() {
                @Override
                public void onObjectReady(FilterItem filterItem) {
                    Log.e("HOME", filterItem.getFrom() + " === " + filterItem.getTo() + " === " + filterItem.getGroupId());
                    // Code to handle object ready
//                    JournalTaskParams params = new JournalTaskParams(
//                            layout.getContext().getApplicationContext(),
//                            _token,
//                            groupId,
//                            start,
//                            finish);
//
//                    Log.e("RunJournalTask", "g:" + groupId + ", ds:" + _dateStart + ", df:" + _dateFinish);
//
//                    StudentsLoaderTask loaderTask = new StudentsLoaderTask();
//                    loaderTask.execute(params);
                }
            });
            object.Init();
        }
        else {
            // Custom filter panel
            _multiClassFilter = new CompoundFilter(getActivity(), layout, ConfigUtils.FILTER_MODE_DEFAULT);
            _multiClassFilter.setCustomObjectListener(new CompoundFilter.MyCustomObjectListener() {
                @Override
                public void onObjectReady(FilterItem filterItem) {

                    Log.e("HOME", filterItem.getFrom() + " === " + filterItem.getTo() + " === " + filterItem.getGroupId());
                    _currentFilterItem = filterItem;

                    RenderJournal(tabs.getCurrentTab(), true);

//                    switch (tabs.getCurrentTab()) {
//                        case 0:
//                            loadMarksJournal();
//                            break;
//                        case 1:
//                            loadFinalMarksJournal();
//                            break;
//                        default:
//                            break;
//                    }


//                    FinalJournalTask finalJournalTask = new FinalJournalTask();
//                    finalJournalTask.execute(filterItem);

                    // Code to handle object ready
//                    HomeworkTaskParams homeworkTaskParams = new HomeworkTaskParams(
//                            filterItem.getGroupId(),
//                            DateUtils.dateToString(filterItem.getFrom()),
//                            DateUtils.dateToString(filterItem.getTo()),
//                            _roleType);
//                    HomeworkLoaderTask task = new HomeworkLoaderTask();
//                    task.execute(homeworkTaskParams);
                }
            });
            _multiClassFilter.Init();
        }




        return layout;
    }


    private void RenderJournal(int tabIndex, boolean isTabChanged) {
        GroupJournal gjournal;
        StudentJournal journal;
//        if (journalView != null) {
            switch (tabIndex) {
                case 0:
                    if (isTabChanged) {
                        _multiClassFilter.hideWeeks(false);
                        loadMarksJournal();
                    }
//                    if (_roleType == RoleType.Teacher) {
//                        if (isTabChanged) {
//                            reloadJournal();
//                        } else {
//                            gjournal = new GroupJournal(JournalFragment.this);
//                            gjournal.RenderJournal(journalView, ConstantsHelper.TAB_JOURNAL_MARKS);
//                        }
//                    } else {
//                        journal = new StudentJournal(JournalFragment.this);
//                        journal.RenderStudentJournal(journalView, ConstantsHelper.TAB_JOURNAL_MARKS);
//                    }
                    break;
                case 1:
                    if (isTabChanged) {
                        _multiClassFilter.hideWeeks(false);
                        loadBehaviourJournal();
                    }
                    break;
                case 2:
//                    gjournal = new GroupJournal(JournalFragment.this);
//                    gjournal.RenderFinalJournal(journalView);
                    if (isTabChanged) {
                        _multiClassFilter.hideWeeks(true);
                        loadFinalMarksJournal();
                    }


//                    if (_roleType == RoleType.Teacher) {
//                        if (isTabChanged) {
//                            reloadJournal();
//                        } else {
//                            gjournal = new GroupJournal(JournalFragment.this);
//                            gjournal.RenderJournal(journalView, ConstantsHelper.TAB_JOURNAL_PRESENCE);
//                        }
//                    } else {
//                        journal = new StudentJournal(JournalFragment.this);
//                        journal.RenderStudentJournal(journalView, ConstantsHelper.TAB_JOURNAL_PRESENCE);
//                    }
                    break;
                case 3:
                    if (_roleType == RoleType.Teacher) {
                        if (isTabChanged) {
                            reloadJournal();
                        } else {
                            gjournal = new GroupJournal(JournalFragment.this);
                            gjournal.RenderJournal(journalView, ConstantsHelper.TAB_JOURNAL_BEHAVIOUR);
                        }
                    } else {
                        journal = new StudentJournal(JournalFragment.this);
                        journal.RenderStudentJournal(journalView, ConstantsHelper.TAB_JOURNAL_BEHAVIOUR);
                    }
                    break;
                default:
                    break;
            }
//        }
    }


    private void loadMarksJournal() {
        Log.e("HOME", "loadMarksJournal()----------------");
        JournalTaskParams params = new JournalTaskParams(
                layout.getContext().getApplicationContext(),
                _token,
                _currentFilterItem.getGroupId(),
                DateUtils.dateToString(_currentFilterItem.getFrom()),
                DateUtils.dateToString(_currentFilterItem.getTo())
        );

//        Log.e("RunJournalTask", "g:" + _currentFilterItem.getGroupId() + ", ds:" + startDate + ", df:" + finishDate);

        StudentsLoaderTask loaderTask = new StudentsLoaderTask();
        loaderTask.execute(params);
    }


    private void loadFinalMarksJournal() {
        FinalJournalTask finalJournalTask = new FinalJournalTask();
        finalJournalTask.execute(_currentFilterItem);
    }


    private void loadBehaviourJournal() {
        BehaviourJournalTask behaviourJournalTask = new BehaviourJournalTask();
        behaviourJournalTask.execute(_currentFilterItem);
    }



    private void reloadJournal() {
        //todo: unkomment
//        JournalTaskParams params = new JournalTaskParams(
//                layout.getContext().getApplicationContext(),
//                _token,
//                groupId,
//                _dateStart,
//                _dateFinish);
//
////        Log.e("reloadJournal", "g:" + groupId + ", ds:" + _dateStart + ", df:" + _dateFinish);
//
//        StudentsLoaderTask loaderTask = new StudentsLoaderTask();
//        loaderTask.execute(params);
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
        tv.setTypeface(FontUtils.getRobotoMedium(context.getAssets()));
        tv.setText(title);
        return view;
    }
    //endregion


    //region Behaviour list task -----------------------------------------
    private class BehaviourListTask extends AsyncTask<GroupTaskParams, Void, List<BehaviourStatus>> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<BehaviourStatus> doInBackground(GroupTaskParams... params) {

            //todo: get teacher comments and save to the session
            if (new SessionManager(getActivity()).getTeacherCommentList() == null) {
                String commentsContent = HttpManager.getTeacherCommentsBySchool(
                        getActivity(),
                        params[0].token,
                        params[0].schoolId);
                if (commentsContent != null) {
                    List<TeacherComment> teacherCommentList = CommentJSONParser.parseTeacherComments(commentsContent);
                    if (teacherCommentList != null && teacherCommentList.size() > 0) {
                        new SessionManager(getActivity()).updateTeacherComments(teacherCommentList);
                    }
                }
            }

            //get behaviour status list by schoolId
            List<BehaviourStatus> behaviourStatuses = new SessionManager(getActivity()).getBehaviourStatusList();
            if (behaviourStatuses == null) {
                String contentPeriods = HttpManager.getBehaviourStatusList(getActivity(), params[0].token, params[0].schoolId);
                Log.e("BehaviourListTask", contentPeriods);
                if (contentPeriods != null) {
                    behaviourStatuses = BehaviourPresenceJSONParser.parseBeharviourStatus(contentPeriods);
                    new SessionManager(getActivity()).setBehaviourStatus(behaviourStatuses);
                }
            }

            //return null;
            return behaviourStatuses;
        }

        @Override
        protected void onPostExecute(final List<BehaviourStatus> result)
        {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result != null && result.size() > 0) {
                _listBehaviour = result;
            }

            //call journal
        }
    } // END: Behaviour status list
    //endregion



    private String getBehaviourNameByIdList(Behaviour behaviour) {
        String result = "";
        if (behaviour.getBehaviours() != null && behaviour.getBehaviours().size() > 0) {
            for (Long behStatusId : behaviour.getBehaviours()) {
                for(BehaviourStatus status : _listBehaviour) {
                    if (status.getId() == behStatusId) {
                        result += status.getStatus() + ", ";
                        break;
                    }
                }
            }
            if (result.length() > 0) {
                result = StringUtils.removeLastSymbol(result.trim());
            }
        }

        return result;
    }






    private class BehaviourJournalTask extends AsyncTask<FilterItem, Void, JournalView> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }
        @Override
        protected JournalView doInBackground(FilterItem... params) {
            String contentStudents = HttpManager.getStudents(getActivity(), _token,  params[0].getGroupId());
            if (contentStudents != null) {
                List<Person> students = EduGroupJSONParser.parseEduGroupStudents(contentStudents);
                journalView = new JournalView(getActivity());
                journalView.setStudents(students);



                //Get lessons
                String cLessons;
                if (_roleType == RoleType.Teacher) {
                    cLessons = HttpManager.getLessonsByEduGroupAndPeriod(
                            getActivity(),
                            _token,
                            _currentFilterItem.getGroupId(),
                            DateUtils.dateToString(_currentFilterItem.getFrom()),
                            DateUtils.dateToString(_currentFilterItem.getTo()));
                }
                else {
                    cLessons = HttpManager.getLessonsByPersonAndPeriod(
                            getActivity(),
                            _token,
                            _personId,
                            DateUtils.dateToString(_currentFilterItem.getFrom()),
                            DateUtils.dateToString(_currentFilterItem.getTo()));
                }

                if (cLessons != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                    List<Lesson> lessons = LessonJSONParser.parseLessons(cLessons);

                    if (lessons != null) {
                        LinkedHashMap<String, List<Lesson>> map = new LinkedHashMap<>();

                        if (_roleType == RoleType.Teacher) {
                            for (Lesson lesson : lessons) {
                                String key = sdf.format(lesson.getDate());
                                if (map.get(key) == null) {
                                    map.put(key, new ArrayList<Lesson>());
                                }
                                map.get(key).add(lesson);
                            }
                        }

                        if (map.size() > 0) {
                            journalView.setLessons(map);
                        }
                    }
                }





                //todo: Load behaviours
                if (_listBehaviour != null && _listBehaviour.size() > 0) {
                    //set all behaviours
                    journalView.setBehaviourSchoolList(_listBehaviour);

                    //get specific for person
                    String behaviourContent;
                    if (_roleType == RoleType.Teacher) {
                        behaviourContent = HttpManager.getBehaviourForTeacher(
                                getActivity(),
                                _token,
                                _currentFilterItem.getGroupId(),
                                DateUtils.dateToString(_currentFilterItem.getFrom()),
                                DateUtils.dateToString(_currentFilterItem.getTo()));
                    }
                    else {
                        behaviourContent = HttpManager.getBehaviourForStudent(
                                getActivity(),
                                _token,
                                _personId,
                                DateUtils.dateToString(_currentFilterItem.getFrom()),
                                DateUtils.dateToString(_currentFilterItem.getTo()));
                    }

                    Log.e("BEHAVIOUR", behaviourContent);
                    if (behaviourContent != null) {
                        List<Behaviour> listBehaviour = BehaviourPresenceJSONParser.parseBehaviour(behaviourContent);
                        if (listBehaviour != null && listBehaviour.size() > 0) {
                            Map<Long, List<Behaviour>> map = new LinkedHashMap<>();
                            for (Behaviour behaviour : listBehaviour) {
                                long key = behaviour.getLessonId();
                                if (map.get(key) == null) {
                                    map.put(key, new ArrayList<Behaviour>());
                                }

                                behaviour.setBehaviourName(getBehaviourNameByIdList(behaviour));
                                map.get(key).add(behaviour);
                            }

                            if (map.size() > 0) {
                                journalView.setBehaviours(map);
                            }
                        }
                    }
                }




                //TODO: load presence SCHOOL statuses
                String presenceStatusesContent = HttpManager.getPresenceStatuses(getActivity(), _token);
                Log.e("PRES STATUSES", presenceStatusesContent);
                if (presenceStatusesContent != null) {
                    List<String> allPresenceStatuses = BehaviourPresenceJSONParser.parsePresencesStatus(presenceStatusesContent);
                    if (allPresenceStatuses != null && allPresenceStatuses.size() > 0) {
                        //TODO: parse string array
                        journalView.setPresenceSchoolArray(allPresenceStatuses);
                    }
                }




                //TODO: Load Presences
                String presenceContent;
                if (_roleType == RoleType.Teacher) {
                    presenceContent = HttpManager.getPresenceByGroupAndPeriod(
                            getActivity(),
                            _token,
                            _currentFilterItem.getGroupId(),
                            DateUtils.dateToString(_currentFilterItem.getFrom()),
                            DateUtils.dateToString(_currentFilterItem.getTo()));
                }
                else {
                    presenceContent = HttpManager.getPresenceBySchoolPersonAndPeriod(
                            getActivity(),
                            _token,
                            _schoolId,
                            _personId,
                            DateUtils.dateToString(_currentFilterItem.getFrom()),
                            DateUtils.dateToString(_currentFilterItem.getTo()));
                }
                Log.e("PRESENCE", presenceContent);
                if (presenceContent != null) {
                    List<Presence> presences = BehaviourPresenceJSONParser.parsePresences(presenceContent);
                    if (presences != null && presences.size() > 0) {
                        journalView.setPresences(presences);
                    }
                }
            }

            return journalView;
        }
        @Override
        protected void onPostExecute(final JournalView view) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            clearJournal();

            //if there any lesson, render journal grid
            if (journalView.getLessons() != null) {
                BehaviourJournal finalJournal = new BehaviourJournal(JournalFragment.this);
                finalJournal.RenderJournal(view);
                labelEmtpy.setVisibility(View.INVISIBLE);
            }
        }
    }











    private class FinalJournalTask extends AsyncTask<FilterItem, Void, JournalView> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }
        @Override
        protected JournalView doInBackground(FilterItem... params) {
            String contentStudents = HttpManager.getStudents(getActivity(), _token,  params[0].getGroupId());
            if (contentStudents != null) {
                List<Person> students = EduGroupJSONParser.parseEduGroupStudents(contentStudents);
                journalView = new JournalView(getActivity());
                journalView.setStudents(students);

                journalView.setSubjectGroupId(params[0].getGroupId());
                journalView.setPeriodId(params[0].getPeriodId());

                String contentFinalMarks;
                if (_roleType == RoleType.Teacher) {
                    contentFinalMarks = HttpManager.getFinalMarksTeacherMulti(getActivity(),
                            _token, params[0].getGroupId(), params[0].getPeriodId());

                    Log.e("FINAL_MARKS", contentFinalMarks);
                    if (contentFinalMarks != null) {
                        List<JournalFinalMark> journalFinalMarkList = MarkJSONParser.parseFinalMarks(contentFinalMarks);
                        journalView.setFinalMarks(journalFinalMarkList);
                    }
                }

//                String contentTeacherComments;
//                if (_roleType == RoleType.Teacher) {
//                    contentTeacherComments = HttpManager.getTeacherCommentMarks(getActivity(),
//                            _token, params[0].getGroupId(), params[0].getPeriodId());
//
//                    Log.e("COMMENT_MARKS", contentTeacherComments != null ? contentTeacherComments : "");
//                    if (contentTeacherComments != null) {
//                    }
//                }
            }

            return journalView;
        }
        @Override
        protected void onPostExecute(final JournalView view) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            clearJournal();

//            if (view == null) {
//                return;
//            }

            FinalJournal finalJournal = new FinalJournal(JournalFragment.this);
            finalJournal.RenderJournal(view);


            labelEmtpy.setVisibility(View.INVISIBLE);

//            if (view == null) {
//                //clear journal
//                clearJournal();
//                return;
//            }
////            Log.e("RENDER false", String.valueOf(tabs.getCurrentTab()));
//            RenderJournal(tabs.getCurrentTab(), false);
//            labelEmtpy.setVisibility(View.INVISIBLE);
        }
    }





    //region StudentLoaderTask
    private class StudentsLoaderTask extends AsyncTask<JournalTaskParams, Void, JournalView> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected JournalView doInBackground(JournalTaskParams... params) {

            journalView = new JournalView(params[0].context);

            String content = HttpManager.getEduGroupStudents(
                    params[0].context,
                    params[0].token,
                    params[0].groupId);

            if (content != null) {
                List<Person> students = EduGroupJSONParser.parseEduGroupStudents(content);
                journalView.setStudents(students);

                //TODO: Get marks, if students available
                String contentMarks;
                if (_roleType == RoleType.Teacher) {
                    contentMarks = HttpManager.getMarksByEduGroupAndPeriod(
                            params[0].context,
                            params[0].token,
                            params[0].groupId,
                            params[0].from,
                            params[0].to);
                }
                else {
                    contentMarks = HttpManager.getMarksByPersonSchoolAndPeriod(
                            params[0].context,
                            params[0].token,
                            _schoolId,
                            _personId,
                            params[0].from,
                            params[0].to);
                }

                if (contentMarks != null) {
                    List<Mark> markList = MarkJSONParser.parseMarks(contentMarks);
                    if (markList != null && markList.size() > 0) {
                        journalView.setMarks(markList);
                    }
                }// if contentMarks


//                //TODO: Load behaviours
//                if (_listBehaviour != null && _listBehaviour.size() > 0) {
//
//                    //set all behaviours
//                    journalView.setBehaviourSchoolList(_listBehaviour);
//
//                    //get specific for person
//                    String behaviourContent;
//                    if (_roleType == RoleType.Teacher) {
//                        behaviourContent = HttpManager.getBehaviourForTeacher(
//                                params[0].context,
//                                params[0].token,
//                                params[0].groupId,
//                                params[0].from,
//                                params[0].to);
//                    }
//                    else {
//                        behaviourContent = HttpManager.getBehaviourForStudent(
//                                params[0].context,
//                                params[0].token,
//                                _personId,
//                                params[0].from,
//                                params[0].to);
//                    }
//
//                    if (behaviourContent != null) {
//                        List<Behaviour> listBehaviour = BehaviourPresenceJSONParser.parseBehaviour(behaviourContent);
//                        if (listBehaviour != null && listBehaviour.size() > 0) {
//                            Map<Long, List<Behaviour>> map = new LinkedHashMap<>();
//                            for (Behaviour behaviour : listBehaviour) {
//                                long key = behaviour.getLessonId();
//                                if (map.get(key) == null) {
//                                    map.put(key, new ArrayList<Behaviour>());
//                                }
//
//                                behaviour.setBehaviourName(getBehaviourNameById(behaviour.getBehaviourId()));
//                                map.get(key).add(behaviour);
//                            }
//
//                            if (map.size() > 0) {
//                                journalView.setBehaviours(map);
//                            }
//                        }
//                    }
//                }
//
//
//
//
//                //TODO: load presence SCHOOL statuses
//                String presenceStatusesContent = HttpManager.getPresenceStatuses(params[0].context, params[0].token);
//                if (presenceStatusesContent != null) {
//                    List<String> allPresenceStatuses = BehaviourPresenceJSONParser.parsePresencesStatus(presenceStatusesContent);
//                    if (allPresenceStatuses != null && allPresenceStatuses.size() > 0) {
//                        //TODO: parse string array
//                        journalView.setPresenceSchoolArray(allPresenceStatuses);
//                    }
//                }
//
//
//
//
//                //TODO: Load Presences
//                String presenceContent;
//                if (_roleType == RoleType.Teacher) {
//                    presenceContent = HttpManager.getPresenceByGroupAndPeriod(
//                            params[0].context,
//                            params[0].token,
//                            params[0].groupId,
//                            params[0].from,
//                            params[0].to);
//                }
//                else {
//                    presenceContent = HttpManager.getPresenceBySchoolPersonAndPeriod(
//                            params[0].context,
//                            params[0].token,
//                            _schoolId,
//                            _personId,
//                            params[0].from,
//                            params[0].to);
//                }
//
//                if (presenceContent != null) {
//                    List<Presence> presences = BehaviourPresenceJSONParser.parsePresences(presenceContent);
//                    if (presences != null && presences.size() > 0) {
//                        journalView.setPresences(presences);
//                    }
//                }

                //TODO: Get lessons, if there any marks
                //Get lessons
                String cLessons;
                if (_roleType == RoleType.Teacher) {
                    cLessons = HttpManager.getLessonsByEduGroupAndPeriod(
                            params[0].context,
                            params[0].token,
                            params[0].groupId,
                            params[0].from,
                            params[0].to);
                }
                else {
                    cLessons = HttpManager.getLessonsByPersonAndPeriod(
                            params[0].context,
                            params[0].token,
                            _personId,
                            params[0].from,
                            params[0].to);
                }

                if (cLessons != null)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
                    List<Lesson> lessons = LessonJSONParser.parseLessons(cLessons);

                    if (lessons != null) {
                        LinkedHashMap<String, List<Lesson>> map = new LinkedHashMap<>();

                        if (_roleType == RoleType.Teacher) {
                            for (Lesson lesson : lessons) {
                                String key = sdf.format(lesson.getDate());
                                if (map.get(key) == null) {
                                    map.put(key, new ArrayList<Lesson>());
                                }
                                map.get(key).add(lesson);
                            }
                        }
                        else {
                            if(journalView.getMarks() != null || journalView.getPresences() != null) {
                                for (Lesson lesson : lessons) {
                                    boolean isInclude = false;

                                    //check if this lesson has mark
                                    if (journalView.getMarks() != null) {
                                        for(Mark mark : journalView.getMarks()) {
                                            if(mark.getLesson() == lesson.getId()) {
                                                isInclude = true;
                                                break;
                                            }
                                        }
                                    }

                                    //check if this lesson has presence
                                    if (journalView.getPresences() != null && !isInclude) {
                                        for(Presence presence : journalView.getPresences()) {
                                            if(presence.getLesson() == lesson.getId()) {
                                                isInclude = true;
                                                break;
                                            }
                                        }
                                    }

//                                    //check if this lesson has behaviour
                                    if (journalView.getBehaviours() != null && !isInclude) {
                                        for(Long behaviourLessonId : journalView.getBehaviours().keySet()) {
                                            if(behaviourLessonId == lesson.getId()) {
                                                isInclude = true;
                                                break;
                                            }
                                        }
                                    }

                                    if(isInclude) {
                                        String key = sdf.format(lesson.getDate());
                                        if (map.get(key) == null) {
                                            map.put(key, new ArrayList<Lesson>());
                                        }
                                        map.get(key).add(lesson);
                                    }
                                }//for lessons
                            }
                        }// if student or parent

                        if (map.size() > 0) {
                            journalView.setLessons(map);
                            //load edu groups for student and parent, if there any lessons
                            if (_roleType != RoleType.Teacher) {
                                String contentGroups = HttpManager.getSubjectGroupsByStudentAndSchool(
                                        getActivity(),
                                        params[0].token,
                                        _personId,
                                        _schoolId);
                                if (contentGroups != null) {
                                    journalView.setGroups(EduGroupJSONParser.parseEduGroupList(contentGroups, false));
                                }
                            }
                        }
                    }
                } // cLessons != null
            } // if contentStudents

            return (journalView.getStudents() != null && journalView.getLessons() != null) ?
                    journalView : null;
        }

        @Override
        protected void onPostExecute(final JournalView view) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            //clear journal
            clearJournal();

            //if there any lesson, render journal grid
            if (journalView.getLessons() != null) {
                GroupJournal gjournal = new GroupJournal(JournalFragment.this);
                gjournal.RenderJournal(journalView, ConstantsHelper.TAB_JOURNAL_MARKS);
                labelEmtpy.setVisibility(View.INVISIBLE);
            }
        }
    }
    //endregion


    private void clearJournal() {
        GridLayout gridLayout = (GridLayout)getActivity().findViewById(R.id.scrollable_part);
        gridLayout.removeAllViews();
        TableLayout fixedColumn = (TableLayout)getActivity().findViewById(R.id.fixed_column);
        fixedColumn.removeAllViews();
        fixedColumn.setBackgroundColor(Color.parseColor("#cccccc"));

        //show label
        labelEmtpy.setVisibility(View.VISIBLE);
    }
}