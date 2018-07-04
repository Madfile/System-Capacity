import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

//重构数组为ArrayList
public class Dispatcher {
    private int transientNumber = 4000;
    private int printProcess = -1; //print timestamp info
    private int num;
    private int serverNum;
    private float setUpTime;
    private float Tc;
    private ArrayList<Job> jobs = new ArrayList<Job>();
    private CopyOnWriteArrayList<Job> queue = new CopyOnWriteArrayList<Job>();
    private float timeEnd;
    private float lambda;
    private float miu;
    private ArrayList<Float> masterClock = new ArrayList<Float>();

    public float getLambda() {
        return lambda;
    }

    public void setLambda(float lambda) {
        this.lambda = lambda;
    }

    public float getMiu() {
        return miu;
    }

    public void setMiu(float miu) {
        this.miu = miu;
    }

    public float getSetUpTime() {
        return setUpTime;
    }

    public void setSetUpTime(float setUpTime) {
        this.setUpTime = setUpTime;
    }

    public float getTc() {
        return Tc;
    }

    public void setTc(float tc) {
        Tc = tc;
    }

    public int getServerNum() {
        return serverNum;
    }

    public void setServerNum(int serverNum) {
        this.serverNum = serverNum;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<Job> jobs) {
        this.jobs = jobs;
    }

    public float getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(float timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Dispatcher(int num) {
        this.num = num;
    }

    public float run(String mode, String arrival, String service, int m, float setup_time, float delayedoff_time, float time_end) {
        //System.out.println(arrival);
        //System.out.println(service);
        File writename = new File("./departure_" + num + ".txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
        File writename2 = new File("./mrt_" + num + ".txt");
        if (writename.exists()) {
            writename.delete();
            writename2.delete();
        }

        this.serverNum = m;
        this.setSetUpTime(setup_time);
        this.setTc(delayedoff_time);
        String arrivalModified = arrival.substring(1, arrival.length() - 1);
        String[] arrivals = arrivalModified.split(",");

        String serviceModified = service.substring(1, service.length() - 1);
        String[] services = serviceModified.split(",");

        jobs = new ArrayList<Job>();
        masterClock = new ArrayList<Float>();
        queue = new CopyOnWriteArrayList<Job>();
        //write service time into files
        File writename3 = new File("./service_time_" + num + ".txt");
        if (writename3.exists()) {
            writename3.delete();
        }
        try {
            writename3.createNewFile();
            FileOutputStream fos = new FileOutputStream(writename3,true);


        for (int i = 0; i < arrivals.length; i++) {
            this.jobs.add(new Job(Float.valueOf(arrivals[i]), Float.valueOf(services[i])));
            fos.write((round(Float.valueOf(services[i])) + "\r\n").getBytes());

            masterClock.add(Float.valueOf(arrivals[i]));
            if (printProcess == 1) {
                System.out.println("masterClock: " + masterClock);
            }
        }
        fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Server[] servers = new Server[serverNum];
        for (int i = 0; i < serverNum; i++) {
            servers[i] = new Server(Tc, setUpTime);
            //System.out.println("server set up");
        }
        //float masterClock = jobs.get(0).getArriveTime();

        while (masterClock.size() != 0) {
            float time = masterClock.remove(0);
            if (time > time_end) {
                break;
            }
            if (queue.size() > 0) {
                if (printProcess == 1) {
                    System.out.println("handling time:" + time);
                    System.out.println("queue size: " + queue.size());
                }
            }
            if (jobs.size() > 0) {
                //get a job into the queue if it is exactly the arrival time
                if (jobs.get(0).getArriveTime() == time) {
                    Job job = jobs.remove(0);
                    queue.add(job);
                }
            }

            float timeStamp;//the return value from server
            //set server's state at first
            for (Server s : servers) {
                //System.out.println(s.getState());
                //if(time == 61.0 & s.getCurrentJob()!= null){
                //System.out.println(s.getCurrentJob().getArriveTime()+" "+s.getCurrentJob().getServiceTime()+" "+s.getJobFinishingTime());
                //}
                timeStamp = s.run(time, num);
                //if there is coming event from servers
                if (timeStamp != -1) {
                    //System.out.println("Error happens!");
                    masterClock.add(timeStamp);
                    Collections.sort(masterClock);
                    if(printProcess == 1){System.out.println("masterClock: " + masterClock);}
                }
            }

            for (Job waitingJob : queue) {
                int processed = 0;
                if (waitingJob.getState() == Job.State.MARKED) {
                    int handled = 0;
                    for (Server s : servers) {
                        if (s.getState() == Server.State.DELAYEDOFF && s.getCurrentJob().getArriveTime() == (waitingJob.getArriveTime())) {
                            queue.remove(waitingJob);
                            s.setJobFinishingTime(time + s.getCurrentJob().getServiceTime());
                            //if(time == 60.0){
                            //System.out.println("job finishes at: "+ s.getJobFinishingTime());}
                            s.setState(Server.State.BUSY);
                            timeStamp = s.getJobFinishingTime();
                            masterClock.add(timeStamp);
                            Collections.sort(masterClock);
                            if(printProcess == 1){System.out.println("masterClock: " + masterClock);}
                            handled = 1;
                            break;
                        }
                    }
                    if (handled == 1) {
                        continue;
                    }
                    for (Server s : servers) {
                        if (s.getState() == Server.State.DELAYEDOFF) {
                            for (Job job : queue) {
                                if (job.getState() == Job.State.UNMARKED) {
                                    for (Server temp : servers) {
//                                        float tempTime = waitingJob.getArriveTime();
//                                        System.out.println("bingo!");
                                        if(temp.getMarkedJob() != null) {
                                            if (temp.getMarkedJob().getArriveTime() == waitingJob.getArriveTime()) {
                                                job.setState(Job.State.MARKED);
                                                temp.setMarkedJob(job);
                                                break;
                                            }
                                        }
                                    }
                                    processed = 1;
                                    break;
                                }
                            }
                            if (processed == 1) {
                                //waitingJob.setState(Job.State.UNMARKED);
                                s.setCurrentJob(waitingJob, time);
                                queue.remove(waitingJob);
                                s.setState(Server.State.BUSY);
                                timeStamp = s.getJobFinishingTime();
                                masterClock.add(timeStamp);
                                Collections.sort(masterClock);
                                if(printProcess == 1){System.out.println("masterClock: " + masterClock);}
                                break;
                            } else {
                                float latestSetUpTime = 0;
                                int shutDownServer = 0;
                                for (int i = 0; i < servers.length; i++) {
                                    if (servers[i].getSetUpFinishTime() > latestSetUpTime) {
                                        latestSetUpTime = servers[i].getSetUpFinishTime();
                                        shutDownServer = i;
                                    }
                                }
                                servers[shutDownServer].shutdown();
                                waitingJob.setState(Job.State.UNMARKED);
                                s.setCurrentJob(waitingJob, time);
                                s.setState(Server.State.BUSY);
                                timeStamp = s.getJobFinishingTime();
                                queue.remove(waitingJob);
                                masterClock.add(timeStamp);
                                Collections.sort(masterClock);
                                if(printProcess == 1){System.out.println("masterClock: " + masterClock);}
                                break;
                            }
                        }
                    }
                    continue;
                }

                if (waitingJob.getState() == Job.State.UNMARKED) {
                    for (Server s : servers) {
                        if (s.getState() == Server.State.DELAYEDOFF) {
                            //应该还要判断Marked和unmarked
                            s.setCurrentJob(waitingJob, time);
                            queue.remove(waitingJob);
                            s.setState(Server.State.BUSY);
                            timeStamp = s.getJobFinishingTime();
                            masterClock.add(timeStamp);
                            Collections.sort(masterClock);
                            if(printProcess == 1){System.out.println("masterClock: " + masterClock);}
                            processed = 1;
                            break;
                        }
                    }
                    if (processed == 0) {
                        for (Server s : servers) {
                            if (s.getState() == Server.State.OFF) {
                                //应该还要判断Marked和unmarked
                                waitingJob.setState(Job.State.MARKED);
                                s.setMarkedJob(waitingJob);
                                //System.out.println(s.getMarkedJob().getState());
                                s.setState(Server.State.SETUP);
                                s.setSetUpFinishTime(time + s.getTs());
                                timeStamp = s.getSetUpFinishTime();
                                masterClock.add(timeStamp);
                                Collections.sort(masterClock);
                                if(printProcess == 1){System.out.println("masterClock: " + masterClock);}
                                processed = 1;
                                break;
                            }
                        }
                    }
                }
            }
        }

        float mrt = outputMrt(num,mode);
        System.out.println(mode + " mode ends, mrt = " + mrt);
        return mrt;
    }


    public float outputMrt(int num, String mode) {
        try {
            int count = 0;
            float responseSum = 0;
            String pathname = "./departure_" + num + ".txt";
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            line = br.readLine();
            int transientCount = 0;
            while (line != null) {
                String[] temp = line.split(" ");
                if(transientCount >= transientNumber && mode.equals("random")) {
                    count += 1;
                    responseSum += (Float.valueOf(temp[1]) - Float.valueOf(temp[0]));
                }
                else{
                    responseSum += (Float.valueOf(temp[1]) - Float.valueOf(temp[0]));
                    count += 1;
                }
                transientCount++;
                line = br.readLine();
            }
            float mrt = 0;
            if(mode.equals("random")) {
                mrt = responseSum / count;
            }
            else{
                mrt = responseSum / count;
            }
            File writename = new File("./mrt_" + num + ".txt"); // 相对路径，如果没有则要建立一个新的txt文件
            writename.createNewFile();
            FileOutputStream fos = new FileOutputStream(writename);
            fos.write(Server.round(mrt).toString().getBytes()); // \r\n即为换行
            fos.close();
            return mrt;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static BigDecimal round(float ft){
        int   scale  =   4;//设置位数
        int   roundingMode  =  4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
        BigDecimal bd  =   new  BigDecimal((double)ft);
        bd   =  bd.setScale(scale,roundingMode);
        //System.out.println(bd.floatValue());
        return bd;
    }
}
