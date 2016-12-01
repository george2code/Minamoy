package core.services;

import android.content.Context;
import android.util.Log;

import core.handlers.HttpManager;
import core.handlers.SessionManager;
import core.handlers.parser.AuthJSONParser;
import core.model.Authorization;
import core.model.LoginData;
import core.utils.StringUtils;
import il.co.yomanim.mobileyomanim.R;


public class AuthorizationService {

    private SessionManager _session;
    private Context _context;


    public AuthorizationService(SessionManager sessionManager, Context context) {
        _session = sessionManager;
        _context = context;
    }


    public boolean Login(String email, String password) {
        if (_session.isOnlineMode()) {
            // Online
            LoginData loginData = new LoginData();
            loginData.setUsername(email);
            loginData.setPassword(password);
            loginData.setClient_id(_context.getResources().getString(R.string.client_id));
            loginData.setClient_secret(_context.getResources().getString(R.string.client_secret));
            loginData.setScope(_context.getResources().getString(R.string.scope));

            String loginContent = HttpManager.login(_context, loginData);
            Log.e("AUTH", loginContent);
            if (loginContent != null) {
                Authorization user = AuthJSONParser.parseUser(loginContent);
                if (user != null) {
                    // Creating user login session
                    _session.createLoginSession(user.getPersonId(), user.getAccessToken());

                    _session.setPersonId(user.getPersonId());

                    _session.setUserName(StringUtils.removeExtraSpaces(user.getUserFullName()));

                    if (user.getPresences() != null) {
                        _session.updatePresenceStatuses(user.getPresences());
                    }
                    if (user.getWorkTypes() != null) {
                        _session.updateWorkTypes(user.getWorkTypes());
                    }

                    return true;
                }
            }
        }
//        else {
//            // Offline
//            List<Authorization> authList = Authorization.find(Authorization.class,
//                    "Select * from Authorization where email = ?", email);
//
//            if (authList.size() > 0) {
//                // Creating user login session
//                _session.createLoginSession(
//                        authList.get(0).getUserId(),
//                        authList.get(0).getAccessToken());
//                return true;
//            }
//        }

        return false;
    }


    public void Logout() {
        // Clear session & redirect
        _session.logoutUser();
    }
}