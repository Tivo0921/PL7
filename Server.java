
//Server.java 5/30
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//スレッド部（各クライアントに応じて）
class ClientProcThread extends Thread {
    private Socket socket;
    private InputStreamReader inputStreamReader;
    // 接続者の情報
    private int myNumber;
    private String myName;
    private String myPass;
    private String myRoom;

    public static String directoryPath;

    private BufferedReader in;
    private PrintWriter out;

    public ClientProcThread(int n, Socket socket) {
        this.socket = socket;
        this.myNumber = n;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void listen() {
        try {
            while (!in.ready()) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread sleep error");
        } catch (IOException e) {
            System.out.println("BufferedReader ready error");
            e.printStackTrace();
        }
    }

    public void run() {
        directoryPath = "C:/Users/shun0/university/PL7";
        try {
            // ログイン画面でログイン受付
            out.println("Hello, client No." + myNumber);
            listen();
            String receivedLogin = in.readLine();
            System.out.println(receivedLogin + "ログイン情報を受け付けた");
            // カンマを基に名前とパスワードに分割
            String[] splitedReceivedLogin = receivedLogin.split(",");
            System.out.println(splitedReceivedLogin[0] + splitedReceivedLogin[1] + "ログインを受け付けた");

            for (int i = 0; i < splitedReceivedLogin.length; i++) {
                System.out.println(splitedReceivedLogin[i]);
            }
            myName = splitedReceivedLogin[0];
            myPass = splitedReceivedLogin[1];
            if (!UserRepository.exist(myName)) {
                UserRepository.addUser(myName, myPass);
            }
            while (true) {// マッチ画面の操作を受け付ける
                String roomId = "";
                listen();
                String receivedMessage = in.readLine();
                if (receivedMessage.equals("View Results")) {
                    out.println(UserRepository.convertListToString(UserRepository.readUser()));
                    out.flush();
                } else if (receivedMessage.equals("search room")) {
                    listen();
                    roomId = in.readLine();
                    System.out.println(roomId);
                    if (RoomRepository.exist(roomId)) {

                        List<String> room = RoomRepository.search(roomId);
                        out.println(room.get(0) + "," + room.get(1)); // ルームIDとUser1の名前を返す
                        out.flush();
                        RoomRepository.addUser2(roomId, myName);
                        Server.SendAll("connected", myName, myNumber);
                    }
                    break;
                } else if (receivedMessage.equals("make a room")) { // roomIdに直した
                    roomId = Server.makeRoom(myName);
                    String connect = "";
                    out.println(roomId);
                    out.flush();
                    listen();
                    connect = in.readLine();
                    if (connect.equals("connect")) {
                        listen();
                        String enemy = RoomRepository.search(roomId).get(2);
                        out.println(enemy);
                        out.flush();
                        break;
                    } else if (connect.equals("delete")) {
                        Server.deleteRoom(roomId);
                        break;
                    }
                } else {
                    break;
                }
            }

            while (true) {// 対戦画面の操作を受け付ける
                listen();
                String str = in.readLine();
                System.out.println("str = " + str);
                // match endを受け取ったら対戦終了
                if (str.equals("match end")) {
                    listen();
                    String receivedResults1 = in.readLine();
                    String[] splitedReceivedResults1 = receivedResults1.split(",");
                    String name1 = splitedReceivedResults1[0];
                    String result1 = splitedReceivedResults1[1];
                    Server.renewRecord(Integer.parseInt(result1), name1);

                    listen();
                    String receivedResults2 = in.readLine();
                    String[] splitReceivedResults2 = receivedResults2.split(",");
                    String name2 = splitReceivedResults2[0];
                    String result2 = splitReceivedResults2[1];
                    Server.renewRecord(Integer.parseInt(result2), name2);

                    Server.deleteRoom(myRoom);
                    break;
                }
                if (str != null) {// このソケット（バッファ）に入力があるかをチェック
                    Server.SendAll(str, myName, myNumber);// サーバに来たメッセージは接続しているクライアント全員に配る
                }
            }
        } catch (Exception e) {
            // ここにプログラムが到達するときは，接続が切れたとき
            e.printStackTrace();
            Server.SetFlag(myNumber, false);// 接続が切れたのでフラグを下げる
        }
    }

    public static void main(String[] args) {
    }

    public void sendMessage(String message) {
        // if(out != null){
        try {
            out.flush();
            out.println(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // }
    }

}

class Server {

    private static int maxConnection = 100;// 最大接続数
    private static Socket[] incoming;// 受付用のソケット
    private static boolean[] flag;// 接続中かどうかのフラグ
    private static InputStreamReader[] isr;// 入力ストリーム用の配列
    private static BufferedReader[] in;// バッファリングをによりテキスト読み込み用の配列
    private static PrintWriter[] out;// 出力ストリーム用の配列
    private static ClientProcThread[] myClientProcThread;// スレッド用の配列
    private static int member;// 接続しているメンバーの数
    public static int roomId = 0;// 0から昇順
    public static int maxRoomId = 3;// 最大のルームID(ルームID数-1)

    public static String directoryPath;

    public static Set<ClientProcThread> myClientProcThreads = ConcurrentHashMap.newKeySet(); // final に直したらいけた

    public static void setDirectoryPath(String path) {
        directoryPath = path;
    }

    public static boolean readUserID1(String myName, String myPass) {
        String fileName = "C:/Users/shun0/university/PL7/User.txt";
        boolean existlogin = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    continue;
                }
                String Name = parts[1];
                String Pass = parts[2];
                if (myName.equals(Name) && myPass.equals(Pass)) {
                    existlogin = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return existlogin;
    }

    public static boolean readUserID2(String myName, String myPass) {
        String fileName = "C:/Users/shun0/university/PL7/User.txt";
        boolean loginFalse = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    continue;
                }
                String Name = parts[1];
                String Pass = parts[2];
                if (myName.equals(Name) && !myPass.equals(Pass)) {
                    loginFalse = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loginFalse;
    }

    public static int countPlayRecord() {
        String fileName = "C:/Users/shun0/university/PL7/User.txt";
        int cnt = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    public static int[][] transferPlayRecord(int id) {
        String fileName = "C:/Users/shun0/university/PL7/User.txt";
        List<int[]> resultsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    continue;
                }
                int uid = Integer.parseInt(parts[0]);
                if (uid == id) {
                    int[] result = new int[3];
                    result[0] = Integer.parseInt(parts[2]);
                    result[1] = Integer.parseInt(parts[3]);
                    result[2] = Integer.parseInt(parts[4]);
                    resultsList.add(result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultsList.toArray(new int[resultsList.size()][]);
    }

    // フラグの設定を行う
    public static void SetFlag(int n, boolean value) {
        flag[n] = value;
    }

    public synchronized static String makeRoom(String user1) {
        String newRoomId = RoomRepository.addRoom();
        RoomRepository.addUser1(newRoomId, user1);
        return newRoomId;
    }

    public synchronized static void deleteRoom(String roomId) {
        RoomRepository.deleteRoom(String.valueOf(roomId));
    }

    public synchronized static void renewRecord(int result, String name) {
        String userName = UserRepository.search(name).get(1);
        UserRepository.updateRecord(result, userName);
    }

    // 全員にメッセージを送る。要改善
    public static void SendAll(String str, String myName, int num) {
        // 送られた来たメッセージを接続している全員に配る
        for (ClientProcThread myClientProcThread : myClientProcThreads) { // int i = 1; i <= num; i++
            myClientProcThread.sendMessage(str + "," + myName + "," + num);
        }
    }

    // mainプログラム
    public static void main(String[] args) {
        myClientProcThread = new ClientProcThread[maxConnection];
        flag = new boolean[maxConnection];
        setDirectoryPath("C:/Users/shun0/university/PL7/"); // /Users/akitodate/Desktop/PL/PL7/PL7/PL7/PL7/User.txt
        int n = 1;// クライアントの番号
        try (ServerSocket serverSocket = new ServerSocket(10000)) {
            System.out.println("The server has launched!");
            while (true) {
                Socket socket = serverSocket.accept();// 10000番ポートを利用する
                System.out.println("Connected to client No." + n);
                flag[n] = true;
                ClientProcThread myClientProcThread = new ClientProcThread(n, socket);
                // myClientProcThread[n] = new ClientProcThread(n, socket);// スレッドを作成する //
                // リストに変えた．
                // myClientProcThread[n].start(); // スレッドを開始する
                myClientProcThreads.add(myClientProcThread);
                new Thread(myClientProcThread).start();
                member = n; // メンバーの数を更新する
                n++;
            }
        } catch (Exception e) {
            System.err.println("ソケット作成時にエラーが発生しました: " + e);
        }
    }
}

class RoomRepository {

    private static final String FILE_NAME = Server.directoryPath + "Room.txt";
    private static final String DELIMITER = ",";

    public static String addRoom() {
        List<List<String>> rooms = readRoom();
        List<String> newRoom = new ArrayList<>();
        newRoom.add(String.valueOf(rooms.size() + 1));
        newRoom.add("0");
        newRoom.add("0");
        rooms.add(newRoom);
        System.out.println(rooms);
        writeRoom(rooms);

        return String.valueOf(rooms.size());
    }

    public static void deleteRoom(String roomId) {
        List<List<String>> rooms = readRoom();
        for (int index = 0; index < rooms.size(); index++) {
            if (rooms.get(index).get(0).equals(roomId)) {
                rooms.remove(index);
            }
        }
        writeRoom(rooms);
    }

    public static List<String> search(String roomId) {
        List<String> result = new ArrayList<>();
        List<List<String>> rooms = readRoom();
        for (List<String> room : rooms) {
            if (room.get(0).equals(roomId)) {
                result = room;
                break;
            }
        }
        return result;
    }

    public static boolean exist(String roomId) {
        if (search(roomId).isEmpty()) {
            return false;
        }
        return true;
    }

    public static void addUser1(String roomId, String user1) {
        List<List<String>> rooms = readRoom();
        for (List<String> room : rooms) {
            if (room.get(0).equals(roomId)) {
                room.set(1, user1);
                break;
            }
        }
        writeRoom(rooms);
    }

    public static void addUser2(String roomId, String user2) {
        List<List<String>> rooms = readRoom();
        for (List<String> room : rooms) {
            if (room.get(0).equals(roomId)) {
                room.set(2, user2);
                break;
            }
        }
        writeRoom(rooms);
    }

    public static void writeRoom(List<List<String>> rooms) {
        List<String> content = new ArrayList<>();
        for (List<String> room : rooms) {
            content.add(room.get(0) + DELIMITER + room.get(1) + DELIMITER + room.get(2));
        }
        writeToFile(FILE_NAME, content);
    }

    public static void writeToFile(String fileName, List<String> content) {
        System.out.println(content);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            for (String line : content) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<String>> readRoom() {
        List<List<String>> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                List<String> row = new ArrayList<>();
                for (String value : values) {
                    row.add(value);
                }
                result.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}

class UserRepository {
    private static final String FILE_NAME = Server.directoryPath + "User.txt";
    private static final String DELIMITER = ",";

    public static List<String> search(String userName) {
        List<String> result = new ArrayList<>();
        List<List<String>> users = readUser();
        System.out.println(users);
        for (List<String> user : users) {
            if (user.get(1).equals(userName)) {
                result = user;
                break;
            }
        }
        return result;
    }

    public static boolean exist(String userName) {
        if (search(userName).isEmpty()) {
            return false;
        }
        return true;
    }

    public static void addUser(String userName, String password) {
        List<List<String>> users = readUser();
        List<String> newUser = new ArrayList<>();
        int userId = users.size() + 1;
        int win = 0;
        int lose = 0;
        int draw = 0;
        newUser.add(String.valueOf(userId));
        newUser.add(userName);
        newUser.add(password);
        newUser.add(String.valueOf(win));
        newUser.add(String.valueOf(lose));
        newUser.add(String.valueOf(draw));
        users.add(newUser);
        writeUser(users);
    }

    public static void updateRecord(int gameResult, String userName) {
        List<List<String>> users = readUser();
        for (List<String> user : users) {
            if (user.get(1).equals(userName)) {
                int win = Integer.parseInt(user.get(3));
                int lose = Integer.parseInt(user.get(4));
                int draw = Integer.parseInt(user.get(5));
                switch (gameResult) {
                    case 0:
                    case 3:
                        win++;
                        break;
                    case 1:
                    case 4:
                        lose++;
                        break;
                    case 2:
                        draw++;
                        break;
                }
                user.set(3, String.valueOf(win));
                user.set(4, String.valueOf(lose));
                user.set(5, String.valueOf(draw));
                break;
            }
        }
        writeUser(users);
    }

    public static List<List<String>> readUser() {
        List<List<String>> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                List<String> row = new ArrayList<>();
                for (String value : values) {
                    row.add(value);
                }
                result.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String convertListToString(List<List<String>> list) {
        return list.stream()
                .map(innerList -> String.join(",", innerList))
                .collect(Collectors.joining(";"));
    }

    public static void writeUser(List<List<String>> users) {
        List<String> content = new ArrayList<>();
        for (List<String> user : users) {
            content.add(user.get(0) + DELIMITER + user.get(1) + DELIMITER + user.get(2) + DELIMITER + user.get(3)
                    + DELIMITER + user.get(4) + DELIMITER + user.get(5));
        }
        writeToFile(FILE_NAME, content);
    }

    public static void writeToFile(String fileName, List<String> content) {
        System.out.println(content);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            for (String line : content) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}