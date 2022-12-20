package car.bkrc.com.car2021.Utils.CarShape;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
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

import car.bkrc.com.car2021.FragmentView.LeftFragment;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static car.bkrc.com.car2021.ActivityView.FirstActivity.RightAutoFragment;

import static org.opencv.core.Core.BORDER_REPLICATE;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.INTER_LINEAR;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.resize;

public class CarShape {
    public static LeftFragment left_Fragment;//加载类
    private int colornum=0;
    private static int tri,rect,circle,star,rhomb;
    private static int black_tri, black_rect,black_rhomb,black_star,black_cir;
    private static int white_tri, white_rect,white_rhomb,white_star,white_cir;
    private static int red_tri, red_rect,red_rhomb,red_star,red_cir;
    private static int green_tri, green_rect,green_rhomb,green_star,green_cir;
    private static int blue_tri, blue_rect,blue_rhomb,blue_star,blue_cir;
    private static int yellow_tri, yellow_rect,yellow_rhomb,yellow_star,yellow_cir;
    private static int purple_tri, purple_rect,purple_rhomb,purple_star,purple_cir;
    public static int shapeResultNum=0;
    public static int triResultnum,rectResultnum,circleResultnum,starResultnum,rhombResultnum,squareResultnum;
    public static int black_num,white_num, red_num, green_num,blue_num,yellow_num,purple_num,cyan_num;

    private static int[][] HSV_Color_cloudy = {//阴天荧光灯下
            {0,0,0,180,255,95},//黑色0
            {0,0,220,180,42,255},//白色1
            {156,43,46,180,255,255},//正红色2
            {35,43,46,77,255,255},//绿色3
            {100,208,200,126,255,255},//蓝色4，变化较大
            {26,48,46,34,255,255},//黄色5
            {140,43,46,155,255,255},//紫色6
    };


    public void colorAndShape(Bitmap bitmap){
        int i=3;    int j=3;  int aremin=280;  int aremax=3000; double esp=0.030;//运算核默认3*3，可修改
        Log.d("auto","进入colorandshape函数");
        for(colornum=0; colornum<7; colornum++) {
            // circle= contoursCounts=tri= rect=star=0;
            filterColor(bitmap, 1,HSV_Color_cloudy[ colornum], null);//过滤颜色
            findContours(hsvmat, i, j);   //寻找并绘制轮廓
            recognitionShape(aremin, aremax,esp);//形状识别
            switch (colornum)
            {
                case 0:
                    Log.d("shape","黑色0:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                            " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                    black_tri=tri; black_rect=rect;black_rhomb=rhomb;black_star=star;black_cir=circle;
                    black_num=black_tri+black_rect+black_rhomb+black_star+black_cir;
                    break;
                case 1:
                    Log.d("shape","白色1:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                            " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                    white_tri=tri; white_rect=rect;white_rhomb=rhomb;white_star=star;white_cir=circle;
                    white_num=white_tri+white_rect+white_rhomb+white_star+white_cir;
                    break;
                case 2:
                    Log.d("shape","红色2:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                            " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                    red_tri=tri; red_rect=rect;red_rhomb=rhomb;red_star=star;red_cir=circle;
                    red_num=red_tri+red_rect+red_rhomb+red_star+red_cir;
                    break;
                case 3:
                    Log.d("shape","绿色3:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                            " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                    green_tri=tri; green_rect=rect;green_rhomb=rhomb;green_star=star;green_cir=circle;
                    green_num=green_tri+green_rect+green_rhomb+green_star+green_cir;
                    break;
                case 4:
                    Log.d("shape","蓝色4:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                            " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                    blue_tri=tri; blue_rect=rect;blue_rhomb=rhomb;blue_star=star;blue_cir=circle;
                    blue_num=blue_tri+blue_rect+blue_rhomb+blue_star+blue_cir;
                    break;
                case 5:
                    Log.d("shape","黄色5:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                            " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                    yellow_tri=tri; yellow_rect=rect;yellow_rhomb=rhomb;yellow_star=star;yellow_cir=circle;
                    yellow_num=yellow_tri+yellow_rect+yellow_rhomb+yellow_star+yellow_cir;
                    break;
                case 6:
                    Log.d("shape","紫色6:"+"轮廓："+contoursCounts+"个。圆形:"+circle+"个。三角形："+tri+"个。" +
                            " 矩形："+rect+"个。 五角星："+star +"个。 菱形："+rhomb +"个。");
                    purple_tri=tri; purple_rect=rect;purple_rhomb=rhomb;purple_star=star;purple_cir=circle;
                    purple_num=purple_tri+purple_rect+purple_rhomb+purple_star+purple_cir;
                    break;
                case 7:
                    break;
                case 8:
                    break;
                case 9:
                    break;
                case 10:

            }
            //结果统计及处理
            triResultnum=black_tri+white_tri+red_tri+green_tri+blue_tri+yellow_tri+purple_tri;
            rectResultnum=black_rect+white_rect+red_rect+green_rect+blue_rect+yellow_rect+purple_rect;
            rhombResultnum=black_rhomb+white_rhomb+red_rhomb+green_rhomb+blue_rhomb+yellow_rhomb+purple_rhomb;
            starResultnum=black_star+white_star+red_star+green_star+blue_star+yellow_star+purple_star;
            circleResultnum=black_cir+white_cir+red_cir+green_cir+blue_cir+yellow_cir+purple_cir;
        }
        contoursCounts=tri+rect+rhomb+star+circle;
        shapeResultNum=triResultnum+rectResultnum+rhombResultnum+starResultnum+circleResultnum;
        Log.d("auto","识别结果："+ shapeResultNum+"个");
    }
    private Bitmap bitmap_shape;
    //private Mat srcmat,dstmat,hsvmat,outma,mat_cut,hsvmat_1,dstmat_2;
    private Bitmap bitmap_opencv ;
    private Bitmap bitmap_opencv_cut;
    public static Bitmap rebitmap_opencv;


    public Handler showPicHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what)
            {
                case 10:
                   // RightAutoFragment.rec_image_show.setImageBitmap(outline_bitmap);//形状颜色识别显示处理后的图像
                    break;

                case 20:
                    RightAutoFragment.image_show.setImageBitmap(bitmap_opencv_cut);
                    break;

            }
        }
    };

    private static Mat processImage(Mat gray){//灰度化-配合轮廓使用
        Mat b=new Mat();
        Imgproc.medianBlur(gray,b,7);
        Mat t=new Mat();
        Imgproc.threshold(b,t,80,255,Imgproc.THRESH_BINARY);

        return t;
    }

    public static Bitmap getBitmap_opencv_cut(Bitmap bitmap){
        Bitmap bitmaptemp = bitmap.copy(ARGB_8888, true);//用这种方法，处理后的图不会影响到bitmap的原图
        Bitmap bitmapcut=bitmap.copy(ARGB_8888, true);//复制源图片
        //以下为自动裁切代码
        threshold(bitmaptemp);//二值化
        //点击按钮后的操作在这里
        Mat src2=new Mat();
        Mat grayMat=new Mat();
        Mat edge=new Mat();
        Utils.bitmapToMat( bitmaptemp, src2);//转mat
        Imgproc.cvtColor(src2,grayMat,Imgproc.COLOR_RGBA2GRAY);//灰度化
        grayMat=processImage(grayMat);//灰度化
        Imgproc.Canny(grayMat,edge,10,200,3,true);//轮廓检测
        getCornersByContour(edge);//绘制轮廓
        Bitmap bitmapCutResult=opencvCutmap(bitmapcut,ret_x,ret_y,dis_x,dis_y);//裁切裁切之后的图片
        src2.release();
        grayMat.release();
        edge.release();
        return bitmapCutResult;
    }



    public static void threshold(Bitmap res){//二值化
        Mat src,dst;
        src = new Mat();
        dst = new Mat();//声明两个新的mat变量
        Utils.bitmapToMat(res, src);
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGRA2GRAY);//灰度化
        Imgproc.threshold(dst,src,144,255,0);
//        全局二值化,public static void adaptiveThreshold(Mat src, Mat dst, double maxValue, int adaptiveMethod, int thresholdType, int blockSize, double C)
//        参数一：src，待二值化的多通道图像，只能是CV_8U和CV_32F两种数据类型
//        参数二：dst，二值化后的图像，与输入图像具有相同的尺寸、类型和通道数
//        参数三：thresh，二值化的阈值
//        参数四：maxval，二值化过程的最大值，此函数只在THRESH_BINARY和THRESH_BINARY_INV两种二值化方法中才使用
//        参数五：type，二值化类型
//        二值化类型THRESH_BINARY = 0,
//                THRESH_BINARY_INV = 1,
//                THRESH_TRUNC = 2,
//                THRESH_TOZERO = 3,
//                THRESH_TOZERO_INV = 4,
//                THRESH_MASK = 7,
//                THRESH_OTSU = 8,
//                THRESH_TRIANGLE = 16;
        Utils.matToBitmap(src, res);//转化完成后，将dst转化为bitmap格式，以便显示
        src.release();//释放mat空间，避免闪退
        dst.release();//释放mat空间，避免闪退
    }

    public static double x_max;
    public static double x_min;
    public static double y_max;
    public static double y_min;
    public static int ret_x,ret_y,dis_x,dis_y;
    public static void getCornersByContour(Mat res){
        Mat src=new Mat();
        src =res;
        List<MatOfPoint> contours=new ArrayList<>();
        //轮廓检测
        Imgproc.findContours(src,contours,new Mat(),Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        double maxArea=-1;
        int maxAreaIdx=-1;
        MatOfPoint temp_contour=contours.get(0);//假设最大的轮廓在index=0处
        MatOfPoint2f approxCurve=new MatOfPoint2f();
        for (int idx=0;idx<contours.size();idx++){
            temp_contour=contours.get(idx);
            double contourarea=Imgproc.contourArea(temp_contour);
            //当前轮廓面积比最大的区域面积
            if (contourarea>maxArea){
                MatOfPoint2f new_mat=new MatOfPoint2f(temp_contour.toArray());
                int contourSize= (int) temp_contour.total();
                MatOfPoint2f approxCurve_temp=new MatOfPoint2f();
                Imgproc.approxPolyDP(new_mat,approxCurve_temp,contourSize*0.05,true);//对图像轮廓点进行多边形拟合
                if (approxCurve_temp.total()>=4){//判断面积最大的区域是否为四边形
                    maxArea=contourarea;
                    maxAreaIdx=idx;
                    approxCurve=approxCurve_temp;
                }
            }
        }

        //把轮廓画出来
        Mat mRgba=new Mat();
        mRgba.create(src.rows(), src.cols(), CvType.CV_8UC3);
        Imgproc.drawContours(mRgba, contours, maxAreaIdx, new Scalar(0,255,0), 5);//只画出了面积最大的区域
        //Bitmap b12=bitmapOpenCv.copy(Bitmap.Config.ARGB_8888, true);
        //Utils.matToBitmap(mRgba,b12);
        //ivRec.setImageBitmap(b12);

        //获取边缘四个顶点的坐标
        List<Point> pointList = approxCurve.toList();
        int i =approxCurve.rows();
        Log.d("cut","顶点数量为"+i+"");
        Log.d("cut","顶点坐标为"+pointList+"");
        x_min=x_max=pointList.get(0).x;
        y_min=y_max=pointList.get(0).y;
        for(int j=1;j<i;j++)
        {
            if(x_max<pointList.get(j).x)
            {
                x_max=pointList.get(j).x;
            }
            if (x_min>pointList.get(j).x)
            {
                x_min=pointList.get(j).x;
            }

            if(y_max<pointList.get(j).y)
            {
                y_max=pointList.get(j).y;
            }
            if (y_min>pointList.get(j).y)
            {
                y_min=pointList.get(j).y;
            }
        }

        ret_x=(int)x_min;
        ret_y=(int)y_min;
        dis_x=(int)Math.abs(x_max-x_min);
        dis_y=(int)Math.abs(y_max-y_min);
//        double[] temp_double=approxCurve.get(0,0);
//        Point point1=new Point(temp_double[0],temp_double[1]);
//
//        temp_double=approxCurve.get(1,0);
//        Point point2=new Point(temp_double[0],temp_double[1]);
//
//        temp_double=approxCurve.get(2,0);
//        Point point3=new Point(temp_double[0],temp_double[1]);
//        temp_double=approxCurve.get(3,0);
//
//        Point point4=new Point(temp_double[0],temp_double[1]);
//
//        List<Point> source=new ArrayList<>();
//        source.add(point1);
//        source.add(point2);
//        source.add(point3);
//        source.add(point4);
        //对4个点进行排序
//        Point centerPoint=new Point(0,0);//质心
//        for (Point corner:source){
//            centerPoint.x+=corner.x;
//            centerPoint.y+=corner.y;
//        }
//        centerPoint.x=centerPoint.x/source.size();
//        centerPoint.y=centerPoint.y/source.size();
//        Point lefttop=new Point();
//        Point righttop=new Point();
//        Point leftbottom=new Point();
//        Point rightbottom=new Point();
//        for (int i=0;i<source.size();i++){
//            if (source.get(i).x<centerPoint.x&&source.get(i).y<centerPoint.y){
//                lefttop=source.get(i);
//            }else if (source.get(i).x>centerPoint.x&&source.get(i).y<centerPoint.y){
//                righttop=source.get(i);
//            }else if (source.get(i).x<centerPoint.x&& source.get(i).y>centerPoint.y){
//                leftbottom=source.get(i);
//            }else if (source.get(i).x>centerPoint.x&&source.get(i).y>centerPoint.y){
//                rightbottom=source.get(i);
//            }
//        }
//        source.clear();
//        source.add(lefttop);
//        source.add(righttop);
//        source.add(leftbottom);
//        source.add(rightbottom);
//        return source;
    }



    public static Bitmap opencvCutmap(Bitmap bitmap_op, int retx, int rety, int x, int y) {
        Bitmap bitmap=bitmap_op.copy(ARGB_8888, true);
        Mat dstmat,srcmat,mat;
        mat = new Mat();
        srcmat =new Mat();
        Utils.bitmapToMat(bitmap_op, mat);  //通过org.opencv.android.Utils来实现Bitmap和Mat的相互转换
        Rect rect = new Rect(retx,rety,x,y);//裁切尺寸
        Mat mat_cut = new Mat();
//                Bitmap bitmap2 = image_show.setImageBitmap(bitmap);
        if(bitmap_op == null){
            dstmat = new Mat(srcmat,rect);
            //转换为RGB
            Imgproc.cvtColor(dstmat,dstmat,Imgproc.COLOR_BGR2RGB);
        }
        else {
            Utils.bitmapToMat(bitmap,mat_cut);
            dstmat = new Mat(mat_cut,rect);
        }
        Bitmap rebitmap=Bitmap.createBitmap(dstmat.width(),dstmat.height(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dstmat, rebitmap);//mat转化为bitmap
       // ivRec.setImageBitmap(rebitmap);
        dstmat.release();
        mat_cut.release();
        srcmat.release();
        mat.release();
        return rebitmap;
    }

    private Bitmap outline_bitmap;
    private Mat dstmat_2,hsvmat;
    private void filterColor(Bitmap cut_bitmap_op,int way,int[] way_1,int[] way_2){  //过滤颜色
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


    private void findContours(Mat src,int i,int j){
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
        rebitmap_opencv=outline_bitmap;//以便图片显示
        outma.release();
        hsvmat.release();
        src.release();
    }

    private List<MatOfPoint> contours;
    private int contoursCounts;


    private void  recognitionShape(int aremin,int aremax,double eps) {
        MatOfPoint2f contour2f;
        MatOfPoint2f approxCurve;
        MatOfPoint2f contourlength;
        double epsilon;
        tri = rect = circle = star=rhomb= 0;
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

                if (approxCurve.rows() == 3) {
                    tri++;
                    Log.d("are","三角形面积为："+Imgproc.contourArea(contours.get(i))+"");
                }
                else if (approxCurve.rows() == 4){
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
                        rect++;
                    else rhomb++;
                    Log.d("are","矩形面积为："+Imgproc.contourArea(contours.get(i))+"");

                }
                else if (approxCurve.rows() > 4) {
                    int mianji1 = rect1.height*rect1.width;
                    double mianji2 = Imgproc.contourArea(contours.get(i));
                    contourlength = new MatOfPoint2f(contours.get(i).toArray());
                    double zhouchang = Imgproc.arcLength(contourlength,true);
                    if((mianji2/mianji1)>0.75&&(zhouchang/(2*Math.sqrt((mianji2/3.14)))<3.8)){
                        circle++;
                        Log.d("are","圆形面积为："+Imgproc.contourArea(contours.get(i))+"");
                    }
                    else{
                        star++;
                        Log.d("shape","五角星算数结果："+zhouchang/(2*Math.sqrt((mianji2/3.14)))+"");
                    }
                    Log.d("are","五角星面积为："+Imgproc.contourArea(contours.get(i))+"");
                }
            }
        }
        dstmat_2.release();
    }


    public static void autoCut(Bitmap bitmap1 ,Bitmap bitmap2) {
        Mat image1=new Mat();
        Mat image2=new Mat();
        Utils.bitmapToMat(bitmap1, image1);
        Utils.bitmapToMat(bitmap2, image2);
        if ((image1.rows() != image2.rows()) || (image1.cols() != image2.cols()))
        {
            if (image1.rows() > image2.rows())
            {
                resize(image1, image1, image2.size(), 0, 0, INTER_LINEAR);
            }
            else if (image1.rows() < image2.rows())
            {
                resize(image2, image2, image1.size(), 0, 0, INTER_LINEAR);
            }
        }

        Mat image1_gary =new Mat();
        Mat image2_gary=new Mat();
        if (image1.channels() != 1)
        {

            cvtColor(image1, image1_gary, COLOR_BGR2GRAY);
        }
        if (image2.channels() != 1)
        {
            cvtColor(image2, image2_gary, COLOR_BGR2GRAY);
        }

        Mat absFrameDifferece=new Mat();
        //Mat previousGrayFrame = image2_gary.clone();
        //图1减图2
        //subtract(image1_gary, image2_gary, frameDifference, new Mat(), CV_16SC1);

        //图1-图2取绝对值
        Core.absdiff(image1_gary,image2_gary, absFrameDifferece);


        //位深的改变
        absFrameDifferece.convertTo(absFrameDifferece, CV_8UC1, 1, 0);
        Mat segmentation =new Mat();

        //阈值处理（这一步很关键，要调好二值化的值）
        Imgproc.threshold(absFrameDifferece, segmentation,40, 255, THRESH_BINARY);

        //中值滤波
        Imgproc.medianBlur(segmentation, segmentation, 3);

        //形态学处理(开闭运算)
        //形态学处理用到的算子
        Mat morphologyKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(5, 5));
        Imgproc.morphologyEx(segmentation, segmentation, MORPH_CLOSE, morphologyKernel, new Point(-1, -1), 2, BORDER_REPLICATE);

        //找边界
        List<MatOfPoint> contours=new ArrayList<>();
        Imgproc.findContours(segmentation,contours,new Mat(),Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        MatOfPoint temp_contour=new MatOfPoint();
        MatOfPoint2f approxCurve=new MatOfPoint2f();
        double maxArea=-1;
        int maxAreaIdx=-1;
        Rect boundRect ;
        Mat mRgba=new Mat();
        for (int index = 0; index < contours.size(); index++)
        {
            temp_contour=contours.get(index);
            double contourarea=Imgproc.contourArea(temp_contour);
            if (contourarea>maxArea) {
                MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
                int contourSize = (int) temp_contour.total();//计算逼近用
                MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
                Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize * 0.05, true);//对图像轮廓点进行多边形拟合
                if (approxCurve_temp.total()>=4){//判断面积最大的区域是否为四边形
                    maxArea=contourarea;
                    maxAreaIdx=index;
                    approxCurve=approxCurve_temp;
                }
            }

            mRgba.create(image1.rows(), image1.cols(), CvType.CV_8UC3);
            Rect rect = Imgproc.boundingRect((Mat)approxCurve);
            //Imgproc.rectangle(mRgba, rect, new Scalar(0, 255, 0), 2);
            Imgproc.drawContours(mRgba, contours, maxAreaIdx, new Scalar(0,255,0), 5);//画出所有不规则的形状

            List<Point> pointList = approxCurve.toList();
            int i =approxCurve.rows();
            Log.d("cut","顶点数量为"+i+"");
            Log.d("cut","顶点坐标为"+pointList+"");
            if(x_min==0||y_min==0)
            {
                x_min=pointList.get(0).x;
                y_min=pointList.get(0).y;
            }

            for(int j=1;j<i;j++)
            {
                if(x_max<pointList.get(j).x)
                {
                    x_max=pointList.get(j).x;
                }
                if (x_min>pointList.get(j).x)
                {
                    x_min=pointList.get(j).x;
                }

                if(y_max<pointList.get(j).y)
                {
                    y_max=pointList.get(j).y;
                }
                if (y_min>pointList.get(j).y)
                {
                    y_min=pointList.get(j).y;
                }
            }

            ret_x=(int)x_min;
            ret_y=(int)y_min;
            dis_x=(int)Math.abs(x_max-x_min);
            dis_y=(int)Math.abs(y_max-y_min);

        }
        image1.release();
        image2.release();
        image1_gary.release();
        image2_gary.release();
    }

}