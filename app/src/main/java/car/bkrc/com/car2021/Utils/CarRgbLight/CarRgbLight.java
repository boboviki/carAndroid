package car.bkrc.com.car2021.Utils.CarRgbLight;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.List;

import car.bkrc.com.car2021.ActivityView.FirstActivity;
import car.bkrc.com.car2021.FragmentView.RightAutoFragment;
import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.Utils.CarShape.CarShape;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matgreen1;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matgreen2;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matgreen3;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matred1;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matred2;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matred3;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matred4;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matyellow1;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matyellow2;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matyellow3;
import static car.bkrc.com.car2021.FragmentView.RightAutoFragment.matyellow4;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.dis_x;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.dis_y;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.ret_x;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.x_max;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.x_min;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.y_max;
import static car.bkrc.com.car2021.Utils.CarShape.CarShape.y_min;
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


public class CarRgbLight {
    //通过比较三个张图片的不同点，找到裁切的位置
    public void imageSubtract3(Mat image1, Mat image2, Mat image3)
    {
        Log.d("rgb", "进入图片比较" );
        //图片缩放
        if ((image1.rows() != image2.rows()) || (image1.cols() != image2.cols())|| (image1.cols() != image3.cols()))
        {
            if (image1.rows() > image3.rows()&&image2.rows() > image3.rows())
            {
                resize(image1, image1, image3.size(), 0, 0, INTER_LINEAR);
                resize(image2, image2, image3.size(), 0, 0, INTER_LINEAR);
            }
            else if (image1.rows() >  image2.rows()&&image3.rows() >image2.rows())
            {
                resize(image1, image1, image2.size(), 0, 0, INTER_LINEAR);
                resize(image3, image3, image2.size(), 0, 0, INTER_LINEAR);
            }
            else if (image2.rows() > image1.rows()&&image3.rows() >image1.rows())
            {
                resize(image2, image2, image1.size(), 0, 0, INTER_LINEAR);
                resize(image3, image3, image1.size(), 0, 0, INTER_LINEAR);
            }
        }

        Log.d("rgb", "图片压缩完成" );
        Mat image1_gary =new Mat();
        Mat image2_gary=new Mat();
        Mat image3_gary=new Mat();
        if (image1.channels() != 1)
        {
            cvtColor(image1, image1_gary, COLOR_BGR2GRAY);
        }
        if (image2.channels() != 1)
        {
            cvtColor(image2, image2_gary, COLOR_BGR2GRAY);
        }
        if (image3.channels() != 1)
        {
            cvtColor(image3, image3_gary, COLOR_BGR2GRAY);
        }
        Log.d("rgb", "图片灰化完成" );
        Mat absFrameDifferece=new Mat();
        Mat absFrameDifferece2=new Mat();
        Mat absFrameDifferece3=new Mat();
        Mat previousGrayFrame = image2_gary.clone();
        //图1减图2
        //subtract(image1_gary, image2_gary, frameDifference, new Mat(), CV_16SC1);

        //图1-图2取绝对值
        Core.absdiff(image1_gary,image2_gary, absFrameDifferece);
        Core.absdiff(image1_gary,image3_gary, absFrameDifferece2);
        Core.absdiff(image2_gary,image3_gary, absFrameDifferece3);
        Core.add(absFrameDifferece,absFrameDifferece2,absFrameDifferece);
        Core.add(absFrameDifferece,absFrameDifferece3,absFrameDifferece);
        Log.d("rgb", "图片对比合成完成" );

        //位深的改变
        absFrameDifferece.convertTo(absFrameDifferece, CV_8UC1, 1, 0);
        Mat segmentation =new Mat();

        //阈值处理（这一步很关键，要调好二值化的值）
        Imgproc.threshold(absFrameDifferece, segmentation,45, 255, THRESH_BINARY);

        //中值滤波
        Imgproc.medianBlur(segmentation, segmentation, 3);

        //形态学处理(开闭运算)
        //形态学处理用到的算子
        Mat morphologyKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(5, 5));
        Imgproc.morphologyEx(segmentation, segmentation, MORPH_CLOSE, morphologyKernel, new Point(-1, -1), 2, BORDER_REPLICATE);

        //找边界
        List<MatOfPoint> contours=new ArrayList<>();
        Imgproc.findContours(segmentation,contours,new Mat(), RETR_EXTERNAL, CHAIN_APPROX_SIMPLE, new Point(0, 0));
        MatOfPoint temp_contour=new MatOfPoint();
        MatOfPoint2f approxCurve=new MatOfPoint2f();
        double maxArea=-1;
        int maxAreaIdx=-1;
        Rect boundRect ;
        Mat mRgba2=new Mat();
        for (int index = 0; index < contours.size(); index++)
        {
            temp_contour=contours.get(index);
            double contourarea=Imgproc.contourArea(temp_contour);
            MatOfPoint2f contourLength=new MatOfPoint2f(contours.get(index).toArray());
            if (contourarea>maxArea) {
                MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
                int contourSize = (int) temp_contour.total();//计算逼近用
                MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
                Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize * 0.05, true);//对图像轮廓点进行多边形拟合
                Log.d("rgb","筛选前："+contourarea+"");
                if (approxCurve_temp.total()>=4&&contourarea>350){//判断面积最大的区域是否为四边形
                    maxArea=contourarea;
                    if (Imgproc.arcLength(contourLength, true) / (2 * Math.sqrt((contourarea / 3.14))) < 3.8) {
                        approxCurve=approxCurve_temp;
                        Log.d("rgb","筛选后："+contourarea+"");

                        List<Point> pointList = approxCurve.toList();
                        int i =approxCurve.rows();
                        for(int j=1;j<i;j++)
                        {
                            if (x_min==0&&y_min==0)
                            {x_min=pointList.get(j).x;
                                y_min=pointList.get(j).y;}
                            else{
                                if (x_min>pointList.get(j).x) {
                                    x_min=pointList.get(j).x;
                                }
                                if (y_min>pointList.get(j).y) {
                                    y_min=pointList.get(j).y;
                                }

                                if(x_max<pointList.get(j).x) {
                                    x_max=pointList.get(j).x;
                                }

                                if(y_max<pointList.get(j).y) {
                                    y_max=pointList.get(j).y;
                                }
                            }

                        }
                        CarShape.ret_x=(int)x_min;
                        CarShape.ret_y=(int)y_min;
                        CarShape.dis_x=(int)Math.abs(x_max-x_min);
                        CarShape.dis_y=(int)Math.abs(y_max-y_min);
                        Log.d("rgb","坐标："+ "x:"+CarShape.ret_x+"y:"+ CarShape.ret_y+ "disx:"+dis_x+"disy:"+ dis_y);
                    }
                }
            }

        }
        image1.release();        image2.release();        image3.release();
        image1_gary.release();      image2_gary.release();        image3_gary.release();
        absFrameDifferece.release();absFrameDifferece2.release();absFrameDifferece3.release();
        morphologyKernel.release();
        segmentation.release(); mRgba2.release();
        previousGrayFrame.release();
    }


    public int HoughCircles_num=0;
    public int[] HoughCircles(Bitmap bitmap_op){
        Mat src = new Mat();
        Mat mRgb = new Mat();
        Utils.bitmapToMat(bitmap_op, src);
        Mat gray=new Mat();
        //灰度化
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(src, mRgb, Imgproc.COLOR_BGR2RGB);
        //高斯模糊
        Imgproc.GaussianBlur(gray,gray,new Size(5,5),0);
        Mat circles = new Mat();
        int w=src.width();
        int h=src.height();
        int pointx_min=0;
        int pointy_min=0;
        int pointx_max=0;
        int pointy_max=0;
        int r=0;
        int circle_num=0;
        //霍夫圆形检测
        Imgproc.HoughCircles(gray,circles,Imgproc.HOUGH_GRADIENT,1,50,80,30,10,30);
        for (int i=0;i<circles.cols();i++){
            float[] info =new float[3];
            circles.get(0,i,info);
            //画出圆形轮廓
            if ((int)info[1]<h/2){//圆形筛选，只筛选图片上半部分的圆
                Imgproc.circle(mRgb,new Point((int)info[0],(int)info[1]),(int)info[2],new Scalar(0,255,0),2,8,0);
                HoughCircles_num++;//统计筛选的圆形的个数
                r=Math.max(r,(int)info[2]);
                if (x_min==0&&y_min==0)
                {    x_min=(int)info[0];
                    y_min=(int)info[1];}
                else{
                    if (x_min>(int)info[0]) {
                        x_min=(int)info[0];
                    }
                    if (y_min>(int)info[1]) {
                        y_min=(int)info[1];
                    }

                    if(x_max<(int)info[0]) {
                        x_max=(int)info[0];
                    }

                    if(y_max<(int)info[1]) {
                        y_max=(int)info[1];
                    }
                }
            }
        }
        //获取采取二的坐标点
        int cut_x=(int) (x_min-1.5*r);
        int cut_y=(int) (y_min-1.5*r);
        int cut_dis_x=(int)(x_max-x_min+3*r);
        int cut_dis_y=(int)3*r;
        circles.release();
        gray.release();
        mRgb.release();
        int Point[] =new int[4];
        Point[0]=cut_x;
        Point[1]=cut_y;
        Point[2]=cut_dis_x;
        Point[3]=cut_dis_y;
        return Point;
    }

    public Bitmap cutRgbPic(Bitmap bitmap_op, int retx, int rety, int x, int y) {
        Bitmap bitmap = bitmap_op.copy(ARGB_8888, true);
        Bitmap rebitmap;
        Mat dstmat, srcmat, mat;
        mat = new Mat();
        srcmat = new Mat();
        Utils.bitmapToMat(bitmap_op, mat);  //通过org.opencv.android.Utils来实现Bitmap和Mat的相互转换
        Rect rect = new Rect(retx, rety, x, y);//裁切尺寸
        Mat mat_cut = new Mat();
//      Bitmap bitmap2 = image_show.setImageBitmap(bitmap);
//        if (bitmap_op == null) {
//            dstmat = new Mat(srcmat, rect);
//            //转换为RGB
//            Imgproc.cvtColor(dstmat, dstmat, Imgproc.COLOR_BGR2RGB);
//        } else {
            Utils.bitmapToMat(bitmap, mat_cut);
            dstmat = new Mat(mat_cut, rect);
//        }
        rebitmap= Bitmap.createBitmap(dstmat.width(), dstmat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dstmat, rebitmap);//mat转化为bitmap
        dstmat.release();
        mat_cut.release();
        srcmat.release();
        mat.release();
        return rebitmap;
    }

//    private Resources getResources() {
//        // TODO Auto-generated method stub
//        Resources mResources = null;
//        mResources = getResources();
//        return mResources;
//    }
    double target;
    double g,g0,g1,g2,g3,g4;
    double r,r0,r1,r2,r3,r4;
    double y,y0,y1,y2,y3,y4;
    public String trffictResult="red";
    public void trfficLight(Bitmap src){
        Bitmap light=src;
        Mat light1=new Mat();
        Utils.bitmapToMat(light, light1);
        //compareHist(light1,RightAutoFragment.matgreen0);       g0=target;//相似度检测
        compareHist(light1,matgreen1);       g1=target;//相似度检测
        compareHist(light1,matgreen2);       g2=target;
        //compareHist(light1,matgreen3);       g3=target;
        //g=Math.max(g0,g1);
        g=Math.max(g1,g2);
       // g=Math.max(g,g3);
        //compareHist(light1,RightAutoFragment.matred0);       r0=target;//相似度检测
        compareHist(light1,matred1);       r1=target;
        compareHist(light1,matred2);       r2=target;
       // compareHist(light1,matred3);       r3=target;
       // compareHist(light1,matred4);       r4=target;
        //r=Math.max(r0,r1);
        r=Math.max(r1,r2);
       // r=Math.max(r,r3);
       // r=Math.max(r,r4);
        //compareHist(light1,RightAutoFragment.matyellow0);       y0=target;
        compareHist(light1,matyellow1);       y1=target;
        compareHist(light1,matyellow2);       y2=target;
        //compareHist(light1,matyellow3);       y3=target;
       // compareHist(light1,matyellow4);       y4=target;
       // y=Math.max(y0,y1);
        y=Math.max(y1,y2);
       // y=Math.max(y,y3);
        //y=Math.max(y,y4);

        if(r>g&&r>y){
            trffictResult="red";
        }
        if (g>r&&g>y){
            trffictResult="green";
        }
        if (y>g&&y>r){
            trffictResult="yellow";
        }
    }

    private void compareHist(Mat scrMat,Mat desMat){
        if ((scrMat.rows() != desMat.rows()) || (scrMat.cols() != desMat.cols()))
        {
            if (scrMat.rows() > desMat.rows())
            {
                resize(scrMat, scrMat, desMat.size(), 0, 0, INTER_LINEAR);
            }
            else if (scrMat.rows() < desMat.rows())
            {
                resize(desMat, desMat, scrMat.size(), 0, 0, INTER_LINEAR);
            }
        }
        scrMat.convertTo(scrMat,CvType.CV_32F);
        desMat.convertTo(desMat,CvType.CV_32F);
        target = Imgproc.compareHist(scrMat,desMat,Imgproc.CV_COMP_CORREL);
        Log.d("rgb","相似度为："+target);
    }
}




