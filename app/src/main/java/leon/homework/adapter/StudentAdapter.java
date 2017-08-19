package leon.homework.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import leon.homework.app.AppContext;
import leon.homework.R;
import leon.homework.javaBean.StudentObject;
import leon.homework.sqlite.WorkDao;

/**
 * Created by Administrator on 2017/4/2.
 */

public class StudentAdapter extends ArrayAdapter<StudentObject> {
    private int resourceid;
    private String workid;
    public StudentAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<StudentObject> objects,String workid) {
        super(context, resource, objects);
        this.resourceid = resource;
        this.workid =workid;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        StudentObject mstudent = getItem(position);
        View view;
        StudentAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceid, null);
            viewHolder = new StudentAdapter.ViewHolder();
            viewHolder.stuname = (TextView) view.findViewById(R.id.tv_stu_list_name);
            viewHolder.stuimg = (ImageView) view.findViewById(R.id.iv_stu_list);
            viewHolder.stufinish = (TextView) view.findViewById(R.id.tv_stu_list_finish);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (StudentAdapter.ViewHolder) view.getTag();
        }
        viewHolder.stuname.setText(mstudent.getStu_name());
        Bitmap stupic;
        if(new File(mstudent.getStu_img()).exists()){
             stupic = BitmapFactory.decodeFile(mstudent.getStu_img());
        }else{
             stupic = BitmapFactory.decodeResource(AppContext.Companion.getInstance().getResources(),R.mipmap.ic_person_black_48dp);
        }
        viewHolder.stuimg.setImageBitmap(stupic);
        String mid = WorkDao.Companion.getInstance().selectMid(workid,mstudent.getStu_alia());
        if(!WorkDao.Companion.getInstance().tableisexist(mid)){
            viewHolder.stufinish.setText("未提交");
        }else if(!WorkDao.Companion.getInstance().selectTeaIsFinished(mid)){
            viewHolder.stufinish.setText("未批改");
        }else{
            viewHolder.stufinish.setText("批改完成");
        }
        return view;
    }

    private class ViewHolder {
        TextView stuname;
        TextView stufinish;
        ImageView stuimg;
    }
}
