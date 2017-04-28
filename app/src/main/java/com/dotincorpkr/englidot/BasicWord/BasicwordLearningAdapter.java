package com.dotincorpkr.englidot.BasicWord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dotincorpkr.englidot.R;
import com.dotincorpkr.englidot.Singleton;
import com.dotincorpkr.englidot.TagData;

/**
 * Created by wjddk on 2017-02-22.
 */

public class BasicwordLearningAdapter extends BaseAdapter {

    BasicwordLearningVIewHolder holder;
    @Override
    public int getCount() {
        return Singleton.getInstance().getSingleton_List().size();
    }

    @Override
    public Object getItem(int position) {
            return Singleton.getInstance().getSingleton_List().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.basicword_learning_listview_item, parent, false);

                holder = new BasicwordLearningVIewHolder(convertView);
                convertView.setTag(holder);
        } else {
            holder = (BasicwordLearningVIewHolder) convertView.getTag();
        }

        TagData item = Singleton.getInstance().getSingleton_List().get(position);

        holder.word.setText(item.getWord());

        return convertView;
    }
}
