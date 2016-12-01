package il.co.yomanim.mobileyomanim.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import core.handlers.SessionManager;
import core.model.Person;
import de.hdodenhof.circleimageview.CircleImageView;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.items.DrawerItem;


public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    //Declare variable to identify which view that is being inflated
    //The options are either the Navigation Drawer HeaderView or the list items in the Navigation drawer
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    // String Array to store the passed titles Value from MainActivity.java
    private String mNavTitles[];

    // Int Array to store the passed icons resource value from MainActivity.java
    private int mIcons[];

    //String Resource for header View Name
    private String name;

    //int Resource for header view profile picture
    private int profile;

    //String for the email displayed in the Navigation header
    private String schoolName;

    private String role;

    private Context mContext;

    Typeface fontRobotoMedium;
    Typeface fontRobotoLight;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_row,parent,false); //Inflating the layout
            return new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view
            //inflate your layout and pass it to view holder
        }
        else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header,parent,false); //Inflating the layout
            return new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view
        }
        return null;
    }


    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(NavDrawerAdapter.ViewHolder holder, int position) {
        // as the list view is going to be called after the header view so we decrement the
        if(holder.Holderid == 1) {
            // position by 1 and pass it to the holder while setting the text and image
            holder.textView.setTypeface(fontRobotoMedium);
            holder.textView.setText(mNavTitles[position - 1]);      // Setting the Text with the array of our Titles

            //initial
            if(position == 3) {
                holder.linearLayout.setBackgroundColor(Color.parseColor("#f7f7f8"));
            }

            // image view
            holder.imageView.setImageResource(mIcons[position -1]); // Settimg the image with array of our icons
        }
        else {
            //set fonts
            holder._name.setTypeface(fontRobotoMedium);
            holder._schoolName.setTypeface(fontRobotoMedium);
            holder._role.setTypeface(fontRobotoLight);

            //set values
            holder.profile.setImageResource(profile);           // Similarly we set the resources for header view
            holder._name.setText(name);
            holder._schoolName.setText(schoolName);
            holder._role.setText(role);
        }
    }


    @Override
    public int getItemCount() {
        return mNavTitles.length + 1;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        int Holderid;

        LinearLayout linearLayout;
        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView _name;
        TextView _schoolName;
        TextView _role;

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public void setLinearLayout(LinearLayout linearLayout) {
            this.linearLayout = linearLayout;
        }

        public ViewHolder(View itemView,int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);


            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
            if(ViewType == TYPE_ITEM) {
                linearLayout = (LinearLayout) itemView.findViewById(R.id.rowLinear);
                textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from nav_bar_rowrow.xml
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from nav_bar_row.xmlxml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            }
            else {
                _name = (TextView) itemView.findViewById(R.id.headerName);         // Creating Text View object from header.xml for name
                _schoolName = (TextView) itemView.findViewById(R.id.headerSchool);       // Creating Text View object from header.xml for email
                _role = (TextView) itemView.findViewById(R.id.headerRole);
                profile = (ImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view

                renderChild();
            }
        }


        private void renderChild() {

            List<Person> list = new SessionManager(itemView.getContext()).getChildList();
            if (list != null) {

                LinearLayout rootView = (LinearLayout)itemView.findViewById(R.id.childLayout);
                Typeface fontRobotoMedium = Typeface.createFromAsset(
                        itemView.getContext().getAssets(), "Roboto-Medium.ttf");

                SessionManager session = new SessionManager(itemView.getContext());


                for(Person child : list) {
                    View v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.row_child, (ViewGroup) itemView, false);

                    int textColor = Color.parseColor("#ffffff");
                    if (session.getChildCount() > 0 && !Objects.equals(child.getId(), Long.valueOf(session.getUserDetails().get(SessionManager.KEY_CHILD_PROFILE_ID)))) {
                        textColor = Color.parseColor("#B6B6B6");
                    }
                    //name
                    TextView tv = (TextView) v.findViewById(R.id.txtName);
                    tv.setTypeface(fontRobotoMedium);
                    tv.setText(MessageFormat.format("{0} {1}", child.getFirstName(), child.getLastName()));
                    tv.setTextColor(textColor);
                    // Person class name
                    tv = (TextView) v.findViewById(R.id.txtSchool);
                    tv.setTypeface(fontRobotoMedium);
                    if (child.getClassName() != null) {
                        tv.setText(MessageFormat.format("{0}", String.valueOf(child.getClassName())));
                        tv.setTextColor(textColor);
                    }
                    //Display flower photo in ImageView widget
                    CircleImageView image = (CircleImageView) v.findViewById(R.id.circleView);
                    image.setImageURI(null);
                    image.setImageURI(Uri.parse(child.getAvatarUrl()));
                    //return
                    rootView.addView(v);
                }
            }
        }
    }


    /**
     * With this method we determine what type of view being passed.
     */
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    // MyAdapter Constructor with titles and icons parameter
    public NavDrawerAdapter(List<DrawerItem> dataList, Context context,
                            String Name, String SchoolName, String Role, int Profile)
    {
        // titles, icons, name, email, profile pic are passed from the main activity as we
        mNavTitles = new String[dataList.size()];
        mIcons = new int[dataList.size()];

        for (int i = 0; i < dataList.size(); i++){
            mNavTitles[i] = dataList.get(i).getItemName();
            mIcons[i] = dataList.get(i).getImgResId();
        }

        mContext = context;
        name = Name;
        schoolName = SchoolName;
        role = Role;
        profile = Profile;                     //here we assign those passed values to the values we declared here
        //in adapter

        fontRobotoMedium = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Medium.ttf");
        fontRobotoLight = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
    }
}