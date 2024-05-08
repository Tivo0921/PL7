//クライアントプログラム 担当: さどゆう

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame implements ActionListener {
    // グローバル変数
    boolean connectionStatus = false;
    String playerName = new String();
    String playerID = new String();
    String password = new String();
    boolean loginSuccess = false;
    int[][] gameRecord = new int[1][3];
    JButton viewExit = new JButton();
    int roomID = 0;
    boolean matchSuccess = false;
    boolean winFlag = false;
    boolean drawFlag = false;
    boolean resign = false;
    boolean pass = false;
    boolean firstMove; // 0なら自分の手番、1（0以外）なら相手の手番
    boolean deleteRoom = false;
    // 定数
    final int row = 8; // オセロ盤の行数・列数
    // プライベート変数
    private int windowSizeX, windowSizeY; // ウィンドウのサイズ
    private JButton boardButtonArray[][]; // オセロ盤上の8×8のボタン配列
    private JButton resignButton, passButton; // 投了・パスボタン
    private ImageIcon blackIcon, whiteIcon, boardIcon; // アイコン
    private Container c; // ペインを取得するコンテナ
    private int[][] board = new int[row][row]; // 現在の盤面
    private String yourName = "あなた";// 自分のプレイヤ名
    private String opponentName = "あいて";// 相手のプレイヤ名
    private int yourColor = 1;// 自分の石の色
    private JTextArea stoneInfoText; // 石の数を表示するテキストエリア
    private JTextArea instText; // 操作指示を表示するテキストエリア
    private String command; // どのボタンが押されたかを識別するコマンド
    // サーバとの通信用
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    /* コンストラクタ 工数:1 進捗:1 */
    public Client() {
        // ウィンドウ設定
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ウィンドウを閉じる場合の処理
        setTitle("ネットワーク対戦型オセロゲーム");// ウィンドウのタイトル
        windowSizeX = row * 45 + 45; // ウィンドウのサイズ計算(幅)
        windowSizeY = row * 45 + 210; // ウィンドウのサイズ計算(高さ)
        setSize(windowSizeX, windowSizeY); // 計算したサイズを反映
        c = getContentPane();// フレームのペインを取得
        // アイコン設定
        whiteIcon = new ImageIcon("./PL7/White.jpg");
        blackIcon = new ImageIcon("./PL7/Black.jpg");
        boardIcon = new ImageIcon("./PL7/GreenFrame.jpeg");
        c.setLayout(null);
        JButton jb = new JButton();
        jb.addActionListener(this);
        // オセロ盤に必要な情報を生成
        boardButtonArray = new JButton[row][row];// ボタンの配列を作成
    }

    /* 接続要求 工数:0.25 進捗:0.25 */
    public boolean connectDemand() {
        // 接続
        try {
            socket = new Socket("localhost", 10000);// ソケットの生成
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 接続の確認
            writer.println("Client 接続完了");// サーバに接続完了の旨を通知
            System.out.println("[サーバーからの応答]" + reader.readLine());// サーバからメッセージを受け取る
            return true;
        } catch (Exception e) {
            // 接続に失敗した場合はfalseを返す
            e.printStackTrace();
            return false;
        }
    }

    /* 再接続選択 工数:2.25 進捗:2.25 */
    public boolean reconnectSelect() {
        // 画面表示をリセット
        c.removeAll();
        // 再接続を問うメッセージ
        JLabel message1 = new JLabel("サーバとの接続に失敗しました。再接続しますか？");// メッセージ作成
        message1.setBounds(windowSizeX / 2 - 150, windowSizeY / 2 - 70, windowSizeX, 30);// 境界を設定
        c.add(message1);// ペインに追加
        // 選択肢のボタン
        JButton button1 = new JButton("はい");// メッセージ作成
        JButton button2 = new JButton("いいえ(終了)");// メッセージ作成
        button1.addActionListener(this);// ボタンの押下を検知できるようにする
        button2.addActionListener(this);
        button1.setActionCommand(Integer.toString(1));// コマンドの設定
        button2.setActionCommand(Integer.toString(2));
        button1.setBounds(15, row * 45 + 30, (row * 45) / 2 - 5, 30); // 境界を設定
        button2.setBounds((row * 45) / 2 + 20, row * 45 + 30, (row * 45) / 2 - 5, 30);
        c.add(button1);// ペインに追加
        c.add(button2);
        c.repaint();// 再描画
        command = ""; // コマンドの入力内容を検知する変数をリセットしておく
        // ボタンの入力がされるまで待機
        try {
            while (command == "") {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("Error: InterruptedException (in 再接続選択)");
        }
        // 結果によって分岐
        System.out.println(command);
        if (command.equals("1")) {

            // 「はい」
            // 「再接続を試みます」のメッセージを2秒間表示
            c.removeAll();
            c.repaint();
            JLabel message2 = new JLabel("再接続を試みます");
            message2.setBounds(windowSizeX / 2 - 50, windowSizeY / 2 - 70, windowSizeX, 30);// 境界を設定
            c.add(message2);// ペインに追加
            // 2秒待機
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Error: InterruptedException (in 再接続選択)");
            }
            return true;
        }
        // 「いいえ」
        else
            return false;
    }

    /* ログイン情報受付 工数:0.5（Playerに移動？） */
    public boolean loginInfoAccept(String PlayerID, String password) {
        // 受け取った情報をそのままソケット通信で送る
        return false;
    }

    /* プレイヤ名表示 工数:0.5（Playerに移動？） */
    public void displayPlayerName(String x) {
    }

    /* 対戦成績の表示 工数:3 */
    public void displayPlayRecord(int[][] x) {
    }

    /* ルームID取得 工数:1 */
    public int getRoomID() {
        return 0;
    }

    /* プレイヤのID情報送信 工数:0.5（Playerに移動？） */
    public void sendPlayerID(int playerID) {
    }

    /* 入力されたルームID受付 工数:1（Playerに移動？） */
    public boolean acceptRoomID() {
        return false;
    }

    /* 作成したルームの削除 工数:0 進捗:0 */
    public void deleteRoom() {
        // グローバル変数 deleteRoom を true にする。これをサーバ側が検知してルームを削除してくれる
        deleteRoom = true;
    }

    /* サーバに接続 工数:0.25 進捗:0.25 */
    public void connectServer() {
        while (true) {
            if (connectDemand())
                break; // 接続成功したらループを抜ける
            else if (!reconnectSelect())
                System.exit(0); // 接続失敗かつ、再接続しない選択をした場合は終了
            // 接続失敗かつ、再接続する選択をした場合はこのままループの先頭に戻る
        }
    }

    /* 盤面の初期化 工数:0.25 進捗:0.25 */
    public void initBoard() {
        // 盤面の石をすべて取り除く
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < row; j++) {
                board[i][j] = 0;
            }
        }
        // オセロの最初の4石を設定
        board[3][3] = 1;
        board[4][4] = 1;
        board[3][4] = 2;
        board[4][3] = 2;
    }

    /* 盤面を描画 工数:3 進捗:3 */
    public int[][] displayBoard(int[][] board) {
        String yourColorName;// 変数: 石の数の表示時、色名を表示するための変数
        String opponentColorName;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < row; j++) {
                // 石（もしくは背景の緑色）の描画
                // 各マスの石の状態によって、描画するものを変える
                if (board[i][j] == 0) {
                    boardButtonArray[i][j] = new JButton(boardIcon);
                } // アイコン（石無し、緑）
                else if (board[i][j] == 1) {
                    boardButtonArray[i][j] = new JButton(blackIcon);
                } // アイコン（黒）
                else if (board[i][j] == 2) {
                    boardButtonArray[i][j] = new JButton(whiteIcon);
                } // アイコン（白）
                c.add(boardButtonArray[i][j]); // 各ボタンをペインに貼り付け
                // マス上にボタンを配置する
                int x = i * 45 + 15; // x座標計算
                int y = j * 45 + 15; // y座標計算
                boardButtonArray[i][j].setBounds(x, y, 45, 45); // ボタンの大きさと位置の設定
                boardButtonArray[i][j].addActionListener(this);// ボタンの押下を検知できるようにする
                boardButtonArray[i][j].setActionCommand(Integer.toString(i) + "-" + Integer.toString(j));// ボタンを識別するコマンドの設定
                // ※マス上ボタンのコマンドは、"[x座標]-[y座標]"という形で設定している（例: 3-4, 0-7など）
            }
        }
        // 投了ボタン
        resignButton = new JButton("投了");// ボタンの作成
        resignButton.setBounds(15, row * 45 + 30, (row * 45) / 2 - 5, 30);// ボタンの境界を設定
        resignButton.addActionListener(this);// ボタンの押下を検知できるようにする
        resignButton.setActionCommand("resign");// 押されたボタンを識別するコマンドの設定
        // パスボタン
        passButton = new JButton("パス");// ボタンの作成
        passButton.setBounds((row * 45) / 2 + 20, row * 45 + 30, (row * 45) / 2 - 5, 30);// ボタンの境界を設定
        passButton.addActionListener(this);// ボタンの押下を検知できるようにする
        passButton.setActionCommand("pass");// 押されたボタンを識別するコマンドの設定
        // 石の数を表示するテキストエリア
        if (yourColor == 1) {// 石の色を表示するための準備
            yourColorName = "黒";
            opponentColorName = "白";
        } else {
            yourColorName = "白";
            opponentColorName = "黒";
        }

        stoneInfoText = new JTextArea(
                "[" + yourName + "](" + yourColorName + "): " + countStone(board, 1) + "\n[" + opponentName +
                        "](" + opponentColorName + "): " + countStone(board, 2)); // テキストエリア作成
        stoneInfoText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16)); // フォントの設定
        stoneInfoText.setBounds(10, row * 45 + 70, row * 45 + 10, 40); // 境界の設定
        stoneInfoText.setEditable(false); // 編集不可能にする
        // 操作指示を表示するテキストエリア
        instText = new JTextArea("あなたの番です\n石を置くマスをクリックしてください", 3, 20); // テキストエリア作成
        instText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16)); // フォントの設定
        instText.setBounds(10, row * 45 + 120, row * 45 + 10, 40); // 境界の設定
        instText.setEditable(false); // 編集不可能にする
        // 上記2つのボタンと2つのテキストエリアをペインに貼り付け
        c.add(resignButton);
        c.add(passButton);
        c.add(stoneInfoText);
        c.add(instText);
        // 再描画
        repaint();
        return board;
    }

    /* 対戦相手の操作情報を受信 工数:0.5 進捗:0 */
    public void recieveOpponentMove() {
        // ***テスト用として、こちらのプログラム側で盤面を変更しています***//
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < row; j++) {
                board[i][j] = 1;
            }
        }
        System.out.println("対戦相手の操作完了");
    }

    /* プレイヤの操作を受付 工数:2 進捗:0.5 */
    public void acceptPlayerMove() {
        // ループを行う（確認ダイアログでキャンセル（いいえ）が選択された場合、クリックしたマスに石が置けなかった場合は
        // 盤面を更新せずもう一度操作を受け付けるため）
        // 石が置けたか、パスや投了が成立した場合はbreakでループを抜ける
        while (true) {
            command = ""; // コマンドを判別するため、変数を初期化
            // ボタンの入力がされるまで待機
            try {
                while (command == "") {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                System.out.println("Error: InterruptedException (in プレイヤの操作を受付)");
            }
            // 押されたボタンによって分岐
            if (command == "resign") {// 投了ボタンの場合、確認ダイアログを表示
                int confirm = JOptionPane.showConfirmDialog(this, "投了します。よろしいですか？", "確認",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {// 確認ダイアログで「はい」が選択されたら
                    resign = true;// 投了フラグをtrueにして手番終了（このあと相手の手番になる前に投了処理が行われる）
                    break;
                }
            } else if (command == "pass") {// パスボタンの場合も、確認ダイアログを表示
                int confirm = JOptionPane.showConfirmDialog(this, "パスします。よろしいですか？", "確認",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {// 確認ダイアログで「はい」が選択されたら
                    pass = true;// パスフラグをtrueにして手番終了（パスしたという情報をサーバに送る）
                    break;
                }
            } else {// それ以外（=マスがクリックされた）なら、その情報を Othello クラス側に送って盤面チェック・盤面計算
                int x = Integer.parseInt(command.substring(0, 1));
                int y = Integer.parseInt(command.substring(2, 3));
                Othello Othello1 = new Othello(x, y);
                if (Othello1.checkBoard(x, y, yourColor)) {// 指定された場所に石が置けるかを判定
                    // 実際に Othello クラスで石を置いて盤面計算してもらう（未完成）
                    break;
                }
            }
        }
        // ループここまで
    }

    /* 操作情報を送信 工数:0.5 進捗:0 */
    public void sendMoveInfo() {
        // パスしたという情報を送ったらフラグをfalseに戻すこと！！
        pass = false;
    }

    /* 盤面に反映 工数:0.25 進捗:0.25 */
    public void updateBoard() {
        c.removeAll(); // 現在表示されているコンポーネントを削除
        displayBoard(board); // 再び描画
        repaint();
    }

    /* 石の数のカウント 工数:0.25 進捗:0.25 */
    public int countStone(int[][] board, int color) {
        // 0なら黒、1なら白、2なら空白マスがいくつあるかを数える
        int count = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < row; j++) {
                if (board[i][j] == color)
                    count++;
            }
        }
        return count;
    }

    /* 対戦終了確認 工数:0.5 進捗:0.25 */
    public boolean checkGameEnd(int x) {
        /* テスト用に値を直接指定しています */
        boolean whiteMovable = true;
        boolean blackMovable = true;
        // 白黒双方が石を置けるかの情報をOthelloクラスから受け取り、どちらも置けない場合は対戦終了
        if (!whiteMovable & !blackMovable)
            return true;
        else
            return false;
    }

    /* 対戦結果をサーバに送信 工数: 0.25 進捗:0 */
    public void sendGameResult(int result) {

    }

    /* 勝敗分を表示 工数:0.5 進捗:0.5 */
    // 戻り値をbooleanに変更（再戦するかどうかの選択）
    public void displayResult(int x) {
        instText.replaceRange("ゲーム終了！", 0, instText.getText().length());
        // 3秒待機
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Error: InterruptedException (in 再接続選択)");
        }

    }

    /* 対戦成績表示 工数:2 進捗:0 */
    public void displayGameRecord() {
        // 対戦成績をサーバから要求
        // （テスト用にこちらでデータを用意しています）
        int[][] record = { { 1, 1 }, { 1, 2 }, { 1, 3 } };

        JDialog dialog = new JDialog(this, "Dialog Title");
        Container dc = dialog.getContentPane();
        dc.setLayout(null);

        JLabel title = new JLabel("対戦成績");
        title.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 25));
        title.setHorizontalAlignment(JLabel.CENTER);

        JTextField recordText = new JTextField("あいうえお", 20); // テキストエリア作成
        recordText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16)); // フォントの設定
        recordText.setBounds(10, row * 45 + 120, row * 45 + 10, 40); // 境界の設定
        recordText.setEditable(false); // 編集不可能にする

        JTextField recordText2 = new JTextField("", 20); // テキストエリア作成
        recordText2.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16)); // フォントの設定
        recordText2.setBounds(10, row * 45 + 120, row * 45 + 10, 40); // 境界の設定
        recordText2.setEditable(false); // 編集不可能にする

        dc.add(title, "Center");
        dc.add(recordText, "Center");
        dialog.pack();
        dialog.setVisible(true);

    }

    /* 切断のメッセージを表示 工数:0.25 進捗:0 */
    public void displayDisconnectionMessage() {

    }

    /* 投了受付 工数:0.5 進捗:0 */
    public int acceptResign(boolean x) {

        return 0;
    }

    public void mouseClicked(MouseEvent e) {
        command = ((JButton) e.getComponent()).getActionCommand();// クリックしたオブジェクトを取得し、ボタンの名前を取り出す
        System.out.println("マウスがクリックされました。押されたボタンは " + command + "です。");// テスト用に標準出力
    }

    public void mouseEntered(MouseEvent e) {
    }// マウスがオブジェクトに入ったときの処理

    public void mouseExited(MouseEvent e) {
    }// マウスがオブジェクトから出たときの処理

    public void mousePressed(MouseEvent e) {
    }// マウスでオブジェクトを押したときの処理

    public void mouseReleased(MouseEvent e) {
    }// マウスで押していたオブジェクトを離したときの処理

    public void actionPerformed(ActionEvent e) {
        command = e.getActionCommand();// クリックしたオブジェクトを取得し、ボタンの名前を取り出す
        System.out.println("マウスがクリックされました(ActionPerformed)。押されたボタンは " + command + "です。");// テスト用に標準出力
    }

    /* main（ここからスタート） 工数: 1 進捗: 0.5 */
    public static void main(String args[]) {
        int turn;
        Client tc = new Client();// クライアントのインスタンス作成
        tc.setVisible(true);
        // サーバへの接続
        tc.connectServer();

        // ループ
        while (true) {
            // ルーム作成画面などをここに
            // ひとまず仮にこちら側がルームを作ったことにしておく
            turn = 0;
            while (true) {
                // 対戦開始
                tc.initBoard();// 盤面初期化
                tc.displayBoard(tc.board);// 盤面の初期描画
                while (!tc.checkGameEnd(0)) {// 盤面をチェックし、対局終了となるまで繰り返す
                    if (turn == 0) {
                        // 自分の手番の場合
                        tc.acceptPlayerMove();// プレイヤ入力の受け付け
                        tc.sendMoveInfo();// 操作情報をサーバに送信
                    } else {
                        // 相手の手番の場合
                        tc.recieveOpponentMove();// 相手の操作情報をサーバから受信
                    }
                    tc.updateBoard();// 盤面の再描画
                }
                // 対戦終了
                tc.sendGameResult(0);// 対戦結果をサーバに送信
                tc.displayResult(0);// 対戦結果を表示し、再戦するかの選択を受け付ける
            }

        }
    }
}
