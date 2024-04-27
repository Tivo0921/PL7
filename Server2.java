import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.UUID;

public class Server2 {
    private int[][] clientConnectionState = new int[1][2];
    private String playerName = new String();
    private String password = new String();
    private int playerID;
    private boolean loginSuccess = false;
    private int[][] playRecord = new int[1][3];
    private int roomId = 0;

    public Server2() {

    }

    public int makeRoom() {// 工数1.0
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("ルームIDは " + roomId + " です。");
    }

    public boolean confirmRoom(int roomId) {// 工数1.0

    }

    public void removeRoom(int roomId) {// 工数0.5

    }

    public

    public void sendColor(int playerNo) { // 先手後手情報(白黒)の送信,工数1
    }

    public void forwardMessage(String msg, String playerName) { // 操作情報の転送,工数2
        /*
         * try {
         * PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
         * out.println(msg);
         * } catch (IOException e) {
         * e.printStackTrace();
         * }
         */
    }

    public void updateScore(int playerId, boolean isWinner, boolean isDraw) {// 工数4
        /*
         * database.child(playerId).runTransaction(new Transaction.Handler() {
         * 
         * @Override
         * public Transaction.Result doTransaction(MutableData mutableData) {
         * Integer score = mutableData.getValue(Integer.class);
         * if (score == null) {
         * score = 0;
         * }
         * if (isWinner) {
         * score++;
         * }
         * mutableData.setValue(score);
         * return Transaction.success(mutableData);
         * }
         * 
         * @Override
         * public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot
         * dataSnapshot) {
         * if (databaseError != null) {
         * System.out.println("Error updating score: " + databaseError.getMessage());
         * }
         * }
         * });
         */
    }

    public static void main(String[] args) { // main,工数5
        ServerSample2 server = new ServerSample2(10000); // 待ち受けポート10000番でサーバオブジェクトを準備
        server.acceptClient(); // クライアント受け入れを開始
    }
}