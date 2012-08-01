
package android.yeungeek.tk.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.yeungeek.tk.R;

import java.util.List;

/**
 * @ClassName: GroupAdapter
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-7-31 上午11:29:02
 */
public class GroupAdapter extends BaseAdapter {
    private final List<String> mList;
    private final Context mContext;

    public GroupAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_item_view, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.groupItem = (TextView) convertView.findViewById(R.id.group_item);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.groupItem.setTextColor(Color.BLACK);
        holder.groupItem.setText(mList.get(position));

        return convertView;
    }

    static class ViewHolder {
        TextView groupItem;
    }
}
