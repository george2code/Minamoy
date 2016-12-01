package il.co.yomanim.mobileyomanim.grids;

import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import core.handlers.SessionManager;
import core.helpers.ConstantsHelper;
import core.model.EduGroup;
import core.model.journal.Behaviour;
import core.model.journal.BehaviourStatus;
import core.model.journal.PresenceStatus;
import core.model.journal.Lesson;
import core.model.journal.Mark;
import core.model.journal.Presence;
import core.view.JournalView;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.compound.CompoundCell;
import il.co.yomanim.mobileyomanim.fragments.JournalFragment;

public class StudentJournal extends BaseJournal {

    public StudentJournal(JournalFragment context) {
        super(context);
    }


    public void RenderStudentJournal(JournalView view, int journalType) {
        try {

            //get token
            SessionManager session = new SessionManager(getContext().getActivity());
            String token = session.getUserDetails().get(SessionManager.KEY_TOKEN);

            int lessonsCount = getLessonsCount(view);
            int rowsCount = 3 + view.getGroups().size();
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

                //Render Month name
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
                    AddCell(gridLayout, 1, additionalHeaderIndex, dateFormat.format(lesson.getDate()), getColorBgHeader(), getColorText(), 10, fontMedium);

                    //Render type of work
                    StringBuilder lessonNumberAndType = new StringBuilder();
                    lessonNumberAndType.append(lesson.getNumber());
                    if(lesson.getWorks() != null && !lesson.getWorks().isEmpty()) {
                        lessonNumberAndType.append(", ").append(lesson.getWorks().get(0).getJournalWorkType());
                    }

                    AddCell(gridLayout, 2, additionalHeaderIndex, lessonNumberAndType.toString(), getColorBgHeader(), getColorText(), 11, fontLight);

                    int startStudentRowIndex = 3;

                    // Lesson has marks, just render them
                    for (EduGroup eduGroup : view.getGroups())
                    {
                        View cellView = null;

                        //set cell param
                        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
                        param.width = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
                        param.setMargins(1, 0, 1, 1);
                        param.setGravity(Gravity.CENTER);
                        param.rowSpec = GridLayout.spec(startStudentRowIndex);
                        param.columnSpec = GridLayout.spec(additionalHeaderIndex);

                        switch (journalType) {
                            case ConstantsHelper.TAB_JOURNAL_MARKS:
                                Mark journalMark = getJournalMark(view.getMarks(), lesson, eduGroup.getName().trim());
                                Presence journalPresence = getJournalPresence(view.getPresences(), lesson, eduGroup.getName().trim());
                                List<Behaviour> journalBehaviourList = getJournalBehavior(view.getBehaviours(), lesson, eduGroup.getName().trim());

                                CompoundCell compoundCell = new CompoundCell(getContext().getActivity(), null);
//                                cellView = compoundCell.getCell(journalMark, journalPresence, view.getPresenceSchoolArray(),
//                                        journalBehaviourList, _behaviourSchoolList,
//                                        lesson,
//                                        -1,
//                                        token);
                                break;

                            case ConstantsHelper.TAB_JOURNAL_PRESENCE:
                                Presence journalPresence2 = getJournalPresence(view.getPresences(), lesson, eduGroup.getName().trim());
                                CompoundCell compoundCellPresence = new CompoundCell(getContext().getActivity(), null);
//                                cellView = compoundCellPresence.getPresenceCell(journalPresence2);
                                break;

                            case ConstantsHelper.TAB_JOURNAL_BEHAVIOUR:
                                List<BehaviourStatus> journalBehaviourList2 = getJournalBehaviour(view.getBehaviours(), lesson, eduGroup.getName().trim());
                                CompoundCell compoundCellBehaviour = new CompoundCell(getContext().getActivity(), null);
//                                cellView = compoundCellBehaviour.getBehaviourCell(journalBehaviourList2);
                                break;
                            default: break;
                        } //end switch


                        if (cellView != null)
                            gridLayout.addView(cellView, param);

                        //increment row
                        startStudentRowIndex++;
                    }
                    //increment column
                    additionalHeaderIndex++;
                }
            }


            //region Render Student list
            RenderGroupList(view);

//            pb.setVisibility(View.INVISIBLE);
        }
        catch(Exception ex) {
            Toast.makeText(getContext().getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void RenderGroupList(JournalView view) {
        TableLayout.LayoutParams paramsStudentTop = new TableLayout.LayoutParams(
                new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        paramsStudentTop.setMargins(1, 1, 1, 1);

        TableLayout.LayoutParams paramsStudent = new TableLayout.LayoutParams(
                new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        paramsStudent.setMargins(1, 0, 0, 1);


        TableLayout fixedColumn = (TableLayout) getContext().getActivity().findViewById(R.id.fixed_column);
        fixedColumn.removeAllViews();


        // top empty cell, Student name
        //                              f            text / width / height / textSize
        TextView fixedView1 = makeTableRowWithText(
                getContext().getString(R.string.button_group),
                _headerWidth,
                _headerWidth,   //118 - height
                13);
        fixedView1.setBackgroundColor(getContext().getResources().getColor(R.color.jr_bg_header));
        fixedView1.setTextColor(getContext().getResources().getColor(R.color.jr_text_header));
        fixedView1.setTypeface(fontMedium);
        fixedView1.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        fixedColumn.addView(fixedView1, paramsStudentTop);

        // students list
        for (EduGroup eduGroup : view.getGroups())
        {
            TextView fixedView = makeTableRowWithText(
                    eduGroup.getName().trim(),
                    _headerWidth,           //27
                    _cellHeight,            //69
                    11);

            fixedView.setTextColor(getContext().getResources().getColor(R.color.jr_text_header));
            fixedView.setTypeface(fontMedium);
            fixedView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            fixedView.setPadding(15, 0, 20, 0);
            fixedColumn.addView(fixedView, paramsStudent);
        } // foreach Person
    }
}