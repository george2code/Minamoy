package il.co.yomanim.mobileyomanim.grids;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import core.enums.RoleType;
import core.handlers.HttpManager;
import core.handlers.SessionManager;
import core.handlers.parser.LessonJSONParser;
import core.handlers.parser.MarkJSONParser;
import core.model.Task;
import core.model.TaskStatus;
import core.model.journal.Mark;
import core.utils.AppUtils;
import core.utils.FontUtils;
import core.utils.InputFilterMinMax;
import core.utils.LocaleUtils;
import core.utils.StringUtils;
import core.view.HomeworkView;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.fragments.HomeworkFragment;


public class HomeworkTable {

    private HomeworkFragment _context;
    private ProgressBar _progressBar;
    private List<Object> _tasks;
    private int _gridId;
    private List<HomeworkView> _list;
    private String _token;
    Typeface fontMedium;
    Typeface fontLight;
    RoleType _roleType;
    private boolean isRtl;

    public HomeworkTable(HomeworkFragment context, int gridId, List<HomeworkView> list, String token, RoleType role) {
        _context = context;
        _gridId = gridId;
        _list = list;
        _token = token;
        _roleType = role;
        _tasks = new ArrayList<>();
        isRtl = LocaleUtils.isRtl(_context.getActivity());
    }

    public static void ClearGrid(FragmentActivity activity, int gridId) {
        GridLayout gridLayout = (GridLayout) activity.findViewById(gridId);
        gridLayout.removeAllViews();
    }

    public void Render() {
        try {
            int rows = 1 + _list.size();

            GridLayout gridLayout = (GridLayout) _context.getActivity().findViewById(_gridId);
            gridLayout.removeAllViews();
            gridLayout.setColumnCount(4);
            gridLayout.setRowCount(rows);

            //Fonts
            fontMedium = FontUtils.getRobotoMedium(_context.getActivity().getAssets());
            fontLight = FontUtils.getRobotoLight(_context.getActivity().getAssets());

            //region Header --------------------------------------------------
            int header_bg = _context.getResources().getColor(R.color.hw_header_bg);
            int header_text_color = _context.getResources().getColor(R.color.hw_header_text);

            if (isRtl) {
                //grid //row //column //width //height //text //bg //text color //textSize sp //font
                AddCell(gridLayout, 0, 0, 15, 70, _context.getString(R.string.homework_th_status), header_bg, header_text_color, 13, Gravity.CENTER, fontMedium);
                AddCell(gridLayout, 0, 1, 20, 70, _context.getString(R.string.homework_th_lesson), header_bg, header_text_color, 13, Gravity.CENTER, fontMedium);
                AddCell(gridLayout, 0, 2, 54, 70, _context.getString(R.string.homework_th_description), header_bg, header_text_color, 13, Gravity.START | Gravity.CENTER, fontMedium);
                AddCell(gridLayout, 0, 3, 10, 70, "#", header_bg, header_text_color, 13, Gravity.CENTER, fontMedium);
            } else {
                AddCell(gridLayout, 0, 0, 10, 70, "#", header_bg, header_text_color, 13, Gravity.CENTER, fontMedium);
                AddCell(gridLayout, 0, 1, 54, 70, _context.getString(R.string.homework_th_description), header_bg, header_text_color, 13, Gravity.START | Gravity.CENTER_VERTICAL , fontMedium);
                AddCell(gridLayout, 0, 2, 20, 70, _context.getString(R.string.homework_th_lesson), header_bg, header_text_color, 13, Gravity.CENTER, fontMedium);
                AddCell(gridLayout, 0, 3, 15, 70, _context.getString(R.string.homework_th_status), header_bg, header_text_color, 13, Gravity.CENTER, fontMedium);
            }
            //endregion


            // Body ----------------------------------------------------
            int hw_white_color = _context.getResources().getColor(R.color.hw_state_text);
            int hw_item_text_color = _context.getResources().getColor(R.color.hw_item_text);


            int index = 1;
            for (HomeworkView item : _list) {
                int statusBg = _context.getResources().getColor(R.color.hw_header_bg);

                // get state bg color
                switch (item.getWorkStatus()) {
                    case "Sent":
                        statusBg = _context.getResources().getColor(R.color.hw_state_bg_sent);
                        break;
                    case "Closed":
                        statusBg = _context.getResources().getColor(R.color.hw_state_bg_close);
                        break;
                    default:
                        break;
                }

                String commentText = StringUtils.cutString(item.getWorkText(), 37).replaceAll("[\n\r]", " ");

                if (isRtl) {
                    AddCell(gridLayout, index, 0, 15, 120, item.getWorkStatus(), statusBg, hw_white_color, 10, Gravity.CENTER, fontMedium);
                    AddTwoRowCell(gridLayout, index, 1, item.getLessonDate(), "lesson " + item.getLessonNumber(), hw_item_text_color);
                    AddCell(gridLayout, index, 2, 54, 120, commentText, hw_white_color, header_text_color, 10, Gravity.START | Gravity.CENTER_VERTICAL, fontLight);
                    AddCell(gridLayout, index, 3, 10, 120, String.valueOf(item.getWorkId()), hw_white_color, hw_item_text_color, 10, Gravity.CENTER, fontLight);
                } else {
                    AddCell(gridLayout, index, 0, 10, 120, String.valueOf(item.getWorkId()), hw_white_color, hw_item_text_color, 10, Gravity.CENTER, fontLight);
                    AddCell(gridLayout, index, 1, 54, 120, commentText, hw_white_color, header_text_color, 10, Gravity.START | Gravity.CENTER_VERTICAL, fontLight);
                    AddTwoRowCell(gridLayout, index, 2, item.getLessonDate(), "lesson " + item.getLessonNumber(), hw_item_text_color);
                    AddCell(gridLayout, index, 3, 15, 120, item.getWorkStatus(), statusBg, hw_white_color, 10, Gravity.CENTER, fontMedium);
                }

                index ++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------------------------
    private TextView initCell(int row, int column, int width, int height, String text, int bgColor,
                              int textColor, int textSize, int gravity, Typeface textFont)
    {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.setMargins(1, 0, 1, 1);
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(row);

        TextView recyclableTextView = makeTableRowWithText(text, width, height);
        recyclableTextView.setGravity(gravity);
        if (gravity == (Gravity.START | Gravity.CENTER_VERTICAL)) {
            if (isRtl)
                recyclableTextView.setPadding(20, 0, 20, 0);
            else
                recyclableTextView.setPadding(20, 0, 0, 0);
        }


        recyclableTextView.setLayoutParams(param);
        recyclableTextView.setTypeface(textFont);
        recyclableTextView.setBackgroundColor(bgColor);
        recyclableTextView.setTextColor(textColor);
        recyclableTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        return recyclableTextView;
    }


    private void AddTwoRowCell(GridLayout gridLayout, final int row, int column,
                               String textTop, String textBottom, int textTopColor) {
        LinearLayout ll = new LinearLayout(_context.getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);

        int screenWidth = _context.getResources().getDisplayMetrics().widthPixels;

        ll.setHorizontalGravity(View.TEXT_ALIGNMENT_CENTER);

        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = ActionBar.LayoutParams.MATCH_PARENT;
        param.setMargins(1, 0, 1, 1);
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(row);

        ll.setLayoutParams(param);

        TextView recyclableTextView = new TextView(_context.getActivity());
        recyclableTextView.setText(textTop);
        recyclableTextView.setTextColor(textTopColor);
        recyclableTextView.setTextSize(10);
        recyclableTextView.setHeight(60);
        recyclableTextView.setWidth(20 * screenWidth / 100);
        recyclableTextView.setGravity(Gravity.BOTTOM | Gravity.CENTER);
        ll.addView(recyclableTextView);


        TextView recyclableTextView2 = new TextView(_context.getActivity());
        recyclableTextView2.setText(textBottom);
        recyclableTextView2.setTextColor(Color.BLACK);
        recyclableTextView2.setTextSize(10);
        recyclableTextView2.setHeight(60);
        recyclableTextView2.setGravity(Gravity.CENTER);
        ll.addView(recyclableTextView2);

        gridLayout.addView(ll);
    }


    private void AddCell(GridLayout gridLayout, final int row, int column, int width,
                         int height, String text, int bgColor, int textColor,
                         int textSize, int gravity, Typeface textFont)
    {
        TextView recyclableTextView = initCell(row, column, width, height, text, bgColor, textColor, textSize, gravity, textFont);
        final int itemIndex = row - 1;

        //row Click
        recyclableTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (itemIndex >= 0) {
                    HomeworkView homeworkView = _list.get(itemIndex);

                    //show dialog
                    MaterialDialog materialDialog = new MaterialDialog.Builder(_context.getActivity())
                            .title(MessageFormat.format("Homework # {0}", StringUtils.removeComma(homeworkView.getWorkId())))
                            .customView(R.layout.dialog_homework, false)
                            .positiveText("Ok")
                            .titleColorRes(R.color.hw_dialog_title)
                            .widgetColorRes(R.color.primary)
                            .positiveColorRes(R.color.hw_dialog_positive_button)
                            .backgroundColor(_context.getResources().getColor(R.color.hw_state_text))
                            .build();

                    View view = materialDialog.getCustomView();

                    //set styles for head titles
                    TextView textViewTitle = (TextView) view.findViewById(R.id.tvHomeworkTitleSubject);
                    textViewTitle.setTypeface(fontMedium);
                    textViewTitle = (TextView) view.findViewById(R.id.tvHomeworkTitleLesson);
                    textViewTitle.setTypeface(fontMedium);
                    textViewTitle = (TextView) view.findViewById(R.id.tvHomeworkTitleWork);
                    textViewTitle.setTypeface(fontMedium);
                    textViewTitle = (TextView) view.findViewById(R.id.tvHomeworkTitleDesc);
                    textViewTitle.setTypeface(fontMedium);

                    _progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

                    //render head info
                    TextView textView = (TextView)view.findViewById(R.id.tvHomeworkSubjectName);
                    textView.setTypeface(fontMedium);
                    textView.setText(homeworkView.getSubjectName());

                    textView = (TextView)view.findViewById(R.id.tvHomeworkLessonName);
                    textView.setTypeface(fontMedium);
                    textView.setText(MessageFormat.format("{0}, {1}", homeworkView.getLessonDate(), homeworkView.getLessonNumber()));

                    textView = (TextView)view.findViewById(R.id.tvHomeworkWorkText);
                    textView.setTypeface(fontMedium);
                    textView.setText(homeworkView.getWorkStatus());

                    textView = (TextView)view.findViewById(R.id.tvHomeworkDescName);
                    textView.setTypeface(fontMedium);
                    textView.setText(homeworkView.getWorkText());

                    //render task table
                    TaskParamResult taskParamResult = new TaskParamResult(
                            view,
                            _list.get(row-1).getLessonId(),
                            _list.get(row-1).getWorkId());
                    WorksLoaderTask worksLoaderTask = new WorksLoaderTask();
                    worksLoaderTask.execute(taskParamResult);


                    materialDialog.show();
                }
            }
        });


        gridLayout.addView(recyclableTextView);
    }


    //region util method
    private TextView recyclableTextView;

    public TextView makeTableRowWithText(String text, int widthInPercentOfScreenWidth, int fixedHeightInPixels) {
        int screenWidth = _context.getResources().getDisplayMetrics().widthPixels;

        recyclableTextView = new TextView(_context.getActivity());
        recyclableTextView.setText(text);
        recyclableTextView.setTextColor(Color.BLACK);
        recyclableTextView.setBackgroundColor(Color.WHITE);
        recyclableTextView.setTextSize(12);
        recyclableTextView.setWidth(widthInPercentOfScreenWidth * screenWidth / 100);
        recyclableTextView.setHeight(fixedHeightInPixels);

        return recyclableTextView;
    }
    //endregion


    //region Homework task -----------------------------------------
    private class TaskParamResult {
        public List<Task> tasks;
        public List<Mark> marks;
        public View view;
        public long lessonId;
        public long workId;

        public TaskParamResult(View view, long lessonId, long workId) {
            this.view = view;
            this.lessonId = lessonId;
            this.workId = workId;
        }
    }


    public TextView getTableTaskCell(String text, int gravity, Typeface font, int fontSize, int textColor) {
        recyclableTextView = new TextView(_context.getActivity());
        recyclableTextView.setText(text);
        recyclableTextView.setTextColor(textColor);
        recyclableTextView.setBackground(_context.getResources().getDrawable(R.drawable.valuecellborder)); //Color.parseColor("#efeff0")
        recyclableTextView.setTextSize(fontSize);
        recyclableTextView.setTypeface(font);
        recyclableTextView.setGravity(gravity);

        return recyclableTextView;
    }



    //new - working - discuss - reopened - completed - closed - cancelled
    //выдано = выполнено, отменено
    //выполнено = на доработке
    //на доработке = выполнено, отменено
    //отменено = выполнено, на доработке
    public Spinner getTableSpinnerCell(String text, int gravity, final Task task, final TableRow tableRow) {
        final Spinner mySpinner = new Spinner(_context.getActivity());
        try {
            ArrayAdapter<TaskStatus> arrayAdapter = new ArrayAdapter<TaskStatus>(_context.getActivity(), R.layout.spinner_item, TaskStatus.values()) {
                // Disable click item < month current
                @Override
                public boolean isEnabled(int position) {
                    if ((task.getStatus().equalsIgnoreCase("new") && (position == 0 || position == 2 || position == 4 || position == 5 || position == 6)) ||
                            (task.getStatus().equalsIgnoreCase("working") && (position == 1 || position == 2 || position == 4 || position == 5 || position == 6)) ||
                            (task.getStatus().equalsIgnoreCase("discuss") && (position == 2 || position == 1 || position == 4 || position == 5 || position == 6)) ||
                            (task.getStatus().equalsIgnoreCase("reopened") && (position == 3 || position == 4 || position == 5 || position == 6)) ||
                            (task.getStatus().equalsIgnoreCase("completed") && (position == 4 || position == 3)) ||
                            (task.getStatus().equalsIgnoreCase("closed") && (position == 5 || position == 3)) ||
                            (task.getStatus().equalsIgnoreCase("cancelled") && (position == 6 || position == 4 || position == 3))) {
                        return true;
                    }
                    return false;
                }
                // Change color item
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View mView = super.getDropDownView(position, convertView, parent);
                    TextView mTextView = (TextView) mView;
                    if ((task.getStatus().equalsIgnoreCase("new") && (position == 0 || position == 2 || position == 4 || position == 5 || position == 6)) ||
                            (task.getStatus().equalsIgnoreCase("working") && (position == 1 || position == 2 || position == 4 || position == 5 || position == 6)) ||
                            (task.getStatus().equalsIgnoreCase("discuss") && (position == 2 || position == 1 || position == 4 || position == 5 || position == 6)) ||
                            (task.getStatus().equalsIgnoreCase("reopened") && (position == 3 || position == 4 || position == 5 || position == 6)) ||
                            (task.getStatus().equalsIgnoreCase("completed") && (position == 4 || position == 3)) ||
                            (task.getStatus().equalsIgnoreCase("closed") && (position == 5 || position == 3)) ||
                            (task.getStatus().equalsIgnoreCase("cancelled") && (position == 6 || position == 4 || position == 3))) {
                        mTextView.setTextColor(Color.BLACK);
                    } else {
                        mTextView.setTextColor(Color.GRAY);
                    }
                    return mView;
                }
            };
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            mySpinner.setAdapter(arrayAdapter);

            if (!TextUtils.isEmpty(text)) {
                mySpinner.setSelection(arrayAdapter.getPosition(TaskStatus.valueOf(text)));
            }

            mySpinner.setGravity(gravity);
        }
        catch (Exception e) {
            Log.e("getTableSpinner", e.getMessage());
        }
        mySpinner.setBackground(_context.getResources().getDrawable(R.drawable.valuecellborder));
        mySpinner.setTag(false);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if ((boolean)parentView.getTag()) {
                    String newStatus = (TaskStatus.values()[position]).toString();
                    Log.e("SPINNER1", newStatus);
                    task.setStatus(newStatus);

                    StatusTask statusTask = new StatusTask(task, tableRow);
                    statusTask.execute();
                } else {
                    parentView.setTag(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        return mySpinner;
    }


    private class WorksLoaderTask extends AsyncTask<TaskParamResult, Void, TaskParamResult> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected TaskParamResult doInBackground(TaskParamResult... params) {
            //get person name and work state
            String taskListContent = HttpManager.getTasksByWork(_context.getActivity(), _token, params[0].workId);
            if (taskListContent != null) {
                params[0].tasks = LessonJSONParser.parseTasks(taskListContent);
            }
            if (params[0].tasks != null && params[0].tasks.size() > 0) {
                //get person marks
                String cmarks = HttpManager.getHomeworkMarks(_context.getActivity(), _token, params[0].workId);
                if (cmarks != null) {
                    params[0].marks = MarkJSONParser.parseMarks(cmarks);
                }
            }
            return params[0];
        }
        @Override
        protected void onPostExecute(final TaskParamResult taskParamResult) {
            if (taskParamResult.tasks != null && taskParamResult.tasks.size() > 0) {
                //init grid
                final TableLayout tableLayoutLayoutTasks = (TableLayout) taskParamResult.view.findViewById(R.id.tableLayoutHomeworkTasks);
                tableLayoutLayoutTasks.removeAllViews();

                long childId = 0;
                if (_roleType != RoleType.Teacher)
                    childId = Long.parseLong(new SessionManager(_context.getActivity()).getUserDetails().get(SessionManager.KEY_CHILD_PROFILE_ID));

                for(final Task task : taskParamResult.tasks) {
                    //Skip all persons except current child, if ROLE is not teacher. I mean,
                    //show only current child, not all list
                    if (childId > 0 && task.getPerson() != childId)
                        continue;

                    //Create new table row
                    final TableRow tableRow = new TableRow(_context.getActivity());

                    //student name
                    TextView textName = getTableTaskCell(task.getPersonName(), Gravity.START | Gravity.CENTER_VERTICAL, fontMedium, 11, Color.parseColor("#000000"));
                    //status
                    Spinner spinnerStatus = getTableSpinnerCell(task.getStatus(), Gravity.CENTER, task, tableRow);
                    //mark or message
                    TextView textMark = new TextView(_context.getActivity());
                    boolean isMarkExists = false;
                    if (taskParamResult.marks != null && taskParamResult.marks.size() > 0) {
                        for(Mark mark : taskParamResult.marks) {
                            if (mark.getPerson() == task.getPerson()) {
                                textMark = getTableTaskCell(mark.getValue(), Gravity.CENTER, fontMedium, 11, mark.getMarkBackground());
                                isMarkExists = true;
                                break;
                            }
                        }
                    }
                    if (!isMarkExists) {
                        textMark = getTableTaskCell((_roleType == RoleType.Teacher || task.getStatus().equalsIgnoreCase("completed"))
                                ? "Add score" : "", Gravity.CENTER, fontLight, 11, Color.parseColor("#000000"));
                    }


//                    if (isRtl) {
//                        tableRow.addView(textMark);
//                        tableRow.addView(spinnerStatus);
//                        tableRow.addView(textName);
//                    }
//                    else
//                    {
                        tableRow.addView(textName);
                        tableRow.addView(spinnerStatus);
                        tableRow.addView(textMark);
//                    }


                    //set mark setup dialog only for the teachers
                    if (_roleType == RoleType.Teacher) {
                        setupMarkCell(textMark, tableRow);
                    }

                    //add row to the table
                    tableLayoutLayoutTasks.addView(tableRow);
                }
            }
        }
    }
    // END: Homework Loader Task


    private void setupMarkCell(TextView text, final TableRow tableRow) {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
                EditText edit = new EditText(_context.getActivity());
                edit.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                edit.setGravity(Gravity.CENTER_HORIZONTAL);
                edit.setLayoutParams(params);
                edit.setMaxLines(1);
                edit.setTextSize(11);
                edit.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "100")});

                edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            //code to execute when EditText loses focus
                            String txt = ((EditText)v).getText().toString();
                            if (!TextUtils.isEmpty(txt)) {
                                //mark
                                Mark mark = new Mark();
                                mark.setValue(txt);
                                TextView tvMark = getTableTaskCell(mark.getValue(), Gravity.CENTER, fontMedium, 11, mark.getMarkBackground());

                                tableRow.removeViewAt(2);
                                tableRow.addView(tvMark);

                                setupMarkCell(tvMark, tableRow);
                            } else {
                                //add score text
                                TextView tv1 = getTableTaskCell((_roleType == RoleType.Teacher) ?
                                        "Add score" : "", Gravity.CENTER, fontLight, 11, Color.parseColor("#000000"));

                                tableRow.removeViewAt(2);
                                tableRow.addView(tv1);

                                setupMarkCell(tv1, tableRow);
                            }
                        }
                    }
                });

                edit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            hideKeyboard();
                            if (!TextUtils.isEmpty(v.getText())) {
                                //mark
                                Mark mark = new Mark();
                                mark.setValue(v.getText().toString());
                                TextView tvMark = getTableTaskCell(mark.getValue(), Gravity.CENTER, fontMedium, 11, mark.getMarkBackground());

                                tableRow.removeViewAt(2);
                                tableRow.addView(tvMark);

                                setupMarkCell(tvMark, tableRow);
                            } else {
                                //add score text
                                TextView tv1 = getTableTaskCell((_roleType == RoleType.Teacher) ? "Add score" : "", Gravity.CENTER, fontLight, 11, Color.parseColor("#000000"));

                                tableRow.removeViewAt(2);
                                tableRow.addView(tv1);

                                setupMarkCell(tv1, tableRow);
                            }
                            return true;
                        }
                        return false;
                    }
                });

                if(edit.requestFocus()) {
                    InputMethodManager inputMethodManager = (InputMethodManager)_context.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }

                tableRow.removeViewAt(2);
                tableRow.addView(edit);
            }
        });
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)_context.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }





    private class StatusTask extends AsyncTask<Void, Void, Boolean> {
        private Task newTask;
        private TableRow tableRow;
        StatusTask(Task task, TableRow row) {
            newTask = task;
            tableRow = row;
        }
        @Override
        protected void onPreExecute() {
            if (_tasks.size() == 0) {
                _progressBar.setVisibility(View.VISIBLE);
            }
            _tasks.add(this);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            String content = HttpManager.changeHomeworkTaskStatus(_context.getActivity(), _token, newTask);
            Log.e("HOME", "==>" + content);
            if (!AppUtils.checkInvalidParams(_context.getActivity(), content)) {
//                return LessonJSONParser.parseLessons(content);
                return true;
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            _tasks.remove(this);
            if (_tasks.size() == 0) {
                _progressBar.setVisibility(View.INVISIBLE);
            }
            if (result) {
                //add score text
                TextView tv = getTableTaskCell((_roleType == RoleType.Teacher || newTask.getStatus().equalsIgnoreCase("completed")) ? "Add score" : "",
                        Gravity.CENTER, fontLight, 11, Color.parseColor("#000000"));

                tableRow.removeViewAt(2);
                tableRow.addView(tv);

                setupMarkCell(tv, tableRow);
            }
        }
    } // END: Homework Loader Task
}