package car.bkrc.com.car2021.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
//保存图片
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

    /**
     * 将uri照片转为bitmap图片
     *
     * @param uri
     * @return Bitmap
     */
    public static Bitmap ImageSizeCompress(Uri uri){
        InputStream Stream = null;
        InputStream inputStream = null;
        try {
            //根据uri获取图片的流
            inputStream = FirstActivity.getContext().getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options的in系列的设置了，injustdecodebouond只解析图片的大小，而不加载到内存中去
            options.inJustDecodeBounds = true;
            //1.如果通过options.outHeight获取图片的宽高，就必须通过decodestream解析同options赋值
            //否则options.outheight获取不到宽高
            BitmapFactory.decodeStream(inputStream,null,options);
            //2.通过 btm.getHeight()获取图片的宽高就不需要1的解析，我这里采取第一张方式
//            Bitmap btm = BitmapFactory.decodeStream(inputStream);
            //以屏幕的宽高进行压缩
            DisplayMetrics displayMetrics = FirstActivity.getContext().getResources().getDisplayMetrics();
            int heightPixels = displayMetrics.heightPixels;
            int widthPixels = displayMetrics.widthPixels;
            //获取图片的宽高
            int outHeight = options.outHeight;
            int outWidth = options.outWidth;
            //heightPixels就是要压缩后的图片高度，宽度也一样
            int a = (int) Math.ceil((outHeight/(float)heightPixels));
            int b = (int) Math.ceil(outWidth/(float)widthPixels);
            //比例计算,一般是图片比较大的情况下进行压缩
            int max = Math.max(a, b);
            if(max > 1){
                options.inSampleSize = max;
            }
            //解析到内存中去
            options.inJustDecodeBounds = false;
//            根据uri重新获取流，inputstream在解析中发生改变了
            Stream = FirstActivity.getContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(Stream, null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
                if(Stream != null){
                    Stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return  null;
    }



}
