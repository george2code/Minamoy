package il.co.yomanim.mobileyomanim.compound;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import core.handlers.HttpManager;
import core.handlers.parser.MarkJSONParser;
import core.model.journal.Behaviour;
import core.model.journal.BehaviourStatus;
import core.model.journal.PresenceStatus;
import core.model.journal.Lesson;
import core.model.journal.LessonActivity;
import core.model.journal.Mark;
import core.model.journal.Presence;
import core.utils.FontUtils;
import core.utils.NumberUtils;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.adapters.BehaviourPresenceAdapter;
import il.co.yomanim.mobileyomanim.adapters.PresenceSchoolAdapter;


public class CompoundCell extends LinearLayout {

    private TextView _txtPresence, _txtBehaviour;
    private Context _context;
    private View _rootView;
    private Mark _mark;
    private Lesson _lesson;
    private long _personId;
    private List<BehaviourStatus> _behaviourSchoolList;
    private List<String> _presenceArray;
    private Presence _presence;
    private List<Behaviour> _behaviours;
    private String _token;


    public CompoundCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _rootView = inflater.inflate(R.layout.complex_cell, this);

        _txtPresence = (TextView) _rootView.findViewById(R.id.textViewPresence);
        _txtBehaviour = (TextView) _rootView.findViewById(R.id.textViewBehaviour);
    }


//    public View getPresenceCell(Presence presence) {
//        if (presence != null) {
//            _txtMark.setText(presence.getStatus().substring(0, 1));
//            _rootView.setBackgroundColor(presence.getPresenceBackground());
//
//            _txtPresence.setVisibility(GONE);
//            _txtBehaviour.setVisibility(GONE);
//
//            return _rootView;
//        } else {
//            //Render empty cell
//            return AddCell(Color.WHITE, Color.WHITE, 14, FontUtils.getRobotoMedium(_context.getAssets()));
//        }
//    }
//
//
//    public View getBehaviourCell(List<BehaviourStatus> behaviours) {
//        if (behaviours != null) {
//            _txtMark.setText(String.valueOf(behaviours.size()));
//            _rootView.setBackgroundColor(Color.parseColor("#116ea9"));
//
//            _txtPresence.setVisibility(GONE);
//            _txtBehaviour.setVisibility(GONE);
//
//            ShowBehavioursDialog(_rootView, behaviours);
//
//            return _rootView;
//        } else {
//            _txtBehaviour.setText("");
//            //Render empty cell
//            return AddCell(Color.WHITE, Color.WHITE, 14, FontUtils.getRobotoMedium(_context.getAssets()));
//        }
//    }


    public View getMarkCell(Mark mark, Lesson lesson, long personId, String token) {
        _mark = mark;
        _lesson = lesson;
        _token = token;
        _personId = personId;

        _txtPresence.setVisibility(GONE);
        _txtBehaviour.setText("");

        if (mark != null) {
            setMarkValue(mark);
        } else {
            //Render empty cell
            _rootView.setBackgroundColor(Color.WHITE);
        }

        return _rootView;
    }



    public View getCell(Presence presence, List<String> presenceArray,
                        List<Behaviour> behaviours, List<BehaviourStatus> behaviourSchoolList,
                        Lesson lesson, long personId, String token)
    {
        _behaviourSchoolList = behaviourSchoolList;
        _presence = presence;
        _presenceArray = presenceArray;
        _behaviours = behaviours;
        _lesson = lesson;
        _token = token;
        _personId = personId;

        if (presence != null || behaviours != null) {
            setValue(presence, behaviours);
        } else {
            //Render empty cell
            _rootView.setBackgroundColor(Color.WHITE);
        }

        //only edu staff can edit journal, so we need some person
//        if (_personId > 0)
//            ShowDialog(_rootView);

        return _rootView;
    }


    private void setMarkValue(Mark mark) {
        _rootView.setBackgroundColor(mark.getMarkBackground());
        _txtBehaviour.setText(mark.getValue());
    }


    private void setValue(Presence presence, List<Behaviour> behaviours) {

        _txtBehaviour.setVisibility(GONE);
        _txtPresence.setVisibility(GONE);


            try {
                int backgroundColor = -1;

                if (presence != null) {
                    _txtPresence.setVisibility(VISIBLE);
                    _txtPresence.setText(presence.getStatus().substring(0, 1));
                    if (backgroundColor == -1) {
                        backgroundColor = presence.getPresenceBackground();

                        //TODO: maybe, remove this block... ?
                        if (!_rootView.isShown()) {
                            _rootView.setVisibility(VISIBLE);
                        }
                        // end of remove

                        _rootView.setBackgroundColor(backgroundColor);
                    }
                } else {
                    _txtPresence.setText("");
                    _rootView.setBackgroundColor(Color.WHITE);
                }

                if (behaviours != null) {
                    _txtBehaviour.setVisibility(VISIBLE);
                    _txtBehaviour.setText("1");
                    if (!TextUtils.isEmpty(behaviours.get(0).getBehaviourName())) {
                        _txtBehaviour.setText(behaviours.get(0).getBehaviourName());
                    }
                } else {
                    _txtBehaviour.setText("");
                }
            } catch (Exception e) {
                Log.e("cell", e.getMessage());
            }

    }


    public View AddCell(int bgColor, int textColor, int textSizeSp, Typeface textFont) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.setMargins(1, 0, 1, 1);
        param.setGravity(Gravity.CENTER);
        //make textview
        TextView recyclableTextView = makeTableRowWithText(
                "",
                NumberUtils.dpToPixels(_context, 50),
                NumberUtils.dpToPixels(_context, 39),
                textSizeSp);
        recyclableTextView.setLayoutParams(param);
        recyclableTextView.setTypeface(textFont);
        recyclableTextView.setBackgroundColor(bgColor);
        recyclableTextView.setTextColor(textColor);

        return recyclableTextView;
    }


    public TextView makeTableRowWithText(String text, int widthInPercentOfScreenWidth, int fixedHeightInPixels, int textSizeSp) {
        TextView recyclableTextView = new TextView(_context);
        recyclableTextView.setText(text);
        recyclableTextView.setTextColor(Color.BLACK);
        recyclableTextView.setBackgroundColor(Color.WHITE);
        recyclableTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
        recyclableTextView.setWidth(widthInPercentOfScreenWidth);
        recyclableTextView.setHeight(fixedHeightInPixels);
        recyclableTextView.setGravity(Gravity.CENTER);

        return recyclableTextView;
    }


    private void ShowBehavioursDialog(View view, final List<BehaviourStatus> behaviours) {
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String[] arr = new String[behaviours.size()];
                int i = 0;
                for (BehaviourStatus bhs : behaviours) {
                    arr[i] = bhs.getStatus();
                    i++;
                }

                new MaterialDialog.Builder(_context)
                        .items(arr)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            }
                        })
                        .negativeText("Close")
                        .negativeColorRes(R.color.red)
                        .show();
            }
        });
    }


    private void ShowDialog(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = android.text.format.DateFormat.format("dd MMMM yyyy", _lesson.getDate()).toString();

                //show dialog
                MaterialDialog materialDialog = new MaterialDialog.Builder(_context)
                    .title(title)
                    .customView(R.layout.dialog_setmark, true)
                        .titleColorRes(R.color.primary)
                        .widgetColorRes(R.color.primary)
                        .positiveText("Save")
                        .positiveColorRes(R.color.primary)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                boolean isValid = true;

                                // Code
                                LessonActivity lessonActivity = new LessonActivity();

                                //init mark
                                if (_lesson.getJournalWork() != null) {
                                    EditText txtMark = (EditText) dialog.findViewById(R.id.editTextMark);
                                    txtMark.setError(null);
                                    if (!txtMark.getText().toString().equals("")) {
                                        Integer markValue = Integer.parseInt(txtMark.getText().toString());
                                        if (markValue > 0 && markValue <= 100)
                                        {
                                            Mark mark = new Mark();
                                            mark.setLesson(_lesson.getId());
                                            mark.setPerson(_personId);
                                            if (_lesson.getJournalWork() != null)
                                                mark.setWork(_lesson.getJournalWork().getId());
                                            mark.setValue(markValue.toString());

                                            //set mark
                                            lessonActivity.setResource(mark);
                                        }
                                        else {
                                            txtMark.setError("Error mark");
                                            isValid = false;
                                        }
                                    }
                                }
                                //////////////////////////////////// end mark


                                ListView lvBehavior = (ListView) dialog.findViewById(R.id.listView);
                                int count = lvBehavior.getChildCount();
                                if (count > 0) {
                                    List<Behaviour> listChecked = new ArrayList<>();
                                    for (int j=0; j<count; j++) {
                                        View behaviorView = lvBehavior.getChildAt(j);
                                        if (((CheckBox)behaviorView.findViewById(R.id.checkBox)).isChecked()) {
                                            long sid = Long.parseLong(behaviorView.getTag().toString());

                                            Behaviour behavior = new Behaviour();
                                            behavior.setLessonId(_lesson.getId());
                                            behavior.setPersonId(_personId);
                                            behavior.setBehaviourId(sid);
                                            listChecked.add(behavior);
                                        }
                                    }
                                    //set
                                    if (listChecked.size() > 0)
                                        lessonActivity.setBehaviors(listChecked);
                                }
                                //////////////////////////////////// end behaviors



                                //init presence
                                ListView lv = (ListView) dialog.findViewById(R.id.listViewPresence);
                                int pos = ((PresenceSchoolAdapter) lv.getAdapter()).selectedPosition;
                                if (pos > 0) {
                                    String presenceStr = lv.getAdapter().getItem(pos).toString();
                                    //build object
                                    Presence presence = new Presence();
                                    presence.setLesson(_lesson.getId());
                                    presence.setPerson(_personId);
                                    presence.setStatus(presenceStr);
                                    //update lessonactivity
                                    lessonActivity.setLessonLogEntry(presence);
                                }
                                //////////////////////////////////// end presence



                                //other id
                                lessonActivity.setLesson(_lesson.getId());
                                lessonActivity.setPerson(_personId);
                                if (_lesson.getJournalWork() != null) {
                                    lessonActivity.setWorkId(_lesson.getJournalWork().getId());
                                }

                                //Request task
//                                if (isValid) {
//                                    RequestTask task = new RequestTask();
//                                    task.execute(lessonActivity);
//                                }
                            }
                        })
                        .negativeText("Cancel")
                    .negativeColorRes(R.color.red)
                    .build();

                View view = materialDialog.getCustomView();


                if (view != null)
                {
                    BehaviourPresenceAdapter adapter = new BehaviourPresenceAdapter(
                            _context,
                            _behaviourSchoolList,
                            _behaviours);
                    ListView listView = (ListView) view.findViewById(R.id.listView);
                    listView.setAdapter(adapter);

                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listView.getLayoutParams();
                    lp.height = NumberUtils.dpToPixels(_context, 31) * _behaviourSchoolList.size();
                    listView.setLayoutParams(lp);


                    //mark
//                    EditText txtMark = (EditText) view.findViewById(R.id.editTextMark);
//                    if (_lesson.getJournalWork() != null) {
//                        if (_mark != null) {
//                            txtMark.setText(_mark.getValue());
//                        }
//                    } else {
//                        txtMark.setVisibility(View.GONE);
//                    }



                    //presence buttons
                    if (_presenceArray != null && _presenceArray.size() > 0)
                    {
                        if (!_presenceArray.contains("NotSet"))
                            _presenceArray.add(0, "NotSet");

                        final ListView lvPresences = (ListView) view.findViewById(R.id.listViewPresence);
                        final PresenceSchoolAdapter adapterPresence = new PresenceSchoolAdapter(
                                _context,
                                _presenceArray,
                                _presence);
                        lvPresences.setAdapter(adapterPresence);

                        LinearLayout.LayoutParams lpl = (LinearLayout.LayoutParams) lvPresences.getLayoutParams();
                        lpl.width = NumberUtils.dpToPixels(getContext(), 120);
                        lpl.height = NumberUtils.dpToPixels(getContext(), 30) * _presenceArray.size();
                        lvPresences.setLayoutParams(lpl);

                        //
                        lvPresences.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //highligh active item
                                TextView textView = (TextView) view.findViewById(R.id.textViewName);
                                view.setBackgroundColor(Color.parseColor("#116ea9"));
                                textView.setTextColor(Color.WHITE);

                                ((PresenceSchoolAdapter)lvPresences.getAdapter()).selectedPosition = position;

                                //clear other items
                                for(int j=0; j<lvPresences.getChildCount(); j++) {
                                    if (j != position) {
                                        View childView = lvPresences.getChildAt(j);
                                        childView.setBackgroundColor(Color.WHITE);
                                        TextView childText = (TextView) childView.findViewById(R.id.textViewName);
                                        childText.setTextColor(Color.parseColor("#116ea9"));
                                    }
                                }
                            }
                        });
                    }
                }

                materialDialog.show();
            }
        });
    }


//    private class RequestTask extends AsyncTask<LessonActivity, Void, LessonActivity> {
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected LessonActivity doInBackground(LessonActivity... params) {
//            String content = HttpManager.postMark(_context, params[0], _token);
//            if (content != null) {
//                return MarkJSONParser.parseLessonActivity(content);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(final LessonActivity result) {
//            if (result != null) {
//                //update local variables with response
//                _presence = result.getLessonLogEntry();
//                _behaviours = result.getBehaviors();
//                _mark = result.getResource();
//
//                if (result.getBehaviors() == null && result.getResource() == null && result.getLessonLogEntry() == null) {
//                    //clear
//                    _txtBehaviour.setVisibility(GONE);
//                    _txtPresence.setVisibility(GONE);
//                    _rootView.setBackgroundColor(Color.WHITE);
//                } else {
//                    //set mark-presence-behaviors
//                    setValue(result.getResource(), result.getLessonLogEntry(), result.getBehaviors());
//                }
//            }
//        }
//    }
}