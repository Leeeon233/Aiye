package leon.homework.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import leon.homework.app.AppContext;
import leon.homework.data.SaveData;
import leon.homework.javaBean.Msg;
import leon.homework.R;

/**
 * Created by mjhzds on 2017/2/14.
 */

public class MsgAdapter extends ArrayAdapter<Msg> {

    private int resourceId;

    public MsgAdapter(Context context, int textViewResource, List<Msg> objects) {
        super(context, textViewResource, objects);
        resourceId = textViewResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            viewHolder.leftHead = (ImageView) view.findViewById(R.id.left_head);
            viewHolder.leftHead.setImageResource(R.mipmap.icon);
            viewHolder.rightHead = (ImageView) view.findViewById(R.id.right_head);
            viewHolder.rightHead.setImageBitmap(BitmapFactory.decodeFile(SaveData.INSTANCE.getAvatar_path()));
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (msg.getMsgType() == Msg.Companion.getTYPE_RECEIVED()){
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.leftMsg.setText(msg.getContent());
            viewHolder.leftMsg.setTextColor(AppContext.Companion.getInstance().getResources().getColor(R.color.primary_text));
            viewHolder.leftMsg.setGravity(Gravity.CENTER);
        } else if(msg.getMsgType() == Msg.Companion.getTYPE_SENT()){
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(msg.getContent());
            viewHolder.rightMsg.setTextColor(AppContext.Companion.getInstance().getResources().getColor(R.color.primary_text));
            viewHolder.rightMsg.setGravity(Gravity.CENTER);
        }
        return view;
    }

    private class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        ImageView leftHead;
        ImageView rightHead;
    }
}
