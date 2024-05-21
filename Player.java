//Player.java
// 工数1 = 1時間

//パッケージのインポート
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Player extends JFrame {

    String playerName = "プレイヤ名"; // プレイヤー名
    int turn; // 先手後手(白黒)情報
    int roomId = -1; // ルームID
    String password; // パスワード
    String playerId; // プレイヤのID
    // String[] loginPlayer = new String[2]; // パスワードをIDを配列にして送る // って話じゃなかったっけ
    int[][] myBoard = new int[8][8]; // 石を置いた場所
    // int[][] gameRecord = new int[][]; // 対戦成績
    // JButton gameSet = new JButton("投了"); // 投了ボタン
    private String command = "";

    // Clientクラスのインスタンスを作成
    Client client = new Client();

    // コンストラクタ
    public Player() {

    }

    // ログイン情報受付可否の変数宣言
    boolean loginAccept = false;

    // 工数2,進捗2
    // アプリ立ち上げ
    public void setupApp() {

        setBounds(100, 100, 600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 接続画面の構成要素
        JFrame frame = new JFrame("ログイン"); // ログイン画面フレーム
        JPanel connectionPanel = new JPanel(); // 全体パネル
        JPanel connectionPanelTop = new JPanel(); // 全体パネル>上部パネル
        JPanel connectionPanelCenter = new JPanel(); // 全体パネル>中央部パネル
        JPanel centerPanelPlayer = new JPanel(); // 中央部パネル>プレイヤ名入力部パネル
        JPanel centerPanelPassword = new JPanel(); // 中央部パネル>パスワード入力部パネル
        JPanel connectionPanelBottom = new JPanel(); // 全体パネル>下部パネル
        JLabel loginMessage = new JLabel("プレイヤ情報を入力してください"); // 主メッセージ
        JLabel cautionMessage = new JLabel("パスワード又はプレイヤ名が間違っています"); // エラー時メッセージ
        JLabel playerNameLabel = new JLabel("プレイヤ名"); // プレイヤー名入力部ラベル
        JLabel passwordLabel = new JLabel("パスワード"); // パスワード入力部ラベル
        JTextField passwordField = new JTextField(20); // パスワード入力部
        JTextField playerNameField = new JTextField(20); // プレイヤー名入力部
        JButton exit = new JButton("終了");
        ; // 終了ボタン
        JButton ok = new JButton("OK"); // OK(入力情報送信)ボタン

        Container contentPane1 = frame.getContentPane();
        frame.setBounds(450, 300, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // クライアントプログラムから接続要求メソッドを呼び出し
        client.connectServer(); // テスト用にコメントアウト
        // boolean connection = true; System.out.println("クライアントプログラムから接続要求メソッドを呼び出し");

        ActionListener connectAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // [OKボタン]が押されたらログイン情報受付メソッドを呼び出し
                if (e.getSource() == ok) {
                    playerName = playerNameField.getText();
                    password = passwordField.getText();
                    // クライアントプログラムからログイン可否を受け取る
                    loginAccept = client.loginInfoAccept(playerName, password); // テスト用にコメントアウト
                    command = "1";
                    // ログイン情報が受け付けられなかった場合はメッセージを表示
                    // if (!loginAccept) {
                    // cautionMessage.setForeground(Color.red);
                    // connectionPanelTop.add(loginMessage);
                    // }
                }
            }
        };

        // 接続できたら
        // ログイン画面描画
        // ログイン情報が受け付けられるまで繰り返し
        // フレーム設定
        frame.setSize(500, 300);
        // 全体のパネル設定(縦に[メッセージ部分のパネル]と[入力部分のパネル]と[ボタン部分のパネル]を並べる)
        connectionPanel.setLayout(new GridLayout(3, 1));
        // メッセージ部分のパネル設定
        connectionPanelTop.setLayout(new FlowLayout());
        // メッセージ部分のラベル設定(上に余白を入れる)
        loginMessage.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 30));
        // 入力部分のパネル設定
        connectionPanelCenter.setLayout(new GridLayout(2, 1));
        // プレイヤ名入力部分のパネル設定
        centerPanelPlayer.setLayout(new FlowLayout());
        // プレイヤ名入力部分のパネル設定
        centerPanelPassword.setLayout(new FlowLayout());
        // ボタン部分のパネル設定(横に[OKボタン]と[終了ボタン]を並べる
        connectionPanelBottom.setLayout(new FlowLayout());
        // ボタン部分の設定
        ok.addActionListener(connectAction);
        ok.setActionCommand("1");
        exit.addActionListener(connectAction);
        exit.setActionCommand("2");
        // 各パーツ配置
        connectionPanelTop.add(loginMessage);
        connectionPanelCenter.add(centerPanelPlayer);
        connectionPanelCenter.add(centerPanelPassword);
        centerPanelPlayer.add(playerNameLabel);
        centerPanelPlayer.add(playerNameField);
        centerPanelPassword.add(passwordLabel);
        centerPanelPassword.add(passwordField);
        connectionPanelBottom.add(ok);
        connectionPanelBottom.add(exit);
        connectionPanel.add(connectionPanelTop);
        connectionPanel.add(connectionPanelCenter);
        connectionPanel.add(connectionPanelBottom);
        contentPane1.add(connectionPanel);
        frame.setVisible(true);

        // ボタンの入力がされるまで待機
        try {
            while (command == "") {
                // System.out.println(command);
                // System.out.println("何も入力されていない時間");
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("Error: InterruptedException (in ログイン画面)");
        }

        // 結果によって分岐
        if (command.equals("1")) {
            contentPane1.removeAll();
            contentPane1.repaint();
        }

        // ログイン情報が受け付けられたらマッチ画面を描画
        if (loginAccept) {
            System.out.println("ログイン情報受付");
            frame.setVisible(false);
            displayMatchScreen();
        }
    }

    int inputRoomId; // 作成したルームID
    boolean successMatching; // ルームID受理

    // 工数1.5,進捗1.5
    // マッチ画面描画
    // (旧:ルームIDを入力)
    public void displayMatchScreen() {

        command = "";

        JFrame matchFrame = new JFrame("マッチ画面"); // フレーム
        JPanel mainPanel = new JPanel(); // 全体を覆うパネル
        JPanel mainPanelHead = new JPanel(); // 上部を覆うパネル
        JPanel roomIdPanel = new JPanel(); // ルームID入力部を覆うパネル
        JLabel playerInfo = new JLabel("ユーザ : " + playerName); // プレイヤ名を表示
        JLabel mainMessage = new JLabel("ルームIDを入力してください");// メインメッセージ表示
        JLabel or = new JLabel("or");// or表示
        JTextField roomIdField = new JTextField(20); // ルームID入力
        JButton playRecord = new JButton("対戦成績閲覧"); // 対戦成績閲覧ボタン
        JButton ok = new JButton("OK"); // OKボタン
        JButton makeNewRoom = new JButton("新規ルーム作成"); // 新規ルーム作成ボタン
        JLabel cautionMessage = new JLabel("新規ルームIDを作成しました");

        Container contentPane2 = matchFrame.getContentPane();
        matchFrame.setBounds(450, 200, 600, 400);
        matchFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ActionListener matchAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // [対戦成績ボタン]が押されたら対戦成績画面表示
                if (e.getSource() == playRecord) {
                    command = "3";
                }
                // [OKボタン]が押されたらルームIDを送信
                else if (e.getSource() == ok) {
                    System.out.println("sasakinodebug command 1");
                    command = "1";
                }
                // [新規ルーム作成ボタン]が押されたらルーム作成へ
                else if (e.getSource() == makeNewRoom) {
                    System.out.println("sasakinodebug command 2");
                    command = "2";
                }
            }
        };

        // ルームIDが受理されるまで繰り返し
        // マッチ画面フレーム
        matchFrame.setSize(500, 300);
        // 全体パネル
        mainPanel.setLayout(new GridLayout(5, 1));
        // 全体パネル>上部パネル
        mainPanelHead.setLayout(new GridLayout(1, 2));
        // 中心部パネル>ルームID入力部パネル
        roomIdPanel.setLayout(new FlowLayout());
        // メッセージ部分
        mainMessage.setHorizontalAlignment(JLabel.CENTER);
        or.setHorizontalAlignment(JLabel.CENTER);
        // 対戦成績閲覧ボタン
        playRecord.addActionListener(matchAction);
        playRecord.setPreferredSize(new Dimension(200, 100));
        // OK(ルームID送信)ボタン
        ok.addActionListener(matchAction);
        ok.setActionCommand("1");
        // 新規ルーム作成ボタン
        makeNewRoom.addActionListener(matchAction);
        makeNewRoom.setPreferredSize(new Dimension(200, 100));
        // 各パーツ配置
        mainPanelHead.add(playerInfo);
        mainPanelHead.add(playRecord);
        roomIdPanel.add(roomIdField);
        roomIdPanel.add(ok);
        mainPanel.add(mainPanelHead);
        mainPanel.add(mainMessage);
        mainPanel.add(roomIdPanel);
        mainPanel.add(or);
        mainPanel.add(makeNewRoom);
        contentPane2.add(mainPanel, BorderLayout.CENTER);
        matchFrame.setVisible(true);

        while (true) {
            System.out.println("sasakinodebug infinit");
            // ボタンの入力がされるまで待機
            // try {
            // command = "";
            // while (command == "") {
            // System.out.println("何も入力されていない時間");
            // Thread.sleep(100);
            // }
            // } catch (InterruptedException e) {
            // System.out.println("Error: InterruptedException (in マッチ画面)");
            // }

            // 結果によって分岐
            if (command.equals("1")) {
                if (roomIdField.getText().matches("^\\d{1,9}$")) {
                    System.out.println("マッチ画面再描画");
                    roomId = Integer.parseInt(roomIdField.getText());
                    System.out.println(roomId + "が入力された");
                    // クライアントプログラムから該当ルームの有無を受け取る
                    inputRoomId = client.acceptRoomID(roomId); // テスト用にコメントアウト
                    System.out.println(inputRoomId + "でルームIDが返された");
                    // System.out.println("クライアントプログラムから該当ルームの有無を受け取る");
                    if (inputRoomId != roomId) {
                        // ルームIDが存在しなかった場合は新規ルーム作成
                        roomId = inputRoomId;
                        matchFrame.setVisible(false);
                        makeRoom(playerName);
                        matchFrame.setVisible(true);
                        System.out.println("ルームが存在しなかったため新規ルームを作成");
                    } else {
                        successMatching = true;
                        System.out.println(inputRoomId + "のルームがあったので入室");
                    }
                    roomId = Integer.parseInt(roomIdField.getText());
                    System.out.println(roomId + "が入力された");
                    // クライアントプログラムから該当ルームの有無を受け取る
                    inputRoomId = client.acceptRoomID(roomId);
                    System.out.println(inputRoomId + "でルームIDが返された");
                    // System.out.println("クライアントプログラムから該当ルームの有無を受け取る");
                    if (inputRoomId != roomId) {
                        // ルームIDが存在しなかった場合は新規ルーム作成
                        roomId = inputRoomId;
                        matchFrame.setVisible(false);
                        makeRoom(playerName);
                        matchFrame.setVisible(true);
                        System.out.println("ルームが存在しなかったため新規ルームを作成");
                    } else {
                        successMatching = true;
                        System.out.println("サーバからの相手のユーザ名情報の送信待ち");
                        client.opponentName = client.getServerMessage();// 今度は相手のユーザ名が送られてくるのでそれを受け取って反映
                        System.out.println("サーバからの相手のユーザ名情報を受け取りました");
                        System.out.println(inputRoomId + "のルームがあったので入室");

                    }
                }

                if (command.equals("2")) {
                    matchFrame.setVisible(false);
                    System.out.println("sasakinodebug jump1");
                    makeRoom(playerName);
                    System.out.println("sasakinodebug jump2");
                    matchFrame.setVisible(true);
                    System.out.println("sasakinodebug jump3");
                }

                if (command.equals("3")) {
                    displayPlayRecord();
                    command = "";
                }

                // ルームIDが受理されたらマッチ確認画面へ遷移
                if (successMatching) {
                    matchFrame.setVisible(false);
                    client.firstMove = false; // 自分が後手であることをクライアントに通知
                    // マッチ確認画面描画
                    displayMatching();
                    matchFrame.setVisible(true);
                }
            }
        }
    }

    // 工数0
    // 対戦成績の閲覧
    public void displayPlayRecord() {
        // クライアントプログラムで対戦成績を描画
        System.out.println("対戦成績閲覧を選択");
        client.displayGameRecord();
        System.out.println("クライアントプログラムで対戦成績を描画");
    }

    // 工数1,進捗1
    // ルームの作成
    public void makeRoom(String playerName) {

        command = "2";

        // IDが-1のときはルーム未作成を表す
        // if (roomId == -1) {
        // クライアントプログラムからルームIDの取得
        roomId = client.getRoomID(); // テスト用にコメントアウト
        System.out.println("クライアントプログラムからルームIDを取得");
        // }

        JFrame roomFrame = new JFrame("ルーム作成"); // ルーム作成画面
        JPanel roomPanel = new JPanel(); // 全体を覆うパネル
        JPanel roomIdPanel = new JPanel(); // ルームID表示部パネル
        JPanel roomPanelTop = new JPanel(); // 上部パネル
        JLabel roomIdIs = new JLabel("あなたのルームIDは");
        JLabel roomIdLabel = new JLabel(Integer.toString(roomId));
        JLabel desuLabel = new JLabel("です");
        JLabel waiting = new JLabel("マッチ待機中");
        JButton exitRoom = new JButton("キャンセル");

        roomFrame.setBounds(450, 300, 600, 400);
        roomFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ActionListener makeRoomAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // クライアントプログラムを介してサーバプログラムにルームIDを削除してもらう
                System.out.println("sasakinodebug");
                client.getRoomID(); // テスト用にコメントアウト
                System.out.println("クライアントプログラムを介してサーバプログラムにルームIDを削除してもらう");
                // マッチ画面に戻る
                roomFrame.setVisible(false);
                roomId = -1; // ルーム未作成を表すID -1 に変更
                command = "";
            }
        };

        // ルーム作成画面のフレーム
        roomFrame.setSize(500, 300);
        // 画面全体のパネル
        roomPanel.setLayout(new GridLayout(4, 1));
        // 上部パネル
        roomPanelTop.setLayout(new BorderLayout());
        // ルームID表示部のパネル
        roomIdPanel.setLayout(new FlowLayout());
        // ルームIDは少し大きく表示する
        roomIdLabel.setFont(new Font("Century", Font.BOLD, 30));
        // マッチ待機中表示はグレーにする
        waiting.setForeground(Color.gray);
        waiting.setHorizontalAlignment(JLabel.CENTER);
        // [キャンセルボタン]の設定
        exitRoom.addActionListener(makeRoomAction);
        exitRoom.setActionCommand("cancel");
        // ルームIDは」の表示
        roomIdIs.setHorizontalAlignment(JLabel.CENTER);

        // 配置
        roomPanel.add(roomPanelTop);
        roomPanel.add(roomIdIs);
        roomPanel.add(roomIdPanel);
        roomPanel.add(waiting);
        roomPanelTop.add(exitRoom, BorderLayout.EAST);
        roomIdPanel.add(roomIdLabel);
        roomIdPanel.add(desuLabel);
        Container contentPane3 = roomFrame.getContentPane();
        contentPane3.add(roomPanel, BorderLayout.CENTER);
        roomFrame.setVisible(true);

        // ボタンの入力がされるまで・または対戦相手が現れるまで待機
        try {
            command = "";
            while (command == "") {
                Thread.sleep(1000);
                if (client.checkServerMessage()) {// クライアントプログラムが対戦相手が現れた旨のメッセージを受け取ったら
                    roomFrame.setVisible(false);
                    client.firstMove = true; // 自分が先手であることをクライアントに通知
                    if (client.checkServerMessage()) {// クライアントプログラムが対戦相手が現れた旨のメッセージを受け取った場合
                        System.out.println("サーバからの相手のユーザ名情報の送信待ち");
                        client.opponentName = client.getServerMessage();// 今度は相手のユーザ名が送られてくるのでそれを受け取って反映
                        System.out.println("サーバからの相手のユーザ名情報を受け取りました");
                        roomFrame.setVisible(false);
                        System.out.println("display呼び出しB");
                        displayMatching();// マッチ確認画面に移行
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Error: InterruptedException (in ルーム作成画面)");
        }

        // 結果によって分岐
        if (command.equals("cancel")) {
            System.out.println("ルーム作成再描画");
            contentPane3.removeAll();
            contentPane3.repaint();
        }
    }

    // 工数0.5,進捗0.5
    // マッチ確認画面描画
    public void displayMatching() {

        JFrame matchingFrame = new JFrame("対戦相手確認");
        JPanel matchingPanel = new JPanel();
        JPanel opponentPanel = new JPanel();
        JLabel opponentIs = new JLabel("あなたの対戦相手は");
        JLabel opponentName = new JLabel("Student"); // Studentは仮名
        JLabel desuLabel = new JLabel("さんです");
        JButton startGame = new JButton("対戦開始");

        matchingFrame.setBounds(450, 300, 600, 400);
        matchingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // マッチ確認画面のフレーム
        matchingFrame.setSize(500, 300);
        // 画面全体のパネル
        matchingPanel.setLayout(new BorderLayout());
        // 対戦相手表示部パネル
        opponentPanel.setLayout(new FlowLayout());
        // 対戦相手の名前は少し大きく表示する
        opponentName.setFont(new Font("Century", Font.BOLD, 30));
        // [対戦開始ボタン]の設定
        ActionListener displayMatchingAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                command = "start";
            }
        };
        startGame.addActionListener(displayMatchingAction);
        // 配置
        matchingPanel.add(opponentIs, BorderLayout.NORTH);
        matchingPanel.add(opponentPanel, BorderLayout.CENTER);
        opponentPanel.add(opponentName);
        opponentPanel.add(desuLabel);
        matchingPanel.add(startGame, BorderLayout.SOUTH);
        Container contentPane4 = matchingFrame.getContentPane();
        contentPane4.add(matchingPanel, BorderLayout.CENTER);
        matchingFrame.setVisible(true);
        System.out.println("全体表示");

        command = "";
        // ボタンの入力がされるまで待機
        try {
            while (command == "") {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Error: InterruptedException (in マッチ確認画面描画)");
        }
        if (command.equals("start")) {
            matchingFrame.setVisible(false);
            // クライアントプログラムの対戦画面へ
            System.out.println("クライアントプログラムの対戦画面へ");
            boolean rematch = client.game();
            if (rematch)
                makeRoom(playerName);// 再戦する場合は同じルームIDで再び待機
        }
    }

    // 工数0
    // プレイヤ名を受付
    public void acceptPlayerName(String name) {
        playerName = name;
    }

    // 工数0
    // プレイヤ名を取得
    public String getPlayerName() {
        return playerName;
    }

    // 工数0
    // 先手後手(白黒)情報を取得
    public void getTurn(String turnInfo) { // 引数はプレイヤ名、?
        if (turnInfo == playerName) {
            turn = 0; // 自分のターン
            client.acceptPlayerMove(); // テスト用にコメントアウト
            // System.out.println("操作");
        } else {
            turn = 1; // 相手のターン
        }
    }

    // 工数0
    // 盤面の石の位置情報を送信
    public void sendPosition(int[][] board) {
        myBoard = board;
    }

    // 工数0
    // 盤面の石の位置情報を受信
    public int[][] getPosition() {
        return myBoard;
    }

    // 工数0
    // 投了受付
    public boolean setGame(JButton resign) {
        return true;
    }

    public static void main(String[] args) {
        Player player = new Player();
        player.setupApp();
    }

}