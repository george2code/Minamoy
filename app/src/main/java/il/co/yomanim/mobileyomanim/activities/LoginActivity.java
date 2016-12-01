package il.co.yomanim.mobileyomanim.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import core.handlers.SessionManager;
import core.helpers.ConstantsHelper;
import core.services.AuthorizationService;
import core.utils.LocaleUtils;
import il.co.yomanim.mobileyomanim.R;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>
{
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Session Manager Class
    SessionManager session;

    // UI references.
    private AutoCompleteTextView mLoginView;
    private EditText mPasswordView;
    private TextView mErrorView;

    private ProgressBar progressBar;
    private CheckBox checkBoxRemember;

//    private View mProgressView;
//    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.loadLocale(getBaseContext());
        setContentView(R.layout.activity_login);


        // Session Manager
        session = new SessionManager(getApplicationContext());


        progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
        progressBar.setVisibility(View.INVISIBLE);

        checkBoxRemember = (CheckBox) findViewById(R.id.checkBoxRemember);


        TextView loginTitle = (TextView) findViewById(R.id.tvLoginTitle);
        String title = getResources().getString(R.string.signin_title);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            loginTitle.setText(MessageFormat.format("{0} {1}", title, version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        // Set up the login form.
        mLoginView = (AutoCompleteTextView) findViewById(R.id.login);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConstantsHelper.PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("saveInfo", false)) {
            checkBoxRemember.setChecked(true);
            mLoginView.setText(sharedPreferences.getString("login", ""));
            mPasswordView.setText(sharedPreferences.getString("password", ""));
        }



        mErrorView = (TextView) findViewById(R.id.textViewLoginError);

        final Button mEmailSignInButton = (Button) findViewById(R.id.btnEnter);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });



        //LOST FOCUS CASE
        mLoginView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // code to execute when EditText loses focus
                    hideSoftKeyboard(v);
                }
            }
        });
        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // code to execute when EditText loses focus
                    hideSoftKeyboard(v);
                }
            }
        });

        //KEYBOARD ENTER KEY
        mLoginView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_CALL) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    hideSoftKeyboard(v);
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_CALL) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    hideSoftKeyboard(v);

                    mEmailSignInButton.performClick();
                    mEmailSignInButton.setPressed(true);
                    mEmailSignInButton.invalidate();


                    return true;
                }
                return false;
            }
        });
    }



    public void hideSoftKeyboard(View view) {
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception ex) {
            Log.e("Login", ex.getMessage());
        }
    }



    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);
        mErrorView.setVisibility(View.INVISIBLE);

        // Store values at the time of the login attempt.
        String email = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            SetErrorMessage(getString(R.string.error_invalid_password));
//            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            SetErrorMessage(getString(R.string.error_field_required));
//            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            SetErrorMessage(getString(R.string.error_invalid_email));
//            mLoginView.setError(getString(R.string.error_invalid_email));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
            SetErrorMessage("Loging in, please wait...");
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
//        return email.contains("@");
        return email.length() > 4;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mLoginView.setAdapter(adapter);
    }


    public void SetErrorMessage(String message) {
        mErrorView.setText(message);
        mErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try
            {


                AuthorizationService service = new AuthorizationService(session, getApplicationContext());
                return service.Login(mEmail, mPassword);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            progressBar.setVisibility(View.INVISIBLE);
            mErrorView.setVisibility(View.INVISIBLE);


            if (success) {

                //save info
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConstantsHelper.PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (checkBoxRemember.isChecked()) {
                    editor.putBoolean("saveInfo", true);
                    editor.putString("login", mEmail);
                    editor.putString("password", mPassword);
                } else {
                    editor.putBoolean("saveInfo", false);
                    editor.remove("login");
                    editor.remove("password");
                }
                editor.apply();


                // Staring SplashActivity
                Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(i);
                finish();
            }
            else {
                SetErrorMessage(getString(R.string.error_incorrect_password));
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progressBar.setVisibility(View.INVISIBLE);
            mErrorView.setVisibility(View.INVISIBLE);
        }
    }
}