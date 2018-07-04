import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class Wrapper {
    public static void main(String[] args) {
        if (args.length == 0) {
            //read in the num_test file
            String pathname = "./num_tests.txt";
            File filename = new File(pathname);
            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader(
                        new FileInputStream(filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int num = Integer.valueOf(line);


            for (int order = 1; order <= num; order++) {
                Dispatcher dispatcher = new Dispatcher(order);
                try {
                    /* 读入TXT文件 */
                    pathname = "./mode_" + order + ".txt";
                    filename = new File(pathname);
                    reader = new InputStreamReader(
                            new FileInputStream(filename));
                    br = new BufferedReader(reader);
                    line = "";
                    line = br.readLine();
                    String mode = new String(line);
                    //random mode
                    if (line.equals("random")) {
                        pathname = "./para_" + order + ".txt";
                        filename = new File(pathname);
                        reader = new InputStreamReader(
                                new FileInputStream(filename));
                        br = new BufferedReader(reader);
                        line = "";
                        line = br.readLine();
                        int m = Integer.valueOf(line);
                        line = br.readLine();
                        float setup_time = Float.valueOf(line);
                        line = br.readLine();
                        float delayedoff_time = Float.valueOf(line);
                        line = br.readLine();
                        float time_end = Float.valueOf(line);

                        pathname = "./arrival_" + order + ".txt";
                        filename = new File(pathname);
                        reader = new InputStreamReader(
                                new FileInputStream(filename));
                        br = new BufferedReader(reader);
                        line = "";
                        line = br.readLine();
                        String arrival = line;


                        pathname = "./service_" + order + ".txt";
                        filename = new File(pathname);
                        reader = new InputStreamReader(
                                new FileInputStream(filename));
                        br = new BufferedReader(reader);
                        line = "";
                        line = br.readLine();
                        String service = line;

                        float mrt = 0, newMrt = 0;
                        int seed1 = 100 + 3 * (int)time_end;
                        int seed2 = 10 + 3 * (int)time_end;
                        int seed3 = 11 + 3 * (int)time_end;
                        int seed4 = 14 + 3 * (int)time_end;
                        ArrayList<Float> arrayArrivalList = new ArrayList<Float>();
                        ArrayList<Float> arrayServiceList = new ArrayList<Float>();


                        double tempArrival, tempService, tempRandom, tempRandom2, tempRandom3, tempRandom4;
                        Random random = new Random(seed1);
                        Random random2 = new Random(seed2);
                        Random random3 = new Random(seed3);
                        Random random4 = new Random(seed4);

                        tempArrival = 0;
                        String newArrival;
                        String newService;
                        while(true) {
                            tempRandom = random.nextFloat();
                            tempService = 0;
                            tempArrival += -(1 / (Float.valueOf(arrival))) * Math.log(1 - tempRandom);
                            tempRandom2 = random.nextFloat();
                            tempService += -(1 / (Float.valueOf(service))) * Math.log(1 - tempRandom2);
                            tempRandom3 = random.nextFloat();
                            tempService += -(1 / (Float.valueOf(service))) * Math.log(1 - tempRandom3);
                            tempRandom4 = random.nextFloat();
                            tempService += -(1 / (Float.valueOf(service))) * Math.log(1 - tempRandom4);
                            if(tempArrival + tempService > time_end){
                                break;
                            }

                            arrayArrivalList.add((float)tempArrival);
                            arrayServiceList.add((float)tempService);
                        }

                        float[] arrayArrival = new float[arrayArrivalList.size()];
                        float[] arrayService = new float[arrayServiceList.size()];
                        int index = 0;
                        for(float tempNum: arrayArrivalList){
                            arrayArrival[index] = tempNum;
                            index++;
                        }

                        index = 0;
                        for(float tempNum: arrayServiceList){
                            arrayService[index] = tempNum;
                            index++;
                        }

                        Arrays.sort(arrayArrival);
                        newArrival = Arrays.toString(arrayArrival);
                        //System.out.println(arrayArrival.length);
                        newService = Arrays.toString(arrayService);
                        int trigger = 0;
                        //System.out.println(newArrival+"    "+newService);
                        File writename = new File("./departure_0.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
                        File writename2 = new File("./mrt_0.txt");
                        if (writename.exists()) {
                            writename.delete();
                            writename2.delete();
                        }
                        //生成ariival和service
                        newMrt = dispatcher.run(mode, newArrival, newService, m, setup_time, delayedoff_time, time_end);

                    } else {
                        //trace mode
                        pathname = "./para_" + order + ".txt";
                        filename = new File(pathname);
                        reader = new InputStreamReader(
                                new FileInputStream(filename));
                        br = new BufferedReader(reader);
                        line = "";
                        line = br.readLine();
                        int m = Integer.valueOf(line);
                        line = br.readLine();
                        float setup_time = Float.valueOf(line);
                        line = br.readLine();
                        float delayedoff_time = Float.valueOf(line);

                        pathname = "./arrival_" + order + ".txt";
                        filename = new File(pathname);
                        reader = new InputStreamReader(
                                new FileInputStream(filename));
                        br = new BufferedReader(reader);
                        line = "";
                        line = br.readLine();
                        String arrival = '[' + line;

                        String pathname2 = "./service_" + order + ".txt";
                        File filename2 = new File(pathname2);
                        InputStreamReader reader2 = new InputStreamReader(
                                new FileInputStream(filename2));
                        BufferedReader br2 = new BufferedReader(reader2);
                        String line2 = "";
                        line2 = br2.readLine();
                        String service = '[' + line2;

                        while (line != null) {
                            line = br.readLine();
                            line2 = br2.readLine();
                            //System.out.println(line+" "+line2);
                            if (line != null) {
                                arrival += ',' + line;
                                service += ',' + line2;
                            }
                        }
                        arrival += ']';
                        service += ']';

                        dispatcher.run(mode, arrival, service, m, setup_time, delayedoff_time, Float.MAX_VALUE);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Dispatcher dispatcher = new Dispatcher(0);
            String mode = args[0];
            String arrival = args[1];
            String service = args[2];
            int m = Integer.valueOf(args[3]);
            float setup_time = Float.valueOf(args[4]);
            float delayedoff_time = Float.valueOf(args[5]);
            if (args[0].equals("trace")) {
                File writename = new File("./departure_0.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
                File writename2 = new File("./mrt_0.txt");
                if (writename.exists()) {
                    writename.delete();
                    writename2.delete();
                }
                dispatcher.run(mode, arrival, service, m, setup_time, delayedoff_time, Float.MAX_VALUE);
            } else {
                //Attributes
                float meanMrt = 0;
                int replication = 15;

                for (int k = 0; k < replication; k++) {
                    System.out.print((k+1)+". ");
                    float mrt = 0, newMrt = 0;
                    int seed1 = 100 + 3 * k;
                    int seed2 = 10 + 3 * k;
                    int seed3 = 11 + 3 * k;
                    int seed4 = 14 + 3 * k;
                    ArrayList<Float> arrayArrivalList = new ArrayList<Float>();
                    ArrayList<Float> arrayServiceList = new ArrayList<Float>();
                    ArrayList<Float> arrayInterArrivalList = new ArrayList<Float>();


                    double tempArrival, tempService, tempRandom, tempRandom2, tempRandom3, tempRandom4;
                    float time_end = Float.valueOf(args[6]);
                    Random random = new Random(seed1);
                    Random random2 = new Random(seed2);
                    Random random3 = new Random(seed3);
                    Random random4 = new Random(seed4);

                    tempArrival = 0;
                    String newArrival;
                    String newService;
                    double interArrival;
                    while(true) {
                        tempRandom = random.nextFloat();
                        tempService = 0;
                        interArrival = -(1 / (Float.valueOf(arrival))) * Math.log(1 - tempRandom);
                        tempArrival += interArrival;
                        tempRandom2 = random.nextFloat();
                        tempService += -(1 / (Float.valueOf(service))) * Math.log(1 - tempRandom2);
                        tempRandom3 = random.nextFloat();
                        tempService += -(1 / (Float.valueOf(service))) * Math.log(1 - tempRandom3);
                        tempRandom4 = random.nextFloat();
                        tempService += -(1 / (Float.valueOf(service))) * Math.log(1 - tempRandom4);
                        if(tempArrival + tempService > time_end){
                            break;
                        }

                        arrayInterArrivalList.add((float)interArrival);
                        arrayArrivalList.add((float)tempArrival);
                        arrayServiceList.add((float)tempService);
                    }

                    float[] arrayInterArrival = new float[arrayInterArrivalList.size()];
                    float[] arrayArrival = new float[arrayArrivalList.size()];
                    float[] arrayService = new float[arrayServiceList.size()];
                    int index = 0;
                    for(float num: arrayArrivalList){
                        arrayArrival[index] = num;
                        index++;
                    }

                    index = 0;
                    for(float num: arrayServiceList){
                        arrayService[index] = num;
                        index++;
                    }

                    index = 0;
                    for(float num: arrayInterArrivalList){
                        arrayInterArrival[index] = num;
                        index++;
                    }

                    //Arrays.sort(arrayInterArrival);
                    File writename = new File("./inter_arrival_time_" + (k+1) + ".txt");
                    if(writename.exists()){
                        writename.delete();
                    }
                    try {
                        writename.createNewFile();
                        FileOutputStream fos = new FileOutputStream(writename,true);
                        for(float num: arrayInterArrival) {
                            fos.write((round(num) + "\r\n").getBytes());
                        }
                        fos.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Arrays.sort(arrayArrival);
                    newArrival = Arrays.toString(arrayArrival);
                    //System.out.println(arrayArrival.length);
                    newService = Arrays.toString(arrayService);
                    int trigger = 0;
                    //System.out.println(newArrival+"    "+newService);
                    writename = new File("./departure_0.txt");
                    File writename2 = new File("./mrt_0.txt");
                    if (writename.exists()) {
                        writename.delete();
                        writename2.delete();
                    }
                    //生成ariival和service
                    dispatcher.setNum(k+1);
                    newMrt = dispatcher.run(mode, newArrival, newService, m, setup_time, delayedoff_time, time_end);
                    meanMrt += newMrt;
                }
//                if (mrt == 0) {
//                    mrt = newMrt;
//                } else if (mrt - newMrt < 2 && trigger == 1) {
//                    System.out.println(delayedoff_time);
//                    mrt = newMrt;
//                } else if (mrt - newMrt > 2 && trigger == 0) {
//                    System.out.println(delayedoff_time);
//                    trigger = 1;
//                    mrt = newMrt;
//                } else {
//                    mrt = newMrt;
//                }
                System.out.println("Replication ends, mean mrt = "+meanMrt/replication);
            }
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

