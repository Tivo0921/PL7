//クラスClientのテスト用ドライバ
//担当: 佐渡
public class ClientDriver {

    public static void main(String args[]) {
        Client client = new Client();
        System.out.println("サーバに接続します");
        client.connectServer();
        System.out.println("サーバ接続完了");
        if (client.loginInfoAccept("TestMyName", "12345")) {
            System.out.println("ログイン情報がサーバに受理されました");
        } else {
            System.out.println("ログイン情報がサーバに存在しません");
        }
        System.out.println("ルームID 10000 があるかサーバに問い合わせます");
        int returnRoomID = client.acceptRoomID(10000);
        if (returnRoomID != 10000) {
            System.out.println("このルームIDが存在しなかったので、代わりに新規にID " + returnRoomID + " のルームを作成しました");
        } else {
            System.out.println("この部屋は存在し空きがあるため、入室しました");
        }
        System.out.println("ルームを新規作成します");
        returnRoomID = client.getRoomID();
        System.out.println("ID" + returnRoomID + " のルームを作成完了しました");
        System.out.println("対戦成績を表示します");
        client.displayGameRecord();
        client.myName = "TestMyName";
        client.opponentName = "TestOpName";
        client.firstMove = true;
        System.out.println("自分が先手としてゲームを開始します");
        client.game();
    }
}