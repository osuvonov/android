package org.telegram.irooms.company;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.telegram.irooms.IRoomsManager;
import org.telegram.irooms.database.Company;
import org.telegram.messenger.LocaleController;
import org.rooms.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class CompanyMemberAdapter extends ListAdapter<TLRPC.User, CompanyMemberAdapter.CompanyMemberViewHolder> {

    private Context mContext;
    private Company company;
    private ArrayList<TLRPC.User> userList = new ArrayList<>();

    public interface OnRemoveUserListener {
        void removeUserFromCompany(int position, TLRPC.User user);
    }

    private OnRemoveUserListener userListener;

    public CompanyMemberAdapter(@NonNull DiffUtil.ItemCallback<TLRPC.User> diffCallback, Context context, Company company, List<TLRPC.User> users, OnRemoveUserListener ls) {
        super(diffCallback);
        this.mContext = context;
        this.company = company;
        this.userList = (ArrayList<TLRPC.User>) users;
        this.userListener = ls;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @NonNull
    @Override
    public CompanyMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.company_member_item, parent, false);
        return new CompanyMemberViewHolder(root, company);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyMemberViewHolder holder, int position) {

        TLRPC.User user = getItem(position);

        boolean[] isOnline = new boolean[1];

        isOnline[0] = false;
        String fullName = user.first_name != null ? user.first_name : "" + user.last_name != null ? user.last_name : "";
        holder.bind(fullName, LocaleController.formatUserStatus(UserConfig.selectedAccount, user, isOnline));
    }

    public void removeItem(int position) {
        userList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    protected TLRPC.User getItem(int position) {
        return userList.get(position);
    }

    class CompanyMemberViewHolder extends RecyclerView.ViewHolder {
        private final TextView memberName;
        private final TextView lastSeen;
        private final TextView remove;
        private Company company;

        private CompanyMemberViewHolder(View itemView, Company company) {
            super(itemView);
            this.company = company;
            int textColor = mContext.getResources().getColor(android.R.color.black);
            if (IRoomsManager.getInstance().isDarkMode(mContext)) {
                textColor =mContext.getResources().getColor(R.color.disabled_text_color);
            }
            memberName = itemView.findViewById(R.id.member_name);
            memberName.setTextColor(textColor);

            lastSeen = itemView.findViewById(R.id.last_seen);
            lastSeen.setTextColor(textColor);

            remove = itemView.findViewById(R.id.remove_member);
            remove.setText(LocaleController.getInstance().getRoomsString("delete"));
            remove.setTextColor(mContext.getResources().getColor(R.color.key_dialogRedIcon));
            if (company.getOwner_id() != UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                remove.setVisibility(View.GONE);
            }
        }

        public void bind(String text, String lastSeenText) {
            memberName.setText(text);
            if (lastSeenText.trim().toLowerCase().equals("online")) {
                lastSeen.setTextColor(mContext.getResources().getColor(R.color.key_chats_onlineCircle));
            }
            lastSeen.setText(lastSeenText);
            if (company.getOwner_id() == getItem(getAdapterPosition()).id) {
                remove.setTextColor(mContext.getResources().getColor(R.color.key_actionBarDefaultSelector));
                remove.setText(LocaleController.getInstance().getRoomsString("owner"));
            } else {
                remove.setOnClickListener(view -> {
                    if (userListener != null) {
                        userListener.removeUserFromCompany(getAdapterPosition(), getItem(getAdapterPosition()));
                    }
                });
            }
        }
    }

    public static class UserDiff extends DiffUtil.ItemCallback<TLRPC.User> {
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
