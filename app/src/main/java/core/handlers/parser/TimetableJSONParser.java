package core.handlers.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import core.model.Timetable;
import core.model.TimetableItem;
import core.utils.DateUtils;


public class TimetableJSONParser {

    public static List<TimetableItem> parseTimetableItems(String content)
    {
        try {
            JSONArray arr = new JSONArray(content);
            JSONObject obj = arr.getJSONObject(0);

            if (!obj.isNull("Items")) {
                JSONArray subjArr = obj.getJSONArray("Items");
                List<TimetableItem> itemList = new ArrayList<>();
                for (int j = 0; j < subjArr.length(); j++) {
                    JSONObject wObj = subjArr.getJSONObject(j);
                    TimetableItem time = new TimetableItem();

                    time.setStart(DateUtils.deserializeWithTime(wObj.getString("Start")));
                    time.setFinish(DateUtils.deserializeWithTime(wObj.getString("Finish")));
                    time.setName(wObj.getString("Name"));
                    time.setType(wObj.getString("Type"));
                    time.setNumber(wObj.getInt("Number"));

                    itemList.add(time);
                }

                return itemList;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


    public static Timetable parseTimetable(String content)
    {
        //TODO: check
        try {
            Timetable timetable = new Timetable();
            JSONObject obj = new JSONObject(content);

            timetable.setName(obj.getString("Name"));
//            timetable.setStart(DateUtils.deserialize(obj.getString("start")));
//            timetable.setEnd(DateUtils.deserialize(obj.getString("end")));

            if (!obj.isNull("Items")) {
                JSONArray subjArr = obj.getJSONArray("Items");
                List<TimetableItem> itemList = new ArrayList<>();
                for (int j = 0; j < subjArr.length(); j++) {
                    JSONObject wObj = subjArr.getJSONObject(j);
                    TimetableItem time = new TimetableItem();

                    time.setStart(DateUtils.deserializeWithTime(wObj.getString("Start")));
                    time.setFinish(DateUtils.deserializeWithTime(wObj.getString("Finish")));
                    time.setName(wObj.getString("Name"));
                    time.setType(wObj.getString("Type"));

                    itemList.add(time);
                }

                timetable.setItems(itemList);
            }

            return timetable;
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}