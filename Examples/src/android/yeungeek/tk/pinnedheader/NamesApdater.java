
package android.yeungeek.tk.pinnedheader;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.yeungeek.tk.R;
import android.yeungeek.tk.pinnedheader.PinnedHeaderListView.PinnedHeaderAdapter;

/**
 * @ClassName: NamesApdater
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-7-29 下午12:02:20
 */
public class NamesApdater extends ArrayAdapter<String> implements PinnedHeaderAdapter,
        SectionIndexer, OnScrollListener {

    private final SectionIndexer mIndexer;

    public NamesApdater(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        mIndexer = new StringArrayAlphabetIndexer(objects, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        bindSelctionHeader(view, position);
        return view;
    }

    /**
     * @param view
     * @param position
     */
    private void bindSelctionHeader(View itemView, int position) {
        final TextView headerView = (TextView) itemView.findViewById(R.id.header_text);
        final View dividerView = itemView.findViewById(R.id.list_divider);
        // final EditText searchView = (EditText)
        // itemView.findViewById(R.id.search_text);

        final int section = getSectionForPosition(position);
        int ps = getPositionForSection(section);

        // if (position == 0) {
        // searchView.setVisibility(View.VISIBLE);
        // } else {
        // searchView.setVisibility(View.GONE);
        // }
        if (getPositionForSection(section) == position) {
            String title = (String) mIndexer.getSections()[section];
            headerView.setText(title);
            headerView.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.GONE);
        } else {
            headerView.setVisibility(View.GONE);
            dividerView.setVisibility(View.VISIBLE);
        }

        // move the divider for the last item in a section
        if (getPositionForSection(section + 1) - 1 == position) {
            dividerView.setVisibility(View.GONE);
        } else {
            dividerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param section
     * @return
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        if (mIndexer == null) {
            return -1;
        }

        return mIndexer.getPositionForSection(sectionIndex);
    }

    /**
     * @param position
     * @return
     */
    @Override
    public int getSectionForPosition(int position) {
        if (mIndexer == null) {
            return -1;
        }

        return mIndexer.getSectionForPosition(position);
    }

    @Override
    public int getPinnedHeaderState(int position) {
        if (mIndexer == null || getCount() == 0) {
            return PINNED_HEADER_GONE;
        }

        if (position < 0) {
            return PINNED_HEADER_GONE;
        }

        // The header should get pushed up if the top item shown
        // is the last item in a section for a particular letter.
        int section = getSectionForPosition(position);
        int nextSectionPosition = getPositionForSection(section + 1);

        Log.d("HeaderView", "position:" + position + ",section:" + section
                + ", nextSectionPosition:" + nextSectionPosition);
        if (nextSectionPosition != -1 && position == nextSectionPosition - 1) {
            Log.e("HeaderView", "PINNED_HEADER_PUSHED_UP");
            return PINNED_HEADER_PUSHED_UP;
        }

        return PINNED_HEADER_VISIBLE;
    }

    @Override
    public void configurePinnedHeader(View v, int position, int alpha) {
        TextView header = (TextView) v;

        final int section = getSectionForPosition(position);
        final String title = (String) getSections()[section];

        header.setText(title);
    }

    @Override
    public Object[] getSections() {
        if (mIndexer == null) {
            return new String[] {
                    " "
            };
        } else {
            return mIndexer.getSections();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
        }
    }

}
