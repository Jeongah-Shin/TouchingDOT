package com.dotincorpkr.englidot.Alphabet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dotincorpkr.englidot.R;
import com.dotincorpkr.englidot.Singleton;
import com.dotincorpkr.englidot.TagData;

/**
 * Created by wjddk on 2017-02-06.
 */

public class AlphabetReviewAdapter extends BaseAdapter {
    AlphabetReviewViewHolder holder;
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
            convertView = inflater.inflate(R.layout.alphabet_review_listview_item, parent, false);

            holder = new AlphabetReviewViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (AlphabetReviewViewHolder) convertView.getTag();
        }

        TagData item = Singleton.getInstance().getSingleton_List().get(position);

        holder.order.setText(item.getId().replace("0",""));
        holder.wrongAnswer.setText(String.valueOf(item.getWrongAnswer()));

        return convertView;
    }
}
