package il.co.yomanim.mobileyomanim.adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

import core.model.journal.Presence;
import il.co.yomanim.mobileyomanim.R;


public class PresenceSchoolAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;
    private Presence currentPresence;

    public int selectedPosition = 0;

    public PresenceSchoolAdapter(Context context, List<String> values, Presence presence) {
        super(context, R.layout.row_presence, values);
        this.context = context;
        this.values = values;
        this.currentPresence = presence;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_presence, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewName);
        textView.setText(values.get(position));

        if ((currentPresence == null && position == 0)
                || (currentPresence != null && currentPresence.getStatus().equals(values.get(position)))) {
            rowView.setBackgroundColor(Color.parseColor("#116ea9"));
            textView.setTextColor(Color.WHITE);
            selectedPosition = position;
        }

        return rowView;
    }
}