public class Job {
    public enum State{
        MARKED,UNMARKED
    }

    private State state;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    private float arriveTime;
    private float serviceTime;

    public float getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(float arriveTime) {
        this.arriveTime = arriveTime;
    }

    public float getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(float serviceTime) {
        this.serviceTime = serviceTime;
    }

    public Job(float arriveTime, float serviceTime) {
        this.state = State.UNMARKED;
        this.arriveTime = arriveTime;
        this.serviceTime = serviceTime;
    }

    public Job() {
    }
}
