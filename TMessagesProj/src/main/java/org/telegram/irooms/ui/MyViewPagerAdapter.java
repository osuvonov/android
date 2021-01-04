package org.telegram.irooms.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

public class MyViewPagerAdapter extends PagerAdapter {

    ArrayList<View> views;
    LayoutInflater inflater;
    int capacity = 2;
    Context context;
    private ArrayList<String> titles = new ArrayList();

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public MyViewPagerAdapter(Context ctx) {
        inflater = LayoutInflater.from(ctx);
        context = ctx;
        //instantiate your views list
        views = new ArrayList<>(capacity);
        titles.addAll(Arrays.asList(new String[]{"Chat", "Task"}));
    }

    public void addViews(ArrayList<RecyclerListView> views) {
        this.views.clear();
        for (View a : views) {
            this.views.add(a);
        }
    }

    /**
     * To be called by onStop
     * Clean the memory
     */
    public void release() {
        views.clear();
        views = null;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return views.size();
    }

    /**
     * Create the page for the given position. The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return Returns an Object representing the new page. This does not
     * need to be a View, but can be some other container of
     * the page.  ,container
     */
    public Object instantiateItem(ViewGroup container, int position) {
        View currentView;

        currentView = views.get(position);
        try{
            container.removeViewAt(0);
        }catch (Exception x){}
        container.addView(currentView);
        return currentView;
    }

    /**
     * Remove a page for the given position. The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View from which the page will be removed.
     * @param position  The page position to be removed.
     * @param object    The same object that was returned by
     *                  {@link #instantiateItem(View, int)}.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);

    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with <code>object</code>
     * @param object Object to check for association with <code>view</code>
     * @return true if <code>view</code> is associated with the key object <code>object</code>
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }
}