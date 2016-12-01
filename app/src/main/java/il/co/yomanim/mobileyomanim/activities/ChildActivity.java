package il.co.yomanim.mobileyomanim.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import core.handlers.SessionManager;
import core.model.Person;
import il.co.yomanim.mobileyomanim.R;
import il.co.yomanim.mobileyomanim.adapters.ChildAdapter;


public class ChildActivity extends AppCompatActivity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        Typeface fontRobotoMedium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        TextView titleTextView = (TextView) findViewById(R.id.tvChooseChildTitle);
        titleTextView.setTypeface(fontRobotoMedium);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        //Log out button
        ImageButton logout = (ImageButton) findViewById(R.id.imageButtonChildrenLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View args) {
                session.logoutUser();
            }
        });

        //Load child list
        List<Person> list = session.getChildList();
        if (list != null) {
            final ChildAdapter adapter = new ChildAdapter(getApplicationContext(),
                    R.layout.item_school_row,
                    list);
            ListView lv = (ListView) findViewById(R.id.listChild);
            lv.setAdapter(adapter);

            // listView onclick event
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Person item = (Person)parent.getAdapter().getItem(position);
                    session.setChildPersonId(item.getId());

                    //todo: check, update role
                    session.setRole("EduParent");

                    //redirect
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child, menu);
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
            long childId = Long.parseLong(session.getUserDetails().get(SessionManager.KEY_CHILD_PROFILE_ID));
            if (childId > 0) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
            else {
                session.logoutUser();
            }
        } catch (Exception ex) {
            Log.e("ChildActivity", ex.getMessage());
        }
    }
}