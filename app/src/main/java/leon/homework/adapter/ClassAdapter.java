package leon.homework.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import leon.homework.R;
import leon.homework.javaBean.ClassObject;
import leon.homework.javaBean.TodayWork;

/**
 * Created by Administrator on 2017/3/31.
 */

public class ClassAdapter extends ArrayAdapter<ClassObject> {
    private int resourceid;
    public ClassAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ClassObject> objects) {
        super(context, resource, objects);
        this.resourceid = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ClassObject mclass = getItem(position);
        View view;
        ClassAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceid, null);
            viewHolder = new ClassAdapter.ViewHolder();
            viewHolder.className = (TextView) view.findViewById(R.id.tv_classlistitem);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ClassAdapter.ViewHolder) view.getTag();
        }
        viewHolder.className.setText(mclass.getClassname());
        return view;
    }

    private class ViewHolder {
        TextView className;
    }

}
