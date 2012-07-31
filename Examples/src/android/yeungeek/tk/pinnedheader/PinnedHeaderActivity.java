
package android.yeungeek.tk.pinnedheader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.yeungeek.tk.R;

import java.util.Arrays;

/**
 * @ClassName: PinnedHeaderActivity
 * @Description: PinnedHeaderView
 * @author Anson.Yang
 * @date 2012-7-29 上午11:41:56
 */
public class PinnedHeaderActivity extends Activity {

    private static final String[] names = {
            "Geoffrey Hampton",
            "Ciaran Holcomb",
            "Marshall Kelly",
            "Mufutau Saunders",
            "Ishmael Durham",
            "Brock Golden",
            "Dalton Britt",
            "Tad Wright",
            "Carl Olsen",
            "Jack Cote",
            "Damian Carpenter",
            "Burke Cochran",
            "Sebastian Mcmahon",
            "Talon Stout",
            "Anthony Johnston",
            "Calvin Howell",
            "Simon Hale",
            "Talon Leon",
            "Stephen Mayo",
            "Ezra Graham",
            "Ryan Juarez",
            "Nathan Bowman",
            "Kermit Mcclure",
            "Axel Rhodes",
            "David Maynard",
            "Wing Larsen",
            "Noah Buchanan",
            "Nathan Mayer",
            "Nigel Mccormick",
            "Herrod Rivera",
            "Armando Meyers",
            "Colin Velasquez",
            "Zeus Brooks",
            "Hilel Stafford",
            "Merrill Russo",
            "Cole Lang",
            "Dieter Velez",
            "Lance Stokes",
            "Jarrod Oneil",
            "Louis Robbins",
            "Daquan Mclean",
            "Dorian Wong",
            "Nicholas Adams",
            "Kaseem Holt",
            "Kevin Alvarado",
            "Clarke Munoz",
            "Logan Holmes",
            "Kennedy Moody",
            "Joshua Barker",
            "Jamal David"
    };
    // 排序
    static {
        Arrays.sort(names);
    }

    private NamesApdater mApdater;
    private PinnedHeaderListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pinnedheader);

        mApdater = new NamesApdater(this, R.layout.list_item, android.R.id.text1, names);
        mListView = (PinnedHeaderListView) findViewById(R.id.pinnedheaer_listview);
        View mSearchView = LayoutInflater.from(this).inflate(R.layout.search_item,
                mListView, false);
        Log.e("mListView", "after mListView count:" + mApdater.getCount());

        mListView.addHeaderView(mSearchView);

        mListView.setAdapter(mApdater);

        Log.e("mListView", "after mListView count:" + mListView.getAdapter().getCount());
        View mHeaderView = LayoutInflater.from(this).inflate(R.layout.list_item_header,
                mListView, false);
        mListView.setPinnedHeaderView(mHeaderView);
        mListView.setOnScrollListener(mApdater);

    }
}
