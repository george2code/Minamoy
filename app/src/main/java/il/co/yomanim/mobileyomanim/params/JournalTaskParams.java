package il.co.yomanim.mobileyomanim.params;

import android.content.Context;

public class JournalTaskParams {
    public Context context;
    public String token;
    public long groupId;
    public String from;
    public String to;

    public JournalTaskParams(Context context, String token, long groupId, String from, String to) {
        this.context = context;
        this.token = token;
        this.groupId = groupId;
        this.from = from;
        this.to = to;
    }
}