package il.co.yomanim.mobileyomanim.compound;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import core.handlers.SessionManager;
import core.helpers.ConstantsHelper;
import core.helpers.FilterHelper;
import core.model.EduGroup;
import core.model.ReportingPeriod;
import core.utils.ConfigUtils;
import core.utils.DateUtils;
import core.utils.StringUtils;
import il.co.yomanim.mobileyomanim.R;


public class CompoundFilter {
    // Step 1 - This interface defines the type of messages I want to communicate to my owner
    public interface MyCustomObjectListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        void onObjectReady(FilterItem filterItem);
        // or when data has been loaded
//        public void onDataLoaded(SomeData data);
    }

    // Step 2 - This variable represents the listener passed in by the owning object
    // The listener must implement the events interface and passes messages up to the parent.
    private MyCustomObjectListener listener;


    private String TAG = "CompoundFilter";
    private String _token;
    private Long _schoolId;
    private Long _personId;
    private SessionManager session;
    private FragmentActivity _activity;
    private FrameLayout _layout;
    private int _mode;
    private FilterItem _filterItem;
    private TextView _subTitle;
    ProgressBar pb;
    List<Object> tasks;
    public int groupSelectedIndex = 0;
    public int periodSelectedIndex = 0;
    public int weekSelectedIndex = 0;
    private boolean isWeekHidden = false;


    public void hideWeeks(boolean flag) {
        isWeekHidden = flag;
        RelativeLayout rel = (RelativeLayout) _activity.findViewById(R.id.relButtonWeek);
        if (rel != null) {
            rel.setVisibility((flag) ? View.GONE : View.VISIBLE);
        }
    }

    public CompoundFilter(FragmentActivity activity, FrameLayout layout, int mode) {
        _activity = activity;
        _layout = layout;
        _mode = mode;


        // Session class instance
        session = new SessionManager(layout.getContext());
        _token = session.getUserDetails().get(SessionManager.KEY_TOKEN);
        _schoolId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_SCHOOL_ID));
        String role = session.getUserDetails().get(SessionManager.KEY_ROLE);
//        RoleType roleType = RoleType.valueOf(session.getUserDetails().get(SessionManager.KEY_ROLE));
        if (role.equals(ConstantsHelper.ROLE_EDU_PARENT)) {
            _personId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_CHILD_PROFILE_ID));
        } else {
            _personId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_PROFILE_ID));
        }

        _subTitle = (TextView) _activity.findViewById(R.id.toolbar_subtitle);
        _subTitle.setVisibility(View.GONE);


        _filterItem = new FilterItem();
        _filterItem.setToken(_token);
        _filterItem.setGroupId(-1);
        _filterItem.setSchoolId(_schoolId);
        _filterItem.setPersonId(_personId);


        // set null or default listener or accept as argument to constructor
        this.listener = null;

        //init
        pb = (ProgressBar) layout.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        tasks = new ArrayList<>();
    }


    // Assign the listener implementing events interface that will receive the events
    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }


    public void Init() {
        PeriodLoaderTask periodLoaderTask = new PeriodLoaderTask();
        periodLoaderTask.execute();
    }



    //region Period task -----------------------------------------
    private class PeriodLoaderTask extends AsyncTask<Void, Void, List<ReportingPeriod>> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<ReportingPeriod> doInBackground(Void... params) {
            Log.e(TAG, "---------- PERIOD");
            FilterHelper filterHelper = new FilterHelper(_activity, _token);
            return filterHelper.getReportingPeriods(_schoolId);
        }

        @Override
        protected void onPostExecute(final List<ReportingPeriod> result) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            ImageButton btn = (ImageButton) _activity.findViewById(R.id.imageButtonToolbarPeriod);
            RelativeLayout yourRelLay = (RelativeLayout) btn.getParent();

            if (result != null) {
                //reverse periods from oldest to newer
//                Collections.reverse(result);

                // build string array
                final String[] items = new String[result.size()];
                for (int i = 0; i < result.size(); i++) {
                    items[i] = MessageFormat.format("{0} {1}",
                            String.valueOf(result.get(i).getYear()).replace(",", ""),
                            StringUtils.removeExtraSpaces(result.get(i).getName())
                    );
                }

                // generate modal dialog
                yourRelLay.setVisibility(View.VISIBLE);

                // click
                ((RelativeLayout) btn.getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View args) {
                        new MaterialDialog.Builder(_activity)
                            .title(_activity.getResources().getString(R.string.modal_period))
                            .items(items)
                            .itemsCallbackSingleChoice(periodSelectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    periodSelectedIndex = which;
                                    session.updateFilterSelectedIndex(SessionManager.KEY_FILTER_PERIOD_INDEX, which);
                                    RunPeriod(result.get(which));
                                    return true;
                                }
                            })
                            .positiveText(_activity.getResources().getString(R.string.button_choose))
                            .negativeText(_activity.getResources().getString(R.string.button_cancel))
                            .titleColorRes(R.color.modal_title)
                            .widgetColorRes(R.color.modal_radio)
                            .itemColorRes(R.color.modal_title)
                            .positiveColorRes(R.color.modal_btn_choose)
                            .negativeColorRes(R.color.modal_btn_cancel)
                            .show();
                    }
                });

                //LOAD PERIOD BY DEFAULT FIRST
                RunPeriod(result.get(periodSelectedIndex));
            }
            else {
                yourRelLay.setVisibility(View.GONE);
            }
        }
    }


    private void RunPeriod(ReportingPeriod period) {
        Log.e(TAG, period.getStart().toString() + " : " + period.getFinish().toString());

        if (_mode == ConfigUtils.FILTER_MODE_ONLY_PERIODS) {
            _filterItem.setPeriodId(period.getId());
            _filterItem.setFrom(period.getStart());
            _filterItem.setTo(period.getFinish());
            listener.onObjectReady(_filterItem);
        }
        else {
            EduGroupLoaderTask eduGroupLoaderTask = new EduGroupLoaderTask(period);
            eduGroupLoaderTask.execute();
        }
    }



    //region MultiClass
    public class EduGroupLoaderTask extends AsyncTask<Void, Void, List<EduGroup>> {
        private ReportingPeriod currentPeriod;
        public EduGroupLoaderTask(ReportingPeriod period) {
            currentPeriod = period;
        }
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }
        @Override
        protected List<EduGroup> doInBackground(Void... params) {
            Log.e(TAG, "---------- GROUP");
            FilterHelper filterHelper = new FilterHelper(_activity, _token);
            return filterHelper.getEduSubjectGroups(_personId, _schoolId);
        }

        @Override
        protected void onPostExecute(final List<EduGroup> result) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null || result.size() == 0) {
                Toast.makeText(_activity, "Cant' connect to web service", Toast.LENGTH_LONG).show();
//                labelEmtpy.setText(_role.equals(ConstantsHelper.ROLE_EDU_STAFF) ? "У вас нет групп, где вы преподаете" : "Вы не состоите ни в одной группе");
//                labelEmtpy.setVisibility(View.VISIBLE);
                //TODO: set and return empty message
            }
            else {
                //get groups with period rules
                final List<EduGroup> filteredGroups = new ArrayList<>();
                for (EduGroup group : result) {
                    if (group.getPeriodId() == 0 || group.getPeriodId() == currentPeriod.getId()) {
                        filteredGroups.add(group);
                    }
                }

                if (filteredGroups.size() > 0) {
                    // build string array
                    final String[] items = new String[filteredGroups.size()];
                    for (int i=0; i<filteredGroups.size(); i++) {
                        items[i] = filteredGroups.get(i).getFullName();
                    }

                    // generate modal dialog
                    ImageButton btn = (ImageButton) _activity.findViewById(R.id.imageButtonToolbarGroup);
                    if (!session.isSchoolClassicalSystem()) {
                        TextView btnTitle = (TextView) _activity.findViewById(R.id.textViewToolbarGroup);
                        btnTitle.setText(_layout.getResources().getString(R.string.button_group));
                    }
                    RelativeLayout yourRelLay = (RelativeLayout) btn.getParent();
                    yourRelLay.setVisibility(View.VISIBLE);

                    ((RelativeLayout) btn.getParent()).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View args) {
                            new MaterialDialog.Builder(_layout.getContext())
                                    .title(_layout.getResources().getString(R.string.button_group))
                                    .items(items)
                                    .itemsCallbackSingleChoice(groupSelectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            /**
                                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                             * returning false here won't allow the newly selected radio button to actually be selected.
                                             **/
                                            groupSelectedIndex = which;
                                            _filterItem.setGroupId(filteredGroups.get(which).getId());
                                            _filterItem.setPeriodId(currentPeriod.getId());
                                            showGroupSubTitle(filteredGroups.get(which).getName());

                                            //todo: update session
                                            session.updateFilterSelectedIndex(SessionManager.KEY_FILTER_GROUP_INDEX, which);

                                            listener.onObjectReady(_filterItem);

                                            return true;
                                        }
                                    })
                                    .positiveText(_layout.getResources().getString(R.string.button_choose))
                                    .negativeText(_layout.getResources().getString(R.string.button_cancel))
                                    .titleColorRes(R.color.primary)
                                    .widgetColorRes(R.color.primary)
                                    .positiveColorRes(R.color.primary)
                                    .negativeColor(Color.RED)
                                    .show();
                        }
                    });

                    //todo: session?
                    _filterItem.setGroupId(filteredGroups.get(groupSelectedIndex).getId());
                    showGroupSubTitle(filteredGroups.get(groupSelectedIndex).getName());

                    if (_mode == ConfigUtils.FILTER_MODE_NO_WEEKS) {
                        _filterItem.setPeriodId((int)currentPeriod.getId());

                        //todo: comment maybe
                        _filterItem.setPeriodId(currentPeriod.getId());
                        _filterItem.setFrom(currentPeriod.getStart());
                        _filterItem.setTo(currentPeriod.getFinish());
                        listener.onObjectReady(_filterItem);
                    }
                    else {
                        if (!isWeekHidden)
                            LoadWeeks(currentPeriod);
                    }
                }
            }

            //todo: shit
//            listener.onObjectReady(_filterItem);
        }
    } // END: EduGroup Loader Task, Multiclass
    //endregion


    private void showGroupSubTitle(String groupName) {
        _subTitle.setText(groupName);
        _subTitle.setVisibility(View.VISIBLE);
    }


    private void LoadWeeks(ReportingPeriod period) {
        Log.e(TAG, "---------- WEEK");
        //load weeks
        final List<ReportingPeriod> weeks = DateUtils.getDatesInRange(period.getStart(), period.getFinish());
        ImageButton btn = (ImageButton) _activity.findViewById(R.id.imageButtonToolbarWeek);
        RelativeLayout yourRelLay = (RelativeLayout) btn.getParent();
        if (weeks != null && weeks.size() > 0) {

            Collections.reverse(weeks);

            // generate modal dialog
            yourRelLay.setVisibility(View.VISIBLE);

            //build week strings
            final String[] witems = new String[weeks.size()];
            Date currentDate = new Date();
            for (int i = 0; i < weeks.size(); i++) {
                witems[i] =DateUtils.getWeekDateRange(weeks.get(i).getStart(), weeks.get(i).getFinish());

                if (weekSelectedIndex == 0) {
                    if (currentDate.after(weeks.get(i).getStart()) && currentDate.before(weeks.get(i).getFinish())) {
                        weekSelectedIndex = i;
                    }
                }
            }

            // click
            ((RelativeLayout) btn.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View args) {
                    new MaterialDialog.Builder(_activity)
                            .title(_activity.getResources().getString(R.string.modal_week))
                            .items(witems)
                            .itemsCallbackSingleChoice(weekSelectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    /**
                                     * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                     * returning false here won't allow the newly selected radio button to actually be selected.
                                     **/
                                    weekSelectedIndex = which;

                                    session.updateFilterSelectedIndex(SessionManager.KEY_FILTER_WEEK_INDEX, which);

                                    _filterItem.setFrom(weeks.get(weekSelectedIndex).getStart());
                                    _filterItem.setTo(weeks.get(weekSelectedIndex).getFinish());
                                    listener.onObjectReady(_filterItem);

                                    return true;
                                }
                            })
                            .positiveText(_activity.getResources().getString(R.string.button_choose))
                            .negativeText(_activity.getResources().getString(R.string.button_cancel))
                            .titleColorRes(R.color.modal_title)
                            .widgetColorRes(R.color.modal_radio)
                            .itemColorRes(R.color.modal_title)
                            .positiveColorRes(R.color.modal_btn_choose)
                            .negativeColorRes(R.color.modal_btn_cancel)
                            .show();
                }
            });


            ReportingPeriod pr = weeks.get(weekSelectedIndex);
            _filterItem.setPeriodId(pr.getId());
            _filterItem.setFrom(pr.getStart());
            _filterItem.setTo(pr.getFinish());
            listener.onObjectReady(_filterItem);
        }
        else {
            yourRelLay.setVisibility(View.GONE);
        }
    }
}