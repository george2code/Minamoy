package il.co.yomanim.mobileyomanim.items;

import core.model.journal.Mark;
import core.model.journal.Presence;

public class JournalItem {
    private Mark mark;
    private Presence presence;

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }
}