
package org.telegram.irooms.ui.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.rooms.messenger.R;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserSpinnerAdapter extends ArrayAdapter<MemberState> {
    private Context mContext;
    private ArrayList<MemberState> itemList = new ArrayList<>();
     private boolean isFromView;

    public UserSpinnerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.mContext = context;
    }

    public UserSpinnerAdapter(@NonNull Context context, int resource, int textviewId, List<MemberState> objects) {
        super(context, resource, textviewId, objects);
        this.mContext = context;
         this.itemList.addAll((ArrayList<MemberState>) objects);
     }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = convertView.findViewById(R.id.spinner_text);
            holder.mCheckBox = convertView.findViewById(R.id.spinner_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TLRPC.User user = itemList.get(position+1).getUser();
        String userName = "";
        if (user != null) {
            userName = ((user.first_name == null ? "" : user.first_name) + " " + (user.last_name == null ? "" : user.last_name));
        }
        holder.mTextView.setText(userName);

        isFromView = true;
        holder.mCheckBox.setChecked(itemList.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            // holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);
//        holder.mCheckBox.setOnClickListener(view -> {
//            int getPosition = (Integer) holder.mCheckBox.getTag();
//            itemList.get(getPosition).setSelected(holder.mCheckBox.isSelected());
//        });

        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}
