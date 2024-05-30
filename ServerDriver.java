
//Server.java 5/24 4:23 更新
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
    public static PrintWriter out;

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

    public void run() {
        for (ClientProcThread myClientProcThread : Server.myClientProcThreads) {
            System.out.println("拡張for 文呼び出し:" + myName + "," + roomId);
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
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

    // 全員にメッセージを送る。要改善
    public static void SendAll(String str, String myName, int num) {
        // 送られた来たメッセージを接続している全員に配る
        System.out.println("SendAll 呼び出しfor文直前 (str,myName,num) =  " + str + myName + num);
        for (ClientProcThread myClientProcThread : myClientProcThreads) { // int i = 1; i <= num; i++
            myClientProcThread.sendMessage(str + "," + myName + "," + num);
        }
    }

    // mainプログラム
    public static void main(String[] args) {
        myClientProcThread = new ClientProcThread[maxConnection];
        try (ServerSocket serverSocket = new ServerSocket(10000)) {
            System.out.println("The server has launched!");
            while (true) {
                System.out.println("Waiting for connection...");
                Socket socket = serverSocket.accept();// 10000番ポートを利用する
                System.out.println("Connected to client No." + n);
                flag[n] = true;
                System.out.println("Accept client No." + n);
                ClientProcThread myClientProcThread = new ClientProcThread(n, socket);
                // myClientProcThread[n] = new ClientProcThread(n, socket);// スレッドを作成する //
                // リストに変えた．
                // myClientProcThread[n].start(); // スレッドを開始する
                myClientProcThreads.add(myClientProcThread);
                for (ClientProcThread mainClientProcThread : myClientProcThreads) {
                    System.out.println("main メソッドで拡張for 文呼び出し:" + mainClientProcThread);
                }
                new Thread(myClientProcThread).start();
            }
        } catch (Exception e) {
            System.err.println("ソケット作成時にエラーが発生しました: " + e);
        }
    }
}