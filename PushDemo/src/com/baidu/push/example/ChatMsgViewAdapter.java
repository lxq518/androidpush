
package com.baidu.push.example;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.shortmessage.R;

public class ChatMsgViewAdapter extends BaseAdapter {//聊天窗口适配器
    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();//获取类路径

    private ArrayList<ChatMsgEntity> coll;
//    int count = 10;
    private Context ctx;

    public ChatMsgViewAdapter(Context context, ArrayList<ChatMsgEntity> coll) {
        ctx = context;
        this.coll = coll;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int arg0) {
        return false;
    }

    public int getCount() {
        return coll.size();
//    	return count;
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(TAG, "getView>>>>>>>");
        ChatMsgEntity entity = coll.get(position);
//        System.out.println("??????????????????"+position);
        int itemLayout = entity.getLayoutID();

        LinearLayout layout = new LinearLayout(ctx);
        LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//将指定的xml文件转变成一个view
        vi.inflate(itemLayout, layout, true);

        TextView tvName = (TextView) layout.findViewById(R.id.messagedetail_row_name);
        tvName.setText(entity.getName());

        TextView tvDate = (TextView) layout.findViewById(R.id.messagedetail_row_date);
        tvDate.setText(entity.getDate());

        TextView tvText = (TextView) layout.findViewById(R.id.messagedetail_row_text);
        tvText.setText(entity.getText());
        return layout;
    }

    public int getViewTypeCount() {
        return coll.size();
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isEmpty() {
        return false;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
}
