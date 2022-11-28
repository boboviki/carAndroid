package car.bkrc.com.car2021.Utils.CarShape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import car.bkrc.com.car2021.R;

/**
 * 重构 OpenCV Utils
 *
 * @author PiaoZhenJia
 *
 */
public class OpenCvUtils {
    private static final String tag = "CVUT2";

    /**
     * 得到图形中心点
     */
    public Point getCenterPoint(Point[] points) {
        double centerX = 0.0;
        double centerY = 0.0;
        for (Point point : points) {
            centerX += point.x;
            centerY += point.y;
        }
        Point result = new Point();
        result.x = (int) (centerX / points.length);
        result.y = (int) (centerY / points.length);
        return result;
    }

    /**
     * 获得角点 传入单调且不能是二值化的图像
     */
    public Point[] findp(Mat mat) {
        final int maxCorners = 50, blockSize = 3;
        final double qualityLevel = 0.01, minDistance = 20.0, k = 0.04;
        final boolean userHarrisDetector = false;
        MatOfPoint corners = new MatOfPoint();// 省略了转化成灰度图像
        this.toGrayMat(mat);
        // 计算角点
        // image：8位或32位浮点型输入图像，单通道
        // corners：保存检测出的角点
        // maxCorners：角点数目最大值，如果实际检测的角点超过此值，则只返回前maxCorners个强角点
        // qualityLevel：角点的品质因子---------重要
        // minDistance：对于初选出的角点而言，如果在其周围minDistance范围内存在其他更强角点，则将此角点删除
        // mask：指定感兴趣区，如不需在整幅图上寻找角点，则用此参数指定ROI。也可以new Mat()来代替，这样就是没有mask.
        // blockSize：计算协方差矩阵时的窗口大小
        // useHarrisDetector：指示是否使用Harris角点检测，如不指定，则计算shi-tomasi角点
        // harrisK：Harris角点检测需要的k值
        Imgproc.goodFeaturesToTrack(mat, corners, maxCorners, qualityLevel,
                minDistance, new Mat(), blockSize, userHarrisDetector, k);
        Point[] pCorners = corners.toArray();
        return pCorners;
    }

    /**
     * 画个圈圈祝福你
     */
    public void drawCircleByPoint(Mat mat, Point point) {
        Imgproc.circle(mat, point, 2, new Scalar(255, 255, 0), 1);
    }

    /**
     * 横向填充杂色
     */
    public void coverBackGroundToBlack(Mat mat) {
        final double blackPixle[] = { 0.0 };
        for (int y = 0; y < mat.height(); y++) {
            for (int x = 0; x < mat.width(); x++) {
                double pixle[] = mat.get(y, x);
                if (pixle[0] == 255.0) {// 如果是白色
                    mat.put(y, x, blackPixle);
                } else {// 遇到黑色
                    break;
                }
            }
            for (int x = mat.width() - 1; x > 0; x--) {
                double pixle[] = mat.get(y, x);
                if (pixle[0] == 255.0) {// 如果是白色
                    mat.put(y, x, blackPixle);
                } else {// 遇到黑色
                    break;
                }
            }
        }
        Log.d(tag, "背景涂黑完成");
    }

    /**
     * 从Bitmap得到Mat
     */
    public Mat bitmapToMat(Bitmap bitmap) {
        Bitmap bit = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        Mat src = new Mat(bit.getHeight(), bit.getWidth(), CvType.CV_8UC(3));
        Utils.bitmapToMat(bit, src);
        Log.d(tag, "Bitmap转换Mat完成");
        return src;
    }

    /**
     * 膨胀处理 参数 3,3,1
     */
    public void toDilate(Mat mat, int i, int j, int iterations) {
        Imgproc.dilate(mat, mat, Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT, new Size(i, j)), new Point(-1, -1),
                iterations);
        Log.d(tag, "膨胀完成");
    }

    /**
     * 腐蚀处理 参数3,3,1
     */
    public void toErode(Mat mat, int i, int j, int iterations) {
        Imgproc.erode(mat, mat, Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT, new Size(i, j)), new Point(-1, -1),
                iterations);
        Log.d(tag, "腐蚀完成");
    }

    /**
     * 复制Mat对象
     */
    public Mat cloneMat(Mat mat) {
        return mat.clone();
    }

    /**
     * 生成纯色Mat对象
     */
    public Mat makeBGRMat(int b, int g, int r) {
        return new Mat(360, 640, CvType.CV_8UC3, new Scalar(b, g, r));
    }

    /**
     * 查找轮廓并返回轮廓数组 最好传入阈值图
     */
    public List<MatOfPoint> findContoursList(Mat mat) {
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE);
        Log.d(tag, "找到轮廓" + contours.size() + "个");
        return contours;
    }

    /**
     * 画出轮廓 根据轮廓数组在一张图上画 需指定数组下标
     */
    public void drawContoursToMat(Mat mat, List<MatOfPoint> contours,
                                  int index, int b, int g, int r, int size) {
        Imgproc.drawContours(mat, contours, index, new Scalar(b, g, r, 0), size);
        Log.v(tag, "绘制第" + index + "个轮廓");
    }

    /**
     * 二值化图片 参数i,j参考150,255
     */
    public void toBinaryMat(Mat from, int i, int j) {
        Imgproc.threshold(from, from, i, j, Imgproc.THRESH_BINARY);
        Log.d(tag, "二值化完成");
    }

    /**
     * 灰度化图像
     */
    public void toGrayMat(Mat from) {
        Imgproc.cvtColor(from, from, Imgproc.COLOR_BGR2GRAY);
        Log.d(tag, "灰度化完成");
    }

    /**
     * 图像颜色反转
     */
    public void toReverseColorMat(Mat from) {
        Core.bitwise_not(from, from);
        Log.d(tag, "颜色反转完成");
    }

    /**
     * 模糊图像，参数i,j,k参考5,5,0
     */
    public void toGaussUnClearMat(Mat from, int i, int j, int k) {
        Imgproc.GaussianBlur(from, from, new Size(i, j), k);
        Log.d(tag, "高斯模糊完成");
    }

    /**
     * 在图上写字
     */
    public void printWordsOnMat(Mat mat, Point p, String text) {
        p.x = p.x - 100;
        Imgproc.putText(mat, text, p, 18, 0.5, new Scalar(255, 255, 255), 1);
    }

    /**
     * 合并直线上的点[汇总]
     *
     * @param points
     * @return
     */
    public Point[] checkPoint(Point[] points) {
        int lastLength = -1;
        int thisLength = 0;
        Point[] lp = points;
        Point[] np;
        while (true) {
            np = checkPointOnce(lp);
            thisLength = np.length;
            if (thisLength == lastLength) {
                break;
            }
            lastLength = thisLength;
            lp = np;
        }
        Log.d(tag, "数组变化:" + points.length + " -> " + np.length);
        return np;
    }

    /**
     * 合并直线上的点[分步]
     */
    private Point[] checkPointOnce(Point[] points) {
        int length = points.length;
        boolean flag = false;// 是否找到可删除点
        if (length < 4) {
            return points;// 如果小于四个点 免了判断
        }
        label: for (int i = 0; i < length; i++) {// 得到点1
            for (int j = 0; j < length; j++) {// 得到点2
                if (j == i) {
                    continue;
                }
                for (int k = 0; k < length; k++) {// 得到点3
                    if (k == j || k == i) {
                        continue;
                    }
                    // int slope = 0;//斜率
                    double d1 = twoPointsAngel(points[i], points[j]);// i,j直线角度
                    double d2 = twoPointsAngel(points[i], points[k]);// i,k直线角度
                    double angelMin = d1 - d2;
                    if (Math.abs(angelMin) < 10) {// 如果倾角非常接近，删除中间的点
                        int needDelete = deleteMiddlePointToNull(points[i],
                                points[j], points[k]);
                        if (needDelete == 1) {
                            points[i] = null;
                        } else if (needDelete == 2) {
                            points[j] = null;
                        } else if (needDelete == 3) {
                            points[k] = null;
                        }
                        flag = true;
                        break label;
                    }
                }
            }
        }
        if (flag) {
            Point[] newPoints = new Point[length - 1];
            int index = 0;
            for (Point p : points) {// 准备一个没有空值的新数组
                if (null != p) {
                    newPoints[index] = p;
                    index++;
                }
            }
            return newPoints;
        } else {
            return points;
        }
    }

    /**
     * 删除三点中处于中间的点
     */
    private int deleteMiddlePointToNull(Point p1, Point p2, Point p3) {
        double a = p1.x + p1.y;
        double b = p2.x + p2.y;
        double c = p3.x + p3.y;
        if ((a > b && b > c) || (a < b && b < c)) {// b在中间
            return 2;
        } else if ((c > a && a > b) || (c < a && a < b)) {// a在中间
            return 1;
        } else {
            return 3;
        }
    }

    /**
     * 通过描边点得出形状
     */
    public String findShape(Point[] checkedPoints) {
        int length = checkedPoints.length;
        if (length < 3) {
            return "ShapeError";
        } else if (length == 3) {
            return "SanJiao";
        } else if (length == 5) {
            return "WuJiao";
        } else if (length > 5) {
            return "YuanXing";
        } else if (length == 4) {// 四边形
            double d1 = twoPointsDistance(checkedPoints[0], checkedPoints[1]);
            double d2 = twoPointsDistance(checkedPoints[0], checkedPoints[2]);
            double d3 = twoPointsDistance(checkedPoints[0], checkedPoints[3]);
            Point[] p = new Point[2];
            // 找与第一个点相邻的两个点(舍弃最远的那个点)
            if (d1 > d2 && d1 > d3) {// d1最大，舍弃下标1
                p[0] = checkedPoints[2];
                p[1] = checkedPoints[3];
            } else if (d2 > d1 && d2 > d3) {// d2最大，舍弃下标2
                p[0] = checkedPoints[1];
                p[1] = checkedPoints[3];
            } else {
                p[0] = checkedPoints[1];
                p[1] = checkedPoints[2];
            }// 现在数组p中是两个最近的点
            double angelL1 = twoPointsAngel(checkedPoints[0], p[0]);
            double angelL2 = twoPointsAngel(checkedPoints[0], p[1]);
            double angelP = Math.abs(angelL1 - angelL2);
            Log.d(tag, String.format("四边形某顶点角度为%.2f", angelP));
            if (angelP > 80 && angelP < 100) {// 直角
                double dis1 = twoPointsDistance(checkedPoints[0], p[0]);
                double dis2 = twoPointsDistance(checkedPoints[0], p[1]);
                double distanceRatio = dis1 / dis2;
                Log.d(tag, String.format("四边形临边长度差距比为%.2f", distanceRatio));
                if (distanceRatio > 0.80 && distanceRatio < 1.20) {
                    return "ZhengFang";
                } else {
                    return "ChangFang";
                }
            } else {
                return "LingXing";
            }
        } else {
            return "ShapeError";
        }
    }

    /**
     * 求两点之间距离
     */
    private double twoPointsDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * 求两点所在直线水平夹角
     */
    private double twoPointsAngel(Point p1, Point p2) {
        if (p1.y == p2.y) {
            p1.y += 0.01;
        }
        return Math.toDegrees(Math.atan((p1.x - p2.x) / (p1.y - p2.y)));
    }

    /**
     * 将Mat对象保存到文件系统
     */
    public void saveMatAsPngFile(Mat mat, String filename) {
        Log.d(tag, "储存Mat");
        try {
            Imgcodecs.imwrite(Environment.getExternalStorageDirectory()
                    + "/CV/" + filename + ".png", mat);
            Log.i(tag, "Mat存储完成");
        } catch (Exception e) {
            Log.e(tag, "Mat存储出错");
        }
    }

    /**
     * 将Mat对象保存到文件系统
     */
    public void saveMatAsPngFileAndTimestamp(Mat mat, String filename) {
        Log.d(tag, "储存Mat");
        try {
            Imgcodecs.imwrite(Environment.getExternalStorageDirectory()
                            + "/CV/" + filename + System.currentTimeMillis() + ".jpg",
                    mat);
            Log.i(tag, "Mat存储完成");
        } catch (Exception e) {
            Log.e(tag, "Mat存储出错");
        }
    }

    //	private static final int[] RED = { 255, 0, 0 };
//	private static final int[] GREEN = { 0, 255, 0 };
//	private static final int[] BLUE = { 0, 0, 255 };
//	private static final int[] YELLOW = { 255, 255, 0 };
//	private static final int[] PIN = { 255, 0, 255 };
//	private static final int[] QING = { 0, 255, 255 };
//	private static final int[] BLACK = { 0, 0, 0 };
//	private static final int[] WHITE = { 255, 255, 255 };
//
    private static final int[] RED = { 255, 0, 0 };
    private static final int[] GREEN = { 0, 255, 0 };
    private static final int[] BLUE = { 0, 0, 255 };
    private static final int[] YELLOW = { 255, 255, 0 };
    private static final int[] PIN = { 255, 0, 255 };
    private static final int[] QING = { 0, 255, 255 };
    private static final int[] BLACK = { 0, 0, 0 };
    private static final int[] WHITE = { 255, 255, 255 };
    private static ArrayList<int[]> COLORS = new ArrayList<>();

    static {
        COLORS.add(RED);
        COLORS.add(GREEN);
        COLORS.add(BLUE);
        COLORS.add(YELLOW);
        COLORS.add(PIN);
        COLORS.add(QING);
        COLORS.add(BLACK);
        COLORS.add(WHITE);
    }

    /**
     * 确定形状的颜色
     */
    public String findColor(Mat colorfulMat, Point[] checkedPoints) {
        Point centerPoint = getCenterPoint(checkedPoints);
        double[] colorBGR = colorfulMat.get((int) centerPoint.y,
                (int) centerPoint.x);
        double maxlightR = 0;// 确定亮度增大系数
        double maxlightG = 0;
        double maxlightB = 0;
        for (int y = 0; y < colorfulMat.height(); y++) {// 遍历图片 找到最亮的点做参考
            for (int x = 0; x < colorfulMat.width(); x++) {
                double pixle[] = colorfulMat.get(y, x);
                if (pixle[0] > maxlightB)
                    maxlightB = pixle[0];
                if (pixle[1] > maxlightG)
                    maxlightG = pixle[1];
                if (pixle[2] > maxlightR)
                    maxlightR = pixle[2];
            }
        }// 用于去除亮度和色调影响
        maxlightR = 255 / maxlightR;
        maxlightG = 255 / maxlightG;
        maxlightB = 255 / maxlightB;
        int[] colorRGB = { (int) (colorBGR[2] * maxlightR),
                (int) (colorBGR[1] * maxlightG),
                (int) (colorBGR[0] * maxlightB) };
        int[] missNmuber = new int[8];
        for (int i = 0; i < 8; i++) {
            missNmuber[i] = colorMiss(colorRGB, COLORS.get(i));
        }
        int minIndex = 0;
        int minNumb = 999;
        for (int i = 0; i < missNmuber.length; i++) {
            if (missNmuber[i] < minNumb) {
                minNumb = missNmuber[i];
                minIndex = i;
            }
        }
        Log.d(tag, String.format(
                "R:%d, G:%d, B:%d, light:[%.2f * %.2f * %.2f]  =>  ",
                colorRGB[0], colorRGB[1], colorRGB[2], maxlightR, maxlightG,
                maxlightB));
        switch (minIndex) {
            case 0:
                return "Hong";
            case 1:
                return "Lv";
            case 2:
                return "Lan";
            case 3:
                return "Huang";
            case 4:
                return "Pin";
            case 5:
                return "Qing";
            case 6:
                return "Hei";
            case 7:
                return "Bai";
            default:
                return "ColorError";
        }
    }

    /**
     * 计算颜色差距
     */
    private int colorMiss(int[] c1, int[] c2) {
        return Math.abs(c1[0] - c2[2]) + Math.abs(c1[1] - c2[1])
                + Math.abs(c1[2] - c2[0]);
    }

    /**
     * 从文件系统图片读取Mat对象
     */
    public Mat getImageAsFile(String filename) {// FIXME
        Log.d(tag, "读取Mat");
        Mat image = Imgcodecs.imread(Environment.getExternalStorageDirectory()
                + "/CV/" + filename);
        Log.i(tag, "Mat读取完成");
        return image;
    }

    /***函数调用示例****/
    public static String sharp_result;
    private Bitmap bitmap_shape;
    private Bitmap bitmap_shape_rec;
    public void colorAndShape() {// XXX opencv图形识别
        bitmap_shape_rec=null;
//        if(bitmap_shape == null)
//        {
//            bitmap_shape= BitmapFactory.decodeResource(getResources(), R.drawable.sharp01);
//        }
//        image_show.setImageBitmap(bitmap_shape);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            OpenCvUtils cvut = new OpenCvUtils();
            ArrayList<ColorShapeInfo> resultList = new ArrayList<>();
            int erzhi = 130;

            @Override
            public void run() {//说明：主要的调用在这里哈，线程那块可以忽略不计
                Log.d("cmd", "in to cv !!");
                // new FileService().savePhoto(bitmap, "CV/doit.png");
                // Mat image = cvut.getImageAsFile("CV/doit.png");
                Mat image = cvut.bitmapToMat(bitmap_shape);//图片格式转换
                Mat m1 = cvut.cloneMat(image);//复制mat对象
                Mat declarMat = cvut.cloneMat(image);//复制mat对象
                cvut.toReverseColorMat(image);// 反色
                cvut.toGaussUnClearMat(declarMat, 9, 9, 9);// 模糊图像，用于推测颜色
                Mat m2 = cvut.cloneMat(declarMat);//复制高斯模糊后的mat
                Mat srcWrite = cvut.cloneMat(declarMat);
                cvut.toDilate(image, 1, 1, 1);// 膨胀
                cvut.toGrayMat(image);// 灰度化
                Mat binary = cvut.cloneMat(image);//复制反色、膨胀、灰度化后的图像
                cvut.toBinaryMat(binary, erzhi, 255);// 二值化
                cvut.coverBackGroundToBlack(binary);// 背景变黑

//                bitmap_shape_rec = Bitmap.createBitmap(binary.width(), binary.height(), Bitmap.Config.ARGB_8888);
//                Utils.matToBitmap(binary, bitmap_shape_rec);//mat转化为bitmap
//                Message msg = new Message();
//                msg.what = 10;
//                CSHandler.sendMessage(msg);//调用handler显示，避免闪退

                cvut.saveMatAsPngFileAndTimestamp(binary, "binary");// SAVE
                List<MatOfPoint> contoursList = cvut.findContoursList(binary);// 找到轮廓
                for (int i = 0; i < contoursList.size()
                        && contoursList.size() <= 9; i++) {// 处理单个轮廓
                    Mat mat = cvut.makeBGRMat(0, 0, 0);// 黑色背景
                    cvut.drawContoursToMat(mat, contoursList, i, 0, 255, 0, 1);// 白色描边
                    Point[] points = cvut.findp(mat);// 找到描边点
                    Point[] checkedPoints = cvut.checkPoint(points);// 清除同一条直线上的点
                    //ColorShapeInfo csi = ;
                    //String shape = cvut.findShape(checkedPoints);// 确定形状
                    //String color = cvut.findColor(declarMat, checkedPoints);// 确定颜色
                    resultList.add(new ColorShapeInfo(cvut.findColor(declarMat, checkedPoints), cvut.findShape(checkedPoints)));
                    for (int j = 0; j < checkedPoints.length; j++) {
                        cvut.drawCircleByPoint(mat, checkedPoints[j]);
                    }

                }
                if (resultList.size() != 0) {
                    Log.d("cmd", Arrays.toString(resultList.toArray()));

                    cvut.saveMatAsPngFileAndTimestamp(m2, "declar");// SAVE
                    cvut.saveMatAsPngFileAndTimestamp(m1, "image");// SAVE
                    timer.cancel();
                    this.cancel();
                } else {
                    resultList.clear();
                    if (erzhi <= 180) {
                        erzhi += 5;
                    } else {
                        erzhi = 150;
                    }
                }
            }
        }, 0, 200);
    }







}

