package il.co.yomanim.mobileyomanim.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.MessageFormat;
import java.util.List;
import core.model.Person;
import de.hdodenhof.circleimageview.CircleImageView;
import il.co.yomanim.mobileyomanim.R;


public class ChildAdapter extends ArrayAdapter<Person>
{
    private Context context;
    private List<Person> childList;

    public ChildAdapter(Context context, int resource, List<Person> objects) {
        super(context, resource, objects);
        this.context = context;
        this.childList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_child_row, parent, false);

        Typeface fontRobotoMedium = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");

        try {
            // Display child name in the TextView widget
            Person child = childList.get(position);
            TextView tv = (TextView) view.findViewById(R.id.textViewChildTitle);
            tv.setTypeface(fontRobotoMedium);
            tv.setText(MessageFormat.format("{0} {1}", child.getFirstName(), child.getLastName()));

            // Title class name
            TextView tvClassTitle = (TextView) view.findViewById(R.id.textViewChildClassTitle);
            tvClassTitle.setTypeface(fontRobotoMedium);

            // Person class name
            TextView tvPersonId = (TextView) view.findViewById(R.id.textViewChildPersonId);
            tv.setTypeface(fontRobotoMedium);
            if (child.getClassName() != null) {
                tvPersonId.setText(MessageFormat.format("{0}", String.valueOf(child.getClassName())));
            }

            //Display flower photo in ImageView widget
            CircleImageView image = (CircleImageView) view.findViewById(R.id.circleViewChild);
            image.setImageURI(null);
            image.setImageURI(Uri.parse(child.getAvatarUrl()));
        } catch (Exception ex) {
            Log.e("Child row", ex.getMessage());
        }

        return view;
    }
}