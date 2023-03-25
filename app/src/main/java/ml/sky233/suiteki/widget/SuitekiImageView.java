package ml.sky233.suiteki.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.io.IOException;

import ml.sky233.suiteki.R;
import ml.sky233.suiteki.util.MsgUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("AppCompatCustomView")
public class SuitekiImageView extends ImageView {
    public SuitekiImageView(Context context) {
        super(context);
    }

    public SuitekiImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SuitekiImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SuitekiImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setImageFromUrl(String url) {
        new Thread(() -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                public void onFailure(Call call, IOException e) {
                    handler.sendMessage(MsgUtils.build("", 1));
                }

                public void onResponse(Call call, Response response) throws IOException {
                    byte[] bytes = response.body().bytes();
                    handler.sendMessage(MsgUtils.build(BitmapFactory.decodeByteArray(bytes, 0, bytes.length), 0));
                }
            });
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    SuitekiImageView.this.setImageBitmap(scaleBitmap((Bitmap) msg.obj, 1.8F));
                    break;
                case 1:
                    SuitekiImageView.this.setImageResource(R.drawable.ic_error_outline);
                    SuitekiImageView.this.setPadding(20, 20, 20, 20);
                    break;
            }
        }
    };

    public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        // 获取原始图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 计算新的宽高
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        // 创建新的Bitmap对象
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        // 创建Canvas对象，并在其中绘制原始Bitmap对象
        Canvas canvas = new Canvas(scaledBitmap);
        RectF rectF = new RectF(0, 0, newWidth, newHeight);
        canvas.drawBitmap(bitmap, null, rectF, null);

        // 返回新的Bitmap对象
        return scaledBitmap;
    }

}
