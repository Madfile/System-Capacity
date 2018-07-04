import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Server {
    public enum State {
        OFF, SETUP, BUSY, DELAYEDOFF
    }

    private float setUpFinishTime;
    private float jobFinishingTime;
    private State state;
    private float timeCounter;
    private float shutDownTime;

    //Time to terminate itself to OFF
    private float Tc;
    //Time to set up
    private float Ts;
    private Job currentJob;
    private Job markedJob;

    public Server(State state, float timeCounter, float tc, float ts) {
        this.state = state;
        this.timeCounter = timeCounter;
        Tc = tc;
        Ts = ts;
        currentJob = null;
        markedJob = null;
    }

    public Server(float tc, float ts) {
        this.state = State.OFF;
        Tc = tc;
        Ts = ts;
    }

    public State getState() {

        return state;
    }

    public void setState(State state) {
        this.state = state;
    }


    public Job getMarkedJob() {
        return markedJob;
    }

    public Server() {
    }

    public void setMarkedJob(Job markedJob) {
        this.markedJob = markedJob;
    }

    public float getTimeCounter() {
        return timeCounter;
    }

    public void setTimeCounter(float timeCounter) {
        this.timeCounter = timeCounter;
    }

    public float getTc() {
        return Tc;
    }

    public void setTc(float tc) {
        Tc = tc;
    }

    public float getTs() {
        return Ts;
    }

    public void setTs(float ts) {
        Ts = ts;
    }

    public Job getCurrentJob() {
        return currentJob;
    }

    public float getSetUpFinishTime() {
        return setUpFinishTime;
    }

    public void setSetUpFinishTime(float setUpFinishTime) {
        this.setUpFinishTime = setUpFinishTime;
    }

    public float getJobFinishingTime() {
        return jobFinishingTime;
    }

    public void setJobFinishingTime(float jobFinishingTime) {
        this.jobFinishingTime = jobFinishingTime;
    }


    public void setCurrentJob(Job currentJob, float time) {
        this.currentJob = currentJob;
        if(currentJob != null) {
            this.setJobFinishingTime(time + currentJob.getServiceTime());
        }
        else{
            this.setJobFinishingTime(-1);
        }
    }

    public float getShutDownTime() {
        return shutDownTime;
    }

    public void setShutDownTime(float shutDownTime) {
        this.shutDownTime = shutDownTime;
    }

    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    public float run(float time, int num) {
        if (this.getState() == State.SETUP) {
            if (time == setUpFinishTime) {
                this.setState(State.DELAYEDOFF);
                this.setCurrentJob(markedJob, time + markedJob.getServiceTime());
                this.setMarkedJob(new Job(-1,-1));
                return -1;//(time + currentJob.getServiceTime());
            }
        }

        if (this.getState() == State.BUSY) {
            if (time == jobFinishingTime) {
                this.setState(State.DELAYEDOFF);
                //write into files
                File writename = new File("./departure_" + num + ".txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
                if (writename.exists()) {
                    try {
                        FileOutputStream fos = new FileOutputStream(writename,true);
                        fos.write((this.round(this.getCurrentJob().getArriveTime())+" "+ this.round(this.jobFinishingTime) +"\r\n").getBytes());
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        writename.createNewFile(); // 创建新文件FileOutputStream fos = new FileOutputStream(writename,true);
                        FileOutputStream fos = new FileOutputStream(writename,true);
                        fos.write((this.round(this.getCurrentJob().getArriveTime())+" "+ this.round(this.jobFinishingTime) +"\r\n").getBytes());
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                this.setShutDownTime(this.getJobFinishingTime() + this.getTc());
                this.setJobFinishingTime(-1);
                this.setCurrentJob(new Job(-1,-1), -1);
                return (time + Tc);
            }
        }

        if (this.getState() == State.OFF) {
            return -1;
        }

        if (this.getState() == State.DELAYEDOFF) {
            if (time == this.getShutDownTime()) {
                this.setShutDownTime(-1);
                this.setState(State.OFF);
                //System.out.println("server Off");
                return -1;
            }
        }

        return -1;
    }

    public void shutdown() {
        this.setState(State.OFF);
        this.setMarkedJob(null);
        this.setSetUpFinishTime(-1);
    }

    public static BigDecimal round(float ft){
        int   scale  =   3;//设置位数
        int   roundingMode  =  4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
        BigDecimal bd  =   new  BigDecimal((double)ft);
        bd   =  bd.setScale(scale,roundingMode);
        //System.out.println(bd.floatValue());
        return bd;
    }


}
