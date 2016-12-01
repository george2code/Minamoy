package il.co.yomanim.mobileyomanim.grids;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import core.helpers.ConstantsHelper;
import core.model.TimetableItem;
import core.utils.DateUtils;
import core.utils.FontUtils;
import core.utils.LocaleUtils;
import core.utils.NumberUtils;
import il.co.yomanim.mobileyomanim.R;


public class TimetableTiles
{
    private Context _context;
    private FrameLayout _layout;
    private FragmentActivity _activity;
    private LayoutInflater _inflater;
    private GridLayout _gridLayout;

    private int _cellWidth;
    private int _cellBigWidth;
    private int _rowHeight;
    private int _rowDateHeight;

    private Typeface _fontMedium;


    public TimetableTiles(FrameLayout layout, FragmentActivity activity, LayoutInflater inflater) {
        //initial
        _layout = layout;
        _context = layout.getContext();
        _activity = activity;
        _inflater = inflater;

        _cellWidth = NumberUtils.dpToPixels(_context, 48);
        _cellBigWidth = NumberUtils.dpToPixels(_context, 340);
        _rowHeight = NumberUtils.dpToPixels(_context, 56);
        _rowDateHeight = NumberUtils.dpToPixels(_context, 55);

        _fontMedium = FontUtils.getRobotoMedium(_context.getAssets());
    }


    public void Render(List<TimetableItem> list)
    {
        try {
            Map<Date, List<TimetableItem>> map = new TreeMap<>();
            for (TimetableItem item : list) {
                Date key = item.getLessonDate();
                if (map.get(key) == null) {
                    map.put(key, new ArrayList<TimetableItem>());
                }
                map.get(key).add(item);
            }

            //init grid
            _gridLayout = (GridLayout) _layout.findViewById(R.id.gridLayoutTimeTable);
            _gridLayout.removeAllViews();
            _gridLayout.setColumnCount(2);
            _gridLayout.setRowCount(list.size());


            int colorWhite = _context.getResources().getColor(R.color.white);
            int colorGrey = _context.getResources().getColor(R.color.grey_text);

            int row = 0;
            for(Date key : map.keySet()) {
                List<TimetableItem> values = map.get(key);

                int rowSpan = 0;
                if (values.size() > 1) {
                    rowSpan = values.size();
                }

                String dateText = MessageFormat.format("{0} {1}\n{2}",
                        DateUtils.dateToDayString(key),
                        DateUtils.dateToMonthString(key),
                        DateUtils.dateToDayOfWeekString(key));

                int bgColor = GetBgColorByDate(key);

                if (LocaleUtils.isRtl(_context)) {
                    int initialRow = row;

                    for(TimetableItem tti : values) {
                        AddCell(row, 0, _cellBigWidth, bgColor, tti);
//                        AddCell(row, 0, 0, bgColor, tti);
                        row++;
                    }

                    //Render date cell, 1st column
                    if (rowSpan > 0) {
                        AddRowspanCell(initialRow, 1, rowSpan, colorWhite, dateText, colorGrey, 14, _fontMedium);
                    } else {
                        AddCell(initialRow, 1, _cellWidth, colorWhite, dateText, colorGrey, 14, _fontMedium);
                    }
                } else {
                    //Render date cell, 1st column
                    if (rowSpan > 0) {
                        AddRowspanCell(row, 0, rowSpan, colorWhite, dateText, colorGrey, 14, _fontMedium);
                    } else {
                        AddCell(row, 0, _cellWidth, colorWhite, dateText, colorGrey, 14, _fontMedium);
                    }

                    for(TimetableItem tti : values) {
                        AddCell(row, 1, 0, bgColor, tti);
                        row++;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TimetableTiles", e.getMessage());
        }
    }


    // Header date
    private void AddRowspanCell(int row, int column, int rowspan, int bgColor, String text,
                                int textColor, int textSize, Typeface textFont) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = ActionBar.LayoutParams.WRAP_CONTENT;
        param.setMargins(1, 1, 1, 1);
        param.setGravity(Gravity.CENTER);
        param.rowSpec = GridLayout.spec(row, rowspan);
        param.columnSpec = GridLayout.spec(column);

        int calcWidth = _cellWidth;
        int calcHeight = _rowDateHeight * rowspan;

        TextView recyclableTextView = makeTableRowWithText(text, calcWidth, calcHeight);
        recyclableTextView.setLayoutParams(param);
        recyclableTextView.setTypeface(textFont);
        recyclableTextView.setBackgroundColor(bgColor);
        recyclableTextView.setTextColor(textColor);
        recyclableTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        _gridLayout.addView(recyclableTextView);
    }

    private void AddCell(int row, int column, int width, int bgColor, String text,
                         int textColor, int textSize, Typeface textFont) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();

        param.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
        param.setMargins(1, 0, 1, 1);
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(row);

        TextView recyclableTextView = makeTableRowWithText(text, width, _rowHeight);
        recyclableTextView.setLayoutParams(param);
        recyclableTextView.setTypeface(textFont);
        recyclableTextView.setBackgroundColor(bgColor);
        recyclableTextView.setTextColor(textColor);
        recyclableTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        _gridLayout.addView(recyclableTextView);
    }

    private void AddCell(int row, int column, int width, int bgColor, TimetableItem timetableItem) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = ActionBar.LayoutParams.WRAP_CONTENT;
        param.width = (width > 0) ? width : ActionBar.LayoutParams.MATCH_PARENT;
        param.setMargins(1, 0, 1, 1);

        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(row);

        View view = _inflater.inflate(R.layout.item_timetable_cell, null);
        FrameLayout fl = (FrameLayout)view.findViewById(R.id.frameLayoutTimetable);
        fl.setBackgroundColor(bgColor);

        TextView tvName = (TextView) view.findViewById(R.id.textViewTimeName);
        tvName.setText(timetableItem.getName());

        String typeString = MessageFormat.format("Lesson {0}", timetableItem.getNumber());
        if (timetableItem.getStart() != null && timetableItem.getFinish() != null) {
            typeString += MessageFormat.format(": {0} - {1}",
                    DateUtils.dateToTimeString(timetableItem.getStart()),
                    DateUtils.dateToTimeString(timetableItem.getFinish()));
        }
        TextView tvType = (TextView) view.findViewById(R.id.textViewTimeHours);
        tvType.setText(typeString);

        TextView tvPlace = (TextView) view.findViewById(R.id.textViewPlace);
        if (timetableItem.getPlace() != null) {
            tvPlace.setText(timetableItem.getPlace());

            if (LocaleUtils.isRtl(_context)) {

                FrameLayout.LayoutParams llp = (FrameLayout.LayoutParams) tvPlace.getLayoutParams();
                llp.setMargins(0, 0, 10, 0); // llp.setMargins(left, top, right, bottom);
                tvPlace.setLayoutParams(llp);
            }
        } else {
            tvPlace.setVisibility(View.GONE);
        }



        //todo: если классическая система, показывать Grade
        //todo: а он приедет в поле GroupNames
        //todo: если это классическая система, если это мультикласс там будет предметная группа

        view.setLayoutParams(param);
        _gridLayout.addView(view);
    }

    public TextView makeTableRowWithText(String text, int widthInPercentOfScreenWidth, int fixedHeightInPixels) {
        TextView recyclableTextView = new TextView(_activity);
        recyclableTextView.setText(text);
        recyclableTextView.setWidth(widthInPercentOfScreenWidth);
        recyclableTextView.setHeight(fixedHeightInPixels);
        recyclableTextView.setGravity(Gravity.CENTER);
        return recyclableTextView;
    }

    private int GetBgColorByDate(Date date) {
        int day = DateUtils.getDayOfWeek(date);
        int color = _context.getResources().getColor(R.color.tm_bg_day);

        switch (day) {
            case ConstantsHelper.DAY_SUNDAY:
                color = _context.getResources().getColor(R.color.tm_bg_sunday);
                break;
            case ConstantsHelper.DAY_MONDAY:
                color = _context.getResources().getColor(R.color.tm_bg_monday);
                break;
            case ConstantsHelper.DAY_TUESDAY:
                color = _context.getResources().getColor(R.color.tm_bg_tuesday);
                break;
            case ConstantsHelper.DAY_WEDNESDAY:
                color = _context.getResources().getColor(R.color.tm_bg_wednesday);
                break;
            case ConstantsHelper.DAY_THURSDAY:
                color = _context.getResources().getColor(R.color.tm_bg_thursday);
                break;
            default:
                break;
        }

        return color;
    }
}