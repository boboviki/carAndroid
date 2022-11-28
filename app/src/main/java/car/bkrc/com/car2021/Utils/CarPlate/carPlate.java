package car.bkrc.com.car2021.Utils.CarPlate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import car.bkrc.com.car2021.ActivityView.FirstActivity;
import car.bkrc.com.car2021.FragmentView.RightAutoFragment;
import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.Utils.CarShape.CarShape;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.findContours;

public class carPlate {
    private static String TAG = "PlateDetector";

    public static double[][] HSV_VALUE_LOW = {
            {10, 163, 147},//浅蓝0
            {77, 163, 147},//黄色1
            {146, 212, 140},//品红2
            {126, 155, 160},//浅红色3
            {0, 204, 178},//蓝色4
            {35, 163, 147},//青色5
            {110, 155, 160},// 深红色6
            {0, 0, 0},//黑色7
            {0, 0, 192},//标准蓝8
            {0, 190, 190},//车牌蓝底9      暗的TFT：0,190,190   亮的：0,180,190
            {22, 195, 158}//车牌绿底10    暗的TFT H:21 S要调高一点:210  V:211  亮的TFT S值要调底一点：110    10,100,148
    };

    public static double[][] HSV_VALUE_HIGH = {
            {47, 255, 255},//浅蓝0
            {111, 255, 255},//黄色1
            {241, 255, 255.0},//品红2
            {150, 255, 255},//浅红色3
            {21, 255, 255},//蓝色4
            {75, 255.0, 255},//青色5
            {150, 255, 255},// 深红色6
            {180, 255, 120},//黑色7
            {45, 238, 255},//标准蓝8
            {28, 255, 255},//车牌蓝底9   亮暗一样
            {73, 255, 255}//车牌绿底10   暗H:66     亮H:83
    };


    public String plateDetector(Bitmap bitmap) {
        String plateStr = null;
        Log.e("cmd","进入车牌识别");

        Bitmap bitmap_plate = bitmap.copy(ARGB_8888, true);
        Bitmap bitmap_plate2 =bitmap.copy(ARGB_8888, true);
        Mat gray_image=new Mat();
        Mat candy_image=new Mat();
        Mat src=new Mat();
        Mat src2=new Mat();
        Mat dst=new Mat();
        Utils.bitmapToMat(bitmap_plate, src);
        Utils.bitmapToMat(bitmap_plate2, src2);

        //灰度化
        Imgproc.cvtColor(src, gray_image, Imgproc.COLOR_RGB2GRAY);//灰度化
        Imgproc.threshold(gray_image,dst,160,255,1);//二值化
        Imgproc.medianBlur(dst, src,29);//中值滤波
        //Imgproc.bilateralFilter(src,src,13,15,15);//双边滤波
        Mat element=Imgproc.getStructuringElement(MORPH_RECT,new Size(10,10));
        //Imgproc.dilate(src,dst,element);//膨胀1次
        Imgproc.Canny(src,dst,80,200,3,true);//边缘检测
        //寻找轮廓

        List<MatOfPoint> contours=new ArrayList<MatOfPoint>();
        findContours(src,contours,new Mat(),1, CHAIN_APPROX_SIMPLE);
        //绘制轮廓
        Log.d("plate","找到轮廓："+contours.size());
        Mat mRgba=new Mat();
//                mRgba.create(src.rows(), src.cols(), CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap_plate2,mRgba);

        Mat result=null;
        for (int i=0;i<contours.size();i++)
        {
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());//轮廓所有点的集合对象
            double epsilon = 0.05 * Imgproc.arcLength(contour2f, true);
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true);//多边形进行拟合
            if (approxCurve.rows() >3){//过滤掉杂乱的点，提取四个顶点以上的图形
                Rect rect1 = Imgproc.boundingRect((Mat) approxCurve);//获得形状外界矩形框
                double w=rect1.width;
                double h=rect1.height;
                double rate =Math.max(w,h)/Math.min(w,h);//得到外界矩形框的纵横比
                Log.d("plate","面积为："+w*h);
                if (rate>=2.2&&rate<=4.0){
                    if (w*h>30000&&w*h<50000) {
                        Log.d("plate","车牌面积为："+w*h);
                        Imgproc.drawContours(mRgba,contours,i,new Scalar(255,0,0),3);//画出轮廓
                        Imgproc.rectangle(mRgba,new Point(rect1.x,rect1.y),new Point(rect1.x+rect1.width,rect1.y+rect1.height),new Scalar(0,255,0),3);
                        //画出最小正方形
                        Bitmap plate = CarShape.opencvCutmap(bitmap,rect1.x,rect1.y,(int)w,(int)h);//裁切车牌
                        RightAutoFragment.rec_image_show.setImageBitmap(plate);
                        Utils.bitmapToMat(plate,result);
                    }
                }
            }
        }

        if (result != null) {
            Log.e("cmd", "TFT屏幕裁剪成功: ");
            //******使用HSV阈值分割***************************
            Mat hsv_img = result.clone();

//                save_pic(result,true);
            Imgproc.cvtColor(hsv_img, hsv_img, Imgproc.COLOR_BGR2HSV);//Hsv颜色空间转换


            //车牌蓝色底9阈值分割
            Mat plate_blue = new Mat();
            Core.inRange(hsv_img, new Scalar(HSV_VALUE_LOW[9]), new Scalar(HSV_VALUE_HIGH[9]), plate_blue);

            int blue_pixle_num = 0;
            for (int x = 0; x < plate_blue.width(); x++) {
                for (int y = 0; y < plate_blue.height(); y++) {
                    double pixle[] = plate_blue.get(y, x);
                    if (pixle[0] == 255.0) {// 如果是白色
                        blue_pixle_num++;
                    }
                }
            }
//            Log.e("PlateDetector", "蓝色车牌像素面积: "+blue_pixle_num );//42873
            if (blue_pixle_num > 40000 && blue_pixle_num < 70000) {
//                Log.e("PlateDetector", "进入蓝色车牌识别");
                plateStr = rect(plate_blue, result, 1);
            }

            //车牌绿色底10阈值分割
            Mat plate_green = new Mat();
            Core.inRange(hsv_img, new Scalar(HSV_VALUE_LOW[10]), new Scalar(HSV_VALUE_HIGH[10]), plate_green);
            int green_pixle_num = 0;
            for (int x = 0; x < plate_green.width(); x++) {
                for (int y = 0; y < plate_green.height(); y++) {
                    double pixle[] = plate_green.get(y, x);
                    if (pixle[0] == 255.0) {// 如果是白色
                        green_pixle_num++;
                    }
                }
            }
//            Log.e("PlateDetector", "绿色车牌像素面积: "+green_pixle_num );//42873
            if (green_pixle_num > 50000 && green_pixle_num < 90000) {
                Log.e("cmd", "进入绿色车牌识别");
                plateStr = rect(plate_green, result, 2);
                Log.e("cmd",plateStr+"");
            }
        }
        Log.d("plate","车牌号为："+plateStr);
        return plateStr;

        /**
         * ***********************车牌识别**************
         */
    }

    private static int[][] HSV_Color_cloudy = {//阴天荧光灯下
            {0,0,0,180,255,95},//黑色0
            {0,0,220,180,42,255},//白色1
            {156,43,46,180,255,255},//正红色2
            {35,43,46,77,255,255},//绿色3
            {100,208,200,126,255,255},//蓝色4，变化较大
            {26,48,46,34,255,255},//黄色5
            {140,43,46,155,255,255},//紫色6
    };
    car.bkrc.com.car2021.Utils.CarShape.CarShape CarShape=new CarShape();
    private int colornum=0;
    private Bitmap outline_bitmap;
    private Mat dstmat_2,hsvmat;
    public static Bitmap rebitmapPlate;
    public static Bitmap bitmapPlate;

    public void getPlate(Bitmap bitmap){
        int i=3;    int j=3;  int aremin=10;  int aremax=30000; double esp=0.030;//运算核默认3*3，可修改
        Log.d("auto","进入colorandshape函数");
        bitmapPlate=outline_bitmap;//以便图片显示
        plateColor(bitmap, 2,HSV_Color_cloudy[1], HSV_Color_cloudy[3]);//过滤颜色
        findPlateContours(hsvmat, i, j);   //寻找并绘制轮廓
        recognitionPlate(aremin, aremax,esp);//形状识别
    }

    private void plateColor(Bitmap cut_bitmap_op,int way,int[] way_1,int[] way_2){  //过滤颜色
        hsvmat = new Mat();
        Mat hsvmat_1 = new Mat();
        dstmat_2 = new Mat();

        outline_bitmap=cut_bitmap_op.copy(ARGB_8888, true);
        Utils.bitmapToMat(outline_bitmap, dstmat_2);//mat原图

        Imgproc.cvtColor(dstmat_2,hsvmat,Imgproc.COLOR_RGB2HSV);//hsvmat HSV格式mat原图
        Imgproc.cvtColor(dstmat_2,hsvmat_1,Imgproc.COLOR_RGB2HSV);//hsvmat HSV格式mat原图
        if(way==1)
        {
            Core.inRange(hsvmat,new Scalar(way_1[0],way_1[1],way_1[2]),new Scalar(way_1[3],way_1[4],way_1[5]),hsvmat);
        }
        else if(way==2)
        {
            Core.inRange(hsvmat,new Scalar(way_1[0],way_1[1],way_1[2]),new Scalar(way_1[3],way_1[4],way_1[5]),hsvmat);//范围1
            Core.inRange(hsvmat_1,new Scalar(way_2[0],way_2[1],way_2[2]),new Scalar(way_2[3],way_2[4],way_2[5]),hsvmat_1);//范围2
            Core.add(hsvmat,hsvmat_1,hsvmat);//合并，颜色处理完之后输出的是hsvmat
        }
        hsvmat_1.release();
    }

    private void findPlateContours(Mat src,int i,int j){
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(i,j));//这就是一个运算核，一个ixj的矩阵
        Imgproc.morphologyEx(src,src,Imgproc.MORPH_OPEN,kernel);//进行开运算
        Imgproc.morphologyEx(src,src,Imgproc.MORPH_CLOSE,kernel);//进行闭运算
        Utils.matToBitmap(src,outline_bitmap);
        Mat outma = new Mat();
        //轮廓识别
        contours= new ArrayList<>();
        Imgproc.findContours(hsvmat,contours,outma,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        contoursCounts  = contours.size();//轮廓数量
        Imgproc.drawContours(dstmat_2,contours,-1 , new Scalar(0,255,0),1);//绘制多边形
        Utils.matToBitmap(dstmat_2,outline_bitmap);
        outma.release();
        hsvmat.release();
        src.release();
    }

    private List<MatOfPoint> contours;
    private int contoursCounts;


    private void  recognitionPlate(int aremin,int aremax,double eps) {
        MatOfPoint2f contour2f;
        MatOfPoint2f approxCurve;
        MatOfPoint2f contourlength;
        double epsilon;

        for (int i= 0;i<contoursCounts;i++) {
            Log.d("cmd","第"+i+"个轮廓面积为："+Imgproc.contourArea(contours.get(i))+"");
            if (Imgproc.contourArea(contours.get(i)) > aremin&& Imgproc.contourArea(contours.get(i)) <aremax) {//面积筛选
                contour2f = new MatOfPoint2f(contours.get(i).toArray());
                epsilon = eps * Imgproc.arcLength(contour2f, true);
                approxCurve = new MatOfPoint2f();
                Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true);
                System.out .println("数量："+approxCurve.rows());
                Rect rect1 = Imgproc.boundingRect((Mat) approxCurve);
                Imgproc.rectangle(dstmat_2,new Point(rect1.x,rect1.y),new Point(rect1.x+rect1.width,rect1.y+rect1.height),new Scalar(255,255,0),4);
                Utils.matToBitmap(dstmat_2,outline_bitmap);
                rebitmapPlate=outline_bitmap;
                if (approxCurve.rows() == 4){
                    //Log.d("cmd","contour2f:"+approxCurve.toList()+"");//获取顶点的坐标
                    List<Point> pointList = approxCurve.toList();//得到顶点坐标放入pointList
                    Point point1 = pointList.get(0);//将四个坐标分别保存
                    Point point2 = pointList.get(1);
                    Point point3 = pointList.get(2);
                    Point point4 = pointList.get(3);
                    double distance_x1 = Math.abs(point3.x - point1.x);//将对角线x坐标值相减
                    double distance_x2 = Math.abs(point4.x - point2.x);//将对角线y坐标值相减
                    double distance_y1 = Math.abs(point3.y - point1.y);
                    double distance_y2 = Math.abs(point4.y - point2.y);
                    double  diagonal_1 =Math.sqrt(Math.pow(distance_x1,2)+Math.pow(distance_y1,2));//得到对角线的长度
                    double  diagonal_2 =Math.sqrt(Math.pow(distance_x2,2)+Math.pow(distance_y2,2));
                    if (diagonal_1/diagonal_2>0.92&&diagonal_1/diagonal_2<1.08)//判断对角线是否相等
                        Log.e("cmd","矩形面积为："+Imgproc.contourArea(contours.get(i))+"");

                }
            }
        }
        dstmat_2.release();
    }













    //通过HSV阈值得到的new_mask车牌的外矩形，new_src为了切割传进来的值
    //需要调整分割出来的矩形宽的长度和宽度    adaptiveThreshold要调节自适应阈值
    //temp==1识别蓝色车牌   temp==2识别绿色车牌
    public String rect(Mat new_mask, Mat new_src, int temp) {
        String result_str = null;

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(new_mask, contours, hierarchy,
                Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);//查找轮廓

        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea) {
                maxArea = area;
            }
        }
        Mat result = null;
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            double area = Imgproc.contourArea(contour);
            if (area > 0.01 * maxArea) {
                // 多边形逼近 会使原图放大4倍
                Core.multiply(contour, new Scalar(4, 4), contour);
                MatOfPoint2f newcoutour = new MatOfPoint2f(contour.toArray());
                MatOfPoint2f resultcoutour = new MatOfPoint2f();
                double length = Imgproc.arcLength(newcoutour, true);
                Double epsilon = 0.01 * length;
                Imgproc.approxPolyDP(newcoutour, resultcoutour, epsilon, true);
                contour = new MatOfPoint(resultcoutour.toArray());
                // 进行修正，缩小4倍改变联通区域大小
                MatOfPoint new_contour = new MatOfPoint();
                new_contour = ChangeSize(contour);
                double new_area = Imgproc.contourArea(new_contour);//轮廓的面积

                //最小外接矩形
                Rect rect = Imgproc.boundingRect(new_contour);
                double rectarea = rect.area();//最小外接矩形面积
                if (Math.abs((new_area / rectarea) - 1) < 0.2) {
//                    Log.e("PlateDetector", "车牌宽度 "+rect.width);
                    if (rect.width > 300) {
                        Mat imgSource = new_src.clone();
                        Imgproc.rectangle(imgSource, rect.tl(), rect.br(),
                                new Scalar(255, 0, 0), 2);

                        if (temp == 1) {
                            //蓝色车牌裁剪范围**************88
//                        rect.x+=8;
                            rect.x += 65;
//                        rect.width-=15;
                            rect.width -= 69;
                            rect.y += 8;
                            rect.height -= 15;
                            //蓝色车牌裁剪范围****************8888
                        }
                        if (temp == 2) {
                            //绿色车牌裁剪范围**************
                            rect.x += 97;
                            rect.width -= 109;
                            rect.y += 8;
                            rect.height -= 15;
                            //绿色车牌裁剪范围**************
                        }

                        result = new Mat(imgSource, rect);
                        Mat gray = new Mat();
                        Imgproc.cvtColor(result, gray, Imgproc.COLOR_BGR2GRAY);//灰度化
                        //字体黑色时，要反色
                        if (temp == 2) {
                            Core.bitwise_not(gray, gray);//绿色的，要取反(因为绿色中间的字是黑色的)   蓝色的不用(因为蓝色中间的字是白色的)
                        }
                        Mat threshold = new Mat();
                        //蓝色自适应阈值
                        if (temp == 1) {
                            Imgproc.adaptiveThreshold(gray, threshold, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 111, -7);
                            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_ERODE, kernel);
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_ERODE, kernel);
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_DILATE, kernel);
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_DILATE, kernel);
                        }
                        //绿色自适应阈值
                        if (temp == 2) {
                            Imgproc.adaptiveThreshold(gray, threshold, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 111, -7);
                            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_ERODE, kernel);
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_ERODE, kernel);
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_ERODE, kernel);
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_ERODE, kernel);
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_DILATE, kernel);
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_DILATE, kernel);
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_DILATE, kernel);
                            Imgproc.morphologyEx(threshold, threshold, Imgproc.MORPH_DILATE, kernel);
                        }


                        Bitmap threshold_bitmap = Mat2Bitmap(threshold);

                        String tess_str = doOcr(threshold_bitmap, "eng");//num4 和num6比较准确
                        Log.e("PlateDetector", "车牌识别结果 " + tess_str);


                        if (tess_str != null) {
                            if (tess_str.length() >= 6 && tess_str.length() <= 10) {
                                result_str = plateString(tess_str);
                                String result_str1 = result_str;
                                Log.i("PlateDetector", "车牌处理后结果: " + result_str);
                                return result_str1;
                            }
                        }

                    }
                }
            }
        }
        return result_str;
    }


    //车牌发送给道闸
    public static void plateToGate(String plateResult) {
        if (plateResult != null && plateResult.length() == 6) {
            Log.i(TAG, "正在发送车牌识别结果1：" + plateResult);
            FirstActivity.Connect_Transport.yanchi(100);
            FirstActivity.Connect_Transport.gate(0x10, plateResult.charAt(0), plateResult.charAt(1), plateResult.charAt(2));
            FirstActivity.Connect_Transport.yanchi(100);//多发几次防止数据丢失
            FirstActivity.Connect_Transport.gate(0x10, plateResult.charAt(0), plateResult.charAt(1), plateResult.charAt(2));
            FirstActivity.Connect_Transport.yanchi(100);//多发几次防止数据丢失
            FirstActivity.Connect_Transport.gate(0x10, plateResult.charAt(0), plateResult.charAt(1), plateResult.charAt(2));
            FirstActivity.Connect_Transport.yanchi(100);
            FirstActivity.Connect_Transport.gate(0x11, plateResult.charAt(3), plateResult.charAt(4), plateResult.charAt(5));
            FirstActivity.Connect_Transport.yanchi(100);
            FirstActivity.Connect_Transport.gate(0x11, plateResult.charAt(3), plateResult.charAt(4), plateResult.charAt(5));
            FirstActivity.Connect_Transport.yanchi(100);
            FirstActivity.Connect_Transport.gate(0x11, plateResult.charAt(3), plateResult.charAt(4), plateResult.charAt(5));
            FirstActivity.Connect_Transport.yanchi(100);
            Log.i(TAG, "正在发送车牌识别结果2：" + plateResult);
        }
    }


    /**
     * 车牌字符串处理
     * 将传入的车牌字符串进行识别
     */

    private String plateString(String plateResult) {

        //后面的六位字符toLowerCase()  大写转小写
        try {
            plateResult = plateResult.replaceAll(" ", "");

            //后面的六位字符toUpperCase()  小写转大写
            String platNumber = plateResult.substring(plateResult.length() - 6, plateResult.length()).toUpperCase();
            StringBuilder strBuilder = new StringBuilder(platNumber);
            strBuilder = plateReplace(strBuilder);//按照A123B4  的格式   进行相似匹配替换
            platNumber = strBuilder.toString();

            plateResult = platNumber;
        } catch (StringIndexOutOfBoundsException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plateResult;
    }


    /**
     * 车牌数据替换，减少错误率
     */
    private StringBuilder plateReplace(StringBuilder platNumber) {

        if (platNumber.charAt(1) >= 'A' && platNumber.charAt(1) <= 'Z') {
            platNumber = charToNum(platNumber, 1);//第1、2、3、5位本应该为数字，如果出现识别为字符就将其转换为数字
        }
        if (platNumber.charAt(2) >= 'A' && platNumber.charAt(2) <= 'Z') {
            platNumber = charToNum(platNumber, 2);//第1、2、3、5位本应该为数字，如果出现识别为字符就将其转换为数字
        }
        if (platNumber.charAt(3) >= 'A' && platNumber.charAt(3) <= 'Z') {
            platNumber = charToNum(platNumber, 3);//第1、2、3、5位本应该为数字，如果出现识别为字符就将其转换为数字
        }
        if (platNumber.charAt(5) >= 'A' && platNumber.charAt(5) <= 'Z') {
            platNumber = charToNum(platNumber, 5);//第1、2、3、5位本应该为数字，如果出现识别为字符就将其转换为数字
        }

        if (platNumber.charAt(0) >= '0' && platNumber.charAt(0) <= '9') {
            platNumber = numToChar(platNumber, 0);//第0、4位本应该为字符，如果出现识别为数字就将其转换为字符
        }

        if (platNumber.charAt(4) >= '0' && platNumber.charAt(4) <= '9') {
            platNumber = numToChar(platNumber, 4);//第0、4位本应该为字符，如果出现识别为数字就将其转换为字符
        }

//        platNumber=numToChar(platNumber,4);
//        sleep(10);

        return platNumber;
    }

    /**
     * 车牌中的数字转换为字符
     * 车牌:H833E8    位置：i=(0)、（4）
     */
    private StringBuilder numToChar(StringBuilder platNumber, int i) {

        char a = platNumber.charAt(i);
        switch (a) {
            case '0':
                platNumber.setCharAt(i, 'D');
                break;
            case '1':
                platNumber.setCharAt(i, 'I');
                break;
            case '2':
                platNumber.setCharAt(i, 'Z');
                break;
            case '3':
                platNumber.setCharAt(i, 'B');
                break;
            case '4':
                platNumber.setCharAt(i, 'A');
                break;
            case '5':
                platNumber.setCharAt(i, 'S');
                break;
            case '6':
                platNumber.setCharAt(i, 'G');
                break;
            case '7':
                platNumber.setCharAt(i, 'T');
                break;
            case '8':
                platNumber.setCharAt(i, 'B');
                break;
            case '9':

                break;
            default:
                break;
        }
        return platNumber;
    }

    /**
     * 车牌中字符转换为数字
     * 车牌:H833E8    位置：i=(1 2 3)、（5）
     */
    private StringBuilder charToNum(StringBuilder platNumber, int i) {
        char a = platNumber.charAt(i);
        switch (a) {
            case 'A':
                platNumber.setCharAt(i, '4');
                break;
            case 'B':
                platNumber.setCharAt(i, '8');
                break;
            case 'C':
                platNumber.setCharAt(i, '0');
                break;
            case 'D':
                platNumber.setCharAt(i, '4');
                break;
            case 'E':

                break;
            case 'F':

                break;
            case 'G':
                platNumber.setCharAt(i, '6');
                break;
            case 'H':

                break;
            case 'I':
                platNumber.setCharAt(i, '1');
                break;
            case 'J':

                break;
            case 'K':

                break;
            case 'L':
                platNumber.setCharAt(i, '1');
                break;
            case 'M':

                break;
            case 'N':

                break;
            case 'O':
                platNumber.setCharAt(i, '0');
                break;
            case 'P':

                break;
            case 'Q':

                break;
            case 'R':

                break;
            case 'S':
                platNumber.setCharAt(i, '5');
                break;
            case 'T':
                platNumber.setCharAt(i, '7');
                break;
            case 'U':

                break;
            case 'V':

                break;
            case 'W':

                break;
            case 'X':

                break;
            case 'Y':

                break;
            case 'Z':
                platNumber.setCharAt(i, '2');
                break;
            case '?':
                platNumber.setCharAt(i, '9');
                break;
        }

        return platNumber;
    }


    /**
     * 进行图片识别
     *
     * @param bitmap   待识别图片
     * @param language 识别语言
     * @return 识别结果字符串
     */
    public String doOcr(Bitmap bitmap, String language) {
//        if (language == null)
//            language = "eng";
        TessBaseAPI baseApi = new TessBaseAPI();
        FirstActivity firstActivity = new FirstActivity();
        // 必须加此行，tess-two要求BMP必须为此配置
        Log.e("语言包路径", "/sdcard/Download/");
         /*
        参数一：路径下 需要有tessdata 语言包
        (例如：/sdcard/Download/tessdata/eng.traineddata)
        参数二： 设置识别语言包
        chi_sim:简体中文
        eng:英文
        * */
        baseApi.init("/sdcard/Download/WeiXin", language);
        //白名单
        baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        // 黑名單
        baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "[email protected]#$%^&*()_+=-[]}{;:'\"\\|~`,./<>?' 'abcdefghijklmnopqrstuvwxyz");
        System.gc();
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        baseApi.setImage(bitmap);

        String text = baseApi.getUTF8Text();

        baseApi.clear();

        baseApi.end();

        return text;
    }


    // 转换工具
    public static Bitmap Mat2Bitmap(Mat cannyMat) {
        Bitmap bmpCanny = Bitmap.createBitmap(cannyMat.cols(), cannyMat.rows(),
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cannyMat, bmpCanny);
        return bmpCanny;
    }

    // 转换工具
    public static Mat Bitmap2Mat(Bitmap bmp) {
        Mat mat = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmp, mat);
        return mat;
    }

    // 把坐标降低到4分之一
    MatOfPoint ChangeSize(MatOfPoint contour) {
        for (int i = 0; i < contour.height(); i++) {
            double[] p = contour.get(i, 0);
            p[0] = p[0] / 4;
            p[1] = p[1] / 4;
            contour.put(i, 0, p);
        }
        return contour;
    }
}
