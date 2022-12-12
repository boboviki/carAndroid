package car.bkrc.com.car2021.yolov5ncnn;

import static android.graphics.Bitmap.Config.ARGB_8888;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.tencent.yolov5ncnn.YoloV5Ncnn;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import car.bkrc.com.car2021.ActivityView.LoginActivity;
import car.bkrc.com.car2021.FragmentView.LeftFragment;
import car.bkrc.com.car2021.FragmentView.RightAutoFragment;
import car.bkrc.com.car2021.FragmentView.RightFragment1;
import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.ViewAdapter.InfrareAdapter;
import car.bkrc.com.car2021.ViewAdapter.Infrared_Landmark;

public class Yolov5Fragment extends Fragment {
    public static final String TAG = "Yolov5";
    public static Yolov5Fragment getInstance() {
        return Yolov5FragmentHolder.sInstance;
    }
    private static class Yolov5FragmentHolder {
        private static final Yolov5Fragment sInstance = new Yolov5Fragment();
    }
    private View view = null;
    private static final int SELECT_IMAGE = 1;
    public static ImageView iv,iv2;
    private Bitmap bitmap = null;
    public static Bitmap yourSelectedImage = null;
    Button SelectPicBtn,DetectBtnCpu,DetectBtnGpu;



    /**
     * 页面加载，绑定滑动界面
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        /*这部分实现与滑动界面绑定*/
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        } else {
            //判断是否为平板，本处保留，但展现的是同一个界面
            if (LoginActivity.isPad(getActivity()))
                view = inflater.inflate(R.layout.yolov5_fragment, container, false);
            else
                view = inflater.inflate(R.layout.yolov5_fragment, container, false);
        }
        initView(view);
        setListen();
        return view;

        /*这部分实现与滑动界面绑定*/
    }


    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
    private void initView(View view){
        iv = view.findViewById(R.id.image_show);
        iv2= view.findViewById(R.id.rec_image_show);
        SelectPicBtn = view.findViewById(R.id.SelectPicBtn);
        DetectBtnCpu = view.findViewById(R.id.RecCpuBtn);
        DetectBtnGpu = view.findViewById(R.id.RecGpuBtn);


        boolean ret_init = yolov5ncnn.Init(getContext().getAssets());
        if (!ret_init)
        {
            Log.e(TAG, "yolov5ncnn Init failed");
        }
    }

    private void setListen(){
        SelectPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {//选择图片
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, SELECT_IMAGE);
            }
        });

        DetectBtnCpu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {//视觉识别，通过cpu
                if (yourSelectedImage == null)
                    return;
                YoloV5Ncnn.Obj[] objects = yolov5ncnn.Detect(yourSelectedImage, false);
               String label = objects[0].label;
                float x=objects[0].x;
                float y=objects[0].y;
                float w=objects[0].w;
                float h=objects[0].h;
                float prob=objects[0].prob;
                Log.d(TAG, "onClick: "+label+"x: "+(int)x+"y: "+(int)y+"w: "+(int)w+"h: "+(int)h+"p: "+(int)prob);
                showObjects(objects);
            }
        });

        DetectBtnGpu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });

    }

    private void showObjects(YoloV5Ncnn.Obj[] objects)
    {
        if (objects == null)
        {
            iv.setImageBitmap(bitmap);
            return;
        }

        // draw objects on bitmap
        Bitmap rgba = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        final int[] colors = new int[] {
                Color.rgb( 54,  67, 244),
                Color.rgb( 99,  30, 233),
                Color.rgb(176,  39, 156),
                Color.rgb(183,  58, 103),
                Color.rgb(181,  81,  63),
                Color.rgb(243, 150,  33),
                Color.rgb(244, 169,   3),
                Color.rgb(212, 188,   0),
                Color.rgb(136, 150,   0),
                Color.rgb( 80, 175,  76),
                Color.rgb( 74, 195, 139),
                Color.rgb( 57, 220, 205),
                Color.rgb( 59, 235, 255),
                Color.rgb(  7, 193, 255),
                Color.rgb(  0, 152, 255),
                Color.rgb( 34,  87, 255),
                Color.rgb( 72,  85, 121),
                Color.rgb(158, 158, 158),
                Color.rgb(139, 125,  96)
        };
        //画出标注框
        Canvas canvas = new Canvas(rgba);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);

        Paint textbgpaint = new Paint();
        textbgpaint.setColor(Color.WHITE);
        textbgpaint.setStyle(Paint.Style.FILL);

        Paint textpaint = new Paint();
        textpaint.setColor(Color.BLACK);
        textpaint.setTextSize(26);
        textpaint.setTextAlign(Paint.Align.LEFT);

        for (int i = 0; i < objects.length; i++)
        {
            paint.setColor(colors[i % 19]);

            canvas.drawRect(objects[i].x, objects[i].y, objects[i].x + objects[i].w, objects[i].y + objects[i].h, paint);

            // draw filled text inside image
            {
                String text = objects[i].label + " = " + String.format("%.1f", objects[i].prob * 100) + "%";

                float text_width = textpaint.measureText(text);
                float text_height = - textpaint.ascent() + textpaint.descent();

                float x = objects[i].x;
                float y = objects[i].y - text_height;
                if (y < 0)
                    y = 0;
                if (x + text_width > rgba.getWidth())
                    x = rgba.getWidth() - text_width;

                canvas.drawRect(x, y, x + text_width, y + text_height, textbgpaint);

                canvas.drawText(text, x, y - textpaint.ascent(), textpaint);
            }
        }

       iv.setImageBitmap(rgba);
    }
    public static LeftFragment left_Fragment;

    /**选择照片代码模块--->*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            try
            {
                if (requestCode == SELECT_IMAGE) {
                    bitmap = decodeUri(selectedImage);

                    yourSelectedImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                    iv.setImageBitmap(bitmap);
                }
            }
            catch (FileNotFoundException e)
            {
                Log.e("MainActivity", "FileNotFoundException");
                return;
            }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException
    {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 640;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(selectedImage), null, o2);

        // Rotate according to EXIF
        int rotate = 0;
        try
        {
            ExifInterface exif = new ExifInterface(getContext().getContentResolver().openInputStream(selectedImage));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        }
        catch (IOException e)
        {
            Log.e("MainActivity", "ExifInterface IOException");
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**<-----选择照片代码模块*/




    }

