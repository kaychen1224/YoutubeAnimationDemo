package idv.oscar.youtube;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class YoutubeActivity extends Activity {

    String TAG = "YoutubeActivity";

    private ListView mListView;

    private YoutubePlayView mYoutubePlayView;

    private class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Bitmap mIcon1;
        private Bitmap mIcon2;

        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

            // Icons bound to the rows.
            mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.pic1);
            mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.pic2);
        }

        public int getCount() {
            return DATA.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid
            // unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is
            // no need
            // to reinflate it. We only inflate a new View when the convertView
            // supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_icon_text, null);

                // Creates a ViewHolder and store references to the two children
                // views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.text.setText(DATA[position]);
            holder.icon.setImageBitmap((position & 1) == 1 ? mIcon1 : mIcon2);

            holder.icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mYoutubePlayView.setVisibility(View.VISIBLE);
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView text;
            ImageView icon;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new EfficientAdapter(this));

        mYoutubePlayView = (YoutubePlayView) findViewById(R.id.youtubePlayView);
    }

    public static final String[] DATA = { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance",
            "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis" };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mYoutubePlayView.getVisibility() == View.VISIBLE && mYoutubePlayView.isMaxmum()) {
                mYoutubePlayView.startMinimize();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
