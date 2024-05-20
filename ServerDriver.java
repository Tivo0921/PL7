import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ClientProcThread1 extends Thread {
    private int number;
    private BufferedReader myIn;
    private PrintWriter myOut;
    private String myName;
    private String myPass;

    public ClientProcThread1(int n, BufferedReader in, PrintWriter out) {
        number = n;
        myIn = in;
        myOut = out;
    }

    public void run() {
        try {
            myOut.println("Hello, client No." + number + "! Enter 'Bye' to exit."); // 初回だけ呼ばれる

            while (true) { // 無限ループで、ソケットへの入力を監視する
                String str = myIn.readLine();
                System.out.println("Received from client No." + number + "(" + myName + "), Messages: " + str);
                if (str != null) { // このソケット（バッファ）に入力があるかをチェック
                    ServerDriver.SendAll(str, myName); // サーバに来たメッセージは接続しているクライアント全員に配る
                }
            }
        } catch (Exception e) {
            // ここにプログラムが到達するときは、接続が切れたとき
            System.out.println("Disconnect from client No." + number + "(" + myName + ")");
            ServerDriver.SetFlag(number, false); // 接続が切れたのでフラグを下げる
        }
    }
}

class ServerDriver {

    private static int maxConnection = 100; // 最大接続数
    private static Socket[] incoming; // 受付用のソケット
    private static boolean[] flag; // 接続中かどうかのフラグ
    private static InputStreamReader[] isr; // 入力ストリーム用の配列
    private static BufferedReader[] in; // バッファリングをによりテキスト読み込み用の配列
    private static PrintWriter[] out; // 出力ストリーム用の配列
    private static ClientProcThread1[] myClientProcThread; // スレッド用の配列
    private static int member; // 接続しているメンバーの数

    public static String directoryPath;

    // 全員にメッセージを送る
    public static void SendAll(String str, String myName) {
        // 送られた来たメッセージを接続している全員に配る
        for (int i = 1; i <= member; i++) {
            if (flag[i]) {
                out[i].println(str);
                out[i].flush(); // バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
                System.out.println("Send messages to client No." + i);
            }
        }
    }

    // フラグの設定を行う
    public static void SetFlag(int n, boolean value) {
        flag[n] = value;
    }

    public static void setDirectoryPath(String path) {
        directoryPath = path.endsWith("/") ? path : path + "/";
    }

    private synchronized static void writeToFile(String fileName, List<String> content, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, append))) {
            for (String line : content) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void writeUserID(int id, String myName, String myPass) {
        String fileName = directoryPath + "User.txt";
        boolean idExists = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    continue;
                }
                int uid = Integer.parseInt(parts[0]);
                if (uid == id) {
                    idExists = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!idExists) {
            List<String> content = new ArrayList<>();
            content.add(id + "," + myName + "," + myPass);
            writeToFile(fileName, content, true);
        }
    }

    public static boolean readUserID1(String myName, String myPass) {
        String fileName = directoryPath + "User.txt";
        boolean existlogin = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
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

    public synchronized static void writeRoomID(int roomId, String user1, String user2) {
        String fileName = directoryPath + "Room.txt";
        boolean roomIdExists = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && Integer.parseInt(parts[0]) == roomId) {
                    roomIdExists = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!roomIdExists) {
            List<String> content = new ArrayList<>();
            content.add(roomId + "," + user1 + "," + user2);
            writeToFile(fileName, content, true);
        }
    }

    public static List<String> readRoomIDFile() {
        String fileName = directoryPath + "Room.txt";
        List<String> fileContent = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    public static void readRoomID() {
        List<String> fileContent = readRoomIDFile();
        for (String line : fileContent) {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                continue;
            }
            int roomId = Integer.parseInt(parts[0]);
            String user1 = parts[1];
            String user2 = parts[2];
            System.out.println("ID: " + roomId + ", User1: " + user1 + ", User2: " + user2);
        }
    }

    public synchronized static int makeRoom(String user1) {
        List<String> fileContent = readRoomIDFile();
        Set<Integer> roomIds = new HashSet<>();

        for (String line : fileContent) {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                continue;
            }
            int roomId = Integer.parseInt(parts[0]);
            roomIds.add(roomId);
        }

        int newRoomId = 1;
        while (roomIds.contains(newRoomId)) {
            newRoomId++;
        }

        writeRoomID(newRoomId, user1, "");

        System.out.println("Created room with ID: " + newRoomId + " for user: " + user1);
        return newRoomId;
    }

    public synchronized static int enterRoom(int roomId, String user2) {
        List<String> fileContent = readRoomIDFile();
        boolean roomFound = false;
        int room = 0;

        for (int i = 0; i < fileContent.size(); i++) {
            String[] parts = fileContent.get(i).split(",");
            if (parts.length != 3) {
                continue;
            }
            int currentRoomId = Integer.parseInt(parts[0]);
            if (currentRoomId == roomId) {
                if (!parts[2].isEmpty()) {
                    System.out.println("部屋は満員です");
                    room = 2;
                    return room;
                }
                fileContent.set(i, parts[0] + "," + parts[1] + "," + user2);
                roomFound = true;
                break;
            }
        }

        if (!roomFound) {
            System.out.println("Room ID " + roomId + " not found.");
            return room;
        }

        String fileName = directoryPath + "Room.txt";
        writeToFile(fileName, fileContent, false);

        room = 1;

        System.out.println("User " + user2 + " entered room with ID: " + roomId);
        return room;
    }

    public synchronized static void deleteRoom(int roomId) {
        String fileName = directoryPath + "Room.txt";
        List<String> fileContent = new ArrayList<>();
        boolean roomFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    continue;
                }
                int currentRoomId = Integer.parseInt(parts[0]);
                if (currentRoomId == roomId) {
                    roomFound = true;
                    continue;
                }
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!roomFound) {
            System.out.println("Room ID " + roomId + " not found.");
            return;
        }

        writeToFile(fileName, fileContent, false);

        System.out.println("Room with ID: " + roomId + " deleted.");
    }

    // mainプログラム
    public static void main(String[] args) {
        // 必要な配列を確保する
        incoming = new Socket[maxConnection];
        flag = new boolean[maxConnection];
        isr = new InputStreamReader[maxConnection];
        in = new BufferedReader[maxConnection];
        out = new PrintWriter[maxConnection];
        myClientProcThread = new ClientProcThread1[maxConnection];

        setDirectoryPath("C:/Users/shun0/university/PL7");

        int n = 1;

        try {
            System.out.println("The server has launched!");
            int number = 1;
            String myName = "a";
            String myPass = "a";
            writeUserID(number, myName, myPass);
            readUserID1(myName, myPass);

            int roomId = makeRoom(myName);
            System.out.println(roomId + "を作成");
            number = 2;
            String enemyName = "b";
            String enemyPass = "b";

            writeUserID(2, enemyName, enemyPass);
            readUserID1(enemyName, enemyPass);
            System.out.println(ServerDriver.enterRoom(roomId, enemyName));

            ServerSocket server = new ServerSocket(10000); // 10000番ポートを利用する
            while (true) {
                incoming[n] = server.accept();
                flag[n] = true;
                System.out.println("Accept client No." + n);
                // 必要な入出力ストリームを作成する
                isr[n] = new InputStreamReader(incoming[n].getInputStream());
                in[n] = new BufferedReader(isr[n]);
                out[n] = new PrintWriter(incoming[n].getOutputStream(), true);

                myClientProcThread[n] = new ClientProcThread1(n, in[n], out[n]); // 必要なパラメータを渡しスレッドを作成
                myClientProcThread[n].start(); // スレッドを開始する
                member = n; // メンバーの数を更新する
                n++;
            }
        } catch (Exception e) {
            System.err.println("ソケット作成時にエラーが発生しました: " + e);
        }
    }
}
