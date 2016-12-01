package il.co.yomanim.mobileyomanim.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import core.gcm.GcmHelper;
import core.handlers.SessionManager;
import core.utils.LocaleUtils;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.adapters.NavDrawerAdapter;
import il.co.yomanim.mobileyomanim.fragments.HomeworkFragment;
import il.co.yomanim.mobileyomanim.fragments.JournalFragment;
import il.co.yomanim.mobileyomanim.fragments.LanguageFragment;
import il.co.yomanim.mobileyomanim.fragments.TimetableFragment;
import il.co.yomanim.mobileyomanim.items.DrawerItem;
import il.co.yomanim.mobileyomanim.params.BaseTaskParams;


public class MainActivity extends AppCompatActivity {

    //region Fields
    SessionManager session;

    private Toolbar mToolbar;
    public static MainActivity instance = null;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout Drawer;
    private RecyclerView.Adapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    private List<DrawerItem> navigationItemsList;

    private int schoolCount = 0;
    private int childCount = 0;

    public String HEADER_NAME = "User Name";
    public String HEADER_SCHOOL = "School name";
    public String HEADER_ROLE = "User";
    public int HEADER_IMAGE = R.drawable.no_ava;

    private final static int JOURNAL_FRAGMENT = 1;
    private final static int HOMEWORK_FRAGMENT = 2;
    private final static int TIMETABLE_FRAGMENT = 3;
    private int _currentFragment = 3;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        LocaleUtils.loadLocale(getBaseContext());
        setContentView(R.layout.activity_main);

        // Session class instance
        session = new SessionManager(getApplicationContext());


        schoolCount = session.getSchoolCount();
        childCount = session.getChildCount();


        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        navigationItemsList = new ArrayList<>();
        addItemsToNavigationList();


        // Setup Drawer
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);



        HEADER_NAME = session.getUserDetails().get(SessionManager.KEY_USER_NAME);
        HEADER_SCHOOL = session.getUserDetails().get(SessionManager.KEY_SCHOOL_NAME);
        HEADER_ROLE = MessageFormat.format("{0}: {1}",
                getResources().getString(R.string.choose_school_role),
                session.getUserDetails().get(SessionManager.KEY_ROLE));

        mAdapter =
                new NavDrawerAdapter(navigationItemsList,
                this,
                HEADER_NAME,
                HEADER_SCHOOL,
                HEADER_ROLE,
                HEADER_IMAGE);
        mRecyclerView.setAdapter(mAdapter);


        UserLoaderTask task = new UserLoaderTask();
        BaseTaskParams baseTaskParams = new BaseTaskParams(
                session.getUserDetails().get(SessionManager.KEY_TOKEN),
                Long.parseLong(session.getUserDetails().get(SessionManager.KEY_USER_ID)));
        task.execute(baseTaskParams);


        final GestureDetector mGestureDetector =
                new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)){
                    Drawer.closeDrawers();
                    int pos = 0;

                    try{
                        pos = recyclerView.getChildLayoutPosition(child);
                        if (pos > 0) {
                            //reset items bg
                            for (int i=1; i<mRecyclerView.getChildCount(); i++) {
                                if (i != pos) {
                                    mRecyclerView.getChildAt(i).findViewById(R.id.rowLinear).setBackgroundColor(Color.parseColor("#ffffff"));
                                }
                            }

                            //highlight selected menu item
                            mRecyclerView.getChildViewHolder(child).itemView.findViewById(R.id.rowLinear).setBackgroundColor(Color.parseColor("#f7f7f8"));

                            onTouchDrawer(pos);
                        }
                    } catch (Exception ex) {
                        Log.e("NawDrawer highlight", ex.getMessage());
                    }

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        Drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        onTouchDrawer(TIMETABLE_FRAGMENT);
        // END drawer
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (_currentFragment != 3) {
            openFragment(new TimetableFragment());
            setTitle(getResources().getString(R.string.timetable_title));
        }
    }


    private void openFragment(final Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left,
                        R.anim.slide_out_left)
                .replace(R.id.container, fragment)
                .commit();
    }

    private void openFragment(final FragmentActivity fragment){
//        getSupportFragmentManager()
//                .beginTransaction()
//                .setCustomAnimations(R.anim.slide_in_left,
//                        R.anim.slide_out_left)
//                .replace(R.id.container, fragment)
//                .commit();
    }


//    public void ReloadFragmentsWithLocale(final int position) {
//        LocaleUtils.loadLocale(getBaseContext());
//        onTouchDrawer(position);
//    }


    public void onTouchDrawer(final int position) {
        _currentFragment = position;
        switch (position) {
            case JOURNAL_FRAGMENT:
                openFragment(new JournalFragment());
                setTitle(getResources().getString(R.string.journal_title));
                break;
            case HOMEWORK_FRAGMENT:
                openFragment(new HomeworkFragment());
                setTitle(getResources().getString(R.string.homework_title));
                break;
            case TIMETABLE_FRAGMENT:
//                openFragment(new TestFragment());
                openFragment(new TimetableFragment());
                setTitle(getResources().getString(R.string.timetable_title));
                break;
            default:
                break;
        }

        if (position > 3) {
            if (childCount > 1 && schoolCount > 1) {
                switch (position) {
                    case 4:
                        Intent intent = new Intent(getApplicationContext(), ChildActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 5:
                        Intent intentS = new Intent(getApplicationContext(), SchoolActivity.class);
                        startActivity(intentS);
                        finish();
                        break;
                    case 6:
                        openFragment(new LanguageFragment());
                        setTitle(getResources().getString(R.string.language_title));
                        break;
                    case 7:
                        session.logoutUser();
                        break;
                    default:
                        break;
                }
            }
            else if (schoolCount > 1) {
                switch (position) {
                    case 4:
                        Intent intent = new Intent(getApplicationContext(), SchoolActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 5:
                        openFragment(new LanguageFragment());
                        setTitle(getResources().getString(R.string.language_title));
                        break;
                    case 6:
                        session.logoutUser();
                        break;
                    default: break;
                }
            }
            else if (childCount > 1) {
                switch (position) {
                    case 4:
                        Intent intent = new Intent(getApplicationContext(), ChildActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 5:
                        openFragment(new LanguageFragment());
                        setTitle(getResources().getString(R.string.language_title));
                        break;
                    case 6:
                        session.logoutUser();
                        break;
                    default: break;
                }
            }
            else {
                switch (position) {
                    case 4:
                        openFragment(new LanguageFragment());
                        setTitle(getResources().getString(R.string.language_title));
                        break;
                    case 5:
                        session.logoutUser();
                        break;
                    default: break;
                }
            }
        }
    }


    private void setTitle(String title) {
        TextView toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        toolbarTitle.setTypeface(myTypeface);
        toolbarTitle.setText(title);
    }


    private void addItemsToNavigationList() {
        navigationItemsList.add(new DrawerItem(getResources().getString(R.string.menu_journal), R.mipmap.m_ico_journal));
        navigationItemsList.add(new DrawerItem(getResources().getString(R.string.menu_homework), R.mipmap.m_ico_homework));
        navigationItemsList.add(new DrawerItem(getResources().getString(R.string.menu_timetable), R.mipmap.m_ico_timetable));
        if (childCount > 1) {
            navigationItemsList.add(new DrawerItem(getResources().getString(R.string.menu_child), R.mipmap.m_ico_child));
        }
        if (schoolCount > 1) {
            navigationItemsList.add(new DrawerItem(getResources().getString(R.string.menu_schools), R.mipmap.m_ico_school));
        }
        navigationItemsList.add(new DrawerItem(getResources().getString(R.string.menu_settings), R.mipmap.m_ico_settings));
        navigationItemsList.add(new DrawerItem(getResources().getString(R.string.menu_logout), R.mipmap.m_ico_logout));
    }


    public void hideToolbarFilters() {
        ImageButton btn = (ImageButton)findViewById(R.id.imageButtonToolbarGroup);
        RelativeLayout yourRelLay = (RelativeLayout) btn.getParent();
        yourRelLay.setVisibility(View.GONE);

        btn = (ImageButton)findViewById(R.id.imageButtonToolbarSubject);
        yourRelLay = (RelativeLayout) btn.getParent();
        yourRelLay.setVisibility(View.GONE);

        btn = (ImageButton)findViewById(R.id.imageButtonToolbarPeriod);
        yourRelLay = (RelativeLayout) btn.getParent();
        yourRelLay.setVisibility(View.GONE);

        btn = (ImageButton)findViewById(R.id.imageButtonToolbarWeek);
        yourRelLay = (RelativeLayout) btn.getParent();
        yourRelLay.setVisibility(View.GONE);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
    }


    //region UserLoader task -----------------------------------------
    private class UserLoaderTask extends AsyncTask<BaseTaskParams, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Bitmap doInBackground(BaseTaskParams... params) {

            //TODO: gcm test
            String gcmIdentifier = new GcmHelper().init(getApplicationContext());
            Log.e("Login GCM", String.valueOf(gcmIdentifier));

            String imagePath = session.getUserDetails().get(SessionManager.KEY_AVATAR);
            if (imagePath != null && !imagePath.contains("a.l.jpg")) {
                InputStream in = null;
                try {
                    in = (InputStream) new URL(imagePath).getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return BitmapFactory.decodeStream(in);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            if (result != null) {
                ImageView imageView = (ImageView) findViewById(R.id.circleView);
                if (imageView != null)
                    imageView.setImageBitmap(result);
            }
        }

    } // END: Group Loader Task
    //endregion
}