package org.telegram.irooms.ui.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.rooms.messenger.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerAdapter extends ArrayAdapter<State> {
    private Context mContext;
    private ArrayList<State> itemList;
    private boolean isFromView;

    public SpinnerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.mContext = context;
    }


    public SpinnerAdapter(@NonNull Context context, int resource, int tvid, ArrayList<State> objects) {
        super(context, resource, tvid, objects);
        this.mContext = context;
        this.itemList = objects;
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(itemList.get(position).getName());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
//        holder.mCheckBox.setChecked(itemList.get(position).isSelected());
//        isFromView = false;
//
//        if ((position == 0)) {
//            holder.mCheckBox.setVisibility(View.INVISIBLE);
//        } else {
//            holder.mCheckBox.setVisibility(View.VISIBLE);
//        }
//        holder.mCheckBox.setTag(position);
//        holder.mCheckBox.setOnClickListener(view -> {
//            int getPosition = (Integer) holder.mCheckBox.getTag();
//            itemList.get(getPosition).setSelected(holder.mCheckBox.isSelected());
//        });

        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
    }
}
