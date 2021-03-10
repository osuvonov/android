package org.telegram.irooms.database;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "tbl_company")
public class Company {

    public Company() {
        id = 0;
        name = "";
        members=new ArrayList<>();
    }

    public Company(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "logo")
    private String logo;

    @ColumnInfo(name = "owner_id")
    private long owner_id;

    @ColumnInfo(name = "created_at")
    private String created_at;

    @TypeConverters(Converters.class)
    @ColumnInfo(name = "members")
    private List<Integer> members;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public long getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(int ownerId) {
        this.owner_id = ownerId;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String createdAt) {
        this.created_at = createdAt;
    }

    public long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(long owner_id) {
        this.owner_id = owner_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getMembers() {
        return members;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
    }
}
