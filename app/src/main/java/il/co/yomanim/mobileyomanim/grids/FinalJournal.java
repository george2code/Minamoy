package il.co.yomanim.mobileyomanim.grids;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.List;

import core.enums.RoleType;
import core.handlers.HttpManager;
import core.handlers.SessionManager;
import core.handlers.parser.MarkJSONParser;
import core.model.Person;
import core.model.journal.JournalFinalMark;
import core.model.journal.Mark;
import core.model.journal.TeacherComment;
import core.utils.AppUtils;
import core.utils.InputFilterMinMax;
import core.utils.NumberUtils;
import core.utils.StringUtils;
import core.view.JournalView;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.fragments.JournalFragment;


public class FinalJournal extends BaseJournal {

    public FinalJournal(JournalFragment context) {
        super(context);
    }

    private List<JournalFinalMark> journalFinalMarkList;
    private int bgHeaderColor;
    private int textHeaderColor;
    private List<TeacherComment> teacherCommentList;
    private RoleType roleType;
    private String token;

    private Long subjectGroupId;
    private Long periodId;


    public void RenderJournal(JournalView journalView) {
        //get token
        SessionManager session = new SessionManager(getContext().getActivity());
        token = session.getUserDetails().get(SessionManager.KEY_TOKEN);

        journalFinalMarkList = journalView.getFinalMarks();
        roleType = RoleType.valueOf(session.getUserDetails().get(SessionManager.KEY_ROLE));

        bgHeaderColor = getContext().getResources().getColor(R.color.jr_bg_header);
        textHeaderColor = getContext().getResources().getColor(R.color.jr_text_header);
        int headerWidth = NumberUtils.dpToPixels(getContext().getActivity(), 85);
        int testWidth = NumberUtils.dpToPixels(getContext().getActivity(), 60);
        TableRow.LayoutParams paramsExample = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

        teacherCommentList = new SessionManager(getContext().getActivity()).getTeacherCommentList();

        subjectGroupId = journalView.getSubjectGroupId();
        periodId = journalView.getPeriodId();

        if (!session.isSchoolClassicalSystem()) {
            TableLayout.LayoutParams paramsStudentTop = new TableLayout.LayoutParams(
                    new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

            TableLayout fixedColumn = (TableLayout) getContext().getActivity().findViewById(R.id.fixed_column);
            fixedColumn.setLayoutParams(paramsStudentTop);
            fixedColumn.removeAllViews();
            fixedColumn.setBackgroundColor(Color.parseColor("#cccccc"));

            TableRow row = new TableRow(getContext().getActivity());

            row.addView(getTableCell(getContext().getString(R.string.journal_th_student), bgHeaderColor, textHeaderColor, 11, fontMedium, headerWidth, _cellHeight));
            row.addView(getTableCell("Average", bgHeaderColor, textHeaderColor, 11, fontMedium, _cellWidth, _cellHeight));
            row.addView(getTableCell("Teachers comment", bgHeaderColor, textHeaderColor, 11, fontMedium, _cellWidth, _cellHeight), paramsExample);
            row.addView(getTableCell("Total", bgHeaderColor, textHeaderColor, 11, fontMedium, _cellWidth, _cellHeight));

            fixedColumn.addView(row);

            // students list
            for (Person person : journalView.getStudents()) {
                row = new TableRow(getContext().getActivity());

                TextView textStudent = getTableCell(MessageFormat.format("{0} {1}", person.getFirstName(), person.getLastName()), bgHeaderColor, textHeaderColor, 9, fontMedium, _cellWidth, _cellHeight);
                textStudent.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                textStudent.setPadding(20, 0, 20, 0);
                row.addView(textStudent);

                row.addView(getAverageCell(person.getId()));
                row.addView(getCommentCell(person.getId()), paramsExample);
                row.addView(getMarkCell(person.getId(), row));

                fixedColumn.addView(row);
            } // foreach Person
        }
    }



    //region Total mark cell
    private TextView getMarkCell(Long personId, TableRow tableRow) {
        if (journalFinalMarkList != null && journalFinalMarkList.size() > 0) {
            for (JournalFinalMark jfm : journalFinalMarkList) {
                if (jfm.getPerson() == personId) {
                    if (jfm.getMark() != null) {
                        //todo: setup mark
                        Mark mark = new Mark();
                        mark.setValue(String.valueOf(jfm.getMark()));
                        TextView textMark = getTableCell(String.valueOf(jfm.getMark()), mark.getMarkBackground(), Color.WHITE, 11, fontMedium, _cellWidth, _cellHeight);

                        if (roleType == RoleType.Teacher) {
                            setupMarkCell(textMark, tableRow, jfm.getMarkid(), personId);
                        }

                        return textMark;
                    }
                    break;
                }
            }
        }

        TextView textEmpty = getTableCell("", bgHeaderColor, textHeaderColor, 11, fontMedium, _cellWidth, _cellHeight);
        if (roleType == RoleType.Teacher) {
            setupMarkCell(textEmpty, tableRow, null, personId);
        }
        return textEmpty;
    }

    private void setupMarkCell(final TextView text, final TableRow tableRow, final Long markId, final long personId) {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(1, 0, 1, 1);

                EditText edit = new EditText(getContext().getActivity());
                edit.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                edit.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
                edit.setLayoutParams(params);
                edit.setWidth(_cellWidth);
                edit.setHeight(_cellHeight);
                edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                edit.setText(((TextView)v).getText());
                edit.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "100")});

                edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            //code to execute when EditText loses focus, - need to back previous cell state
                            tableRow.removeViewAt(3);
                            tableRow.addView(text);
                        }
                    }
                });

                edit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            hideKeyboard();
                            if (!TextUtils.isEmpty(v.getText())) {
                                //mark
                                Mark mark = new Mark();
                                mark.setValue(v.getText().toString());

//                                TextView tvMark = getTableCell(mark.getValue(), mark.getMarkBackground(), Color.WHITE, 11, fontMedium, _cellWidth, _cellHeight);

                                //todo: call mark update http
                                UpdateMarkTask updateMarkTask = new UpdateMarkTask(mark.getValue(), personId, tableRow);
                                updateMarkTask.execute();


//                                tableRow.removeViewAt(3);
//                                tableRow.addView(tvMark);

//                                setupMarkCell(tvMark, tableRow, null);
                            } else {
                                //add score text
                                TextView tv1 = getTableCell("", bgHeaderColor, Color.BLACK, 11, fontLight, _cellWidth, _cellHeight);

                                //todo: call mark delete, if there markId exists
                                if (markId != null) {
                                    MarkDeleteTask markDeleteTask = new MarkDeleteTask();
                                    markDeleteTask.execute(markId);
                                }

                                tableRow.removeViewAt(3);
                                tableRow.addView(tv1);

                                setupMarkCell(tv1, tableRow, null, personId);
                            }
                            return true;
                        }
                        return false;
                    }
                });

                if(edit.requestFocus()) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getContext().getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }

                tableRow.removeViewAt(3);
                tableRow.addView(edit);
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getContext().getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    //endregion


    private TextView getAverageCell(Long personId) {
        if (journalFinalMarkList != null && journalFinalMarkList.size() > 0) {
            for (JournalFinalMark jfm : journalFinalMarkList) {
                if (jfm.getPerson() == personId) {
                    if (jfm.getAvgmark() != null) {
                        DecimalFormat df = new DecimalFormat("#.#");
                        return getTableCell(String.valueOf(df.format(jfm.getAvgmark())),
                                bgHeaderColor, textHeaderColor, 11, fontMedium, _cellWidth, _cellHeight);
                    }
                    break;
                }
            }
        }
        return getTableCell("", bgHeaderColor, textHeaderColor, 11, fontMedium, _cellWidth, _cellHeight);
    }


    private TextView getCommentCell(Long personId) {
        if (journalFinalMarkList != null && journalFinalMarkList.size() > 0) {
            for (final JournalFinalMark jfm : journalFinalMarkList) {
                if (jfm.getPerson() == personId) {
                    if (jfm.getCommentid() > 0 && teacherCommentList != null) {
                        Log.e("COMMENT", jfm.getCommentid() + "");
                        for(TeacherComment comment : teacherCommentList) {
                            if (comment.getId() == jfm.getCommentid()) {
                                //todo: male or female?
                                TextView textView = getTableCell(StringUtils.cutString(comment.getMale(), 37), bgHeaderColor, textHeaderColor, 9, fontLight, _cellWidth, _cellHeight);
                                //
//                                ViewGroup.LayoutParams params= textView.getLayoutParams();
//                                params.width = 1;
//                                textView.setLayoutParams(params);
//                                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);

                                // optional: string.replace("\n",""); or string.replace("\n"," ");
                                textView.setSingleLine(false);
                                textView.setEllipsize(TextUtils.TruncateAt.END);
//                                int n = 2; // the exact number of lines you want to display
//                                textView.setLines(n);


                                textView.setGravity(Gravity.START |Gravity.CENTER_VERTICAL);
                                textView.setPadding(20, 0, 20, 0);

//                                TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
//                                        TableRow.LayoutParams.MATCH_PARENT);
//                                params1.setMargins(1, 0, 1, 1);
//                                textView.setLayoutParams(params1);

//                                textView.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View args) {
//                                        //show dialog
//                                        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext().getActivity())
//                                                .title("Add comment")
//                                                .customView(R.layout.dialog_final, false)
//                                                .positiveText("Done")
//                                                .titleColorRes(R.color.hw_dialog_title)
//                                                .widgetColorRes(R.color.primary)
//                                                .positiveColorRes(R.color.hw_dialog_positive_button)
//                                                .backgroundColor(getContext().getResources().getColor(R.color.hw_state_text))
//                                                .build();
//                                        View view = materialDialog.getCustomView();
//
//                                        //init txt
//                                        TextView txtSubject = (TextView)view.findViewById(R.id.txtSubject);
//                                        txtSubject.setText(jfm.getSabjectname());
//
//                                        TextView txtSemester = (TextView)view.findViewById(R.id.txtSemester);
//
//                                        TextView txtTotal = (TextView)view.findViewById(R.id.txtTotal);
//                                        txtTotal.setText(jfm.getAvgmark() != null ? jfm.getAvgmark().toString() : "");
//
//
//                                        //table
//                                        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.tableLayoutComments);
//
//                                        int count = 1;
//                                        for (TeacherComment teacherComment : teacherCommentList) {
//                                            TableRow row = new TableRow(getContext().getActivity());
//
//                                            row.addView(getTableCell(MessageFormat.format("#{0}", count),
//                                                    Color.WHITE, Color.BLACK, 12, fontMedium,
//                                                    _cellWidth, _cellHeight));
//
//                                            row.addView(getTableCell(teacherComment.getMale(),
//                                                    Color.WHITE, Color.BLACK, 12, fontMedium,
//                                                    _cellWidth, _cellHeight));
//
//                                            //todo: setup radiobutton and click
//                                            RadioButton rb = new RadioButton(getContext().getActivity());
//                                            row.addView(rb);
//
//                                            //
//                                            tableLayout.addView(row);
//                                            count++;
//                                        }
//
//                                        materialDialog.show();
//                                    }
//                                });

                                return textView;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return getTableCell("", bgHeaderColor, textHeaderColor, 11, fontLight, _cellWidth, _cellHeight);
    }



    private TextView getTableCell(String text, int bgColor, int textColor, int textSizeSp, Typeface textFont, int cellWidth, int cellHeight) {
        TextView fixedView = makeTableRowWithText(text, cellWidth, cellHeight, textSizeSp);
        fixedView.setBackgroundColor(bgColor);
        fixedView.setTextColor(textColor);
        fixedView.setTypeface(textFont);
        fixedView.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(1, 0, 1, 1);
        fixedView.setLayoutParams(params);

        return fixedView;
    }



    private class MarkDeleteTask extends AsyncTask<Long, Void, Boolean> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Boolean doInBackground(Long... params) {
            String content = HttpManager.deleteMark(getContext().getContext(), token,  params[0]);
            if (content != null) {
                return true;
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean response) {
            //todo: do something...
            if (response) {
                Toast.makeText(getContext().getActivity(), "Mark has been deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext().getActivity(), "Error while mark delete!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class UpdateMarkTask extends AsyncTask<Void, Void, JournalFinalMark> {
        private String _markValue;
        private long _personId;
        private TableRow _tableRow;
        public UpdateMarkTask(String markValue, long personId, TableRow tableRow) {
            _markValue = markValue;
            _personId = personId;
            _tableRow = tableRow;
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected JournalFinalMark doInBackground(Void... params) {
            String content = HttpManager.setFinalMarkTeacherMulti(getContext().getActivity(), token,  _personId, subjectGroupId, periodId, _markValue);
            Log.e("FINAL", content + "");
            if (content != null) {
                if (!AppUtils.findErrors(getContext().getContext(), content)) {
                    //{"person":1000000009336,"subject":10550,"sabjectname":"","avgmark":58.000000,"markid":2068,"mark":56,"marktype":"Mark100","commentid":null,"comment":null}
                    return MarkJSONParser.parseFinalMark(content);
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(JournalFinalMark response) {
            //todo: do something...
            if (response != null) {
                TextView tvMark = getTableCell(response.getMark().toString(), response.getMarkBackground(), Color.WHITE, 11, fontMedium, _cellWidth, _cellHeight);

                _tableRow.removeViewAt(3);
                _tableRow.addView(tvMark);

                setupMarkCell(tvMark, _tableRow, response.getMarkid(), response.getPerson());
            } else {
                Toast.makeText(getContext().getActivity(), "Error while mark setup!", Toast.LENGTH_LONG).show();
            }
        }
    }
}