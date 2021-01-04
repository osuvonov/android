package org.telegram.irooms.ui.spinner;

import org.telegram.tgnet.TLRPC;

import androidx.annotation.NonNull;

public class MemberState {
    private TLRPC.User user;
    private boolean selected;

    public MemberState() {
    }

    public MemberState(TLRPC.User user) {
        this.user = user;
    }

    public TLRPC.User getUser() {
        return user;
    }

    public void setUser(TLRPC.User user) {
        this.user = user;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public String toString() {
        String userName = ((user.first_name == null ? "" : user.first_name) + " " + (user.last_name == null ? "" : user.last_name));

        return userName;
    }
}
