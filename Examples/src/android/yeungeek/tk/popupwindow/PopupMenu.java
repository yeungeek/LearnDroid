
package android.yeungeek.tk.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.yeungeek.tk.R;

import java.util.List;

/**
 * @ClassName: PopupMenu
 * @Description: PopupMenu
 * @author Anson.Yang
 * @date 2012-8-3 上午9:54:59
 */
public class PopupMenu {
    private final Context context;
    private ListView listView;
    private PopupWindow popupWindow;
    private OnItemClickListener onItemClickListener;

    /**
     * 使用默认的Adapter 显示 ListView
     * 
     * @param ctx
     * @param list
     */
    public PopupMenu(Context ctx, List<String> list) {
        this.context = ctx;
        createPopupWindow(new GroupAdapter(context, list));
    }

    /**
     * 使用自定义的Adapter 显示 ListView
     * 
     * @param ctx
     * @param adapter
     */
    public PopupMenu(Context ctx, ListAdapter adapter) {
        this.context = ctx;
        createPopupWindow(adapter);
    }

    /**
     * @param adapter
     */
    private void createPopupWindow(ListAdapter adapter) {
        if (null == popupWindow) {
            View view = LayoutInflater.from(context).inflate(R.layout.popup_list, null);
            listView = (ListView) view.findViewById(R.id.popup_list);
            listView.setAdapter(adapter);

            popupWindow = new PopupWindow(view, context.getResources().getDimensionPixelSize(
                    R.dimen.popup_menu_width), LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * 显示popMenu,之前需要设置itemListener,否则点击列表没有效果
     * 
     * @param parent
     * @param xoff
     * @param yoff
     */
    public void showPopupMenu(View parent, int xoff, int yoff) {
        showAsDropDown(parent, xoff, yoff);
        if (null != onItemClickListener) {
            listView.setOnItemClickListener(onItemClickListener);
        }
    }

    private void showAsDropDown(View parent, int xoff, int yoff) {
        // 聚焦
        popupWindow.setFocusable(true);
        // 允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击"返回Back"也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.showAsDropDown(parent,
                context.getResources().getDimensionPixelSize(xoff),
                context.getResources().getDimensionPixelSize(yoff));
        popupWindow.update();
    }

    /**
     * @param onItemClickListener the onItemClickListener to set
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * @return the popupWindow
     */
    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    /**
     * @param popupWindow the popupWindow to set
     */
    public void setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }
}
