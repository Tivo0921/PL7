import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;

//工数6,進捗2
public class Server1 {
    // 変数宣言
    public int[][] clientConnectionState = new int[1][2];
    public String playerName = new String();
    public String password = new String();
    public int playerID = 0;
    public boolean loginSuccess = false;
    public int[][] playRecord = new int[1][3];
    public int roomID = 0;// 0から昇順
    public int maxRoomID = 0;// 最大のルームID(ルームID数-1)

    // 工数2,進捗2
    public int[][] connectClient(String[] args) {
        // TCPポートを指定してサーバソケットを作成
        try {
            ServerSocket serverSocket = new ServerSocket(10000);
            System.out.println("サーバーが起動しました。");

            while (true) {
                try {
                    // クライアントからの接続待ち受け(accept)
                    Socket clientSocket1 = serverSocket.accept();// クライアント1に接続
                    System.out.println("先手からの接続がありました");
                    Socket clientSocket2 = serverSocket.accept(); // クライアント2に接続
                    System.out.println("後手からの接続がありました");

                    // クライアント1からの入力
                    InputStream inputStream1 = clientSocket1.getInputStream();
                    ObjectInputStream objectInputStream1 = new ObjectInputStream(inputStream1);
                    int[][] array1 = (int[][]) objectInputStream1.readObject();
                    // クライアント2にデータを転送
                    OutputStream outputStream2 = clientSocket2.getOutputStream();
                    ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(outputStream2);
                    objectOutputStream2.writeObject(array1);
                    objectOutputStream2.flush();

                    // クライアント2からの入力
                    InputStream inputStream2 = clientSocket2.getInputStream();
                    ObjectInputStream objectInputStream2 = new ObjectInputStream(inputStream2);
                    int[][] array2 = (int[][]) objectInputStream2.readObject();
                    // クライアント1にデータを転送
                    OutputStream outputStream1 = clientSocket1.getOutputStream();
                    ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(outputStream1);
                    objectOutputStream1.writeObject(array2);
                    objectOutputStream1.flush();

                    // 接続を閉じる
                    clientSocket1.close();
                    clientSocket2.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 工数1,進捗1
    public int[][] checkClientConnection(void){//clientCOnnectionState[][]を返す
        for(int i = 0;i<maxRoomID;i++){
            for(int j = 0; j < 2 ; j++){
                if(clientConnectionState[i][j]){
                    System.out.println(i +"," + j + ":接続中");
                }
                else{
                    System.out.println(i +"," + j + ":接続切断");
                }

            }
        }
    }

    // 工数2,進捗0
    public boolean checkLoginInformation(String[] ){

    }

    // 工数1,進捗1
    public int[][] transferPlayRecord(int playRecord[][]) {// 対戦成績の転送
        return playRecord;
    }
}