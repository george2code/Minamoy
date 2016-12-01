package il.co.yomanim.mobileyomanim.grids;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import core.model.journal.Behaviour;
import core.model.journal.BehaviourStatus;
import core.model.journal.PresenceStatus;
import core.model.journal.Lesson;
import core.model.journal.Mark;
import core.model.journal.Presence;
import core.model.journal.Work;
import core.utils.FontUtils;
import core.utils.NumberUtils;
import core.view.JournalView;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.adapters.BehaviourAdapter;
import il.co.yomanim.mobileyomanim.fragments.JournalFragment;

public class BaseJournal {

    //region fields and properties
    private JournalFragment _context;
    private int colorWhite, colorText, colorBgHeader;
    Typeface fontMedium, fontLight;

    List<BehaviourStatus> _behaviourSchoolList;

    public int _headerWidth, _headerHeight, _cellWidth, _cellHeight;

//    private static final int CELL_WIDTH = 50;          //  44

    public JournalFragment getContext() {
        return _context;
    }

    public void setContext(JournalFragment _context) {
        this._context = _context;
    }

    public int getColorWhite() {
        return colorWhite;
    }

    public void setColorWhite(int colorWhite) {
        this.colorWhite = colorWhite;
    }

    public int getColorText() {
        return colorText;
    }

    public void setColorText(int colorText) {
        this.colorText = colorText;
    }

    public int getColorBgHeader() {
        return colorBgHeader;
    }

    public void setColorBgHeader(int colorBgHeader) {
        this.colorBgHeader = colorBgHeader;
    }

    public Typeface getFontMedium() {
        return fontMedium;
    }

    public void setFontMedium(Typeface fontMedium) {
        this.fontMedium = fontMedium;
    }

    public Typeface getFontLight() {
        return fontLight;
    }

    public void setFontLight(Typeface fontLight) {
        this.fontLight = fontLight;
    }

    public int getSizeInPx(int dp) {
        return NumberUtils.dpToPx(_context.getActivity(), dp);
    }

    public int getLessonsCount(JournalView view) {
        int all = 0;
        for(String key : view.getLessons().keySet()) {
            all += view.getLessons().get(key).size();
        }
        return all;
    }

    public int getLessonsAndWorkCount(JournalView view) {
        int all = 0;
        for(String key : view.getLessons().keySet()) {
            for(Lesson lesson : view.getLessons().get(key)) {
                all += (lesson.getWorks() != null && lesson.getWorks().size() > 1) ? lesson.getWorks().size() : 1;
            }
//            all += view.getLessons().get(key).size();
        }
        return all;
    }
    //endregion


    public BaseJournal(JournalFragment context) {
        setContext(context);

        //init fonts
        setFontMedium(FontUtils.getRobotoMedium(_context.getActivity().getAssets()));
        setFontLight(FontUtils.getRobotoLight(_context.getActivity().getAssets()));

        //init colors
        setColorWhite(_context.getResources().getColor(R.color.white));
        setColorText(_context.getResources().getColor(R.color.jr_text_header));
        setColorBgHeader(_context.getResources().getColor(R.color.jr_bg_header));

        //set width and height
        _headerWidth = NumberUtils.dpToPixels(context.getActivity(), 118);
        _headerHeight = NumberUtils.dpToPixels(context.getActivity(), 158);
        _cellWidth  = NumberUtils.dpToPixels(context.getActivity(), 50);
        _cellHeight  = NumberUtils.dpToPixels(context.getActivity(), 39);
    }


    //Using only to render Monthes in the journal
    public void AddColspanCell(GridLayout gridLayout, int row, int column, int colspan, String text) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.setMargins(1, 1, 1, 1);
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(column, colspan);
        param.rowSpec = GridLayout.spec(row);

        int calcWidth = colspan * _cellWidth + (colspan*2);
        TextView recyclableTextView = makeTableRowWithText(text, calcWidth, _cellHeight, 10);   //39
        recyclableTextView.setLayoutParams(param);
        recyclableTextView.setBackgroundColor(colorBgHeader);
        recyclableTextView.setTextColor(colorText);
        gridLayout.addView(recyclableTextView);
    }


    public void AddColspanCell(GridLayout gridLayout, int row, int column, int colspan, int cellWidth, String text) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.setMargins(1, 1, 1, 1);
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(column, colspan);
        param.rowSpec = GridLayout.spec(row);

        int calcWidth = colspan * cellWidth + (colspan*2);
        TextView recyclableTextView = makeTableRowWithText(text, calcWidth, _cellHeight, 10);   //39
        recyclableTextView.setLayoutParams(param);
        recyclableTextView.setBackgroundColor(colorBgHeader);
        recyclableTextView.setTextColor(colorText);
        gridLayout.addView(recyclableTextView);
    }


    //util method
    public TextView makeTableRowWithText(String text, int widthInPercentOfScreenWidth,
                                         int fixedHeightInPixels, int textSizeSp) {
        TextView recyclableTextView = new TextView(_context.getActivity());
        recyclableTextView.setText(text);
        recyclableTextView.setTextColor(Color.BLACK);
        recyclableTextView.setBackgroundColor(Color.WHITE);
        recyclableTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
        if (widthInPercentOfScreenWidth > 0)
            recyclableTextView.setWidth(widthInPercentOfScreenWidth);
        if (fixedHeightInPixels > 0)
            recyclableTextView.setHeight(fixedHeightInPixels);
        recyclableTextView.setGravity(Gravity.CENTER);

        return recyclableTextView;
    }



    public void AddHeaderCell(GridLayout gridLayout, int row, int column, String text, int width, int height, int bgColor,

                              int textColor, int textSizeSp, Typeface textFont) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = ActionBar.LayoutParams.WRAP_CONTENT;
        param.setMargins(1, 0, 1, 1);
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(row);

        TextView recyclableTextView = makeTableRowWithText(
                text,
                width,
                height,
                textSizeSp);
        recyclableTextView.setLayoutParams(param);
        recyclableTextView.setTypeface(textFont);
        recyclableTextView.setBackgroundColor(bgColor);
        recyclableTextView.setTextColor(textColor);

        gridLayout.addView(recyclableTextView);
    }



    public void AddCell(GridLayout gridLayout, int row, int column, String text, int bgColor,
                        int textColor, int textSizeSp, Typeface textFont) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.setMargins(1, 0, 1, 1);
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(row);

        TextView recyclableTextView = makeTableRowWithText(
                text,
                _cellWidth,
                _cellHeight,
                textSizeSp);
        recyclableTextView.setLayoutParams(param);
        recyclableTextView.setTypeface(textFont);
        recyclableTextView.setBackgroundColor(bgColor);
        recyclableTextView.setTextColor(textColor);

//        recyclableTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                //show dialog
//                boolean wrapInScrollView = true;
//                MaterialDialog materialDialog = new MaterialDialog.Builder(_context.getActivity())
//                        .title("10 September 2015")
//                        .customView(R.layout.dialog_setmark, wrapInScrollView)
//                        .titleColorRes(R.color.primary)
//                        .widgetColorRes(R.color.primary)
//                        .positiveText("Save")
//                        .positiveColorRes(R.color.primary)
//                        .negativeText("Cancel")
//                        .negativeColorRes(R.color.red)
//                        .build();
//
//                View view = materialDialog.getCustomView();
//
//                // you need to have a list of data that you want the spinner to display
//                List<String> spinnerArray =  new ArrayList<String>();
//                spinnerArray.add("Attend");
//                spinnerArray.add("Absent");
//                spinnerArray.add("Ill");
//                spinnerArray.add("Late");
//                spinnerArray.add("Pass");
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                        _context.getActivity(), R.layout.spinner_mark_item , spinnerArray);
//
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                Spinner sItems = (Spinner) view.findViewById(R.id.spinnerAttendance);
//                sItems.setAdapter(adapter);
//
//
//                materialDialog.show();
//            }
//        });

        gridLayout.addView(recyclableTextView);
    }



    public void AddBehaviourCell(GridLayout gridLayout, int row, int column, final List<BehaviourStatus> list,
                                 int bgColor, int textColor, int textSizeSp, Typeface textFont) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.setMargins(1, 0, 1, 1);
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(row);

        TextView recyclableTextView = makeTableRowWithText(
                (list != null) ? Integer.toString(list.size()) : "",
                _cellWidth,
                _cellHeight,
                textSizeSp);
        recyclableTextView.setLayoutParams(param);
        recyclableTextView.setTypeface(textFont);
        recyclableTextView.setBackgroundColor(bgColor);
        recyclableTextView.setTextColor(textColor);

//        final String[] spinnerArray = new String[list.size()];
//        for(int i=0; i<list.size(); i++) {
//            spinnerArray[i] = list.get(i).getBehaviourName();
//        }

        recyclableTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: show dialog
                AlertDialog.Builder adb = new AlertDialog.Builder(_context.getActivity());
//                adb.setTitle("Behaviour");
                // создаем view из dialog.xml
                LinearLayout view = (LinearLayout) _context.getActivity().getLayoutInflater().inflate(R.layout.dialog_behaviour, null);
                // устанавливаем ее, как содержимое тела диалога
                adb.setView(view);

                final TextView textViewEmpty = (TextView) view.findViewById(R.id.textViewBehaviourEmpty);
                if (list == null || list.size() > 0)
                    textViewEmpty.setVisibility(View.GONE);

                //set list
                ListView listView = (ListView) view.findViewById(R.id.listViewDialogBehaviour);
                final BehaviourAdapter behaviourAdapter = new BehaviourAdapter(_context.getActivity(), list);
                listView.setAdapter(behaviourAdapter);


                final List<BehaviourStatus> choiceList = new ArrayList<>();
                for (BehaviourStatus item : _behaviourSchoolList) {
                    if (!list.contains(item)) {
                        choiceList.add(item);
                    }
                }


                //button Add Behaviour click
                Button b = (Button) view.findViewById(R.id.buttonAddBehaviour);
                b.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(_context.getActivity());
                        builderSingle.setIcon(R.mipmap.ic_launcher);
                        builderSingle.setTitle("Select Behaviour:");


                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                _context.getActivity(),
                                android.R.layout.select_dialog_singlechoice);

                        for (BehaviourStatus status : choiceList) {
                            arrayAdapter.add(status.getStatus());
                        }


                        builderSingle.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builderSingle.setAdapter(
                                arrayAdapter,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //do something...
                                        list.add(choiceList.get(which));
                                        behaviourAdapter.notifyDataSetChanged();
                                        textViewEmpty.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                });

                        builderSingle.show();
                    }
                });


                adb.show();
            }
        });

        gridLayout.addView(recyclableTextView);
    }


    //region Lesson filter
    public List<BehaviourStatus> getJournalBehaviour(Map<Long, List<Behaviour>> mapBehaviour, Lesson lesson, String groupName) {
        List<BehaviourStatus> list = null;
        //Get presence
        if (mapBehaviour != null && mapBehaviour.size() > 0) {
            for(Long id : mapBehaviour.keySet()) {
                if (id == lesson.getId() && lesson.getGroupNames().trim().equals(groupName)) {
                    list = new ArrayList<>();
                    List<Behaviour> behaviourList = mapBehaviour.get(id);
                    for (Behaviour behaviour : behaviourList) {
                        //TODO: need to check!
//                        if (behaviour.getPersonId() == studentId)

                        //todo: fix
//                            list.add(new PresenceStatus(behaviour.getBehaviourId(), behaviour.getBehaviourName()));
                    }
                    break;
                }
            }
        }
        return list;
    }


    //new
    public List<Behaviour> getJournalBehavior(Map<Long, List<Behaviour>> mapBehaviour, Lesson lesson, String groupName) {
        List<Behaviour> list = null;
        //Get presence
        if (mapBehaviour != null && mapBehaviour.size() > 0) {
            for(Long id : mapBehaviour.keySet()) {
                if (id == lesson.getId() && lesson.getGroupNames().trim().equals(groupName)) {
                    list = new ArrayList<>();
                    List<Behaviour> behaviourList = mapBehaviour.get(id);
                    for (Behaviour behaviour : behaviourList) {
                        //TODO: need to check!
//                        if (behaviour.getPersonId() == studentId)
                        list.add(behaviour);
                    }
                    break;
                }
            }
        }
        return list;
    }


    public List<BehaviourStatus> getJournalBehaviour(Map<Long, List<Behaviour>> mapBehaviour, Lesson lesson, Long studentId) {
        List<BehaviourStatus> list = null;
        //Get presence
        if (mapBehaviour != null && mapBehaviour.size() > 0) {
            for(Long id : mapBehaviour.keySet()) {
                if (id == lesson.getId()) {
                    for(Behaviour item : mapBehaviour.get(id)) {
                        if (item.getPersonId() == studentId) {
                            list = new ArrayList<>();
                            List<Behaviour> behaviourList = mapBehaviour.get(id);
                            for (Behaviour behaviour : behaviourList) {
                                if (behaviour.getPersonId() == studentId) {
                                    //todo: fix
//                                    list.add(new PresenceStatus(behaviour.getBehaviourId(), behaviour.getBehaviourName()));
                                }
                            }
                            break;
                        }
                    }
                    if (list != null)
                        break;
                }
            }
        }
        return list;
    }



    //new
    public List<Behaviour> getJournalBehavior(Map<Long, List<Behaviour>> mapBehaviour, Lesson lesson, Long studentId) {
        List<Behaviour> list = null;
        //Get presence
        if (mapBehaviour != null && mapBehaviour.size() > 0) {
            for(Long id : mapBehaviour.keySet()) {
                if (id == lesson.getId()) {
                    for(Behaviour item : mapBehaviour.get(id)) {
                        if (item.getPersonId() == studentId) {
                            list = new ArrayList<>();
                            List<Behaviour> behaviourList = mapBehaviour.get(id);
                            for (Behaviour behaviour : behaviourList) {
                                if (behaviour.getPersonId() == studentId)
                                    list.add(behaviour);
                            }
                            break;
                        }
                    }
                    if (list != null)
                        break;
                }
            }
        }
        return list;
    }




    public Presence getJournalPresence(List<Presence> listPresence, Lesson lesson, String groupName) {
        Presence presence = null;
        //Get presence
        if (listPresence != null) {
            for(Presence p : listPresence) {
                if (p.getLesson() == lesson.getId()
                        && lesson.getGroupNames().trim().equals(groupName) && !p.getStatus().equals("Attend")) {
                    presence = p;
                    break;
                }
            }
        }
        return presence;
    }

    public Presence getJournalPresence(List<Presence> listPresence, Lesson lesson, long personId) {
        Presence presence = null;
        //Get presence
        if (listPresence != null) {
            for(Presence p : listPresence) {
                if (p.getLesson() == lesson.getId() && p.getPerson() == personId && !p.getStatus().equals("Attend")) {
                    presence = p;
                    break;
                }
            }
        }
        return presence;
    }

    public Mark getJournalMark(List<Mark> list, Lesson lesson, long personId) {
        Mark resultMark = null;
        //Get mark
        if (lesson.getWorks() != null && list != null) {
            for (Work work : lesson.getWorks()) {
                for (Mark mark : list) {
                    if (mark.getLesson() == lesson.getId() && mark.getWork() == work.getId() && mark.getPerson() == personId) {
                        resultMark = mark;
                        break;
                    }
                }
            }
        }
        return resultMark;
    }

    public Mark getJournalMarkByWork(List<Mark> list, Work work, long personId) {
        Mark resultMark = null;
        //Get mark
        if (list != null) {
            for (Mark mark : list) {
                if (mark.getWork() == work.getId() && mark.getPerson() == personId) {
                    resultMark = mark;
                    break;
                }
            }
        }
        return resultMark;
    }

    public Mark getJournalMark(List<Mark> list, Lesson lesson, String groupName) {
        Mark resultMark = null;
        //Get mark
        if (lesson.getWorks() != null) {
            for (Work work : lesson.getWorks()) {
                for (Mark mark : list) {
                    if (mark.getLesson() == lesson.getId() && mark.getWork() == work.getId()
                            && lesson.getGroupNames().equals(groupName)) {
                        resultMark = mark;
                        break;
                    }
                }
            }
        }
        return resultMark;
    }
    //endregion


    public Work getLessonWork(Lesson lesson) {
        if (lesson.getWorks() != null)
            for(Work work : lesson.getWorks()) {
                if (work != null) {
                    return work;
                }
            }
        return null;
    }

}