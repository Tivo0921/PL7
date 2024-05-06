import java.io.*;
import java.net.*;

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
    // 工数2,進捗1

    public int[][] connectClient() {// 一旦チャットする仕様にしてある
        // TCPポートを指定してサーバソケットを作成
        try (ServerSocket server = new ServerSocket(10000)) {
            while (true) {
                try {
                    // クライアントからの接続待ち受け
                    Socket sc = server.accept();
                    System.out.println("クライアントからの接続がありました");

                    BufferedReader reader = null;
                    PrintWriter writer = null;

                    // クライアントからの接続ごとにスレッドで通信処理を実行
                    try {
                        // クライアントからの受け取り用
                        reader = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                        // クライアントへの送信用
                        writer = new PrintWriter(sc.getOutputStream(), true);
                        // クライアントから「exit」が入力されるまで無限ループ
                        String line = null;
                        while (true) {
                            line = reader.readLine();
                            // クライアントから送信されたメッセージを取得
                            if (line.equals("exit")) {
                                break;
                            }

                            System.out.println("クライアントからのメッセージ＝" + line);
                            writer.println("Please Input");
                        }

                    } catch (Exception e) {
                        ex.printStackTrace();
                    } finally {
                        // リソースの解放
                        if (reader != null) {
                            reader.close();
                        }
                        if (writer != null) {
                            writer.close();
                        }
                        if (sc != null) {
                            sc.close();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 工数1,進捗1
    public int[][] checkClientConnection(void){//clientCOnnectionState[][]を返す
        for(int i = 0;i<maxRoomID;i++){
            for(int j = 0; j < 1 ; j++){
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
    public boolean checkLoginInformation(String ){

    }

    // 工数1,進捗1
    public int[][] transferPlayRecord(int playRecord[][]) {
        return playRecord;
    }
}