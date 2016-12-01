package il.co.yomanim.mobileyomanim.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;
import core.model.journal.Behaviour;
import core.model.journal.BehaviourStatus;
import core.model.journal.PresenceStatus;
import il.co.yomanim.mobileyomanim.R;


public class BehaviourPresenceAdapter extends ArrayAdapter<BehaviourStatus> {
    private final Context context;
    private final List<BehaviourStatus> values;
    private final List<Behaviour> checkedValues;

    public BehaviourPresenceAdapter(Context context, List<BehaviourStatus> values, List<Behaviour> checkedValues) {
        super(context, R.layout.row_checkboxes, values);
        this.context = context;
        this.values = values;
        this.checkedValues = checkedValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_checkboxes, parent, false);
        rowView.setTag(values.get(position).getId());

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(values.get(position).getStatus());

        if (checkedValues != null && isBehaviorChecked(values.get(position))) {
            CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
            checkBox.setChecked(true);
        }

        return rowView;
    }

    private boolean isBehaviorChecked(BehaviourStatus behaviourPresenceStatus) {
        for(Behaviour item : checkedValues) {
            if (item.getBehaviourId() == behaviourPresenceStatus.getId())
                return true;
        }
        return false;
    }
}