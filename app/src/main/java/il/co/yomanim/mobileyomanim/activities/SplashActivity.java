package il.co.yomanim.mobileyomanim.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import java.util.ArrayList;
import java.util.List;
import core.handlers.HttpManager;
import core.handlers.SessionManager;
import core.handlers.parser.AuthJSONParser;
import core.model.Person;
import core.model.School;
import core.model.SchoolMembership;
import core.model.User;
import il.co.yomanim.mobileyomanim.R;


public class SplashActivity extends AppCompatActivity {

    // Session Manager Class
    SessionManager session;
    List<LoaderTask> tasks;
    ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Session Manager
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        if (!session.isLoggedIn()) {
            setContentView(R.layout.activity_login);
            finish();
        } else {
            pb = (ProgressBar) findViewById(R.id.progressBarSplash);
            pb.setVisibility(View.INVISIBLE);

            tasks = new ArrayList<>();
            LoaderTask task = new LoaderTask();
            task.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_splash, menu);
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
        // do nothing.
    }


    private class LoaderTask extends AsyncTask<Void, Void, Intent> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected Intent doInBackground(Void... params) {

            String token = session.getUserDetails().get(SessionManager.KEY_TOKEN);
            String userProfileData = HttpManager.getUserSchoolMemberships(getApplicationContext(), token);

            Log.e("USER_PROFILE_DATA", userProfileData);

            if (userProfileData != null) {
                User userProfile = AuthJSONParser.parseProfileInfo(userProfileData, true);
                if (userProfile != null) {

                    //try to get user ava
                    String userMeContent = HttpManager.getUsersMe(getApplicationContext(), token);
                    if (userMeContent != null) {
                        User userMe = AuthJSONParser.parserUsersMe(userMeContent);
                        if (userMe != null && !TextUtils.isEmpty(userMe.getAvatar())) {
                            session.setAvatar(userMe.getAvatar());
                        }
                    }

                    if (userProfile.getSchoolMemberships() != null && userProfile.getSchoolMemberships().size() > 0) {
                        //save to the model
                        session.updateSchools(userProfile.getSchoolMemberships());

                        if(userProfile.getSchoolMemberships().size() == 1) {
                            //try to get user childs
                            String contentChildren = HttpManager.getUserChildList(getApplicationContext(),
                                    token, userProfile.getPerson());
                            if (contentChildren != null) {
                                List<Person> listChild = AuthJSONParser.parseUserChilds(contentChildren, getApplicationContext());
                                if (listChild != null && listChild.size() > 0) {
                                    session.updateChild(listChild);
                                    //todo: if not teacher, try to load child
                                    if (!isTeacherRole(userProfile.getSchoolMemberships().get(0).getRoles())) {
                                        if(listChild.size() > 1) {
                                            School studentSchool = userProfile.getSchoolMemberships().get(0).getSchool();
                                            session.setSchoolId(studentSchool.getId());
                                            session.setSchoolName(studentSchool.getName());
                                            //load child list
                                            return new Intent(getApplicationContext(), ChildActivity.class);
                                        }
                                        else {
                                            // load child and school
                                            session.setChildPersonId(listChild.get(0).getId());
                                            //get schools, if schools more than 1, load MainActivity
                                            return RenderSchoolActivity(userProfile.getSchoolMemberships());
                                        }
                                    } // parent or student
                                }
                                else {
                                    //try to get child id
                                    if (!isTeacherRole(userProfile.getSchoolMemberships().get(0).getRoles())) {
                                        session.setChildPersonId(userProfile.getPerson());
                                    }
                                }
                            }

                            return RenderSchoolActivity(userProfile.getSchoolMemberships());
                        }
                        else {
                            //todo: load schools
                            //get schools, if schools more than 1, load MainActivity
                            return RenderSchoolActivity(userProfile.getSchoolMemberships());
                        }
                    }
                }
            }

            //redirect to error page with message
            return RenderErrorActivity("There is no schools for this user! EduMembership error.");
        }


        private Intent RenderSchoolActivity(List<SchoolMembership> memberships) {
            if (memberships != null && memberships.size() > 0) {
                Intent intent;
                if (memberships.size() > 1) {
                    //render school list to choose one
                    intent = new Intent(getApplicationContext(), SchoolActivity.class);
                } else {
                    //get first school id
                    session.setSchoolId(memberships.get(0).getSchool().getId());
                    session.setSchoolName(memberships.get(0).getSchool().getName());
                    session.setSchoolSystem(memberships.get(0).getSchool().isClassicSystem());

                    session.setRole(memberships.get(0).getRoles()[0]);

                    // render main
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }
                return intent;
            } else {
                //redirect to error page with message
                return RenderErrorActivity("There is no schools for this user! EduMembership error.");
            }
        }

        private Intent RenderErrorActivity(String message) {
            Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
            intent.putExtra("message", message);
            return intent;
        }


        @Override
        protected void onPostExecute(final Intent intent) {
            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            startActivity(intent);
            finish();
        }
    }


    private boolean isTeacherRole(String[] roles) {
        if(roles != null && roles.length >0) {
            for (String role : roles) {
                if (role.equals("Teacher")) {
                    return true;
                }
            }
        }
        return false;
    }
}