package car.bkrc.com.car2021.Utils.TrafficRecUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.support.v7.graphics.Palette;
import android.util.Log;

import static android.content.ContentValues.TAG;


public class ColorRecognition {

    // 结果数据通过回调接口实现
    private int progress;
    public static String onSuccess;

//    public static PicColorRecResultCallback resultCallback; // 接口
//

    /**
     * 执行图像处理流程
     * <p>
     * 改变图像亮度 → 提升饱和度 → 定向裁剪图片 → 通过调色板识别图像鲜艳色调
     *
     * @param bitmap
     * @param brightness
     * @param progress
     * @return
     */
    public void PictureProcessing(Context context, Bitmap bitmap, int brightness, int progress, int blur) {
        if (bitmap == null) {
            return;
        }
        this.progress = progress;
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1,
                0, 0, brightness,// 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(bmp);
        // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
        canvas.drawBitmap(bitmap, 0, 0, paint);
        rsBlur(context, bmp, blur);
    }

    /**
     * 图像模糊
     *
     * @param context 上下文
     * @param source  要模糊的图像
     * @param radius  模糊的大小 范围：0-25
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void rsBlur(Context context, Bitmap source, int radius) {
        if (radius > 25)
            radius = 25;
        Bitmap inputBmp = source;
        //(1)
        RenderScript renderScript = RenderScript.create(context);

        Log.i(TAG, "scale size:" + inputBmp.getWidth() + "*" + inputBmp.getHeight());

        // Allocate memory for Renderscript to work with
        //(2)
        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        //(3)
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //(4)
        scriptIntrinsicBlur.setInput(input);
        //(5)
        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius);
        //(6)
        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);
        //(7)
        // Copy the output to the blurred bitmap
        output.copyTo(inputBmp);
        //(8)
        renderScript.destroy();

        imageCrop(inputBmp, progress);
    }

    /**
     * 设置图片饱和度
     *
     * @param bitmap   需要处理的Bitmap
     * @param progress 饱和度范围
     * @return 返回处理后的图像
     */
    private void imageCrop(Bitmap bitmap, int progress) {

        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        ColorMatrix cMatrix = new ColorMatrix();
        // 设置饱和度
        cMatrix.setSaturation((float) progress);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(bmp);
        // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
        canvas.drawBitmap(bitmap, 0, 0, paint);
        ImageCropWithRect(bmp);
    }

    /**
     * 按长方形裁切图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap callBitmap;//处理后的图片
    public void ImageCropWithRect(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int nw, nh, retX, retY;


        nw = (int) (w / 2);//裁切后的宽度
        nh = (int) (h / 3);//裁切后的高度

        retX = w/3;//裁切起点的经度
        retY = 10;//裁切起点的纬度

        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null, false);
        if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled()) {
            bitmap.recycle();//回收原图片
        }
        callBitmap = bmp;//保存处理后的图片，用于传图到RightAutoFragment中
        changeRGB(bmp);
    }


    /**
     * 识别图片主体颜色
     *
     * @param newBitmap 传入图片
     */
       private void changeRGB(Bitmap newBitmap) {
        if (newBitmap != null) {
//            resultCallback.returnBitmap(newBitmap);
            Palette.from(newBitmap).generate(new Palette.PaletteAsyncListener() {
                @SuppressLint("ShowToast")
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch vibrant = palette.getVibrantSwatch(); // 创建调色板
                    if (vibrant == null) {
                        for (Palette.Swatch swatch : palette.getSwatches()) {  // 提取识别到的颜色数据到调色板中
                            vibrant = swatch;
                            break;
                        }
                    }
                    // 这样获取的颜色可以进行改变。
                    assert vibrant != null;
                    int rbg = vibrant.getRgb();  // 获取RGB的值
                    // 设置按钮背景色为图片主色
                    Log.e("This Color ", "" + +Color.red(rbg) + " " + Color.green(rbg) + " " + Color.blue(rbg)
                    );
                    if ((Color.red(rbg) - Color.green(rbg)) > 50 && Color.green(rbg) < 110) {
                        // 红灯
                        onSuccess="red";
                        Log.e("红灯", "" + Color.red(rbg));
                    } else if (Color.red(rbg) > Color.green(rbg)) {
                        // 黄灯
                        onSuccess="yellow";
                        Log.e("黄灯", "" + Color.red(rbg));
                    } else if ((Color.green(rbg) > Color.red(rbg)) && (Color.green(rbg) > Color.blue(rbg))) {
                        // 绿灯
                        onSuccess="green";
                        Log.e("绿灯", "" + Color.red(rbg));
                    } else onSuccess="what？";

                }
            });
        }
    }

    }
