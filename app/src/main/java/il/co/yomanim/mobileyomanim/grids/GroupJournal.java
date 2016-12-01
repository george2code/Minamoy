package il.co.yomanim.mobileyomanim.grids;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import core.handlers.SessionManager;
import core.helpers.ConstantsHelper;
import core.model.Person;
import core.model.journal.Behaviour;
import core.model.journal.BehaviourStatus;
import core.model.journal.PresenceStatus;
import core.model.journal.Lesson;
import core.model.journal.Mark;
import core.model.journal.Presence;
import core.model.journal.Work;
import core.utils.NumberUtils;
import core.view.JournalView;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.compound.CompoundCell;
import il.co.yomanim.mobileyomanim.fragments.JournalFragment;


public class GroupJournal extends BaseJournal
{
    private String TAG = "GroupJournal";

    public GroupJournal(JournalFragment context) {
        super(context);
    }

    private int worksCount(List<Lesson> lessons) {
        int workCount = 0;
        for (Lesson lesson : lessons) {
            workCount += (lesson.getWorks() != null && lesson.getWorks().size() > 0) ? lesson.getWorks().size() : 1;
        }
        return workCount;
    }



    public void RenderJournal(JournalView view, int journalType) {
        long startTime = System.nanoTime();
        try {
            super._behaviourSchoolList = view.getBehaviourSchoolList();

            //get token
            SessionManager session = new SessionManager(getContext().getActivity());
            String token = session.getUserDetails().get(SessionManager.KEY_TOKEN);

            int lessonsCount = getLessonsAndWorkCount(view);
            int rowsCount = 4 + view.getStudents().size();
            SimpleDateFormat dateFormat = new SimpleDateFormat("d ccc", Locale.ENGLISH);

            GridLayout gridLayout = (GridLayout)getContext().getActivity().findViewById(R.id.scrollable_part);
            gridLayout.removeAllViews();
            gridLayout.setColumnCount(lessonsCount);
            gridLayout.setRowCount(rowsCount);


            // Render monthes
            int coli = 0;
            int[] lc = new int[view.getLessons().size()];

            int additionalHeaderIndex = 0;
            int additionalDayHeaderIndex = 0;


            for(int m=0; m < view.getLessons().size(); m++)
            {
                String key = (String)view.getLessons().keySet().toArray()[m];
                List<Lesson> lessonList = view.getLessons().get(key);
                int lessonWorksInMonth = worksCount(lessonList);

                lc[m] = lessonWorksInMonth;
                if (m > 0) {
                    coli += lc[m-1];
                }

                try {
                    AddColspanCell(
                            gridLayout,                 // grid
                            0,                          // row number
                            coli,                       // col number
                            lessonWorksInMonth,             // colspan
                            key);                       // text: month year
                } catch(Exception e) {
                    Log.e(TAG, e.getMessage());
                }


                for (Lesson lesson : lessonList) {
                    // gridLayout, row, column, text, int bgColor, int textColor, int textSizeSp
                    // Render lesson date: Day, day-of-the-week
                    int worksInLessonCount = (lesson.getWorks() != null) ? lesson.getWorks().size() : 0;

                    if (worksInLessonCount > 1) {

                        AddColspanCell(gridLayout, 1, additionalDayHeaderIndex,
                                worksInLessonCount,
                                dateFormat.format(lesson.getDate()));

                        AddColspanCell(gridLayout, 2, additionalDayHeaderIndex,
                                worksInLessonCount,
                                lesson.getNumber() + "");

                        int stIndex = additionalDayHeaderIndex;
                        for (Work work : lesson.getWorks()) {
                            AddCell(gridLayout, 3, stIndex,
                                    work.getJournalWorkType(),
                                    getColorBgHeader(),
                                    getColorText(),
                                    11,
                                    fontLight);
                            stIndex++;
                        }

                        additionalDayHeaderIndex += worksInLessonCount;
                    }
                    else {
                        AddCell(gridLayout, 1, additionalDayHeaderIndex,
                                dateFormat.format(lesson.getDate()),
                                getColorBgHeader(),
                                getColorText(),
                                10,
                                fontMedium);

                        AddCell(gridLayout, 2, additionalDayHeaderIndex,
                            lesson.getNumber() + "",
                            getColorBgHeader(),
                            getColorText(),
                            11,
                            fontLight);


                        Work currentWork = getLessonWork(lesson);
                        AddCell(gridLayout, 3, additionalDayHeaderIndex,
                                currentWork != null ? getLessonWork(lesson).getJournalWorkType() : "",
                                getColorBgHeader(),
                                getColorText(),
                                11,
                                fontLight);

                        additionalDayHeaderIndex++;
                    }


//                    AddCell(gridLayout, 1, additionalHeaderIndex,
//                            dateFormat.format(lesson.getDate()),
//                            getColorBgHeader(),
//                            getColorText(),
//                            10,
//                            fontMedium);

////
//                    //Render type of work
//                    StringBuilder lessonNumberAndType = new StringBuilder();
//                    lessonNumberAndType.append(lesson.getNumber());
//                    if(lesson.getWorks() != null && !lesson.getWorks().isEmpty()) {
//                        lessonNumberAndType.append(", ").append(lesson.getWorks().get(0).getJournalWorkType());
//                    }
//                    AddCell(gridLayout, 2, additionalHeaderIndex,
//                            lessonNumberAndType.toString(),
//                            getColorBgHeader(),
//                            getColorText(),
//                            11,
//                            fontLight);
//
//
//                    AddCell(gridLayout, 3, additionalHeaderIndex,
//                            "T",
//                            getColorBgHeader(),
//                            getColorText(),
//                            11,
//                            fontLight);


                    int startStudentRowIndex = 4;


                    // Lesson has marks, just render them
                    for (Person student : view.getStudents())
                    {
                        View cellView = null;

                        switch (journalType) {
                            case ConstantsHelper.TAB_JOURNAL_MARKS:

                                    Presence journalPresence1 = getJournalPresence(view.getPresences(), lesson, student.getId());
                                    List<Behaviour> journalBehaviourList1 = getJournalBehavior(view.getBehaviours(), lesson, student.getId());

                                    if (worksInLessonCount > 1) {
                                        int startLessonIndex = additionalHeaderIndex;
                                        for (Work work : lesson.getWorks()) {
                                            //set cell param
                                            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                                            param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
                                            param.width = _cellWidth;
                                            param.setMargins(1, 0, 1, 1);
                                            param.setGravity(Gravity.CENTER);
                                            param.rowSpec = GridLayout.spec(startStudentRowIndex);
                                            param.columnSpec = GridLayout.spec(startLessonIndex);

                                            Mark journalWorkMark = getJournalMarkByWork(view.getMarks(), work, student.getId());
                                            CompoundCell compoundCell = new CompoundCell(getContext().getActivity(), null);
                                            cellView = compoundCell.getMarkCell(journalWorkMark,
                                                    lesson,
                                                    student.getId(),
                                                    token);

                                            if (cellView != null) {
                                                gridLayout.addView(cellView, param);
                                                cellView = null;
                                            }

                                            startLessonIndex++;
                                        }
                                    }
                                    else {
                                        Mark journalMark = getJournalMark(view.getMarks(), lesson, student.getId());
                                        CompoundCell compoundCell = new CompoundCell(getContext().getActivity(), null);
                                        cellView = compoundCell.getMarkCell(journalMark,
                                                lesson,
                                                student.getId(),
                                                token);
                                    }
                                break;

                            case ConstantsHelper.TAB_JOURNAL_PRESENCE:
                                Presence journalPresence2 = getJournalPresence(view.getPresences(), lesson, student.getId());
                                CompoundCell compoundCellPresence = new CompoundCell(getContext().getActivity(), null);
//                                cellView = compoundCellPresence.getPresenceCell(journalPresence2);
                                break;

                            case ConstantsHelper.TAB_JOURNAL_BEHAVIOUR:
                                List<BehaviourStatus> journalBehaviourList2 = getJournalBehaviour(view.getBehaviours(), lesson, student.getId());
                                CompoundCell compoundCellBehaviour = new CompoundCell(getContext().getActivity(), null);
//                                cellView = compoundCellBehaviour.getBehaviourCell(journalBehaviourList2);
                                break;
                            default: break;
                        } //end switch


                        if (cellView != null) {
                            //set cell param
                            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                            param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
                            param.width = _cellWidth;
                            param.setMargins(1, 0, 1, 1);
                            param.setGravity(Gravity.CENTER);
                            param.rowSpec = GridLayout.spec(startStudentRowIndex);
                            param.columnSpec = GridLayout.spec(additionalHeaderIndex);

                            gridLayout.addView(cellView, param);
                        }

                        startStudentRowIndex++;
                    }

                    //increment column
                    additionalHeaderIndex += (worksInLessonCount > 0) ? worksInLessonCount : 1;
                }
            }

            //Render Student list
            RenderStudentList(view, _headerHeight);
        }
        catch(Exception ex) {
            Log.e(TAG, ex.getMessage());
//            Toast.makeText(getContext().getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        Log.e(TAG, "NANO: ---------> " + duration);
    }


    private void RenderStudentList(JournalView view, int headerHeight) {
        TableLayout.LayoutParams paramsStudentTop = new TableLayout.LayoutParams(
                new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        paramsStudentTop.setMargins(1, 1, 1, 1);

        TableLayout.LayoutParams paramsStudent = new TableLayout.LayoutParams(
                new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        paramsStudent.setMargins(1, 0, 0, 1);

        TableLayout fixedColumn = (TableLayout) getContext().getActivity().findViewById(R.id.fixed_column);
        TableLayout.LayoutParams linLayoutParam = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 0.0f);
        linLayoutParam.width = _headerWidth;
        fixedColumn.setLayoutParams(linLayoutParam);
        fixedColumn.setWeightSum(1);

        fixedColumn.removeAllViews();
//        TableLayout.LayoutParams linLayoutParam = new TableLayout.LayoutParams(_headerWidth, TableLayout.LayoutParams.MATCH_PARENT, 0.0f);
//        fixedColumn.setLayoutParams(linLayoutParam);

        // top empty cell, Student name
        // text / width / height / textSize
        TextView fixedView1 = makeTableRowWithText(
                getContext().getString(R.string.journal_th_student),
                _headerWidth,
                headerHeight,   //118 - height
                13);
        fixedView1.setBackgroundColor(getContext().getResources().getColor(R.color.jr_bg_header));
        fixedView1.setTextColor(getContext().getResources().getColor(R.color.jr_text_header));
        fixedView1.setTypeface(fontMedium);
        fixedView1.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        fixedColumn.addView(fixedView1, paramsStudentTop);

        // students list
        for (Person person : view.getStudents()) {
            TextView fixedView = makeTableRowWithText(
                    MessageFormat.format("{0} {1}", person.getFirstName(), person.getLastName()),
                    _headerWidth,     //27
                    _cellHeight,            //69
                    11);

            fixedView.setTextColor(getContext().getResources().getColor(R.color.jr_text_header));
            fixedView.setTypeface(fontMedium);
            fixedView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            fixedView.setPadding(15, 0, 15, 0);
            fixedColumn.addView(fixedView, paramsStudent);
        } // foreach Person
    }
}