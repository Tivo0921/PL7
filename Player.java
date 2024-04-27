// 工数1 = 1時間

import Client

public class Player{

    String playerName; // プレイヤー名
    int turn; // 先手後手(白黒)情報
    int roomId; // ルームID
    String password; // パスワード
    String playerId; // プレイヤのID
    int[][] stones = new int[8][8]; // 石を置いた場所
    int[][] gameRecord = new int[1][3]; // 対戦成績
    JButton gameSet = new JButton("投了"); // 投了ボタン 

    // Clientクラスのインスタンスを作成
    Client client = new Client();

    // 再接続確認画面の構成要素
    JFrame connectionFrame;
    JPanel connectionPanel;
    JPanel connectionPanelCenter;
    JPanel connectionPanelBottom;
    JLabel loginMessage;
    JButton retry;
    JButton exit;

    // 工数2,進捗2
    // アプリ立ち上げ
    public void setupApp(){
        // 接続要求メソッドを呼び出し
        boolean connection = client.connectDemand();

        // ボタンのイベント処理
        ActionListener connectAction = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // [リトライボタン]が押されたら接続要求メソッドを呼び出し
                if(e.getSource()==retry){
                    connection = client.connectDemand();
                }
                // [終了ボタン]が押されたら再接続確認画面のフレーム表示を無効にする
                else if(e.getSource()==exit){
                    connectionFrame.setVisible(false);
                }
                // [OKボタン]が押されたらログイン情報受付メソッドを呼び出し
                else if(e.getSource()==ok){
                    client.loginInfoAccept(playerName,password);
                }
            }
        }

        // 再接続確認画面描画
        // 接続できるまでループ
        while(!connection){
            // フレーム設定
            connectionFrame = new JFrame("接続失敗");
            connectionFrame.setSize(500,300);
            connectionFrame.setVisible(true);
            // 全体のパネル設定(縦に[メッセージ]と[ボタン部分のパネル]を並べる)
            connectionPanel = new JPanel();
            connectionPanel.setLayout(new GridLayout(1,2));
            // 接続失敗メッセージ設定
            loginMessage = new JLabel("接続できませんでした");
            // ボタン部分のパネル設定(横に[リトライボタン]と[終了ボタン]を並べる)
            connectionPanelBottom = new JPanel();
            connectionPanelBottom.setLayout(new FrowLayout());
            // ボタン設定
            retry = new JButton("リトライ");
            retry.addActionListener(connectAction);
            exit = new JButton("終了");
            exit.addActionListener(connectAction);
            // 各パーツ配置
            connectionPanel.add(loginMessage)
            connectionPanelBottom.add(retry);
            connectionPanelBottom.add(exit);
        }

        // 接続できたら
        // ログイン画面描画
        // 再接続確認画面のフレーム表示を無効にする
        connectionFrame.setVisible(false);
        // フレーム設定
        JFrame frame = new JFrame("ログイン");
        frame.setSize(500,300);
        frame.setVisible(true);
        // 全体のパネル設定(縦に[メッセージ]と[入力部分のパネル]と[ボタン部分のパネル]を並べる)
        connectionPanel = new JPanel();
        connectionPanel.setLayout(new GridLayout(1,3));
        // ログインメッセージの設定
        JLabel loginMessage = new JLabel("プレイヤ情報を入力してください");
        // 入力部分のパネル設定(2×2で[ラベル]と[入力部]をプレイヤ名とパスワードそれぞれ順番に並べる)
        connectionPanelCenter = new JPanel();
        connectionPanelCenter.setLayout(new GridLayout(2,2));
        // 入力部分の設定
        JLabel playerNameLabel = new JLabel("プレイヤ名");
        JTextField playerNameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("パスワード");
        JTextField passwordField = new JTextField(20);
        // ボタン部分のパネル設定(横に[OKボタン]と[終了ボタン]を並べる)
        connectionPanelBottom = new JPanel();
        connectionPanelBottom.setLayout(new FrowLayout());
        // ボタン部分の設定
        JButton ok = new JButton("OK");
        ok.addActionListener(connectAction);
        exit = new JButton("終了");
        exit.addActionListener(connectAction);
        // 各パーツ配置
        connectionPanelCenter.add(playerNameLabel);
        connectionPanelCenter.add(playerNameField);
        connectionPanelCenter.add(passwordLabel);
        connectionPanelCenter.add(passwordField);
        connectionPanelBottom.add(ok);
        connectionPanelBottom.add(exit);
        connectionPanel.add(loginMessage);
        connectionPanel.add(connectionPanelCenter);
        connectionPanel.add(connectionPanelBottom);
    }

    // 工数1
    // 対戦成績の閲覧
    public void displayPlayRecord(){

    }

    // 工数1
    // ルームの作成
    public void makeRoom(){

    }

    // 工数1
    // ルームIDを入力
    public void inputRoomId(){

    }

    // 工数1
    // プレイヤ名を受付
    public void acceptPlayerId(){

    }

    // 工数1
    // プレイヤ名を取得
    public string getPlayerId(){

        return playerID;
    }

    // 工数0.5
    // 先手後手(白黒)情報を取得
    public int getTurn(){

        return turn;
    }

    // 工数1.5
    // 盤面の石の位置情報を送信
    public void sendPosition(){

    }

    // 工数1.5
    // 盤面の石の位置情報を受信
    public void getPosition(){

    }

    // 工数0.5
    // 投了受付
    public boolean setGame(){

    }
    
}
