package core.model.journal;

import android.graphics.Color;

public class JournalFinalMark {
    private long person;
    private long subject;
    private String sabjectname;
    private Double avgmark;
    private Integer mark;
    private long markid;
    private String marktype;
    private long commentid;
    private String comment;

    public long getPerson() {
        return person;
    }

    public void setPerson(long person) {
        this.person = person;
    }

    public long getSubject() {
        return subject;
    }

    public String getSabjectname() {
        return sabjectname;
    }

    public void setSabjectname(String sabjectname) {
        this.sabjectname = sabjectname;
    }

    public void setSubject(long subject) {
        this.subject = subject;
    }

    public Double getAvgmark() {
        return avgmark;
    }

    public void setAvgmark(Double avgmark) {
        this.avgmark = avgmark;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public long getMarkid() {
        return markid;
    }

    public void setMarkid(long markid) {
        this.markid = markid;
    }

    public String getMarktype() {
        return marktype;
    }

    public void setMarktype(String marktype) {
        this.marktype = marktype;
    }

    public long getCommentid() {
        return commentid;
    }

    public void setCommentid(long commentid) {
        this.commentid = commentid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getMarkBackground()
    {
        int markValue = Integer.valueOf(this.mark);

        if (markValue > 85) {
            return Color.parseColor("#a9bd15");
        }
        else if (markValue <= 85 && markValue > 60) {
            return Color.parseColor("#f3931a");
        }
        else if (markValue <= 60) {
            return Color.parseColor("#ff6666");
        }
        else {
            return Color.parseColor("#000000");
        }
    }
}