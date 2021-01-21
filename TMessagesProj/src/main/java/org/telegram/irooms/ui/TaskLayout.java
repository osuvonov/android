package org.telegram.irooms.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.rooms.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskLayout extends FrameLayout {
    public TextView taskTitle;
    public TextView deadLine;
    private ImageView clockImage;
    public ImageView actionButton;
    public TextView description;
    public RecyclerView avatarList;
    public TextView sentTime;

    public TaskLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        taskTitle = new TextView(context);
        taskTitle.setText("Task");
        deadLine = new TextView(context);
        deadLine.setText("12/12/12");
        clockImage = new ImageView(context);
        clockImage.setBackground(context.getResources().getDrawable(R.drawable.menu_recent));
        actionButton = new ImageView(context);
        actionButton.setBackground(context.getResources().getDrawable(R.drawable.floating_pencil));
        description = new TextView(context);
        description.setText("lskdnfs kfnsdfjsdofsd fsdf sodf sdf sd");
        avatarList = new RecyclerView(context);
        sentTime = new TextView(context);
        View selector = new View(context);
        selector.setBackground(Theme.getSelectorDrawable(false));
        addView(selector, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 0, 0, 0, 2));
        addView(taskTitle, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 5, 5, 0, 2));
    }


}
