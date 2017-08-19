package leon.homework.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import leon.homework.javaBean.TodayWork;
import leon.homework.R;

/**
 * Created by mjhzds on 2017/2/6.
 */

public class SubjectAdapter extends ArrayAdapter<TodayWork> {

    private int resourceId;

    public SubjectAdapter(Activity context, int textViewResourceId, List<TodayWork> objects) {
        super(context,textViewResourceId,objects);
        this.resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TodayWork todayWork = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.subjectImage = (ImageView) view.findViewById(R.id.img_work_subject);
            viewHolder.subjectName = (TextView) view.findViewById(R.id.work_title);
            viewHolder.deadLine = (TextView) view.findViewById(R.id.work_deadline);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.subjectImage.setImageResource(todayWork.getSubjectImage());
        try {
            viewHolder.subjectName.setText(todayWork.getJs().getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        viewHolder.deadLine.setText("截止时间: "+todayWork.getDeadLine().substring(todayWork.getDeadLine().length()-5,todayWork.getDeadLine().length()));
        return view;
    }

    private class ViewHolder {
        ImageView subjectImage;
        TextView subjectName;
        TextView deadLine;
    }
}
