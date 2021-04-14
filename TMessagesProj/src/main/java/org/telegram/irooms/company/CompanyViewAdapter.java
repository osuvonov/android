package org.telegram.irooms.company;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.core.widget.CompoundButtonCompat;
import androidx.preference.PreferenceManager;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.irooms.Constants;
import org.telegram.irooms.IRoomsManager;
import org.telegram.irooms.database.Company;
import org.rooms.messenger.R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class CompanyViewAdapter extends ListAdapter<Company, CompanyViewAdapter.CompanyViewHolder> {

    public interface CompanySelecterListener {
        void onClick(Company company);

        void onSelect(Company company);
    }

    private CompanySelecterListener listener;

    private Context mContext;
    private int lastCheckedPosition;

    public CompanyViewAdapter(@NonNull DiffUtil.ItemCallback<Company> diffCallback, Context context, CompanySelecterListener ls) {
        super(diffCallback);
        this.mContext = context;
        listener = ls;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new CompanyViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {

        int selectedCompanyId = IRoomsManager.getInstance().getSelectedCompanyId(mContext);

        Company company = getItem(position);

        if (company.getId() == selectedCompanyId) {
            lastCheckedPosition = holder.getAdapterPosition();
        }

        holder.bind(company.getName(), company.getId() == selectedCompanyId, company.getMembers() == null ? 0 : company.getMembers().size());

        setFadeAnimation(holder.itemView);

    }

    @Override
    public void submitList(@Nullable List<Company> list) {
        ArrayList<Company> localList = new ArrayList<>(list);
        if (localList != null)
            localList.add(new Company());
        super.submitList(localList);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(900);
        view.startAnimation(anim);
    }

    class CompanyViewHolder extends RecyclerView.ViewHolder {
        private final TextView companyNameTextView;
        private final CheckBox companySelectedCheckBox;
        private final TextView companyMembersCount;

        private final LinearLayout nameMemberParent;

        private CompanyViewHolder(View itemView) {
            super(itemView);
            int textColor = mContext.getResources().getColor(android.R.color.black);
            if (IRoomsManager.getInstance().isDarkMode(mContext)) {
                textColor = mContext.getResources().getColor(R.color.disabled_text_color);
            }
            nameMemberParent = itemView.findViewById(R.id.name_member_parent);
            nameMemberParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(getItem(getAdapterPosition()));
                }
            });

            companyNameTextView = itemView.findViewById(R.id.textView);
            companyNameTextView.setTextColor(textColor);

            companySelectedCheckBox = itemView.findViewById(R.id.company_checkbox);
            ColorFilter colorFilter = new PorterDuffColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
            Drawable drawable = CompoundButtonCompat.getButtonDrawable(companySelectedCheckBox);
            if (drawable != null) {
                drawable.setColorFilter(colorFilter);
            }
            companySelectedCheckBox.setOnClickListener((compoundButton) -> {
                try {
                    if (getAdapterPosition() == lastCheckedPosition && !companySelectedCheckBox.isChecked()) {
                        companySelectedCheckBox.setChecked(true);
                        return;
                    }
                    int copy = lastCheckedPosition;
                    lastCheckedPosition = getAdapterPosition();
                    IRoomsManager.getInstance().setSelectedCompany(mContext, getItem(getAdapterPosition()), getItem(getAdapterPosition()).getOwner_id() == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId);
                    listener.onSelect(getItem(getAdapterPosition()));
                    notifyItemChanged(copy);
                    notifyItemChanged(lastCheckedPosition);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            });
            companyMembersCount = itemView.findViewById(R.id.company_members);
            companyMembersCount.setTextColor(textColor);
        }

        public void bind(String companyName, boolean selected, int members) {
            if ("".equals(companyName)) {
                companyName = LocaleController.getInstance().getRoomsString("no_team");

                companyNameTextView.setText(companyName);
                companySelectedCheckBox.setChecked(selected);
                companyMembersCount.setText("");
                return;
            }
            companyNameTextView.setText(companyName);
            companySelectedCheckBox.setChecked(selected);
            companyMembersCount.setText(members + LocaleController.getInstance().getRoomsString("participants"));
        }
    }

    public static class CompanyDiff extends DiffUtil.ItemCallback<Company> {

        @Override
        public boolean areItemsTheSame(@NonNull Company oldItem, @NonNull Company newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Company oldItem, @NonNull Company newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    }
}
