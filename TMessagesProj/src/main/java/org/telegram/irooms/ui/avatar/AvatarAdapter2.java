/*
 * This is the source code of Telegram for Android v. 1.3.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.irooms.ui.avatar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.rooms.messenger.R;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AvatarAdapter2 extends RecyclerView.Adapter<AvatarAdapter2.AvatarViewHolder> {

    private ArrayList<TLRPC.User> userList = new ArrayList<>();
    private Context mContext;


    public AvatarAdapter2(Context context, ArrayList<TLRPC.User> users) {
        this.mContext = context;
        userList = users;
    }

    public TLRPC.User getItem(int position) {
        return userList.get(position);
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserCell2 avatarCell = new UserCell2(mContext, 0, 0, false);
//        return new AvatarViewHolder(avatarCell);
//        ImageView avatarCell = new ImageView(mContext);
        avatarCell.setLayoutParams(new LinearLayout.LayoutParams(62, 40));
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

    public class AvatarViewHolder extends RecyclerView.ViewHolder {
        UserCell2 userCell;

        //
        AvatarViewHolder(View itemView) {
            super(itemView);
            userCell = (UserCell2) itemView;
        }

        public void bind(TLRPC.User user) {
            userCell.setData(user, null, null, 0);
        }
    }
}
