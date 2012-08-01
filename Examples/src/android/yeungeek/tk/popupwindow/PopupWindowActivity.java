
package android.yeungeek.tk.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.yeungeek.tk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: PopupWindowActivity
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-7-31 上午7:25:01
 */
public class PopupWindowActivity extends Activity {
    private PopupWindow mPopupWindow;
    private View mView;
    private ListView mListView;
    private List<String> mList;
    private GroupAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popupwindow);

        TextView mHeaderView = (TextView) findViewById(R.id.header_text);
        mHeaderView.setText(R.string.contact_list);

        mHeaderView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWindow(v);
            }
        });
    }

    private void showWindow(View parent) {
        if (null == mPopupWindow) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            mView = layoutInflater.inflate(R.layout.group_list, null);
            mListView = (ListView) mView.findViewById(R.id.listview_group);

            mList = new ArrayList<String>();
            mList.add("全部");
            mList.add("我的微博");
            mList.add("好友");
            mList.add("亲人");
            mList.add("同学");
            mList.add("朋友");
            mList.add("陌生人");

            // adapter
            mAdapter = new GroupAdapter(this, mList);
            mListView.setAdapter(mAdapter);

            mPopupWindow = new PopupWindow(mView, 300, 350);
        }

        // 使其聚集
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
        int xPos = windowManager.getDefaultDisplay().getWidth() / 2
                - mPopupWindow.getWidth() / 2;
        Log.i("coder", "xPos:" + xPos);

        // mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        mPopupWindow.showAsDropDown(parent, xPos, 0);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PopupWindowActivity.this, mList.get(position), 1000).show();
                if (null != mPopupWindow) {
                    mPopupWindow.dismiss();
                }
            }
        });
    }
}
