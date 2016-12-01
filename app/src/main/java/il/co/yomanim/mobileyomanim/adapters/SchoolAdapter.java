package il.co.yomanim.mobileyomanim.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;
import core.model.School;
import core.model.SchoolMembership;
import core.utils.StringUtils;
import il.co.yomanim.mobileyomanim.R;


public class SchoolAdapter extends ArrayAdapter<SchoolMembership>
{
    private Context context;
    private List<SchoolMembership> schoolMembershipList;

    public SchoolAdapter(Context context, int resource, List<SchoolMembership> objects) {
        super(context, resource, objects);
        this.context = context;
        this.schoolMembershipList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_school_row, parent, false);

        Typeface fontRobotoMedium = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        Typeface fontRobotoLight = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");

        // Display school name in the TextView widget
        School school = schoolMembershipList.get(position).getSchool();
        TextView tv = (TextView) view.findViewById(R.id.textViewSchoolTitle);
        tv.setTypeface(fontRobotoMedium);
        tv.setText(school.getName());

        // City
        TextView tvCity = (TextView) view.findViewById(R.id.textViewCity);
        tvCity.setTypeface(fontRobotoLight);
        tvCity.setText(school.getCity());

        // Roles
        TextView tvRoleTitle = (TextView) view.findViewById(R.id.textViewRoleTitle);
        tvRoleTitle.setTypeface(fontRobotoMedium);

        TextView tvRoles = (TextView) view.findViewById(R.id.textViewRole);
        String roles = StringUtils.GetCommaSeparatedString(schoolMembershipList.get(position).getRoles());
        if (!roles.equals("")) {
            tvRoles.setTypeface(fontRobotoMedium);
            tvRoles.setText(roles.replace("'", ""));
        } else {
            tvRoles.setVisibility(View.INVISIBLE);
        }


        if (school.getBitmap() != null) {
            // if we already have bitmap in memory
            ImageView image = (ImageView) view.findViewById(R.id.imageViewSchoolAvatar);
            image.setImageBitmap(school.getBitmap());
        }
        else {
            SchoolAndView container = new SchoolAndView();
            container.school = school;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }


        return view;
    }

    class SchoolAndView {
        public School school;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<SchoolAndView, Void, SchoolAndView>
    {
        @Override
        protected SchoolAndView doInBackground(SchoolAndView... params) {

            SchoolAndView container = params[0];
            School school = container.school;

            try {
                String imageUrl = school.getAvatarSmall();
                Bitmap bitmap;

                if(imageUrl != null && !imageUrl.equals("") && !imageUrl.contains("a.s.jpg")) {
                    InputStream in = (InputStream) new URL(imageUrl).getContent();
                    bitmap = BitmapFactory.decodeStream(in);
                    school.setBitmap(bitmap);
                    in.close();
                }
                else {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.school_ico_logo);
                }

                container.bitmap = bitmap;
                return container;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(SchoolAndView schoolAndView) {
            ImageView image = (ImageView) schoolAndView.view.findViewById(R.id.imageViewSchoolAvatar);
            image.setImageBitmap(schoolAndView.bitmap);
            schoolAndView.school.setBitmap(schoolAndView.bitmap);
        }
    }
}