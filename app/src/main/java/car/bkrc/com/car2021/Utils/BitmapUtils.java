package car.bkrc.com.car2021.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import car.bkrc.com.car2021.ActivityView.FirstActivity;
import car.bkrc.com.car2021.FragmentView.RightAutoFragment;

import static org.opencv.core.Core.BORDER_REPLICATE;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.INTER_LINEAR;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.resize;

public class BitmapUtils {
    /**
     * 文字生成图片
     * @param text
     * @param textSize
     * @param textColor
     * @param bgColor
     * @param padding
     * @return
     */
    public static Bitmap text2Bitmap(String text, int textSize, String textColor, String bgColor, int padding) {

        Paint paint = new Paint();
        paint.setColor(Color.parseColor(textColor));
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        float width = paint.measureText(text, 0, text.length());

        float top = paint.getFontMetrics().top;
        float bottom = paint.getFontMetrics().bottom;

        Bitmap bm = Bitmap.createBitmap((int) (width + padding * 2), (int) ((bottom - top) + padding * 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);

        canvas.drawColor(Color.parseColor(bgColor));
        canvas.drawText(text, padding, - top + padding, paint);
        return bm;
    }

    /**
     * 将bitmap转换为本地的图片
     *
     * @param bitmap
     * @return
     */
    public static String bitmap2Path(Bitmap bitmap, String path) {
        try {
            OutputStream os = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
        return path;
    }

    public static void saveBitmap(Bitmap res,String route,String format){
        Bitmap bitmap_save=res;
        if (route==null)
        {
            route="/car/";
        }
        if (format==null)
        {
            format=".png";
        }
        //bitmap_save = Bitmap.createBitmap(bitmap_save.getWidth(), bitmap_save.getHeight(), Bitmap.Config.ARGB_8888);
        String fileDir = Environment.getExternalStorageDirectory() + route;
        String fileName = System.currentTimeMillis() + format;
        String path = fileDir + fileName;
        if (!new File(fileDir).exists()) {
            new File(fileDir).mkdirs();
        }
        BitmapUtils.bitmap2Path(bitmap_save, path);
        Log.d("save","图片保存到: "+fileDir);
        //Toast.makeText(FirstActivity.this, "图片保存到: "+fileDir, Toast.LENGTH_SHORT).show();
    }


}
