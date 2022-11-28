package car.bkrc.com.car2021.Utils.CarShape;

//这其实就是只有两个属性的一个类，自动生成了getset方法和构造器
//我盲写的，如果还跑不通就自己再微调一下哈
public class ColorShapeInfo {
    private String color;
    private String shape;

    public ColorShapeInfo() {
    }

    public ColorShapeInfo(String color, String shape) {
        this.color = color;
        this.shape = shape;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
