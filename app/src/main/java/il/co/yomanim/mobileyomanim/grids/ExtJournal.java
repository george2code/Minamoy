package il.co.yomanim.mobileyomanim.grids;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import core.helpers.ConstantsHelper;
import core.model.Person;
import core.model.journal.BehaviourStatus;
import core.model.journal.PresenceStatus;
import core.model.journal.Lesson;
import core.model.journal.Mark;
import core.model.journal.Presence;
import core.view.JournalView;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.fragments.JournalFragment;

public class ExtJournal extends BaseJournal {

    public ExtJournal(JournalFragment context) {
        super(context);
    }

    public void RenderJournal(JournalView view, int journalType) {
        try {

            super._behaviourSchoolList = view.getBehaviourSchoolList();

            int lessonsCount = getLessonsCount(view);
            int rowsCount = 3 + view.getStudents().size();
            SimpleDateFormat dateFormat = new SimpleDateFormat("d ccc", Locale.ENGLISH);

            GridLayout gridLayout = (GridLayout)getContext().getActivity().findViewById(R.id.scrollable_part);
            gridLayout.removeAllViews();
            gridLayout.setColumnCount(lessonsCount);
            gridLayout.setRowCount(rowsCount);

            // Render monthes
            int coli = 0;
            int[] lc = new int[view.getLessons().size()];
            int additionalHeaderIndex = 0;

            for(int m=0; m < view.getLessons().size(); m++)
            {
                String key = (String)view.getLessons().keySet().toArray()[m];
                int lessonsInMonth = view.getLessons().get(key).size();
                lc[m] = lessonsInMonth;
                if (m > 0) {
                    coli += lc[m-1];
                }
                AddColspanCell(
                        gridLayout,                 // grid
                        0,                          // row number
                        coli,                       // col number
                        lessonsInMonth,             // colspan
                        key);                       // text: month year


                for (Lesson lesson : view.getLessons().get(key))
                {
                    //      gridLayout, row, column, text, int bgColor, int textColor, int textSizeSp
                    //Render lesson date: Day, day-of-the-week
                    AddCell(gridLayout, 1, additionalHeaderIndex,
                            dateFormat.format(lesson.getDate()),
                            getColorBgHeader(),
                            getColorText(),
                            10,
                            fontMedium);


                    //Render type of work
                    StringBuilder lessonNumberAndType = new StringBuilder();
                    lessonNumberAndType.append(lesson.getNumber());
                    if(lesson.getWorks() != null && !lesson.getWorks().isEmpty()) {
                        lessonNumberAndType.append(", ").append(lesson.getWorks().get(0).getJournalWorkType());
                    }
                    AddCell(gridLayout, 2, additionalHeaderIndex,
                            lessonNumberAndType.toString(),
                            getColorBgHeader(),
                            getColorText(),
                            11,
                            fontLight);


                    int startStudentRowIndex = 3;

                    // Lesson has marks, just render them
                    ////////////////////////////////////////////////////////////////////////////////
                    for (Person student : view.getStudents())
                    {
                        switch (journalType) {
                            case ConstantsHelper.TAB_JOURNAL_MARKS:
                                Mark journalMark = getJournalMark(view.getMarks(), lesson, student.getId());

//                                CompoundCell compoundCell = new CompoundCell(getContext().getActivity(), null);
//                                View cellView = compoundCell.getCell(journalMark, null, null, startStudentRowIndex, additionalHeaderIndex);
//
//                                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
//                                param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
//                                param.width = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
//                                param.setMargins(1, 0, 1, 1);
//                                param.setGravity(Gravity.CENTER);
//                                param.rowSpec = GridLayout.spec(startStudentRowIndex);
//                                param.columnSpec = GridLayout.spec(additionalHeaderIndex);
//
//                                gridLayout.addView(cellView, param);

//                                if (journalMark != null) {
//                                    //RENDER MARK
//                                    // render
//                                    AddCell(gridLayout,
//                                            startStudentRowIndex,
//                                            additionalHeaderIndex,
//                                            journalMark.getValue(),       // text
//                                            journalMark.getMarkBackground(),                            // bg color
//                                            getColorWhite(),                             // txt color
//                                            14,                                     // size in Sp
//                                            fontMedium);                            // typeface
//                                } else {
//                                    //EMPTY CELL
//                                    AddCell(gridLayout, startStudentRowIndex, additionalHeaderIndex, "",
//                                            getColorWhite(), getColorWhite(), 14, fontMedium);
//                                }
                                break;

                            case ConstantsHelper.TAB_JOURNAL_PRESENCE:
                                Presence journalPresence = getJournalPresence(view.getPresences(), lesson, student.getId());
                                if (journalPresence != null) {
                                    //RENDER PRESENCE
                                    // render
                                    AddCell(gridLayout, startStudentRowIndex, additionalHeaderIndex,
                                            journalPresence.getStatus().substring(0,1),
                                            journalPresence.getPresenceBackground(), getColorWhite(), 14, fontMedium);
                                } else {
                                    //EMPTY CELL
                                    AddCell(gridLayout, startStudentRowIndex, additionalHeaderIndex, "",
                                            getColorWhite(), getColorWhite(), 14, fontMedium);
                                }
                                break;

                            case ConstantsHelper.TAB_JOURNAL_BEHAVIOUR:
                                List<BehaviourStatus> journalBehaviourList = getJournalBehaviour(view.getBehaviours(), lesson, student.getId());

                                //TODO: behavirour cell rendering
                                AddBehaviourCell(gridLayout,
                                        startStudentRowIndex,
                                        additionalHeaderIndex,
                                        (journalBehaviourList != null) ? journalBehaviourList : new ArrayList<BehaviourStatus>(),
                                        (journalBehaviourList != null) ? Color.parseColor("#459dd6") : getColorWhite(),
                                        getColorWhite(),
                                        14,
                                        fontMedium);

//                                if (journalBehaviour != null) {
//                                    //RENDER PRESENCE
//                                    // render
//                                    AddBehaviourCell(gridLayout, startStudentRowIndex, additionalHeaderIndex,
//                                            journalBehaviour, Color.parseColor("#459dd6"), getColorWhite(), 14, fontMedium);
//                                } else {
//                                    //EMPTY CELL
//                                    AddCell(gridLayout, startStudentRowIndex, additionalHeaderIndex, "",
//                                            getColorWhite(), getColorWhite(), 14, fontMedium);
//                                }
                                break;
                            default: break;
                        } //end switch

                        startStudentRowIndex++;
                    }
                    //increment column
                    additionalHeaderIndex++;
                }
            }

            //region Render Student list
            TableLayout.LayoutParams paramsStudentTop = new TableLayout.LayoutParams(
                    new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            paramsStudentTop.setMargins(1, 1, 1, 1);

            TableLayout.LayoutParams paramsStudent = new TableLayout.LayoutParams(
                    new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            paramsStudent.setMargins(1, 0, 0, 1);


            TableLayout fixedColumn = (TableLayout) getContext().getActivity().findViewById(R.id.fixed_column);
            fixedColumn.removeAllViews();


            // top empty cell, Student name
            //                                          text / width / height / textSize
            TextView fixedView1 = makeTableRowWithText(
                    getContext().getString(R.string.journal_th_student),
                    _headerWidth,
                    _headerWidth,   //118 - height
                    13);
            fixedView1.setBackgroundColor(getContext().getResources().getColor(R.color.jr_bg_header));
            fixedView1.setTextColor(getContext().getResources().getColor(R.color.jr_text_header));
            fixedView1.setTypeface(fontMedium);
            fixedView1.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            fixedColumn.addView(fixedView1, paramsStudentTop);

            // students list
            for (Person person : view.getStudents())
            {
                TextView fixedView = makeTableRowWithText(
                        MessageFormat.format("{0} {1}", person.getFirstName(), person.getLastName()),
                        _headerWidth,     //27
                        _cellHeight,            //69
                        11);

                fixedView.setTextColor(getContext().getResources().getColor(R.color.jr_text_header));
                fixedView.setTypeface(fontMedium);
                fixedView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                fixedView.setPadding(15, 0, 0, 0);
                fixedColumn.addView(fixedView, paramsStudent);
//                break;
            } // foreach Person
            //endregion
        }
        catch(Exception ex) {
            Toast.makeText(getContext().getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}