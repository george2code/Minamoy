package il.co.yomanim.mobileyomanim.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import core.model.TimetableItem;
import core.utils.DateUtils;
import il.co.yomanim.mobileyomanim.R;


public class TimetableAdapter extends ArrayAdapter<TimetableItem>
{
    private Context context;
    private List<TimetableItem> timetableList;

    public TimetableAdapter(Context context, int resource, List<TimetableItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.timetableList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_timetable_row, parent, false);

        // Display tm name in the TextView widget
        TimetableItem timetableItem = timetableList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textViewTimeName);
        tv.setText(timetableItem.getName());

        // Type
        TextView tvType = (TextView) view.findViewById(R.id.textViewTimeType);
        tvType.setText(timetableItem.getType());

        // Time
        TextView tvTime = (TextView) view.findViewById(R.id.textViewTimeHours);
        tvTime.setText(MessageFormat.format("{0} - {1}",
                DateUtils.dateToTimeString(timetableItem.getStart()),
                DateUtils.dateToTimeString(timetableItem.getFinish())
        ));

        // Date
        Date lessonDate = timetableItem.getStart();
        TextView tvDate = (TextView) view.findViewById(R.id.textViewTimeDate);
        tvDate.setText(MessageFormat.format("{0}\n{1}\n{2}",
                DateUtils.dateToDayString(lessonDate),
                DateUtils.dateToDayOfWeekString(lessonDate),
                DateUtils.dateToYearString(lessonDate)
                ));



        // Set type color
        String color = "#9e9e9e";
        switch (timetableItem.getType()) {
            case "Full Week":
                color = "#f44336";
                break;
            case "Each Day":
                color = "#03a9f4";
                break;
            case "Weekend":
                color = "#4caf50";
                break;
            case "Undefined":
                break;
            default:
                break;
        }
        //set color to frame
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frameLayoutTimetable);
        frameLayout.setBackgroundColor(Color.parseColor(color));



        return view;
    }
}