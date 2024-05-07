// 工数1 = 1時間

import Client

//パッケージのインポート
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Player extends JFrame{

    String playerName; // プレイヤー名
    int turn; // 先手後手(白黒)情報
    int roomId; // ルームID
    String password; // パスワード
    String playerId; // プレイヤのID
    String[] loginPlayer = new int[2]; // パスワードをIDを配列にして送る
    int[][] myBoard = new int[8][8]; // 石を置いた場所
    int[][] gameRecord = new int[1][3]; // 対戦成績
    JButton gameSet = new JButton("投了"); // 投了ボタン 

    // Clientクラスのインスタンスを作成
    Client client = new Client();

    // 工数2,進捗1.8
    // アプリ立ち上げ
    public void setupApp(){
        // 接続画面の構成要素
        JFrame frame; // ログイン画面フレーム
        JPanel connectionPanel; // 全体パネル
        JPanel connectionPanelTop; // 全体パネル>上部パネル
        JPanel connectionPanelCenter; // 全体パネル>中央部パネル
        JPanel connectionPanelBottom; // 全体パネル>下部パネル
        JLabel loginMessage; // 主メッセージ
        JLabel cautionMessage; // エラー時メッセージ
        JLabel playerNameLabel; // プレイヤー名入力部ラベル
        JLabel passwordLabel; // パスワード入力部ラベル
        JTextField passwordField; // パスワード入力部
        JTextField playerNameField; //プレイヤー名入力部
        JButton exit; // 終了ボタン
        JButton ok; // OK(入力情報送信)ボタン
        // ログイン情報受付可否の変数宣言
        boolean loginAccept = false;
        // 接続要求メソッドを呼び出し
        boolean connection = client.connectDemand();

        // 接続できるまで
        // 再接続確認画面描画
        while(!connection){
            // Clientに再接続選択画面を描画してもらう
            connection = client.reconnectSelect();
        }

        // ボタンのイベント処理
        ActionListener connectAction = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // [終了ボタン]が押されたら再接続確認画面のフレーム表示を無効にする
                if(e.getSource()==exit){
                    connectionFrame.setVisible(false);
                }
                // [OKボタン]が押されたらログイン情報受付メソッドを呼び出し
                else if(e.getSource()==ok){
                    playerName = playerNameField.getText();
                    password = passwordField.getText();
                    loginPlayer[0] = playerName;
                    loginPlayer[1] = password;
                    loginAccept = client.loginInfoAccept(loginPlayer);
                    // ログイン情報が受け付けられなかった場合はメッセージを表示
                    if(!loginAccept){
                        cautionMessage = new JLabel("パスワード又はログインIDが間違っています");
                        cautionMessage.setForeground(Color.red);
                        connectionPanelTop.add(loginMessage);
                    }
                }
            }
        }

        // 接続できたら
        // ログイン画面描画
        // ログイン情報が受け付けられるまで繰り返し
        while(!loginAccept){
            // フレーム設定
            frame = new JFrame("ログイン");
            frame.setSize(500,300);
            frame.setVisible(true);
            // 全体のパネル設定(縦に[メッセージ部分のパネル]と[入力部分のパネル]と[ボタン部分のパネル]を並べる)
            connectionPanel = new JPanel();
            connectionPanel.setLayout(new GridLayout(1,3));
            // メッセージ部分のパネル設定(縦に各種メッセージを並べる)
            connectionPanelTop = new JPanel();
            connectionPanelTop.setLayout(new GridLayout(1,2));
            // ログインメッセージの設定
            loginMessage = new JLabel("プレイヤ情報を入力してください");
            // 入力部分のパネル設定(2×2で[ラベル]と[入力部]をプレイヤ名とパスワードそれぞれ順番に並べる)
            connectionPanelCenter = new JPanel();
            connectionPanelCenter.setLayout(new GridLayout(2,2));
            // 入力部分の設定
            playerNameLabel = new JLabel("プレイヤ名");
            playerNameField = new JTextField(20);
            passwordLabel = new JLabel("パスワード");
            passwordField = new JTextField(20);
            // ボタン部分のパネル設定(横に[OKボタン]と[終了ボタン]を並べる)
            connectionPanelBottom = new JPanel();
            connectionPanelBottom.setLayout(new FrowLayout());
            // ボタン部分の設定
            ok = new JButton("OK");
            ok.addActionListener(connectAction);
            exit = new JButton("終了");
            exit.addActionListener(connectAction);
            // 各パーツ配置
            connectionPanelTop.add(loginMessage);
            connectionPanelCenter.add(playerNameLabel);
            connectionPanelCenter.add(playerNameField);
            connectionPanelCenter.add(passwordLabel);
            connectionPanelCenter.add(passwordField);
            connectionPanelBottom.add(ok);
            connectionPanelBottom.add(exit);
            connectionPanel.add(connectionPanelTop);
            connectionPanel.add(connectionPanelCenter);
            connectionPanel.add(connectionPanelBottom);
        }

        // ログイン情報が受け付けられたらマッチ画面を描画
        if(loginAccept){
            frame.setVisible(false);
            displayMatchScreen();
        }
    }

    // 工数1,進捗0.8
    // マッチ画面描画
    // (旧:ルームIDを入力)
    public void displayMatchScreen(){
        JFrame matchFrame; // フレーム
        JPanel mainPanel; // 全体を覆うパネル
        JPanel mainPanelHead; // 上部を覆うパネル
        JPanel mainPanelBody; // 中心部を覆うパネル
        JPanel roomIdPanel; // ルームID入力部を覆うパネル
        JLabel playerNameInfo; // プレイヤ名を表示
        JLabel playerIdInfo; // ルームIDを表示
        JLabel mainMessage; //メインメッセージ表示
        JLabel or; // or表示
        JTextField roomIdField; // ルームID入力
        JButton playRecord; // 対戦成績閲覧ボタン
        JButton ok; // OKボタン
        JButton makeNewRoom; // 新規ルーム作成ボタン
        boolean successMatching = false; // ルームID受理
        JLabel cautionMessage; // エラー時メッセージ

        ActionListener matchAction = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // [対戦成績ボタン]が押されたら対戦成績画面表示
                if(e.getSource()==playRecord){
                    matchFrame.setVisible(false);
                    displayPlayRecord();
                }
                // [OKボタン]が押されたらルームIDを送信
                else if(e.getSource()==ok){
                    roomId = roomIdField.getText();
                    successMatching = client.acceptRoomID(roomId);
                    if(!successMatching){
                        // ルームIDが受理されなかった場合はエラーメッセージを表示
                        cautionMessage = new JLabel("このルームIDは使用できません");
                        cautionMessage.setForeground(Color.red);
                        mainPanelBody.add(loginMessage);
                    }
                }
                // [新規ルーム作成ボタン]が押されたらルーム作成へ
                else if(e.getSource()==makeNewRoom){
                    matchFrame.setVisible(false);
                    makeRoom(playerId);
                }
            }
        }

        // ルームIDが受理されるまで繰り返し
        while(!successMatching){
            // マッチ画面フレーム
            matchFrame = new JFrame();
            matchFrame.setSize(500.300);
            matchFrame.setVisible(true);
            // 全体パネル
            mainPanel = new JPanel();
            // 全体パネル>上部パネル
            mainPanelHead = new JPanel();
            mainPanelHead.setLayout(new GridLayout(2,1));
            // 全体パネル>中心部パネル
            mainPanelBody = new JPanel();
            mainPanelBody.setLayout(new GridLayout(1,5));
            // 中心部パネル>ルームID入力部パネル
            roomIdPanel = new JPanel();
            roomIdPanel.setLayout(new FrowLayout());
            // プレイヤ名表示
            playerName = client.displayPlayerName(playerId);
            playerInfo = new JLabel(playerName+"\n(ID:"+playerId+")");
            // ルームIDを入力してください
            mainMessage = new JLabel("ルームIDを入力してください");
            // or
            or = new JLabel("or");
            // ルームID入力部
            roomIdField = new JTextField(20);
            // 対戦成績閲覧ボタン
            playRecord = new JButton("対戦成績閲覧");
            playRecord.addActionListener(matchAction);
            // OK(ルームID送信)ボタン
            ok = new JButton("OK");
            ok.addActionListener(matchAction);
            // 新規ルーム作成ボタン
            makeNewRoom = new JButton("新規ルーム作成");
            makeNewRoom.addActionListener(matchAction);
            // 各パーツ配置
            mainPanelHead.add(playerInfo);
            mainPanelHead.add(playRecord);
            mainPanelBody.add(mainMessage);
            mainPanelBody.add(roomIdPanel);
            roomIdPanel.add(roomIdField);
            roomIdPanel.add(ok);
            mainPanelBody.add(or);
            mainPanelBody.add(makeNewRoom);
        }

        // ルームIDが受理されたらマッチ確認画面へ遷移
        if(successMatching){
            matchFrame.setVisible(false);
            // マッチ確認画面描画
            displayMatching();
        }
    }

    // 工数0
    // 対戦成績の閲覧
    public void displayPlayRecord(){
        // Clientに対戦成績を描画してもらう
        client.displayPlayRecord();
    }

    // 工数1,進捗1
    // ルームの作成
    public void makeRoom(int playerID){
        //ルームIDの取得
        roomId = client.displayRoomID(playerID)

        JFrame roomFrame = new JFrame();
        JPanel roomPanel = new JPanel();
        JPanel roomPanelTop = new JPanel();
        JPanel roomPanelCenter = new JPanel();
        JPanel roomIdPanel = new JPanel();
        JLabel roomIdIs = new JLabel("あなたのルームIDは");
        JLabel roomIdLabel = new JLabel(roomId);
        JLabel desuLabel = new JLabel("です");
        JLabel waiting = new JLabel("マッチ待機中");
        JButton exitRoom = new JButton("キャンセル");

        // ルーム作成画面のフレーム
        roomFrame.setSize(500.300);
        roomFrame.setVisible(true);
        // 画面全体のパネル
        roomPanel.setLayout(new GridLayout(1,2));
        // 画面上部のパネル
        roomPanelTop.setLayout(new BoxLayout.Y_AXIS);
        // 画面中央部のパネル
        roomPanelCenter.setLayout(new GridLayout(1,3));
        // ルームID表示部のパネル
        roomIdPanel.setLayout(new GridLayout(2,1));
        // ルームIDは少し大きく表示する
        roomIdLabel.setFont(new Font("Century",Font.BOLD,30));
        // マッチ待機中表示はグレーにする
        waiting.setForeground(Color.gray);
        // [キャンセルボタン]の設定
        exitRoom.setAlignmentX(getComponent.RIGHT_ALIGNMENT);
        exitRoom.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                // ルームIDを削除する
                client.deleteRoom();
                // マッチ画面に戻る
                roomFrame.setVisible(false);
                displayMatchScreen();
            }
        });

        roomPanel.add(roomPanelTop);
        roomPanel.add(roomPanelCenter);
        roomPanelTop.add(exitRoom);
        roomPanelCenter.add(roomIdIs);
        roomPanelCenter.add(roomIdPanel);
        roomPanelCenter.add(waiting);
        roomIdPanel.add(roomIdLabel);
        roomIdPanel.add(desuLabel);
    }

    // マッチ確認画面描画
    public void displayMatching(){
        JFrame matchingFrame = new JFrame();
        JPanel matchingPanel = new JPanel();
        JPanel opponentPanel = new JPanel();
        JLabel opponentIs = new JLabel("あなたの対戦相手は");
        JLabel opponentName = new JLabel("Student"); // Studentは仮名
        JLabel desuLabel = new Label("さんです");
        JButton startGame = new JButton("対戦開始");

        // マッチ確認画面のフレーム
        matchingFrame.setSize(500.300);
        matchingFrame.setVisible(true);
        // 画面全体のパネル
        matchingPanel.setLayout(new GridLayout(1,3));
        // 対戦相手表示部パネル
        opponentPanel.setLayout(new FrowLayout());
        // 対戦相手の名前は少し大きく表示する
        opponentName.setFont(new Font("Century",Font.BOLD,30));
        // [対戦開始ボタン]の設定
        startGame.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                matchingFrame.setVisible(false);
                // 対戦画面へ
                client.displayBoard(myBoard);
            }
        });

        // 配置
        matchingPanel.add(opponentIs);
        matchingPanel.add(opponentPanel);
        opponentPanel.add(opponentName);
        opponentPanel.add(desuLabel);
        matchingPanel.add(startGame);
    }

    // 工数1
    // プレイヤ名を受付
    public void acceptPlayerName(String name){
        playerName = name;
    }

    // 工数0
    // プレイヤ名を取得
    public string getPlayerName(){
        return playerName;
    }

    // 工数0.5
    // 先手後手(白黒)情報を取得
    public void getTurn(String turnInfo){ // 引数はプレイヤ名、?
        if(turnInfo == playerName){
            turn = 0; //　自分のターン
        }else{
            turn = 1; // 相手のターン
        }
    }

    // 工数1.5
    // 盤面の石の位置情報を送信
    public void sendPosition(int[][] board){
        myBoard = board;
    }

    // 工数1.5
    // 盤面の石の位置情報を受信
    public int[][] getPosition(){ 
        return myBoard;
    }

    // 工数0
    // 投了受付
    public boolean setGame(JButton resign){
        resign.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                return true;
            }
        });
    }
    
}
