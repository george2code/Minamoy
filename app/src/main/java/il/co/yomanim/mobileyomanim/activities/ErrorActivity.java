package il.co.yomanim.mobileyomanim.activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import core.handlers.SessionManager;
import core.services.AuthorizationService;
import core.utils.FontUtils;
import il.co.yomanim.mobileyomanim.R;

public class ErrorActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Typeface fontMedium = FontUtils.getRobotoMedium(getAssets());

        //textViewErrorTitle
        TextView txt = (TextView) findViewById(R.id.textViewErrorTitle);
        txt.setTypeface(fontMedium);

        txt = (TextView) findViewById(R.id.textViewErrorTxt);
        txt.setTypeface(FontUtils.getRobotoLight(getAssets()));


        // Session class instance
        session = new SessionManager(getApplicationContext());

        Button btn = (Button) findViewById(R.id.buttonLogout);
        btn.setTypeface(fontMedium);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                session.logoutUser();
                AuthorizationService service = new AuthorizationService(session, getApplicationContext());
                service.Logout();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_error, menu);
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
        session.logoutUser();
    }
}