//クライアントプログラム 担当: さどゆう

//パッケージのインポート
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends JFrame implements MouseListener{
    //外部に公開する変数
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
    boolean firstMove; //0なら自分の手番、1（0以外）なら相手の手番
    //定数
    final int row = 8;  //オセロ盤の行数・列数
    //プライベート変数
    private int windowSizeX, windowSizeY; //ウィンドウのサイズ
    private JButton boardButtonArray[][]; //オセロ盤上の8×8のボタン配列
    private JButton resignButton, pass; //投了・パスボタン
    private ImageIcon blackIcon, whiteIcon, boardIcon; //アイコン
    private Container c; //ペインを取得するコンテナ
    private int[][] board = new int[row][row]; //現在の盤面
    private JTextArea stoneInfoText; //石の数を表示するテキストエリア
    private JTextArea InstText; //操作指示を表示するテキストエリア
    private String command; //どのボタンが押されたかを識別するコマンド
    private int buttonCommand = 0;

    /*コンストラクタ*/
    public Client() {
        //ウィンドウ設定
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じる場合の処理
        setTitle("ネットワーク対戦型オセロゲーム");//ウィンドウのタイトル
        windowSizeX = row * 45 + 45; //ウィンドウのサイズ計算(幅)
        windowSizeY = row * 45 + 210; //ウィンドウのサイズ計算(高さ)
        setSize(windowSizeX,windowSizeY); //計算したサイズを反映
        c = getContentPane();//フレームのペインを取得
        //アイコン設定
		whiteIcon = new ImageIcon("./PL7/White.jpg");
		blackIcon = new ImageIcon("./PL7/Black.jpg");
		boardIcon = new ImageIcon("./PL7/GreenFrame.jpeg");
		c.setLayout(null);
        //オセロ盤に必要な情報を生成
		boardButtonArray = new JButton[row][row];//ボタンの配列を作成      
    }

    /*接続要求　工数:0.25*/
    public boolean connectDemand() {
        //接続要求をする

            //(保留)

        //接続に失敗した場合はfalseを返す（テスト用にtrueで返しています）
        if(true) return true;
        else return false;
    }

    /*再接続選択　工数:0.5*/
    public boolean reconnectSelect() {
        //画面表示をリセット
        c.removeAll();
        //再接続を問うメッセージ
        JLabel message1 = new JLabel("サーバとの接続に失敗しました。再接続しますか？");//メッセージ作成
        message1.setBounds(windowSizeX / 2 - 150, windowSizeY / 2 - 70, windowSizeX, 30);//境界を設定
        c.add(message1);//ペインに追加
        //選択肢のボタン
        JButton button1 = new JButton("はい");//メッセージ作成
        JButton button2 = new JButton("いいえ(終了)");//メッセージ作成
        button1.addActionListener(new ActionListener() { //ボタンをactionListenerに追加（押された際の挙動の設定）
            public void actionPerformed(ActionEvent e) {
                //「はい」が押されたときのコマンド数値
                buttonCommand = 1;
            }
        });
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //「いいえ(終了)」が押されたときのコマンド数値
                buttonCommand = 2;
            }   
        });
        button1.setBounds(15, row * 45 + 30, (row * 45 ) / 2 - 5, 30); //境界を設定
        button2.setBounds((row * 45 ) / 2 + 20, row * 45 + 30, (row * 45 ) / 2 - 5, 30); //境界を設定
        c.add(button1);//ペインに追加
        c.add(button2);//ペインに追加
        c.repaint();//再描画
        buttonCommand = 0;  //コマンドの入力内容を検知する変数をリセットしておく
        //ボタンの入力がされるまで待機
        try{
            while(buttonCommand == 0){
                Thread.sleep(100);
            }
        }catch(InterruptedException e) {
            System.out.println("Error: InterruptedException (in 再接続選択)");
		}
        //結果によって分岐
        if(buttonCommand == 1){
            //「はい」
            //「再接続を試みます」のメッセージを2秒間表示
            c.removeAll();
            c.repaint();
            JLabel message2 = new JLabel("再接続を試みます");
            message2.setBounds(windowSizeX / 2 - 50, windowSizeY / 2 - 70, windowSizeX, 30);//境界を設定
            c.add(message2);//ペインに追加
            try{
                Thread.sleep(2000);
            }catch(InterruptedException e) {
                System.out.println("Error: InterruptedException (in 再接続選択)");
            }
            return true;
        }
        //「いいえ」
        else return false;
    }

    /*ログイン情報受付　工数:0.5（Playerに移動？）*/
    public boolean loginInfoAccept(String x, int y) {
        return false;
    }

    /*プレイヤ名表示　工数:0.5（Playerに移動？）*/
    public void displayPlayerName(String x) {
    }

    /*対戦成績の表示　工数:3（Playerに移動？）*/
    public void displayPlayRecord(int[][] x) {
    }

    /*ルームID表示　工数:1（Playerに移動？）*/
    public void displayRoomID(int roomID) {
    }

    /*プレイヤのID情報送信　工数:0.5（Playerに移動？）*/
    public void sendPlayerID(int playerID) {
    }

    /*入力されたルームID受付　工数:1（Playerに移動？）*/
    public boolean acceptRoomID() {
        return false;
    }

    /*サーバに接続　工数:0.5*/
    public void connectServer() {
        while(true){
            if(connectDemand()) break; //接続成功したらループを抜ける  
            else if(!reconnectSelect()) System.exit(0); //接続失敗かつ、再接続しない選択をした場合は終了
            //接続失敗かつ、再接続する選択をした場合はこのままループの先頭に戻る
        }
    }

    /*盤面の初期化　工数:0.5*/
    public void initBoard() {
        //盤面の石をすべて取り除く
        for(int i=0; i<row; i++){
            for(int j=0; j<row; j++){
                board[i][j] = 0;
            }
        }
        //オセロの最初の4石を設定
        board[3][3] = 1;
        board[4][4] = 1;
        board[3][4] = 2;
        board[4][3] = 2;
    }

    /*盤面を描画　工数:3*/
    public int[][] displayBoard(int[][] board) {
        System.out.println("描画中・・・");
        for(int i=0; i<row; i++){
            for(int j=0; j<row; j++){
                //石（もしくは背景の緑色）の描画
                //各マスの石の状態によって、描画するものを変える
                if(board[i][j] == 0){ boardButtonArray[i][j] = new JButton(boardIcon);} //アイコン（石無し、緑）
                else if(board[i][j] == 1){ boardButtonArray[i][j] = new JButton(blackIcon);} //アイコン（黒）
                else if(board[i][j] == 2){ boardButtonArray[i][j] = new JButton(whiteIcon);} //アイコン（白）
                c.add(boardButtonArray[i][j]); //各ボタンをペインに貼り付け
                //マス上にボタンを配置する
                int x = i * 45 + 15; //x座標
                int y = j * 45 + 15; //y座標
                boardButtonArray[i][j].setBounds(x, y, 45, 45); //ボタンの大きさと位置の設定
                boardButtonArray[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //このボタンが押されたときのコマンド数値
                        buttonCommand = 100 + 1 * row + 1;
                    }   
                });
		    }
        }
		//投了ボタン
		resignButton = new JButton("投了");//ボタンの作成
		resignButton.setBounds(15, row * 45 + 30, (row * 45 ) / 2 - 5, 30);//ボタンの境界を設定
	    resignButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //このボタンが押されたときのコマンド数値
                buttonCommand = 201;
            }   
        });
		//パスボタン
		pass = new JButton("パス");//ボタンの作成
		pass.setBounds((row * 45 ) / 2 + 20, row * 45 + 30, (row * 45 ) / 2 - 5, 30);//ボタンの境界を設定
		pass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //このボタンが押されたときのコマンド数値
                buttonCommand = 202;
            }   
        });
        //石の数を表示するテキストエリア
            //詳細な表示はあとで作ります
        stoneInfoText = new JTextArea("あなた(黒): " + countStone(board,1) + "\n[相手プレイヤ名](白): " + countStone(board,2)); //テキストエリア作成
        stoneInfoText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16)); //フォントの設定
        stoneInfoText.setBounds(10, row * 45 + 70 , row * 45 + 10, 40); //境界の設定
        stoneInfoText.setEditable(false); //編集不可能にする
        //操作指示を表示するテキストエリア
        InstText = new JTextArea("あなたの番です\n石を置くマスをクリックしてください",3,20); //テキストエリア作成
        InstText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16)); //フォントの設定
        InstText.setBounds(10, row * 45 + 120 , row * 45 + 10, 40); //境界の設定
        InstText.setEditable(false); //編集不可能にする
        //上記2つのボタンと2つのテキストエリアをペインに貼り付け                                                                      
        c.add(resignButton);
        c.add(pass);
        c.add(stoneInfoText);
        c.add(InstText);
        repaint();
        return board;
    }

    /*対戦相手の操作情報を受信　工数:0.25*/
    public int[][] recieveOpponentMove(int[][] x) {
        //***テスト用として、こちらのプログラム側で盤面を変更しています***//
        for(int i=0; i<row; i++){
            for(int j=0; j<row; j++){
                x[i][j] = 1;
            }
        }
        System.out.println("対戦相手の操作完了");
        return x;
    }

    /*プレイヤの操作を受付　工数:0.5*/
    public int[][] acceptPlayerMove(int[][] x) {
        buttonCommand = 0;
        //ボタンの入力がされるまで待機
        try{
            while(buttonCommand == 0){
                Thread.sleep(100);
            }
        }catch(InterruptedException e) {
            System.out.println("Error: InterruptedException (in 再接続選択)");
		}
        return x;
    }

    /*操作情報を送信　工数:0.5*/
    public void sendMoveInfo(int[][] x) {
        
    }

    /*盤面に反映　工数:0.5*/
    public void updateBoard(int[][] x) {
        c.removeAll();      //現在表示されているコンポーネントを削除
        displayBoard(x);    //再び描画
        repaint();
    }

    /*石の数のカウント*/
    public int countStone(int[][] board, int color) {
        //0なら黒、1なら白、2なら空白マスがいくつあるかを数える
        int count = 0;
        for(int i=0; i<row; i++){
            for(int j=0; j<row; j++){
                if(board[i][j] == color)    count++;
            }
        }
        return count;
    }

    /*対戦終了確認　工数:0.5*/
    public boolean checkGameEnd(int x) {
        /*テスト用に値を直接指定しています*/
        boolean whiteMovable = false;
        boolean blackMovable = false;
        //白黒双方が石を置けるかの情報をOthelloクラスから受け取り、どちらも置けない場合は対戦終了
        if(!whiteMovable & blackMovable) return true;
        else return false;
    }

    /*対戦結果をサーバに送信　工数: 0.25*/
    public void sendGameResult(int result) {
        
    }

    /*勝敗分を表示　工数:0.5*/
    public void displayResult(int x) {
        
    }

    /*切断のメッセージを表示　工数:0.5*/
    public void displayDisconnectionMessage() {
        
    }

    /*投了受付　工数:0.5*/
    public int acceptResign(boolean x) {
        
        return 0;
    }

    public void mouseClicked(MouseEvent e) {
		command = ((JButton)e.getComponent()).getActionCommand();//クリックしたオブジェクトを取得し、ボタンの名前を取り出す
		System.out.println("マウスがクリックされました。押されたボタンは " + command + "です。");//テスト用に標準出力
	}
    public void mouseEntered(MouseEvent e) {}//マウスがオブジェクトに入ったときの処理
	public void mouseExited(MouseEvent e) {}//マウスがオブジェクトから出たときの処理
	public void mousePressed(MouseEvent e) {}//マウスでオブジェクトを押したときの処理
	public void mouseReleased(MouseEvent e) {}//マウスで押していたオブジェクトを離したときの処理
        
    public static void main(String args[]){
        int turn; 

        Client tc = new Client();//クライアントのインスタンス作成
        tc.setVisible(true);
        
        //サーバへの接続
        tc.connectServer();

        while(true){
            //ルーム作成画面などをここに
            //ひとまず仮にこちら側がルームを作ったことにしておく
            turn = 0;

            //対戦開始
            tc.initBoard();
            tc.displayBoard(tc.board);
            while(!tc.checkGameEnd(0)){
                
                if(turn == 0){
                    tc.board = tc.acceptPlayerMove(tc.board);
                    tc.sendMoveInfo(tc.board);
                }else{
                    tc.board = tc.recieveOpponentMove(tc.board);
                }
                tc.updateBoard(tc.board);
            }
            tc.sendGameResult(0);
            tc.displayResult(0);
        }
    }
}



