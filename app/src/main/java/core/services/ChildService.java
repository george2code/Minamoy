package core.services;

import android.content.Context;
import java.util.List;
import core.handlers.HttpManager;
import core.handlers.SessionManager;
import core.handlers.parser.AuthJSONParser;
import core.model.Person;

public class ChildService {

    private SessionManager _session;
    private Context _context;

    public ChildService(SessionManager sessionManager, Context context) {
        _session = sessionManager;
        _context = context;
    }

//    public List<Person> getUserChildList(String token, long personId) {
//
//        if (_session.isOnlineMode()) {
//            // Online
//            String contentChild = HttpManager.getUserChildList(_context, token, personId);
//            if (contentChild != null) {
//                List<Person> childList = AuthJSONParser.parseUserChilds(contentChild);
//
//                // Update db
//                Person.deleteAll(Person.class,
//                        "Select * from Authorization where parentPersonId = ?", String.valueOf(personId));
//                if (childList != null && childList.size() > 0) {
//                    for (Person child : childList) {
//                        Person person = new Person();
//                        person.setId(child.getId());
//                        person.setFirstName(child.getFirstName());
//                        person.setMiddleName(child.getMiddleName());
//                        person.setLastName(child.getLastName());
//                        person.setSex(child.getSex());
//                        person.setParentPersonId(personId);
//                        person.save();
//                    }
//                }
//
//                // return result
//                return childList;
//            }
//        } else {
//            // Offline
//            return Person.find(Person.class, "Select * from Authorization where parentPersonId = ?", String.valueOf(personId));
//        }
//
//        return null;
//    }
}