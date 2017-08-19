package leon.homework.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import leon.homework.R;

/**
 * Created by mjhzds on 2017/2/28.
 */

public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

    private List<String> groupArray;
    private List<List<String>> childArray;
    private Context mContext;

    public MyExpandableListViewAdapter(Context mContext, List<String> groupArray, List<List<String>> childArray) {
        this.groupArray = groupArray;
        this.childArray = childArray;
        this.mContext = mContext;
    }

    @Override
    public int getGroupCount() {
        return groupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childArray.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupArray.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childArray.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        GroupHolder holder = null;
        if(view == null){
            holder = new GroupHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.download_parent, null);
            holder.groupName = (TextView)view.findViewById(R.id.tv_group_name);
            holder.arrow = (ImageView)view.findViewById(R.id.iv_arrow);
            view.setTag(holder);
        }else{
            holder = (GroupHolder)view.getTag();
        }

        //判断是否已经打开列表
        if(isExpanded){
            holder.arrow.setBackgroundResource(R.drawable.down_arrow);
        }else{
            holder.arrow.setBackgroundResource(R.drawable.right_arrow);
        }

        holder.groupName.setText(groupArray.get(groupPosition));

        return view;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        ChildHolder holder = null;
        if(view == null){
            holder = new ChildHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.download_child, null);
            holder.childName = (TextView)view.findViewById(R.id.tv_child_name);
            holder.divider = (ImageView)view.findViewById(R.id.iv_divider);
            view.setTag(holder);
        }else{
            holder = (ChildHolder)view.getTag();
        }

        if(childPosition == 0){
            holder.divider.setVisibility(View.GONE);
        }

        holder.childName.setText(childArray.get(groupPosition).get(childPosition));

        return view;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder{
        public TextView groupName;
        public ImageView arrow;
    }

    class ChildHolder{
        public TextView childName;
        public ImageView divider;
    }

}
