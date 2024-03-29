package com.upbest.base;

import android.util.SparseArray;
import android.view.View;

/**
 * listView GridView 通用优化viewHolder
 *
 * @author
 */
public class BaseViewHolder {

    private BaseViewHolder() {

    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            view.setTag(viewHolder);
        }

        View childView = viewHolder.get(id);

        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);

        }

        return (T) childView;

    }
}
