package car.bkrc.com.car2021.Utils.CarColor;

//{
//}

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import car.bkrc.com.car2021.DataProcessingModule.ConnectTransport;
import car.bkrc.com.car2021.FragmentView.LeftFragment;
import car.bkrc.com.car2021.FragmentView.RightAutoFragment;
import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.Utils.TrafficRecUtil.ColorRecognition;

import static car.bkrc.com.car2021.FragmentView.LeftFragment.cameraCommandUtil;

//public class CarColor  extends Fragment {
public class CarColor  {
    private TextView r_num, g_num, blue_num, y_num, s_num, c_num, black_num;
    private EditText r_max, g_max, blue_max, y_max, s_max, c_max, black_max;
    private EditText r_min, g_min, blue_min, y_min, s_min, c_min, black_min;
    private Bitmap bitmap = null;
    private Bitmap bitmap2 = null;
    private boolean flag = true;
    private View view = null;
    private boolean backflag = false;

    public static LeftFragment left_Fragment;//加载类
    public static ColorRecognition Color_Recognition;//加载类
    public static ConnectTransport ConnectTransport;//加载类

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        initView(view);
//        task();
////        if (backflag==false)
////        {
//            view = inflater.inflate(R.layout.car_color, container, false);
////    }
////        else
////        {view = inflater.inflate(R.layout.right_auto_fragment, container, false);
////            RightAutoFragment.fragment_falg=0;}
//        bitmap = LeftFragment.bitmap2;
//        return view;
//    }
//
//    private void initView(View view) {
//        image_show = view.findViewById(R.id.image_show);
//        rec_image_show = view.findViewById(R.id.rec_image_show);
//        image_show.setImageBitmap(bitmap);
    private void initView(View view)  {
        r_num =view.findViewById(R.id.r_num);
        g_num = view.findViewById(R.id.g_num);
        blue_num =view.findViewById(R.id.blue_num);
        y_num = view.findViewById(R.id.y_num);
        s_num = view.findViewById(R.id.s_num);
        c_num = view.findViewById(R.id.c_num);
        black_num = view.findViewById(R.id.black_num);

        r_max = view.findViewById(R.id.r_max);
        g_max = view.findViewById(R.id.g_max);
        blue_max = view.findViewById(R.id.blue_max);
        y_max = view.findViewById(R.id.y_max);
        s_max = view.findViewById(R.id.s_max);
        c_max = view.findViewById(R.id.c_max);
        black_max = view.findViewById(R.id.black_max);

        r_min = view.findViewById(R.id.r_min);
        g_min = view.findViewById(R.id.g_min);
        blue_min = view.findViewById(R.id.blue_min);
        y_min = view.findViewById(R.id.y_min);
        s_min = view.findViewById(R.id.s_min);
        c_min = view.findViewById(R.id.c_min);
        black_min = view.findViewById(R.id.black_min);

        left_Fragment = new LeftFragment();//实例化
        Color_Recognition = new ColorRecognition();
        ConnectTransport = new ConnectTransport();
    }



    private void CarColor() {
        bitmap = LeftFragment.bitmap;//跨页面获取位图
        bitmap2= bitmap;
        if (bitmap2 != null) {
            convertToBlack(bitmap2);// 像素处理背景变为白色，红、绿、蓝、黄、品、青、黑色，白色不变
        }
    }


    // 显示图片
    public Handler phHandler = new Handler() {
        public void handleMessage(Message msg) {
//            if (msg.what == 10) {
//                show_view.setImageBitmap(bitmap);
//            }
            if (msg.what == 20) {
                CarColor();
            }
//            if (msg.what == 30) {
//                Camera_ip.setText(cameraIP);
//            }
            if (msg.what == 40) {
                r_num.setText("" + colorNum[1]);
                g_num.setText("" + colorNum[2]);
                blue_num.setText("" + colorNum[3]);
                y_num.setText("" + colorNum[4]);
                s_num.setText("" + colorNum[5]);
                c_num.setText("" + colorNum[6]);
                black_num.setText("" + colorNum[7]);
                for (int i = 0; i < 8; i++) {
                    colorNum[i] = 0;
                }
            }
        }
    };


    private int red_max = 0, green_max = 0, blues_max = 0, yellow_max = 0, sort_max = 0, ching_max = 0, blacks_max = 0;
    private int red_min = 0, green_min = 0, blues_min = 0, yellow_min = 0, sort_min = 0, ching_min = 0, blacks_min = 0;

    private void get_input_threshold() {
        red_max = Integer.getInteger(r_max.getText().toString());
        green_max = Integer.getInteger(g_max.getText().toString());
        blues_max = Integer.getInteger(blue_max.getText().toString());
        yellow_max = Integer.getInteger(y_max.getText().toString());
        sort_max = Integer.getInteger(s_max.getText().toString());
        ching_max = Integer.getInteger(c_max.getText().toString());
        blacks_max = Integer.getInteger(black_max.getText().toString());

        red_min = Integer.getInteger(r_min.getText().toString());
        green_min = Integer.getInteger(g_min.getText().toString());
        blues_min = Integer.getInteger(blue_min.getText().toString());
        yellow_min = Integer.getInteger(y_min.getText().toString());
        sort_min = Integer.getInteger(s_min.getText().toString());
        ching_min = Integer.getInteger(c_min.getText().toString());
        blacks_min = Integer.getInteger(black_min.getText().toString());

    }


    private int[] colorNum = new int[8];//红、绿、蓝、黄、品、青、黑色个数
    private int blackMax = 255; //黑色最大RGB值和
    private int RGBMax = 365;   //红绿蓝最大RGB值和
    private int noiseMax = 510; //黄品青最大RGB值和

    private Bitmap convertToBlack(Bitmap bip) {// 像素处理背景变为白色，红、绿、蓝、黄、品、青、黑色，白色不变
        int width = bip.getWidth();
        int height = bip.getHeight();
        int[] pixels = new int[width * height];
        bip.getPixels(pixels, 0, width, 0, 0, width, height);       // 把二维图片的每一行像素颜色值读取到一个一维数组中
        int[] pl = new int[bip.getWidth() * bip.getHeight()];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                int pixel = pixels[offset + x];
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                int rgb = r + g + b;
                if (rgb < blackMax)                //黑色
                {
                    pl[offset + x] = pixel;
                    colorNum[7]++;
                } else if (rgb < RGBMax) {        // 红绿蓝
                    pl[offset + x] = pixel;
                    if (r > g && r > b)
                        colorNum[1]++;            //红色
                    else if (g > b)
                        colorNum[2]++;            //绿色
                    else
                        colorNum[3]++;            //蓝色

                } else if (rgb < noiseMax) {      //黄、品和青
                    pl[offset + x] = pixel;
                    if (b < r && b < g)
                        colorNum[4]++;             //黄色
                    else if (g < r)
                        colorNum[5]++;             //品色
                    else
                        colorNum[6]++;            //青色
                } else {
                    pl[offset + x] = 0xffffffff;// 白色
                }
            }
        }
        phHandler.sendEmptyMessage(40);
        Bitmap bitmap_result = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);//把颜色值重新赋给新建的图片 图片的宽高为以前图片的值
        bitmap_result.setPixels(pl, 0, width, 0, 0, width, height);
        return bitmap_result;
    }
}