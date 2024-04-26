//クライアントプログラム 担当: さどゆう

public class Client {
    //変数の宣言
    boolean connectionStatus = false;
    String playerName = new String();
    String password = new String();
    boolean loginSuccess = false;
    int[][] gameRecord = new int[1][3];
    JButton viewExit = new JButton();
    int roomID = 0;
    boolean matchSuccess = false;
    int gameResult = 0;
    boolean resign = false;

    public boolean connectDemand() {
        //接続要求
    }

    public void reconnectSelect(Boolean) {
        //再接続選択
    }

    public boolean loginInfoAccept(String,int) {
        //ログイン情報受付
    }

    public void displayPlayerName(String) [
        //プレイヤ名表示
    ]

    public void sendPlayerID(int) {
        //プレイヤのID情報送信
    }

    public void displayPlayRecord(int[][]) {
         //対戦成績の表示
    }

    public void displayRoomID(int) {
        //ルームID表示
    }

    public boolean acceptRoomID() {
        //入力されたルームID受付
    }

    public void connectServer() {
        //サーバに接続
    }

    public int receiveFirstMove(int) {
        //先手後手情報受診
    }

    public int[][] displayBoard(int[][]) {
        //盤面を描画
    }

    public int[][] recieveOpponentMove(int[][]) {
        //対戦相手の操作情報を受信
    }

    public int[][] acceptPlayerMove(int[][]) {
        //プレイヤの操作を受付
    }

    public void sendMoveInfo(int[][]) {
        //操作情報を送信
    }

    public void updateBoard(int[][]) {
        //盤面に反映
    }

    public boolean checkGameEnd(int) {
        //対戦終了確認
    }

    public void sendGameResult(int) {
        //対戦結果をサーバに送信
    }

    public void displayResult(int) {
        //勝敗分を表示
    }

    public void displayDisconnectionMessage() {
        //切断のメッセージを表示
    }

    public int acceptResign(boolean) {
        //投了受付
    }
}