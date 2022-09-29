import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BatchTelnet {

    public static void main(String[] args) throws IOException {

        BatchTelnet bt = new BatchTelnet();
        String filePath = "C:/Users/HC/Desktop/IpPort.txt";
        Map<String,String> ipPorts = bt.fileReader(filePath);

        Map<String,Boolean> results = bt.run(ipPorts);
        for (String key : results.keySet()){
            System.out.println(key + "的连接情况=========>"+results.get(key));
        }
    }

    /**
     * 批量测试telnet
     * @param ipPorts 需要测试的MAP
     * @return 测试结果
     */
    public Map<String,Boolean> run(Map<String,String> ipPorts){

        Socket socket = null;
        //测试结果集合Map
        Map<String,Boolean> ipFlags = new HashMap<>();
        boolean isConnected = false;
        //循环测试所有ip
        for (String key:ipPorts.keySet()) {
            if (key == null || key.equals("")){
                continue;
            }
            String[] keyArray = key.split(",");
            try {
                isConnected = false;
                socket = new Socket();
//                System.out.println("当前IP"+key+"=======当前port"+ipPorts.get(key));
                socket.connect(new InetSocketAddress(keyArray[1], Integer.parseInt(ipPorts.get(key).trim())), 2000); // 建立连接
                isConnected = socket.isConnected(); // 通过现有方法查看连通状态
//                System.out.println(isConnected);
            } catch (IOException e) {
                System.out.println(keyArray[1] + ":" + ipPorts.get(key) + "=======>IO异常");
//                logger
            } catch (Exception e) {
                System.out.println(keyArray[1] + ":" + ipPorts.get(key) + "=======>其他异常");
//                logger
            } finally {
                ipFlags.put(keyArray[1] + ":" + ipPorts.get(key), isConnected);
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ipFlags;
    }

    /**
     * 将文件ip和port读取进map中
     */
    public Map<String,String> fileReader(String path){

        InputStream is = null;
        BufferedReader br;
        Map<String,String> ipMaps = new HashMap<>();
        try {
            is = new FileInputStream(new File(path));
            br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = br.readLine()) != null) {
                //每行必须存在:才可为有效行
                if (line.contains(":")) {
                    String[] splits = line.split(":");
//                    System.out.println(line);
                    //利用random排除ip相同地址
                    ipMaps.put(UUID.randomUUID()+","+splits[0], splits[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            System.out.println("其他异常");
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ipMaps;
    }

}
