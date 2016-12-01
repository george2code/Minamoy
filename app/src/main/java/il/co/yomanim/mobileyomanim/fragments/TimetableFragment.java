package il.co.yomanim.mobileyomanim.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import core.enums.RoleType;
import core.handlers.HttpManager;
import core.handlers.SessionManager;
import core.handlers.parser.EduGroupJSONParser;
import core.handlers.parser.LessonJSONParser;
import core.handlers.parser.TimetableJSONParser;
import core.model.ReportingPeriod;
import core.model.TimetableItem;
import core.model.journal.Lesson;
import core.utils.ConfigUtils;
import core.utils.DateUtils;
import core.utils.FontUtils;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.activities.MainActivity;
import il.co.yomanim.mobileyomanim.compound.CompoundClassicalFilter;
import il.co.yomanim.mobileyomanim.compound.CompoundFilter;
import il.co.yomanim.mobileyomanim.compound.FilterItem;
import il.co.yomanim.mobileyomanim.grids.TimetableTiles;


public class TimetableFragment extends Fragment {
    //region Fields
    FrameLayout layout;
    LayoutInflater _inflater;
    ProgressBar pb;
    List<Object> tasks;
    SessionManager session;
    String _token;
    Long _schoolId;
    Long _personId;
    Date _date;
    String _from;
    String _to;
    List<TimetableItem> _timetableItems;
    TextView labelEmtpy;

    TabHost tabs;
    HorizontalScrollView horizontalScrollView;
    GridLayout gridLayout;
    RoleType _roleType;
    //endregion

    public TimetableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = (FrameLayout)inflater.inflate(R.layout.fragment_timetable, container, false);
        _inflater = inflater;

        //hide toolbar filter buttons
        ((MainActivity)getActivity()).hideToolbarFilters();

        // Session class instance
        session = new SessionManager(layout.getContext());
        _token = session.getUserDetails().get(SessionManager.KEY_TOKEN);
        _schoolId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_SCHOOL_ID));
        _roleType = RoleType.valueOf(session.getUserDetails().get(SessionManager.KEY_ROLE));
        if (_roleType != RoleType.Teacher) {
            _personId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_CHILD_PROFILE_ID));
        } else {
            _personId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_PROFILE_ID));
        }


        Log.e("SYSTEM", session.isSchoolClassicalSystem() ? "CLASSICAL" : "MULTICLASS");


        labelEmtpy = (TextView) layout.findViewById(R.id.textViewTimetableEmpty);
        labelEmtpy.setText("Timetable is empty");
        labelEmtpy.setVisibility(View.INVISIBLE);

        _date = new Date();

        pb = (ProgressBar) layout.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

//        Typeface fontMedium = FontUtils.getRobotoMedium(getActivity().getAssets());

        _from = DateUtils.getFirstDateOfWeek(_date);
        _to = DateUtils.getLastDateOfWeek(_date);

        //Init tab host
        gridLayout = (GridLayout) layout.findViewById(R.id.gridLayoutTimeTable);
        horizontalScrollView = (HorizontalScrollView) layout.findViewById(R.id.tabscroller);
        tabs = (TabHost) layout.findViewById(R.id.tabHostTimetable);
        tabs.setup();


        //Load filters
        if (session.isSchoolClassicalSystem()) {
            CompoundClassicalFilter object = new CompoundClassicalFilter(getActivity(), layout, true);
            object.setCustomObjectListener(new CompoundClassicalFilter.MyCustomObjectListener() {
                @Override
                public void onObjectReady(FilterItem filterItem) {
                    // Code to handle object ready
                    RunTimetablekTask(filterItem.getFrom(), filterItem.getTo());
                }
            });
            object.Init();
        }
        else {
            CompoundFilter object = new CompoundFilter(getActivity(), layout, ConfigUtils.FILTER_MODE_ONLY_PERIODS);
            object.setCustomObjectListener(new CompoundFilter.MyCustomObjectListener() {
                @Override
                public void onObjectReady(FilterItem filterItem) {
                    // Code to handle object ready
                    RunTimetablekTask(filterItem.getFrom(), filterItem.getTo());
                }
            });
            object.Init();
        }


        return layout;
    }



    private void RunTimetablekTask(final Date start, final Date finish) {
        final List<ReportingPeriod> list = DateUtils.getDatesInRange(start, finish);
        int i = 0;
        if (list != null && list.size() > 0) {
            tabs.getTabWidget().removeAllViews();

            int currentTabIndex = list.size()-1;
            Date currentDate = new Date();

            for (ReportingPeriod period : list) {
                String title = DateUtils.getWeekDateRange(period.getStart(), period.getFinish());
                setNewTab(tabs, String.valueOf(i), title, R.id.linearLayout1);
                if (currentDate.after(period.getStart()) && currentDate.before(period.getFinish())) {
                    currentTabIndex = i;
                }
                i++;
            }

            ///
            tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    Log.e("TABS", "INSIDE CALL");
                    ReportingPeriod reportingPeriod = list.get(Integer.valueOf(tabId));
                    _from = DateUtils.dateToString(reportingPeriod.getStart());
                    _to = DateUtils.dateToString(reportingPeriod.getFinish());

                    TimetableLoaderTask task = new TimetableLoaderTask();
                    task.execute();

                    updateTabColors();
                }
            });
            ///

            ReportingPeriod reportingPeriod = list.get(currentTabIndex);
            _from = DateUtils.dateToString(reportingPeriod.getStart());
            _to = DateUtils.dateToString(reportingPeriod.getFinish());

            //set current tab and scroll to it
            tabs.setCurrentTab(currentTabIndex);
            tabs.post(new Runnable() {
                @Override
                public void run() {
                    int left = tabs.getCurrentTabView().getLeft();
                    horizontalScrollView.scrollTo(left, 0);
                }
            });


            updateTabColors();
            //
        }
    }


    private void setNewTab(TabHost tabHost, String tag, String title, int contentID ){
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(getTabIndicator(tabHost.getContext(), title)); // new function to inject our own tab layout
        tabSpec.setContent(contentID);
        tabHost.addTab(tabSpec);
    }

    private View getTabIndicator(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setTypeface(FontUtils.getRobotoMedium(getActivity().getAssets()));
        tv.setText(title);
        return view;
    }


    private void updateTabColors() {
        View v = tabs.getChildAt(0);
        TabWidget tabWidget = ((TabWidget)((HorizontalScrollView)v.findViewById(R.id.tabscroller)).getChildAt(0));
        for (int i=0; i<tabWidget.getTabCount(); i++) {
            View childTab = tabWidget.getChildTabViewAt(i);
            AppCompatTextView tabText = ((AppCompatTextView)((LinearLayout) childTab).getChildAt(0));
            if (childTab.isSelected()) {
                tabText.setTextColor(Color.WHITE);
            } else {
                tabText.setTextColor(getResources().getColor(R.color.tm_text_date_range));
            }
        }
    }


    //region Timetable task -----------------------------------------
    private class TimetableLoaderTask extends AsyncTask<Void, Void, List<TimetableItem>> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<TimetableItem> doInBackground(Void... params) {
            //Get alarm timetable
            if (_timetableItems == null) {
                SessionManager sessionManager = new SessionManager(getActivity());
                _timetableItems = sessionManager.getSchoolTimetableList();
                if (_timetableItems == null) {
                    String content = HttpManager.getSchoolTimetable(getActivity(), _token, _schoolId);
                    if (content != null) {
                        _timetableItems = TimetableJSONParser.parseTimetableItems(content);
                        session.setSchoolTimetable(_timetableItems);
                    }
                }
            }

            //Get lessons
            String contentLessons;
            if (_roleType == RoleType.Teacher) {
                contentLessons = HttpManager.getTimetableTeacherLessons(
                        getActivity(),
                        _token,
                        _schoolId,
                        _personId,
                        _from,
                        _to
                );
            } else {
                contentLessons = HttpManager.getTimetableStudentLessons(
                        getActivity(),
                        _token,
                        _personId,
                        _from,
                        _to
                );
            }

            if (contentLessons != null) {
                Log.e("TILES", contentLessons);
                List<Lesson> lessons = LessonJSONParser.parseLessons(contentLessons);

                if (lessons != null && lessons.size() > 0) {
                    List<TimetableItem> list = new ArrayList<>();
                    for (Lesson lesson : lessons) {
                        TimetableItem titem = new TimetableItem(lesson.getDate(), lesson.getPlace(), lesson.getNumber());
                        if (_timetableItems != null && _timetableItems.size() > 0) {
                            for(TimetableItem tt : _timetableItems) {
                                if (tt.getNumber() == lesson.getNumber()) {
                                    titem.setStart(lesson.getStart());
                                    titem.setFinish(lesson.getFinish());

                                    titem.setPlace(lesson.getPlace());

                                    if(!session.isSchoolClassicalSystem() && lesson.getGroupNames() != null) {
                                        titem.setName(lesson.getGroupNames());
                                    }
                                    break;
                                }
                            }
                        }
                        list.add(titem);
                    }

                    return list;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final List<TimetableItem> result) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            //always clear grid
            gridLayout.removeAllViews();

            if (result == null) {
                labelEmtpy.setText("Timetable is empty");    //Расписание на эту неделю не заполнено
                labelEmtpy.setVisibility(View.VISIBLE);
            } else {
                labelEmtpy.setVisibility(View.INVISIBLE);
                TimetableTiles tiles = new TimetableTiles(layout, getActivity(), _inflater);
                tiles.Render(result);
            }
        }

    } // END: Timetable Loader Task
    //endregion
}