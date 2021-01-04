/*
 * This is the source code of Telegram for Android v. 1.3.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.irooms.task;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.irooms.ui.avatar.UserCell;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class AvatarAdapter extends ListAdapter<TLRPC.User, AvatarAdapter.AvatarViewHolder> {

     private ArrayList<TLRPC.User> userList = new ArrayList<>();
    private Context mContext;


    public AvatarAdapter(DiffUtil.ItemCallback<TLRPC.User> callback, Context context, ArrayList<TLRPC.User> users) {
        super(callback);
        this.mContext = context;
        userList = users;
    }

    public TLRPC.User getItem(int position) {
        return  userList.get(position);
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserCell avatarCell = new UserCell(mContext,2,0,false);
        return new AvatarViewHolder(avatarCell);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class AvatarViewHolder extends RecyclerView.ViewHolder {

//        private final AvatarCell avatarCell;
        private UserCell avatarCell;


        private AvatarViewHolder(View itemView) {
            super(itemView);
            avatarCell = (UserCell) itemView;
        }

        public void bind(TLRPC.User user) {
            avatarCell.setData(user, null, null, 0);
        }
    }

    public static class AvatarDiff extends DiffUtil.ItemCallback<TLRPC.User> {

        @Override
        public boolean areItemsTheSame(@NonNull TLRPC.User oldItem, @NonNull TLRPC.User newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TLRPC.User oldItem, @NonNull TLRPC.User newItem) {
            return oldItem.username.equals(newItem.username);
        }
    }

}
