package com.example.myokhtttp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * @desc: 作用描述
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/8/12 0012 18:13
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/8/12 0012 18:13
 * @UpdateRemark: 更新说明
 * @version:
 */
public class ListAdapter extends BaseAdapter {

    private Context context;
    private List<String> data;

    public ListAdapter(Context context, List<String> data) {
        this.context = context.getApplicationContext();
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list, null);
            holder = new ViewHolder();
            holder.textView = view.findViewById(R.id.tv_item);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.textView.setText(data.get(i));

        return view;
    }

    class ViewHolder {
        TextView textView;
    }

}
