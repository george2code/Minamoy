package il.co.yomanim.mobileyomanim.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import core.model.journal.BehaviourStatus;
import core.model.journal.PresenceStatus;
import il.co.yomanim.mobileyomanim.R;


public class BehaviourAdapter extends ArrayAdapter<BehaviourStatus> {
    private final Context context;
    private final List<BehaviourStatus> values;

    public BehaviourAdapter(Context context, List<BehaviourStatus> values) {
        super(context, R.layout.row_behaviour, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_behaviour, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values.get(position).getStatus());
        // Change the icon for Windows and iPhone
//        String s = values[position];
//        if (s.startsWith("Windows7") || s.startsWith("iPhone")
//                || s.startsWith("Solaris")) {
//            imageView.setImageResource(R.drawable.no);
//        } else {
//            imageView.setImageResource(R.drawable.ok);
//        }

        return rowView;
    }
}