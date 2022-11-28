package car.bkrc.com.car2021.MessageBean;

public class StateChangeBean {

    /**
     * 主从车接收状态切换 为0时接收主车数据，为1时接收从车数据
     */
    private int stateChange;

    public StateChangeBean(int stateChange){
        this.stateChange = stateChange;
    }

    public int getStateChange() {
        return stateChange;
    }

    public void setStateChange(int stateChange) {
        this.stateChange = stateChange;
    }
}
