package org.telegram.irooms.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

// Task status localCreated=1,localUpdated=2, online=3
@Entity(tableName = "tbl_tasks")
public class Task implements Parcelable {
    public Task(long id, long company_id) {
        this.id = id;
        this.platform = "android";
        this.message_id = -1;
        this.company_id = company_id;
        this.local_id = "";
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    protected long pId;

    @NonNull
    private long id;

    @ColumnInfo(name = "status_code")
    private int status_code;

    @ColumnInfo(name = "company_id")
    private long company_id;

    @ColumnInfo(name = "chat_id")
    private long chat_id;

    @ColumnInfo(name = "message_id")
    private long message_id;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "created_at")
    private String created_at;

    @ColumnInfo(name = "updated_at")
    private String updated_at;

    @ColumnInfo(name = "completed_at")
    private String completed_at;

    @ColumnInfo(name = "expires_at")
    private String expires_at;

    @ColumnInfo(name = "creator_id")
    private long creator_id;

    @ColumnInfo(name = "last_updated_by")
    private long lastUpdater;

    @ColumnInfo(name = "tag")
    private String tag;

    @ColumnInfo(name = "logo")
    private String logo;

    @ColumnInfo(name = "local_status")
    private int localStatus;

    @ColumnInfo(name = "local_id")
    private String local_id;

    @TypeConverters(Converters.class)
    @ColumnInfo(name = "members")
    private List<Integer> members;

    @TypeConverters(Converters.class)
    @ColumnInfo(name = "receivers")
    private List<Integer> receivers;

    @TypeConverters(Converters.class)
    @ColumnInfo(name = "reminders")
    private List<String> reminders;

    @ColumnInfo(name = "chat_type")
    private String chat_type;

    @ColumnInfo(name = "comments_count")
    private int comments_count;

    protected Task(Parcel in) {
        pId = in.readLong();
        id = in.readLong();
        status_code = in.readInt();
        company_id = in.readLong();
        chat_id = in.readLong();
        message_id = in.readLong();
        description = in.readString();
        status = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
        completed_at = in.readString();
        expires_at = in.readString();
        creator_id = in.readLong();
        lastUpdater = in.readLong();
        tag = in.readString();
        logo = in.readString();
        localStatus = in.readInt();
        local_id = in.readString();
        chat_type = in.readString();
        last_read_message_id = in.readLong();
        platform = in.readString();
        comments_count = in.readInt();
        in.readList(reminders, String.class.getClassLoader());
        in.readList(receivers, Integer.class.getClassLoader());
        in.readList(members, Integer.class.getClassLoader());
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public long getLast_read_message_id() {
        return last_read_message_id;
    }

    public void setLast_read_message_id(long last_read_message_id) {
        this.last_read_message_id = last_read_message_id;
    }

    @ColumnInfo(name = "last_read_message_id")
    private long last_read_message_id;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @ColumnInfo(name = "platform")
    private String platform;

    public List<String> getReminders() {
        return reminders;
    }

    public void setReminders(List<String> reminders) {
        this.reminders = reminders;
    }

    public String getChat_type() {
        return chat_type;
    }

    public void setChat_type(String chat_type) {
        this.chat_type = chat_type;
    }

    public List<Integer> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<Integer> receivers) {
        this.receivers = receivers;
    }

    public int getLocalStatus() {
        return localStatus;
    }

    public String getLocal_id() {
        return local_id;
    }

    public void setLocal_id(String local_id) {
        this.local_id = local_id;
    }

    public void setLocalStatus(int localStatus) {
        this.localStatus = localStatus;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(long company_id) {
        this.company_id = company_id;
    }

    public long getChat_id() {
        return chat_id;
    }

    public void setChat_id(long chat_id) {
        this.chat_id = chat_id;
    }

    public long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(long message_id) {
        this.message_id = message_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }

    public long getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(long creator_id) {
        this.creator_id = creator_id;
    }

    public long getChatId() {
        return chat_id;
    }

    public void setChatId(long chatId) {
        this.chat_id = chatId;
    }

    public long getMessageId() {
        return message_id;
    }

    public void setMessageId(long messageId) {
        this.message_id = messageId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String createdAt) {
        this.created_at = createdAt;
    }

    public String getExpiresAt() {
        return expires_at;
    }

    public void setExpiresAt(String expiresAt) {
        this.expires_at = expiresAt;
    }

    public long getCreatorId() {
        return creator_id;
    }

    public void setCreatorId(long creatorId) {
        this.creator_id = creatorId;
    }

    public long getCompanyId() {
        return company_id;
    }

    public void setCompanyId(long companyId) {
        this.company_id = companyId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Integer> getMembers() {
        return members;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updated_at = updatedAt;
    }

    public String getCompletedAt() {
        return completed_at;
    }

    public void setCompletedAt(String completedAt) {
        this.completed_at = completedAt;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
    }

    public long getLastUpdater() {
        return lastUpdater;
    }

    public void setLastUpdater(long lastUpdater) {
        this.lastUpdater = lastUpdater;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(pId);
        dest.writeLong(id);
        dest.writeInt(status_code);
        dest.writeLong(company_id);
        dest.writeLong(chat_id);
        dest.writeLong(message_id);
        dest.writeString(description);
        dest.writeString(status);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeString(completed_at);
        dest.writeString(expires_at);
        dest.writeLong(creator_id);
        dest.writeLong(lastUpdater);
        dest.writeString(tag);
        dest.writeString(logo);
        dest.writeInt(localStatus);
        dest.writeString(local_id);
        dest.writeString(chat_type);
        dest.writeLong(last_read_message_id);
        dest.writeString(platform);
        dest.writeInt(comments_count);
        dest.writeList(receivers);
        dest.writeList(reminders);
        dest.writeList(members);
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }
}
