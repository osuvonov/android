package org.telegram.irooms.models;

import android.text.Spannable;
import android.text.StaticLayout;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_task_message")
public class TaskMessage {
    //----non database fields--------------
    @Ignore
    public int type;
    @Ignore
    public ArrayList<TextLayoutBlock> textLayoutBlocks = new ArrayList<>();
    @Ignore
    public CharSequence messageText;
    @Ignore
    public int textWidth;
    @Ignore
    public int textHeight;
    @Ignore
    public int textXOffset;
    @Ignore
    public int eventId;
    @Ignore
    public Spannable linkDescription;
    @Ignore
    public int gifState;
    @Ignore
    public boolean scheduled;
    @Ignore
    public int audioPlayerDuration;
    @Ignore
    public float audioProgress;
    //----database fields--------------
    @PrimaryKey
    private long id;
    @ColumnInfo(name = "from_id")
    private int from_id;
    @ColumnInfo(name = "task_id")
    private long task_id;
    @ColumnInfo(name = "reply_to")
    private long reply_to;     // maybe null
    @ColumnInfo(name = "date")
    private String date;        // iso8601
    @ColumnInfo(name = "edit_date")
    private String edit_date;   // iso8601
    @ColumnInfo(name = "text")
    private String text;

    public boolean isHasTask() {
        return hasTask;
    }

    public void setHasTask(boolean hasTask) {
        this.hasTask = hasTask;
    }

    @Ignore
    private boolean hasTask;

    public boolean isDate() {
        return isDate;
    }

    public void setIsDate(boolean date) {
        isDate = date;
    }

    @Ignore
    private boolean isDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public long getReply_to() {
        return reply_to;
    }

    public void setReply_to(long reply_to) {
        this.reply_to = reply_to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEdit_date() {
        return edit_date;
    }

    public void setEdit_date(String edit_date) {
        this.edit_date = edit_date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMusic() {
        return false;
    }

    public boolean isVoice() {
        return false;
    }

    public int getDuration() {
        return 0;
    }

    public static class TextLayoutBlock {
        public StaticLayout textLayout;
        public float textYOffset;
        public int charactersOffset;
        public int charactersEnd;
        public int height;
        public int heightByOffset;
        public byte directionFlags;

        public boolean isRtl() {
            return (directionFlags & 1) != 0 && (directionFlags & 2) == 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        TaskMessage anotherTaskMessage = (TaskMessage) obj;
        return this.id == anotherTaskMessage.getId();
    }
}