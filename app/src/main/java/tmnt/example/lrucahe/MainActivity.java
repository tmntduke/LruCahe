package tmnt.example.lrucahe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Button save;
    private ImageView show;

    private LruCache<String, Bitmap> mLruCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        save = (Button) findViewById(R.id.save);
        show = (ImageView) findViewById(R.id.show);

        int memorySize = (int) (Runtime.getRuntime().maxMemory() / 1024);

        int cacheSize = memorySize / 8;

        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBitmap(show);
            }
        });


    }

    private void setBitmap(ImageView imageView) {
        Bitmap bitmap = getBitmapFromCache(CommonUtil.URL);
        if (bitmap == null) {
            imageView.setImageResource(R.drawable.moren);
            new LoadTask(imageView).execute(CommonUtil.URL);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    private void writeCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {
            mLruCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromCache(String key) {
        return mLruCache.get(key);
    }

    class LoadTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;

        public LoadTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mImageView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            byte[] bytes = HttpUtils.doGet(params[0], null, "utf-8");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            writeCache(params[0], bitmap);
            return bitmap;
        }
    }
}
