package il.co.yomanim.mobileyomanim.grids;

import android.app.ActionBar;
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
import core.model.journal.Lesson;
import core.model.journal.Mark;
import core.model.journal.Presence;
import core.model.journal.Work;
import core.utils.NumberUtils;
import core.view.JournalView;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.compound.CompoundCell;
import il.co.yomanim.mobileyomanim.fragments.JournalFragment;


public class BehaviourJournal extends BaseJournal {

    private String TAG = "BehaviourJournal";

    public BehaviourJournal(JournalFragment context) {
        super(context);
    }

    public void RenderJournal(JournalView view) {
        long startTime = System.nanoTime();
        try {
            super._behaviourSchoolList = view.getBehaviourSchoolList();

            //get token
            SessionManager session = new SessionManager(getContext().getActivity());
            String token = session.getUserDetails().get(SessionManager.KEY_TOKEN);

            int lessonsCount = getLessonsCount(view);
            int rowsCount = 2 + view.getStudents().size();
            SimpleDateFormat dateFormat = new SimpleDateFormat("d ccc", Locale.ENGLISH);

            GridLayout gridLayout = (GridLayout)getContext().getActivity().findViewById(R.id.scrollable_part);
            gridLayout.removeAllViews();
            gridLayout.setColumnCount(lessonsCount);
            gridLayout.setRowCount(rowsCount);


            int cellWidth  = NumberUtils.dpToPixels(getContext().getActivity(), 200);
            int cellHeight  = NumberUtils.dpToPixels(getContext().getActivity(), 39);


            // Render monthes
            int coli = 0;
            int[] lc = new int[view.getLessons().size()];

            int additionalHeaderIndex = 0;
            int additionalDayHeaderIndex = 0;


            for(int m=0; m < view.getLessons().size(); m++)
            {
                String key = (String)view.getLessons().keySet().toArray()[m];
                List<Lesson> lessonList = view.getLessons().get(key);
//                int lessonWorksInMonth = worksCount(lessonList);

                lc[m] = view.getLessons().get(key).size();
                if (m > 0) {
                    coli += lc[m-1];
                }

                try {
                    AddColspanCell(
                            gridLayout,                 // grid
                            0,                          // row number
                            coli,                       // col number
                            view.getLessons().get(key).size(),             // colspan
                            cellWidth,
                            key);                       // text: month year
                } catch(Exception e) {
                    Log.e(TAG, e.getMessage());
                }


                for (Lesson lesson : lessonList) {
                    // gridLayout, row, column, text, int bgColor, int textColor, int textSizeSp
                    // Render lesson date: Day, day-of-the-week
                        AddHeaderCell(gridLayout, 1, additionalDayHeaderIndex,
                                dateFormat.format(lesson.getDate()),
                                cellWidth,
                                cellHeight,
                                getColorBgHeader(),
                                getColorText(),
                                10,
                                fontMedium);

                        additionalDayHeaderIndex++;


                    int startStudentRowIndex = 2;


                    // Lesson has marks, just render them
                    for (Person student : view.getStudents())
                    {
                        Presence journalPresence = getJournalPresence(view.getPresences(), lesson, student.getId());
                        List<Behaviour> journalBehaviourList = getJournalBehavior(view.getBehaviours(), lesson, student.getId());


                        CompoundCell compoundCellPresence = new CompoundCell(getContext().getActivity(), null);
                        View cellView = compoundCellPresence.getCell(
                                journalPresence,
                                view.getPresenceSchoolArray(),
                                journalBehaviourList,
                                _behaviourSchoolList,
                                lesson,
                                student.getId(),
                                token);


                        if (cellView != null) {
                            //set cell param
                            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                            param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
                            param.width = cellWidth;
                            param.setMargins(1, 0, 1, 1);
                            param.setGravity(Gravity.CENTER);
                            param.rowSpec = GridLayout.spec(startStudentRowIndex);
                            param.columnSpec = GridLayout.spec(additionalHeaderIndex);

                            gridLayout.addView(cellView, param);
                        }

                        startStudentRowIndex++;
                    }

                    //increment column
                    additionalHeaderIndex ++;
                }
            }

            //Render Student list
            int height = NumberUtils.dpToPixels(getContext().getActivity(), 79);
            RenderStudentList(view, height);
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