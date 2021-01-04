package org.telegram.irooms.ui.spinner;

import androidx.annotation.NonNull;

public class State {
    private int id;
    private String name;
    private boolean selected;

    public State() {
    }

    public State(int id, String title) {
        this.id = id;
        this.name = title;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public String toString() {

        return name;
    }
}
