package il.co.yomanim.mobileyomanim.compound;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import core.enums.RoleType;
import core.handlers.HttpManager;
import core.handlers.SessionManager;
import core.handlers.parser.EduGroupJSONParser;
import core.helpers.FilterHelper;
import core.model.EduGroup;
import core.model.ReportingPeriod;
import core.utils.DateUtils;
import core.utils.StringUtils;
import il.co.yomanim.mobileyomanim.R;


public class CompoundClassicalFilter {
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

    private String TAG = "ClassicalFilter";
    private String _token;
    private Long _schoolId;
    private Long _personId;
    private FragmentActivity _activity;
    private boolean _isOnlyPeriod;
    private FilterItem _filterItem;
    ProgressBar pb;
    List<Object> tasks;
    RoleType _roleType;

    private int gradeSelectedIndex = 0;
    private int subjectSelectedIndex = 0;
    private int periodSelectedIndex = 0;
    private int weekSelectedIndex = 0;


    public CompoundClassicalFilter(FragmentActivity activity, FrameLayout layout, boolean isOnlyPeriod) {
        _activity = activity;
        _isOnlyPeriod = isOnlyPeriod;

        // Session class instance
        SessionManager session = new SessionManager(layout.getContext());
        _token = session.getUserDetails().get(SessionManager.KEY_TOKEN);
        _schoolId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_SCHOOL_ID));
        _roleType = RoleType.valueOf(session.getUserDetails().get(SessionManager.KEY_ROLE));
        if (_roleType != RoleType.Teacher) {
            _personId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_CHILD_PROFILE_ID));
        } else {
            _personId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_PROFILE_ID));
        }

        //init filter
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
        if (_isOnlyPeriod) {
            PeriodLoaderTask periodLoaderTask = new PeriodLoaderTask();
            periodLoaderTask.execute();
        } else {
            GradeLoaderTask gradeLoaderTask = new GradeLoaderTask();
            gradeLoaderTask.execute();
        }
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

                if (currentDate.after(weeks.get(i).getStart()) && currentDate.before(weeks.get(i).getFinish())) {
                    weekSelectedIndex = i;
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

//                ReportingPeriod pr = weeks.get(weekSelectedIndex);
            ReportingPeriod pr = weeks.get(weekSelectedIndex);
            _filterItem.setFrom(pr.getStart());
            _filterItem.setTo(pr.getFinish());
            listener.onObjectReady(_filterItem);
        }
        else {
            yourRelLay.setVisibility(View.GONE);
        }
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
                Collections.reverse(result);

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

                                        if (_isOnlyPeriod) {
                                            _filterItem.setFrom(result.get(which).getStart());
                                            _filterItem.setTo(result.get(which).getFinish());
                                            listener.onObjectReady(_filterItem);
                                        } else {
                                            LoadWeeks(result.get(which));
                                        }

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

                //todo: load weeks
                if (_isOnlyPeriod) {
                    _filterItem.setFrom(result.get(0).getStart());
                    _filterItem.setTo(result.get(0).getFinish());
                    listener.onObjectReady(_filterItem);
                } else {
                    LoadWeeks(result.get(0));
                }
            }
            else {
                yourRelLay.setVisibility(View.GONE);
            }
        }
    }
    //endregion PeriodLoaderTask



    //region Grade task -----------------------------------------
    private class GradeLoaderTask extends AsyncTask<Void, Void, List<EduGroup>> {
        @Override
        protected void onPreExecute() {
            //TODO: progressbar show
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<EduGroup> doInBackground(Void... params) {
            Log.e(TAG, "---------- GRADE");
            FilterHelper filterHelper = new FilterHelper(_activity, _token);
            return filterHelper.getGrades(_personId, _schoolId, _roleType == RoleType.Teacher);
        }

        @Override
        protected void onPostExecute(final List<EduGroup> result) {
            //TODO: hide progressbar
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null || result.size() == 0) {
                Toast.makeText(_activity, "Cant' connect to web service", Toast.LENGTH_LONG).show();
            }
            else {
                // build string array
                final String[] items = new String[result.size()];
                for (int i=0; i<result.size(); i++) {
                    items[i] = result.get(i).getFullName();
                }

                // generate modal dialog
                ImageButton btn = (ImageButton)_activity.findViewById(R.id.imageButtonToolbarGroup);
                RelativeLayout yourRelLay = (RelativeLayout) btn.getParent();
                yourRelLay.setVisibility(View.VISIBLE);

                ((RelativeLayout) btn.getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View args) {
                        new MaterialDialog.Builder(_activity)
                                .title(_activity.getResources().getString(R.string.modal_grade_title))
                                .items(items)
                                .itemsCallbackSingleChoice(gradeSelectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        gradeSelectedIndex = which;
                                        _filterItem.setGradeId(result.get(which).getId());
                                        //load subjects
                                        SubjectLoaderTask groupLoaderTask = new SubjectLoaderTask(false);
                                        groupLoaderTask.execute(result.get(which).getId());
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

                _filterItem.setGradeId(result.get(0).getId());

                //load subjects
                SubjectLoaderTask groupLoaderTaskDefault = new SubjectLoaderTask(true);
                groupLoaderTaskDefault.execute(result.get(0).getId());
            }
        }
    } // END: Grade Loader Task
    //endregion



    //region Subject task -----------------------------------------
    private class SubjectLoaderTask extends AsyncTask<Long, Void, List<EduGroup>> {
        public boolean isLoadPeriods;
        public SubjectLoaderTask(boolean loadPeriods) {
            isLoadPeriods = loadPeriods;
        }
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<EduGroup> doInBackground(Long... params) {
            Log.e(TAG, "---------- SUBJECT");
            String contentGroups;
            if (_roleType == RoleType.Teacher) {
                contentGroups = HttpManager.getSubjectByGradeForTeacher(_activity, _token, params[0], _personId);
            } else {
                contentGroups = HttpManager.getSubjectByGradeForStudent(_activity, _token, params[0]);
            }

            if (contentGroups != null) {
                return EduGroupJSONParser.parseEduGroupList(contentGroups, false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<EduGroup> result)
        {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null || result.size() == 0) {
                Toast.makeText(_activity, "Cant' connect to web service", Toast.LENGTH_LONG).show();
                Log.e("GroupLoaderTask", "Cant' connect to web service");
            }
            else
            {
                // build string array
                final String[] items = new String[result.size()];
                for (int i=0; i<result.size(); i++) {
                    items[i] = (!TextUtils.isEmpty(result.get(i).getFullName())) ?
                            result.get(i).getFullName() :
                            result.get(i).getName();
                }


                // generate modal dialog
                ImageButton btn = (ImageButton)_activity.findViewById(R.id.imageButtonToolbarSubject);
                RelativeLayout yourRelLay = (RelativeLayout) btn.getParent();
                yourRelLay.setVisibility(View.VISIBLE);


                ((RelativeLayout) btn.getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View args) {
                        new MaterialDialog.Builder(_activity)
                                .title(_activity.getResources().getString(R.string.modal_subject_title))
                                .items(items)
                                .itemsCallbackSingleChoice(subjectSelectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        /**
                                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                         * returning false here won't allow the newly selected radio button to actually be selected.
                                         **/
                                        subjectSelectedIndex = which;
                                        _filterItem.setSubjectId(result.get(which).getId());

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

                _filterItem.setSubjectId(result.get(0).getId());

                //Load periods
                if (isLoadPeriods) {
                    PeriodLoaderTask periodLoaderTask = new PeriodLoaderTask();
                    periodLoaderTask.execute();
                } else {
                    listener.onObjectReady(_filterItem);
                }
            }
        }
    } // END: Group Loader Task
    //endregion
}