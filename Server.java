import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;

import javax.sound.midi.Receiver;

public class Server2 {
    public int[][] clientConnectionState = new int[1][2];
    public String playerName = new String();
    public String password = new String();
    public int playerID = 0;
    public boolean loginSuccess = false;
    public int[][] playRecord = new int[1][3];
    public int roomId = 0;// 0から昇順
    public int maxRoomId = 0;// 最大のルームID(ルームID数-1)
    private int maxClient = 25;

    private PrintWriter[] out; // データ送信用オブジェクト
    private Receiver[] receiver; // データ受信用オブジェクト

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:tcp://localhost/~/PL7_server";

    // Database credentials
    static final String USER = "lemon";
    static final String PASS = "Fuu190523";

    public void connectClient(String[] args,Connection conn,Statement stmt) {
        // TCPポートを指定してサーバソケットを作成
        boolean player1Login=false;//player1がログインしてるかのチェック
        boolean player2Login=false;//player2がログインしてるかのチェック
        try {
            ServerSocket serverSocket = new ServerSocket(10000);
            System.out.println("サーバーが起動しました。");

            while (true) {
                try {
                    // クライアントからの接続待ち受け、ログイン情報照合
                    if(player1Login){
                        Socket clientSocket1 = serverSocket.accept();// クライアント1に接続
                        System.out.println("先手からの接続がありました");
                        if(!checkLoginInformation(conn,stmt,login[])){
                            //もう一度入力させる命令をクライアントに送らせる
                        }
                        player1Login = true;
                        //ここでmakeRoomを起動してルーム作成するか、transferRecordをきどうして成績確認するかの判断をクライアントから受け取りたい
                    }
                    if(player2Login){
                        Socket clientSocket2 = serverSocket.accept(); // クライアント2に接続
                        System.out.println("後手からの接続がありました");
                        if(!checkLoginInformation(conn,stmt,login[])){
                            //もう一度入力させる命令をクライアントに送らせる
                        }
                        player2Login = true;
                        //ここでルームIDの入力を受け取る
                        //ここでconfirmRoomを起動し、できてたら次に進む。できてなかったら入力繰り返し
                    }
                    


                    // クライアント1からの入力
                    InputStream inputStream1 = clientSocket1.getInputStream();
                    ObjectInputStream objectInputStream1 = new ObjectInputStream(inputStream1);
                    int[][] array1 = (int[][]) objectInputStream1.readObject();
                    // クライアント2にデータを転送
                    OutputStream outputStream2 = clientSocket2.getOutputStream();
                    ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(outputStream2);
                    objectOutputStream2.writeObject(array1);
                    objectOutputStream2.flush();

                    // クライアント2からの入力
                    InputStream inputStream2 = clientSocket2.getInputStream();
                    ObjectInputStream objectInputStream2 = new ObjectInputStream(inputStream2);
                    int[][] array2 = (int[][]) objectInputStream2.readObject();
                    // クライアント1にデータを転送
                    OutputStream outputStream1 = clientSocket1.getOutputStream();
                    ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(outputStream1);
                    objectOutputStream1.writeObject(array2);
                    objectOutputStream1.flush();

                    //ここにmatchEndがクライアントから送られてくるかどうかの処理を行いたい

                    // 接続を閉じる
                    if(matchEnd){
                        clientSocket1.close();
                        clientSocket2.close();
                        //ここにremoveRoom
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 工数1,進捗1
    public int[][] checkClientConnection(void){//clientCOnnectionState[][]を返す
        for(int i = 0;i<maxRoomID;i++){
            for(int j = 0; j < 2 ; j++){
                if(clientConnectionState[i][j]){
                    System.out.println(i +"," + j + ":接続中");
                }
                else{
                    System.out.println(i +"," + j + ":接続切断");
                }

            }
        }
    }

    // 工数2,進捗0
    public boolean checkLoginInformation(Connection conn, Statement stmt, String login[]) {
        String sql;
        boolean existinfo = false;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            sql = "SELECT name,pass FROM clients";
            ResultSet rs = stmt.executeQuery(sql);

            // STEP 4: Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                if (login[0] == rs.getString("name") && login[1] == rs.getString("pass")) {
                    existinfo = true;
                    break;
                }
            }
            // STEP 5: Clean-up environment
            rs.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
        return existinfo;
    }

    // 工数1,進捗1
    public int[][] transferPlayRecord(Statement stmt, Connection conn, int playerId, int playRecord[][]) {// 対戦成績の転送
        String sql;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            sql = "SELECT win,lose,draw FROM results where id = playerId";
            ResultSet rs = stmt.executeQuery(sql);

            playRecord[playerId][0] = rs.getInt("win");
            playRecord[playerId][1] = rs.getInt("lose");
            playRecord[playerId][2] = rs.getInt("draw");

            // STEP 5: Clean-up environment
            rs.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
        return playRecord;
    }

    public int makeRoom(Connection conn, Statement stmt) {// 工数1.0
        String sql;
        boolean checkRoom[];
        roomId = 0;
        for (int i = 0; i <= maxRoomId; i++) {
            roomId++;
            if (!checkRoom[i]) {
                try {
                    // STEP 1: Register JDBC driver
                    Class.forName(JDBC_DRIVER);

                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    stmt = conn.createStatement();
                    sql = "INSERT INTO rooms" + "VALUES (roomId)";
                    stmt.executeUpdate(sql);

                    // STEP 4: Clean-up environment
                    stmt.close();
                    conn.close();
                } catch (SQLException se) {
                    // Handle errors for JDBC
                    se.printStackTrace();
                } catch (Exception e) {
                    // Handle errors for Class.forName
                    e.printStackTrace();
                } finally {
                    // finally block used to close resources
                    try {
                        if (stmt != null)
                            stmt.close();
                    } catch (SQLException se2) {
                    } // nothing we can do
                    try {
                        if (conn != null)
                            conn.close();
                    } catch (SQLException se) {
                        se.printStackTrace();
                    } // end finally try
                } // end try
            }
        }
        return roomId;
    }

    public boolean confirmRoom(int roomId, Statement stmt, Connection conn) {// 工数1.0
        boolean existRoom = false;

        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            String sql = "SELECT id FROM rooms";
            ResultSet rs = stmt.executeQuery(sql);

            // STEP 4: Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                int id = rs.getInt("id");
                if (roomId == id) {
                    existRoom = true;
                    break;
                }
            }
            // STEP 5: Clean-up environment
            rs.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
        return existRoom;
    }

    public void removeRoom(int roomId, Connection conn, Statement stmt) {// 工数0.5
        String sql;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            sql = "DELETE FROM rooms " + "WHERE id = roomId";
            stmt.executeUpdate(sql);

            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
    }

    public void updateResults(int playerId, boolean isWinner, boolean isDraw, Statement stmt, Connection conn) {//工数4
      String sql;
      int idChecker;
      try {
          //STEP 1: Register JDBC driver
           Class.forName(JDBC_DRIVER);
           conn = DriverManager.getConnection(DB_URL,USER,PASS);
           stmt = conn.createStatement();
            if(isWinner){
                 String sql = "SELECT id FROM rooms";
                ResultSet rs = stmt.executeQuery(sql);
    
                //STEP 4: Extract data from result set
                while(rs.next()) {
               /    /Retrieve by column name
                    int id  = rs.getInt("id");
                    if(roomId == id){
                        existRoom = true;
                        break;
                    }
                }
                sql = "UPDATE Results" + "SET win = win + 1 WHERE playerId = playerId";
                stmt.executeUpdate(sql);
            }
            else if(isDraw){
               sql = "UPDATE Results" + "SET draw = draw + 1 WHERE playerId = playerId";
               stmt.executeUpdate(sql);
            }
            else{
               sql = "UPDATE Results" + "SET lose = lose + 1 WHERE playerId = playerId";
               stmt.executeUpdate(sql);
            }
          //STEP 4: Clean-up environment
           stmt.close();
           conn.close();
        } catch(SQLException se) {
          //Handle errors for JDBC
           se.printStackTrace();
        } catch(Exception e) {
          //Handle errors for Class.forName
           e.printStackTrace();
        } finally {
          //finally block used to close resources
           try{
              if(stmt!=null) stmt.close();
           } catch(SQLException se2) {
           }//nothing we can do
           try {
              if(conn!=null) conn.close();
           } catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try
    }

    String serveName(int id, Statement stmt, Connection conn) {
        String sql;
        String name;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            sql = "SELECT name FROM client WHERE id = id";
            ResultSet rs = stmt.executeQuery(sql);

            // STEP 4: Extract data from result set
            name = rs.getString("name");
            // STEP 5: Clean-up environment
            rs.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
        return name;
    }

    public static void main(String[] args) { // main,工数5
        Connection conn = null;
        Statement stmt = null;

        connectClient(args, conn, stmt);

        // ここでクライアントから対戦結果を受信する

        // ここにupdateResultsを入れて対戦成績を更新

    }
}