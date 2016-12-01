package il.co.yomanim.mobileyomanim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.List;
import core.handlers.SessionManager;
import core.model.SchoolMembership;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.adapters.SchoolAdapter;


public class SchoolActivity extends AppCompatActivity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        //Log out button
        ImageButton logout = (ImageButton) findViewById(R.id.imageButtonSchoolLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View args) {
                session.logoutUser();
            }
        });


        // Async task to get user schools
        List<SchoolMembership> list = session.getSchoolList();
        if (list != null) {
            // Set adapter and populate listView
            final SchoolAdapter adapter = new SchoolAdapter(getApplicationContext(),
                    R.layout.item_school_row,
                    list);
            ListView lv = (ListView) findViewById(R.id.listSchools);
            lv.setAdapter(adapter);

            //todo: Save person to session
            //            session.setPersonId(result.getPerson());

            // listView onclick event
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    SchoolMembership item = (SchoolMembership)parent.getAdapter().getItem(position);
                    long schoolId = item.getSchool().getId();

                    session.clearSchoolMethods();

                    //Save school to session
                    session.setSchoolId(schoolId);
                    session.setSchoolName(item.getSchool().getName());
                    session.setSchoolSystem(item.getSchool().isClassicSystem());

                    //todo: check, update role
                    if (item.getRoles() != null && item.getRoles().length > 0)
                        session.setRole(item.getRoles()[0]);
                    session.setChildPersonId(0);

                    //TODO: get rid from params personId here...
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("personId", session.getUserDetails().get(SessionManager.KEY_PROFILE_ID));
                    intent.putExtra("schoolId", schoolId);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_school, menu);
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
        try {
            long schoolId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_SCHOOL_ID));
            if (schoolId > 0) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
            else {
                session.logoutUser();
            }
        } catch (Exception ex) {
            Log.e("SchoolActivity", ex.getMessage());
        }
    }
}