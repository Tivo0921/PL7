//Client.java
//クライアントプログラム 担当: 佐渡

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame implements ActionListener {
    // グローバル変数
    boolean connectionStatus = false;
    String playerName = new String();
    String playerID = new String();
    String password = new String();
    boolean firstMove = true; // 先手か後手か。trueなら自分が先手、falseなら自分が後手
    String myName;// 自分のプレイヤ名
    String opponentName;// 相手のプレイヤ名
    // 定数
    static final int ROW = 8; // オセロ盤の行数・列数
    // プライベート変数
    private int windowSizeX, windowSizeY; // ウィンドウのサイズ
    private JButton boardButtonArray[][]; // オセロ盤上の8×8のボタン配列
    private JButton resignButton, passButton; // 投了・パスボタン
    private ImageIcon blackIcon, whiteIcon, boardIcon; // アイコン
    private Container c; // ペインを取得するコンテナ
    private int[][] board = new int[ROW][ROW]; // 現在の盤面
    private int myColor = 1;// 自分の石の色
    private int opponentColor = 2;// 相手の石の色
    private JTextArea stoneInfoText; // 石の数を表示するテキストエリア
    private JTextArea instText; // 操作指示を表示するテキストエリア
    private String command; // どのボタンが押されたかを識別するコマンド
    private int placeX; // どの位置に石を置いたかを記憶する
    private int placeY;
    private int nextOp; // 今行った操作（石を置く:0、投了:1、パス:2）を記憶する
    private Othello myOthello;

    // サーバとの通信用
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    /* コンストラクタ */
    public Client() {
        // ウィンドウ設定
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ウィンドウを閉じる場合の処理
        setTitle("Othello");// ウィンドウのタイトル
        windowSizeX = ROW * 45 + 45; // ウィンドウのサイズ計算(幅)
        windowSizeY = ROW * 45 + 210; // ウィンドウのサイズ計算(高さ)
        setSize(windowSizeX, windowSizeY); // 計算したサイズを反映
        c = getContentPane();// フレームのペインを取得
        // アイコン設定
        whiteIcon = new ImageIcon("./white.jpg");
        blackIcon = new ImageIcon("./black.jpg");
        boardIcon = new ImageIcon("./grid.jpg");
        c.setLayout(null);
        JButton jb = new JButton();
        jb.addActionListener(this);
        // オセロ盤に必要な情報を生成
        boardButtonArray = new JButton[ROW][ROW];// ボタンの配列を作成
        myOthello = new Othello(0, 0);// Otelloインスタンスを作成
    }

    /* 接続要求 */
    public boolean connectDemand() {
        // 接続
        try {
            socket = new Socket("localhost", 10000);// ソケットの生成
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 接続の確認
            System.out.println(reader.readLine());// サーバからメッセージを受け取る
            return true;
        } catch (Exception e) {
            // 接続に失敗した場合はfalseを返す
            e.printStackTrace();
            return false;
        }
    }

    /* 再接続選択 */
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
        button1.setBounds(15, ROW * 45 + 30, (ROW * 45) / 2 - 5, 30); // 境界を設定
        button2.setBounds((ROW * 45) / 2 + 20, ROW * 45 + 30, (ROW * 45) / 2 - 5, 30);
        c.add(button1);// ペインに追加
        c.add(button2);
        c.repaint();// 再描画
        command = ""; // コマンドの入力内容を検知する変数をリセットしておく
        // ボタンの入力がされるまで待機
        try {
            while (command.equals("")) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("Error: InterruptedException (in 再接続選択)");
        }
        // 結果によって分岐
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

    /* ログイン情報受付 */
    public boolean loginInfoAccept(String playerName, String password) {
        // 受け取った情報をそのままソケット通信で送る
        // ※その情報が存在しない場合は新たなユーザとして登録する
        writer.println(playerName + "," + password);
        // ここで受け取ったプレイヤ名は変数に記憶しておく
        myName = playerName;
        return true;
    }

    /* 入力されたルームID受付 */
    public int acceptRoomID(int roomID) {
        // 引数に入力したルームが存在するかをサーバに問い合わせ、存在すれば同じIDをそのまま返す
        // 存在しない場合は新たなルームを作成し、そのIDを返す
        String message = "";
        try {
            //サーバにメッセージを送る
            writer.println("search room");
            writer.println(roomID);
            // ルームIDを受け取る
            message = reader.readLine();
            // 相手ユーザーの名前を受信
            opponentName = reader.readLine();
            System.out.println("opponentName = " + opponentName);
            // 先手後手情報の設定
            firstMove = false;
        } catch (IOException e) {
            System.out.println("Error: IOException (in 入力されたルームID受付)");
        }
        return Integer.parseInt(message);
    }

    /* 作成したルームID取得 */
    public int getRoomID() {
        String message = "";
        try {
            writer.println("make a room");
            // ルームIDを受信
            message = reader.readLine();
            // 相手ユーザーの名前を受信
            opponentName = reader.readLine();
            System.out.println("opponentName = " + opponentName);
            // 先手後手情報の設定
            firstMove = true;
        } catch (IOException e) {
            System.out.println("Error: IOException (in 作成したルームID受付)");
        }
        System.out.println("roomID = " + message);
        return Integer.parseInt(message);
    }

    /* 指定したルームの削除 */
    public void deleteRoomID() {
        writer.println("delete");
    }

    /* サーバに接続 */
    public void connectServer() {
        while (true) {
            if (connectDemand())
                break; // 接続成功したらループを抜ける
            else if (!reconnectSelect())
                System.exit(0); // 接続失敗かつ、再接続しない選択をした場合は終了
            // 接続失敗かつ、再接続する選択をした場合はこのままループの先頭に戻る
        }
    }

    /* サーバからメッセージを受信しているか確認し、もしあれば受け取って標準出力 */
    /* 戻り値: メッセージ受信の有無 */
    public boolean checkServerMessage() {
        boolean result = false;
        try {
            if (reader.ready()) {// 受信の有無を確認
                System.out.println(reader.readLine());// メッセージを受け取って出力
                result = true;
            }
        } catch (IOException e) {
        }
        return result;
    }

    /* サーバからメッセージを受信し、その内容を返す（上記checkServer...とは違いこちらはメッセージがあると分かっている前提） */
    /* 戻り値: メッセージ受信の有無 */
    public String getServerMessage() {
        String message;
        try {
            message = reader.readLine();// メッセージを受け取って出力
        } catch (IOException e) {
            message = "";// エラーが起きた場合は空の文字列を返す
        }
        return message;
    }

    /* サーバに任意の文字列を送信する */
    public void sendServerMessage(String message) {
        writer.println(message);
    }

    /* 盤面の初期化 */
    public void initBoard() {
        // 盤面の石をすべて取り除く
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < ROW; j++) {
                board[i][j] = 0;
            }
        }
        // オセロの最初の4石を設定
        board[3][3] = 1;
        board[4][4] = 1;
        board[3][4] = 2;
        board[4][3] = 2;
    }

    /* 盤面を描画 */
    public int[][] displayBoard(int[][] board) {
        String myColorName;// 変数: 石の数の表示時、色名を表示するための変数
        String opponentColorName;

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < ROW; j++) {
                // 石（もしくは背景の緑色）の描画
                // 各マスの石の状態によって、描画するものを変える
                if (board[i][j] == 0) {// アイコン（石無し、緑）
                    boardButtonArray[i][j] = new JButton(boardIcon);
                } // アイコン（黒）
                else if (board[i][j] == 1) {
                    boardButtonArray[i][j] = new JButton(blackIcon);
                } // アイコン（白）
                else if (board[i][j] == 2) {
                    boardButtonArray[i][j] = new JButton(whiteIcon);
                }
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
        resignButton.setBounds(15, ROW * 45 + 30, (ROW * 45) / 2 - 5, 30);// ボタンの境界を設定
        resignButton.addActionListener(this);// ボタンの押下を検知できるようにする
        resignButton.setActionCommand("resign");// 押されたボタンを識別するコマンドの設定
        // パスボタン
        passButton = new JButton("パス");// ボタンの作成
        passButton.setBounds((ROW * 45) / 2 + 20, ROW * 45 + 30, (ROW * 45) / 2 - 5, 30);// ボタンの境界を設定
        passButton.addActionListener(this);// ボタンの押下を検知できるようにする
        passButton.setActionCommand("pass");// 押されたボタンを識別するコマンドの設定
        // 石の数を表示するテキストエリア
        if (myColor == 1) {// 石の色を表示するための準備
            myColorName = "黒";
            opponentColorName = "白";
        } else {
            myColorName = "白";
            opponentColorName = "黒";
        }
        stoneInfoText = new JTextArea("", 3, 20);// テキストエリア作成
        updateTextArea(0, "[" + myName + "](" + myColorName + "): " + countStone(board, 1) + "\n[" + opponentName +
                "](" + opponentColorName + "): " + countStone(board, 2));// 内容の入力
        stoneInfoText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16)); // フォントの設定
        stoneInfoText.setBounds(10, ROW * 45 + 70, ROW * 45 + 10, 40); // 境界の設定
        stoneInfoText.setEditable(false); // 編集不可能にする
        // 操作指示を表示するテキストエリア
        instText = new JTextArea("", 3, 20); // テキストエリア作成
        updateTextArea(1, "あなたの番です\n石を置くマスをクリックしてください"); // 内容の入力
        instText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16)); // フォントの設定
        instText.setBounds(10, ROW * 45 + 120, ROW * 45 + 10, 40); // 境界の設定
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

    /* テキストエリアのメッセージ内容を更新 */
    public void updateTextArea(int textArea, String message) {
        // 引数 textArea の値により、どちらのテキストエリアを更新するのかを指定
        if (textArea == 0) {// stoneInfoText(石の数の表示)の更新
            stoneInfoText.replaceRange(message, 0, stoneInfoText.getText().length());
        }
        if (textArea == 1) {// instText(操作指示等)の更新
            instText.replaceRange(message, 0, instText.getText().length());
        }
    }

    /* 対戦相手の操作情報を受信 */
    public int recieveOpponentMove() {
        int opponentNextOp = 0;
        updateTextArea(1, "対戦相手の番です\n操作を待っています");
        try {
            String boardString = reader.readLine(); // 操作情報を文字列として受信（以下で文字列から数値を抽出）
            opponentNextOp = Integer.parseInt(boardString.substring(0, 1)); // 1つ目の引数（操作の種類）を取得
            int x = Integer.parseInt(boardString.substring(2, 3)); // 2つ目の引数（操作対象のx座標）を取得
            int y = Integer.parseInt(boardString.substring(4, 5)); // 3つ目の引数（操作対象のy座標）を取得
            int opponentColor = 0;// Integer.parseInt(boardString.substring(6,7)); //4つ目の引数（操作対象の色）を取得

            if (opponentNextOp == 0) { // 操作の種類:0（石を置く）
                board = myOthello.calcBoard(x, y, opponentColor); // 実際に盤面を計算
            } else if (opponentNextOp == 1) { // 操作の種類:1（投了）
                updateTextArea(1, "相手が投了を選択しました");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    System.out.println("Error: InterruptedException (in 対戦相手の操作情報受信)");
                }
            } else if (opponentNextOp == 2) { // 操作の種類:1（パス）
                updateTextArea(1, "相手がパスを選択しました");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.out.println("Error: InterruptedException (in 対戦相手の操作情報受信)");
                }
            }
            System.out.println("対戦相手の操作完了");

        } catch (Exception e) {
        }

        return opponentNextOp; // 相手の操作の種類を戻り値として返す
    }

    /* プレイヤの操作を受付 */
    public void acceptPlayerMove() {
        // ループを行う（確認ダイアログでキャンセル（いいえ）が選択された場合、クリックしたマスに石が置けなかった場合は
        // 盤面を更新せずもう一度操作を受け付けるため）
        // 石が置けたか、パスや投了が成立した場合はbreakでループを抜ける
        while (true) {
            command = ""; // コマンドを判別するため、変数を初期化
            // ボタンの入力がされるまで待機
            try {
                while (command.equals("")) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                System.out.println("Error: InterruptedException (in プレイヤの操作を受付)");
            }
            // 押されたボタンによって分岐
            if (command.equals("resign")) {// 投了ボタンの場合、確認ダイアログを表示
                int confirm = JOptionPane.showConfirmDialog(this, "投了します。よろしいですか？", "確認",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {// 確認ダイアログで「はい」が選択されたら
                    nextOp = 1;// 操作の記憶変数を1にして手番終了（このあと相手の手番になる前に投了処理が行われる）
                    break;
                }
            } else if (command.equals("pass")) {// パスボタンの場合も、確認ダイアログを表示
                int confirm = JOptionPane.showConfirmDialog(this, "パスします。よろしいですか？", "確認",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {// 確認ダイアログで「はい」が選択されたら
                    nextOp = 2;// 操作の記憶変数を2にして手番終了（パスしたという情報をサーバに送る）
                    break;
                }
            } else {// それ以外（=マスがクリックされた）なら、その情報を Othello クラス側に送って盤面チェック・盤面計算
                placeY = Integer.parseInt(command.substring(0, 1));
                placeX = Integer.parseInt(command.substring(2, 3));
                nextOp = 0;// 操作の記憶変数を0にする（石を置く、という操作）

                if (myOthello.checkBoard(placeX, placeY, myColor)) {// 指定された場所に石が置けるかを判定
                    // 実際に Othello クラスで石を置いて盤面計算してもらう
                    board = myOthello.calcBoard(placeX, placeY, myColor);
                    break;
                }
            }
        }
        // ループここまで
    }

    /* 操作情報を送信 */
    public void sendMoveInfo() {
        // 操作情報をStringに変換して送信する
        String boardString = nextOp + "," + placeX + "," + placeY + "," + myColor;
        writer.println(boardString);
    }

    /* 盤面に反映 */
    public void updateBoard() {
        c.removeAll(); // 現在表示されているコンポーネントを削除
        displayBoard(board); // 再び描画
        repaint();
    }

    /* 石の数のカウント */
    public int countStone(int[][] board, int color) {
        // 0なら黒、1なら白、2なら空白マスがいくつあるかを数える
        int count = 0;
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < ROW; j++) {
                if (board[i][j] == color)
                    count++;
            }
        }
        return count;
    }

    /* 対戦終了確認 */
    public boolean checkGameEnd(int x) {
        // 白黒双方が石を置けるかの情報をOthelloクラスから受け取り、どちらも置けない場合は対戦終了(trueを返す)
        boolean whiteMovable = myOthello.searchBoard(board, 1);
        boolean blackMovable = myOthello.searchBoard(board, 2);
        if (!whiteMovable & !blackMovable)
            return true;
        else
            return false;
    }

    /* 対戦結果をサーバに送信 */
    public void sendGameResult(int result) {
        writer.println("match end");
        // 勝ちなら0、負けなら1、引き分けなら2を送信
        writer.println(playerID + "," + result);
    }

    /* 勝敗分を表示 */
    // 戻り値boolean（再戦するかどうかの選択）
    public boolean displayResult(int gameResult) {
        System.out.println("gameResult = " + gameResult + ", ");
        updateTextArea(1, "ゲーム終了！");
        // 3秒待機
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("Error: InterruptedException (in 再接続選択)");
        }
        // 今表示されているものをすべて取り除く
        c.removeAll();
        // 相手の石の色を計算
        opponentColor = myColor % 2 + 1; // 自分の色でないほうを相手の石の色とする
        // 対戦結果を描画するコンポーネント
        JLabel myStoneNum = new JLabel(Integer.toString(countStone(board, myColor)));
        JLabel opponentStoneNum = new JLabel(Integer.toString(countStone(board, opponentColor)), SwingConstants.RIGHT);
        JLabel myNameLabel = new JLabel(myName, SwingConstants.CENTER);
        JLabel opponentNameLabel = new JLabel(opponentName, SwingConstants.CENTER);
        JLabel vsLabel = new JLabel("vs");
        JLabel resultLabel = new JLabel("", SwingConstants.CENTER);
        JLabel resignLabel = new JLabel("(投了)");
        JButton endButton = new JButton("対戦終了");
        JButton rematchButton = new JButton("続けて対戦する");
        // resultLabelの表示名は勝ち負けによって変わる
        if (gameResult == 0 || gameResult == 3)
            resultLabel.setText("You Win!");
        else if (gameResult == 0 || gameResult == 4)
            resultLabel.setText("You Lose...");
        else
            resultLabel.setText("Draw");

        // 各コンポーネントのフォントサイズ・位置調整
        myStoneNum.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 32));
        myStoneNum.setBounds(100, 160, 40, 40);
        opponentStoneNum.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 32));
        opponentStoneNum.setBounds(250, 160, 40, 40);
        vsLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
        vsLabel.setBounds(186, 160, 40, 40);
        myNameLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
        myNameLabel.setBounds(0, 100, 180, 40);
        opponentNameLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
        opponentNameLabel.setBounds(205, 100, 180, 40);
        resultLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
        resultLabel.setBounds(105, 220, 180, 40);
        resignLabel.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
        resignLabel.setBounds(170, 190, 180, 40);
        endButton.setBounds(15, 280, 175, 30);
        endButton.addActionListener(this);
        endButton.setActionCommand("end");
        rematchButton.setBounds(200, 280, 175, 30);
        rematchButton.addActionListener(this);
        rematchButton.setActionCommand("rematch");
        // ペインに貼り付けて再描画
        c.add(myStoneNum);
        c.add(opponentStoneNum);
        c.add(vsLabel);
        c.add(myNameLabel);
        c.add(opponentNameLabel);
        c.add(resultLabel);
        if (gameResult == 3 || gameResult == 4)
            c.add(resignLabel); // 投了で決着がついた場合のみ「(投了)」を表示する
        c.add(endButton);
        c.add(rematchButton);
        c.repaint();

        // 操作の受け付け
        command = ""; // コマンドを判別するため、変数を初期化
        // ボタンの入力がされるまで待機
        try {
            while (command.equals("")) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("Error: InterruptedException (勝敗分を表示)");
        }
        // 今表示されているものをすべて取り除く
        c.removeAll();
        // 押されたボタンによって分岐
        if (command.equals("rematch"))
            return true;
        else
            return false;
    }

    /* 対戦成績表示 */
    public void displayGameRecord() {

        // テスト用の仮データ
        // String recordString = "1,2,3;4,5,6;7,8,9;10,11,12;50,50,400;70,70,1120";
        String recordString = "";

        // 対戦成績をサーバから要求
        try {
            System.out.println("client 561");
            writer.println("View Results");
            System.out.println("client 563");
            recordString = reader.readLine();
            System.out.println(recordString);
        } catch (IOException e) {
        }

        // 一列のStringで送られてきたデータを分割していく（セミコロン毎に1人分、カンマ毎に数値区切り）
        // 最終的にrecordに格納される
        String recordString2[] = recordString.split(";");
        String recordString3[][] = new String[recordString2.length][3];
        int record[][] = new int[recordString2.length][3];
        for (int i = 0; i < recordString2.length; i++) {
            recordString3[i] = recordString2[i].split(",");
            for (int j = 0; j < 3; j++)
                record[i][j] = Integer.parseInt(recordString3[i][j]);
        }

        String[] recordName = { "Player001", "Player002",
                "Player003", "Player004",
                "Player005", "Player006",
                "Player007", "Player008",
                "Player009", "Player010",
                "Player011", "Player012" };
        int num = 6;// 成績のデータ行数（＝人数）

        // JFrameの設定
        JFrame myFrame = new JFrame("成績閲覧");// 作成
        myFrame.setSize(400, 450);// ウィンドウの大きさの設定
        myFrame.setLayout(null);// レイアウトマネージャは無し（座標を直接指定）
        // タイトル「対戦成績」の表示
        JLabel title = new JLabel("対戦成績");// 作成
        title.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 25));// フォントの設定
        title.setBounds(140, 0, 200, 40); // 境界の設定
        myFrame.add(title);// ペインに追加
        // 成績本体を表示するためのJPanelの設定
        JPanel p = new JPanel();// 作成
        p.setPreferredSize(new Dimension(350, 22 * num));// サイズの調整（縦はデータの数に応じて大きさを変える）
        // JScrollPaneの設定（pを制御する）
        JScrollPane scrollpane = new JScrollPane(p);// 作成
        scrollpane.setBounds(10, 50, 370, 350); // 境界の設定
        // 成績本体を表示するためのJTextFieldの設定
        JTextField recordText[][] = new JTextField[num][3];
        // 対戦成績本体（1行毎にループを用いてpに貼り付けていく）
        for (int i = 0; i < num; i++) {
            recordText[i][0] = new JTextField(recordName[i], 19); // テキストエリア作成
            recordText[i][1] = new JTextField(Integer.toString(record[i][0]) + "勝" +
                    Integer.toString(record[i][1]) + "敗" +
                    Integer.toString(record[i][2]) + "分", 15);
            recordText[i][2] = new JTextField("投了数" + Integer.toString(record[i][0] + record[i][1] + record[i][2]), 10);
            recordText[i][0].setFont(new Font("ＭＳ ゴシック", Font.BOLD, 13)); // フォントの設定
            recordText[i][1].setFont(new Font("ＭＳ ゴシック", Font.BOLD, 13));
            recordText[i][2].setFont(new Font("ＭＳ ゴシック", Font.BOLD, 13));
            recordText[i][0].setEditable(false); // 編集不可能にする
            recordText[i][1].setEditable(false);
            recordText[i][2].setEditable(false);
            p.add(recordText[i][0]);
            p.add(recordText[i][1]);
            p.add(recordText[i][2]);
        }
        myFrame.add(scrollpane, BorderLayout.CENTER); // データをまとめたJScrollPaneをJFrameに貼り付ける
        myFrame.setVisible(true); // 最後にJFrameの表示処理
    }

    /* 切断のメッセージを表示 */
    public void displayDisconnectionMessage() {
        JLabel label = new JLabel("相手プレイヤの接続が切断されました");
        JOptionPane.showMessageDialog(this, label);
    }

    /* ゲーム全体（ゲーム進行の大枠。マッチングが終了次第Playerプログラムから呼び出される） */
    public boolean game() {
        int turn;
        boolean rematch;

        setVisible(true);
        while (true) {
            int opponentNextOp = 0;
            // 先手・後手の情報（firstMove）をもとに最初の手番と双方の石の色を決定
            if (firstMove) {
                turn = 0;
                myColor = 1;
            } else {
                turn = 1;
                opponentColor = 2;
            }

            // 対戦開始
            initBoard();// 盤面初期化
            displayBoard(board);// 盤面の初期描画
            while (!checkGameEnd(0)) {// 盤面をチェックし、対局終了となるまで繰り返す
                if (turn == 0) {
                    // 自分の手番の場合
                    acceptPlayerMove();// プレイヤ入力の受け付け
                    sendMoveInfo();// 操作情報をサーバに送信
                    if (nextOp == 1)
                        break;// もし投了した場合はそこでゲーム終了（強制的にループを抜ける）
                } else {
                    // 相手の手番の場合
                    opponentNextOp = recieveOpponentMove();// 相手の操作情報をサーバから受信
                    if (opponentNextOp == 1)
                        break;// もし相手が投了したらそこでゲーム終了（強制的にループを抜ける）
                }
                updateBoard();// 盤面の再描画
                if (turn == 1)
                    turn = 0; // 手番を変える
                else if (turn == 0)
                    turn = 1;
            }
            // 対戦終了
            opponentColor = myColor % 2 + 1; // 自分の色でないほうを相手の石の色とする
            int gameResult;
            System.out.println("opponentNextOp = " + opponentNextOp);
            // 対戦結果を判定
            // どちらかが途中で投了した→相手の勝ち そうでなければ石の数を数え、多い方の勝ちとする
            if (nextOp == 1)
                gameResult = 4;// 自分の投了による負け
            else if (opponentNextOp == 1)
                gameResult = 3;// 相手の投了による勝ち
            else if (countStone(board, myColor) > countStone(board, opponentColor))
                gameResult = 0;// 通常の勝ち
            else if (countStone(board, myColor) < countStone(board, opponentColor))
                gameResult = 1;// 通常の負け
            else
                gameResult = 2;// 引き分け
            // 対戦結果を送信
            sendGameResult(gameResult);
            rematch = displayResult(gameResult);// 対戦結果を表示し、再戦するかの選択を受け付ける

            break;
        }
        return rematch;
    }

    public void actionPerformed(ActionEvent e) {
        command = e.getActionCommand();// クリックしたオブジェクトを取得し、ボタンの名前を取り出す
        System.out.println("マウスがクリックされました。押されたボタンは " + command + "です。");// テスト用に標準出力
    }

    /* main（ここからスタート） */
    public static void main(String args[]) {
        Player player = new Player();
        player.setupApp();
    }
}