//クライアントプログラム 担当: さどゆう


//TODO
//各ボタンをクリックした際のコマンドが設定完了した
//NEXT→対局中にボタンを押したときの実際の挙動を実装する


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame implements ActionListener{
    //外部に公開する変数
    boolean connectionStatus = false;
    String playerName = new String();
    String password = new String();
    boolean loginSuccess = false;
    int[][] gameRecord = new int[1][3];
    JButton viewExit = new JButton();
    int roomID = 0;
    boolean matchSuccess = false;
    boolean winFlag = false;
    boolean drawFlag = false;
    boolean resign = false;
    boolean firstMove; //0なら自分の手番、1（0以外）なら相手の手番
    //定数
    final int row = 8;  //オセロ盤の行数・列数
    //プライベート変数
    private int windowSizeX, windowSizeY; //ウィンドウのサイズ
    private JButton boardButtonArray[][]; //オセロ盤上の8×8のボタン配列
    private JButton resignButton, passButton; //投了・パスボタン
    private ImageIcon blackIcon, whiteIcon, boardIcon; //アイコン
    private Container c; //ペインを取得するコンテナ
    private int[][] board = new int[row][row]; //現在の盤面
    private String yourName = "あなた";//自分のプレイヤ名
    private String opponentName = "あいて";//相手のプレイヤ名
    private int yourColor = 1;//自分の石の色
    private JTextArea stoneInfoText; //石の数を表示するテキストエリア
    private JTextArea instText; //操作指示を表示するテキストエリア
    private String command; //どのボタンが押されたかを識別するコマンド
    private int buttonCommand = 0;

    /*コンストラクタ　工数:1　進捗:1*/
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
        JButton jb = new JButton();
        jb.addActionListener(this);
        //オセロ盤に必要な情報を生成
		boardButtonArray = new JButton[row][row];//ボタンの配列を作成
    }

    /*接続要求　工数:0.25　進捗:0*/
    public boolean connectDemand() {
        //接続要求をする

            //(保留)

        //接続に失敗した場合はfalseを返す（テスト用にtrueで返しています）
        if(true) return true;
        else return false;
    }

    /*再接続選択　工数:2.25　進捗:2.25*/
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
        button1.addActionListener(this);//ボタンの押下を検知できるようにする
        button2.addActionListener(this);
        button1.setActionCommand(Integer.toString(1));//コマンドの設定
        button2.setActionCommand(Integer.toString(2));
        button1.setBounds(15, row * 45 + 30, (row * 45 ) / 2 - 5, 30); //境界を設定
        button2.setBounds((row * 45 ) / 2 + 20, row * 45 + 30, (row * 45 ) / 2 - 5, 30);
        c.add(button1);//ペインに追加
        c.add(button2);
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
            //2秒待機
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
    public boolean loginInfoAccept(String[] loginInfo) {
        //受け取った情報をそのままソケット通信で送る
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

    /*サーバに接続　工数:0.25　進捗:0.25*/
    public void connectServer() {
        while(true){
            if(connectDemand()) break; //接続成功したらループを抜ける  
            else if(!reconnectSelect()) System.exit(0); //接続失敗かつ、再接続しない選択をした場合は終了
            //接続失敗かつ、再接続する選択をした場合はこのままループの先頭に戻る
        }
    }

    /*盤面の初期化　工数:0.25　進捗:0.25*/
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

    /*盤面を描画　工数:3　進捗:3*/
    public int[][] displayBoard(int[][] board) {
        String yourColorName;//変数: 石の数の表示時、色名を表示するための変数
        String opponentColorName;

        for(int i=0; i<row; i++){
            for(int j=0; j<row; j++){
                //石（もしくは背景の緑色）の描画
                //各マスの石の状態によって、描画するものを変える
                if(board[i][j] == 0){ boardButtonArray[i][j] = new JButton(boardIcon);} //アイコン（石無し、緑）
                else if(board[i][j] == 1){ boardButtonArray[i][j] = new JButton(blackIcon);} //アイコン（黒）
                else if(board[i][j] == 2){ boardButtonArray[i][j] = new JButton(whiteIcon);} //アイコン（白）
                c.add(boardButtonArray[i][j]); //各ボタンをペインに貼り付け
                //マス上にボタンを配置する
                int x = i * 45 + 15; //x座標計算
                int y = j * 45 + 15; //y座標計算
                boardButtonArray[i][j].setBounds(x, y, 45, 45); //ボタンの大きさと位置の設定
                boardButtonArray[i][j].addActionListener(this);//ボタンの押下を検知できるようにする
                boardButtonArray[i][j].setActionCommand(Integer.toString(100 + i * 10 + j));//コマンドの設定
		    }
        }
		//投了ボタン
		resignButton = new JButton("投了");//ボタンの作成
		resignButton.setBounds(15, row * 45 + 30, (row * 45 ) / 2 - 5, 30);//ボタンの境界を設定
	    resignButton.addActionListener(this);
        resignButton.setActionCommand(Integer.toString(201));
		//パスボタン
		passButton = new JButton("パス");//ボタンの作成
		passButton.setBounds((row * 45 ) / 2 + 20, row * 45 + 30, (row * 45 ) / 2 - 5, 30);//ボタンの境界を設定
		passButton.addActionListener(this);
        passButton.setActionCommand(Integer.toString(202));
        //石の数を表示するテキストエリア
        if(yourColor == 1){//石の色を表示するための準備
            yourColorName = "黒";
            opponentColorName = "白";
        }else{
            yourColorName = "白";
            opponentColorName = "黒";
        }

        stoneInfoText = new JTextArea("[" + yourName + "]("+ yourColorName +"): " + countStone(board,1) + "\n[" + opponentName + "](" + opponentColorName + "): " + countStone(board,2)); //テキストエリア作成
        stoneInfoText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16)); //フォントの設定
        stoneInfoText.setBounds(10, row * 45 + 70 , row * 45 + 10, 40); //境界の設定
        stoneInfoText.setEditable(false); //編集不可能にする
        //操作指示を表示するテキストエリア
        instText = new JTextArea("あなたの番です\n石を置くマスをクリックしてください",3,20); //テキストエリア作成
        instText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16)); //フォントの設定
        instText.setBounds(10, row * 45 + 120 , row * 45 + 10, 40); //境界の設定
        instText.setEditable(false); //編集不可能にする
        //上記2つのボタンと2つのテキストエリアをペインに貼り付け                                                                      
        c.add(resignButton);
        c.add(passButton);
        c.add(stoneInfoText);
        c.add(instText);
        repaint();
        return board;
    }

    /*対戦相手の操作情報を受信　工数:0.5　進捗:0*/
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

    /*プレイヤの操作を受付　工数:1　進捗:0.5*/
    public int[][] acceptPlayerMove(int[][] board) {
        command = "";
        //ボタンの入力がされるまで待機
        try{
            while(command == ""){
                Thread.sleep(100);
            }
        }catch(InterruptedException e) {
            System.out.println("Error: InterruptedException (in プレイヤの操作を受付)");
		}
        //石を置く

        return board;
    }

    /*操作情報を送信　工数:0.5　進捗:0*/
    public void sendMoveInfo(int[][] x) {
        
    }

    /*盤面に反映　工数:0.25　進捗:0.25*/
    public void updateBoard(int[][] board) {
        c.removeAll();      //現在表示されているコンポーネントを削除
        displayBoard(board);    //再び描画
        repaint();
    }


    /*石の数のカウント　工数:0.25　進捗:0.25*/
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

    /*対戦終了確認　工数:0.5　進捗:0.25*/
    public boolean checkGameEnd(int x) {
        /*テスト用に値を直接指定しています*/
        boolean whiteMovable = false;
        boolean blackMovable = false;
        //白黒双方が石を置けるかの情報をOthelloクラスから受け取り、どちらも置けない場合は対戦終了
        if(!whiteMovable & blackMovable) return true;
        else return false;
    }

    /*対戦結果をサーバに送信　工数: 0.25　進捗:0*/
    public void sendGameResult(int result) {
        
    }

    /*勝敗分を表示　工数:0.5　進捗:0.5*/
    //戻り値をbooleanに変更（再戦するかどうかの選択）
    public void displayResult(int x) {
        instText.replaceRange("ゲーム終了！", 0, instText.getText().length());
        //3秒待機
        try{
            Thread.sleep(2000);
        }catch(InterruptedException e) {
            System.out.println("Error: InterruptedException (in 再接続選択)");
        }

    }

    /*切断のメッセージを表示　工数:0.25　進捗:0*/
    public void displayDisconnectionMessage() {
        
    }

    /*投了受付　工数:0.5　進捗:0*/
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
    public void actionPerformed(ActionEvent e){
        command = e.getActionCommand();//クリックしたオブジェクトを取得し、ボタンの名前を取り出す
        System.out.println("マウスがクリックされました(ActionPerformed)。押されたボタンは " + command + "です。");//テスト用に標準出力
    }

    /*main（ここからスタート）　工数: 1　進捗: 0.5*/
    public static void main(String args[]){
        int turn; 

        Client tc = new Client();//クライアントのインスタンス作成
        tc.setVisible(true);
        
        //サーバへの接続
        tc.connectServer();

        //ループ
        while(true){
            //ルーム作成画面などをここに
            //ひとまず仮にこちら側がルームを作ったことにしておく
            turn = 0;

            while(true){
                //対戦開始
                tc.initBoard();//盤面初期化
                tc.displayBoard(tc.board);//盤面の初期描画
                while(!tc.checkGameEnd(0)){//盤面をチェックし、対局終了となるまで繰り返す
                    
                    if(turn == 0){
                        //自分の手番の場合
                        tc.board = tc.acceptPlayerMove(tc.board);//プレイヤ入力の受け付け
                        tc.sendMoveInfo(tc.board);//操作情報をサーバに送信
                    }else{
                        //相手の手番の場合
                        tc.board = tc.recieveOpponentMove(tc.board);//相手の操作情報をサーバから受信
                    }
                    tc.updateBoard(tc.board);//盤面の再描画
                }
                //対戦終了
                tc.sendGameResult(0);//対戦結果をサーバに送信
                tc.displayResult(0);//対戦結果を表示し、再戦するかの選択を受け付ける
            }

            
        }
    }
}



