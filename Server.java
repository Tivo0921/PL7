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

//スレッド部（各クライアントに応じて）
class ClientProcThread extends Thread {
    private int number;// 自分の番号
    private Socket incoming;
    private InputStreamReader myIsr;
    private BufferedReader myIn;
    private PrintWriter myOut;
    private String myName;// 接続者の名前
    private String myPass;
    private String myRoom;
    private boolean roomFull = false;
    private String viewResult;

    public Connection conn;
    public Statement stmt;
    public PreparedStatement pstmt;

    public ClientProcThread(int n, Socket i, InputStreamReader isr, BufferedReader in, PrintWriter out) {
        number = n;
        incoming = i;
        myIsr = isr;
        myIn = in;
        myOut = out;
    }

    public void run() {
        try {
            myOut.println("Hello, client No." + number + "! Enter 'Bye' to exit.");// 初回だけ呼ばれる
            String receivedLogin = myIn.readLine();

            // カンマを基に名前とパスワードに分割
            String[] splitReceivedLogin = receivedLogin.split(",");

            myName = splitReceivedLogin[0];// 初めて接続したときの一行目は名前
            myPass = splitReceivedLogin[1];
            if (!Server.checkLoginInformation(conn, stmt, myName, myPass)) {
                myOut.println("アカウントを新規登録します");
                Server.loginSubmit(conn, pstmt, number, myName, myPass);
            }
            while (true) {
                myRoom = myIn.readLine();
                if (myRoom == "View Results") {
                    myOut.println(Server.countPlayRecord(stmt, conn));
                    for (int i = 1; i <= Server.countPlayRecord(stmt, conn); i++) {
                        int[][] result = Server.transferPlayRecord(pstmt, conn, i);
                        // 二次元配列を文字列に変換
                        StringBuilder sb = new StringBuilder();
                        for (int p = 0; p < result.length; p++) {
                            for (int j = 0; j < result[p].length; j++) {
                                sb.append(result[p][j]);
                                if (j < result[p].length - 1) {
                                    sb.append(",");
                                }
                            }
                            if (p < result.length - 1) {
                                sb.append(";");
                            }
                        }
                        String resultString = sb.toString();
                        myOut.println(Server.serveName(i, pstmt, conn) + ",id:" + resultString);
                        myOut.flush();
                    }
                } else if (MyServer.confirmRoom(Integer.parseInt(myRoom), stmt, conn)) {
                    if (!roomFull) {
                        myOut.println("ルームID" + myRoom + "に接続しました");
                        myOut.flush();
                        roomFull = true;
                        break;
                    } else {
                        myOut.println("ルームが満員です。ルームを作成してください");
                        myOut.flush();
                        int room = Server.makeRoom(conn, pstmt);
                        myOut.println("ルームID" + roomId + "を立てました");
                        myOut.flush();
                        break;
                    }
                } else {
                    int roomId = Server.makeRoom(conn, pstmt);
                    myOut.println("ルームID" + roomId + "を立てました");
                    myOut.flush();

                    break;
                }
            }

            while (true) {// 無限ループで，ソケットへの入力を監視する
                String str = myIn.readLine();
                // match endを受け取ったら対戦終了
                if (str == "match end") {
                    String receivedResults1 = myIn.readLine();
                    String[] splitReceivedResults1 = receivedResults1.split(",");
                    String resultId1 = splitReceivedResults1[0];
                    String resultWin1 = splitReceivedResults1[1];
                    String resultLose1 = splitReceivedResults1[2];
                    Server.updateResults(Integer.parseInt(resultId1), Integer.parseInt(resultWin1),
                            Integer.parseInt(resultLose1), pstmt, conn);

                    String receivedResults2 = myIn.readLine();
                    String[] splitReceivedResults2 = receivedResults1.split(",");
                    String resultId2 = splitReceivedResults1[0];
                    String resultWin2 = splitReceivedResults1[1];
                    String resultLose2 = splitReceivedResults1[2];
                    Server.updateResults(Integer.parseInt(resultId2), Integer.parseInt(resultWin2),
                            Integer.parseInt(resultLose2), pstmt, conn);

                    Server.removeRoom(Integer.parseInt(myRoom), conn, pstmt);

                    break;
                }

                System.out.println("Received from client No." + number + "(" + myName + "), Messages: " + str);
                if (str != null) {// このソケット（バッファ）に入力があるかをチェック
                    Server.SendAll(str, myName);// サーバに来たメッセージは接続しているクライアント全員に配る
                }
            }
        } catch (Exception e) {
            // ここにプログラムが到達するときは，接続が切れたとき
            System.out.println("Disconnect from client No." + number + "(" + myName + ")");
            MyServer.SetFlag(number, false);// 接続が切れたのでフラグを下げる
        }
    }
}

class Server {

    private static int maxConnection = 100;// 最大接続数
    private static Socket[] incoming;// 受付用のソケット
    private static boolean[] flag;// 接続中かどうかのフラグ
    private static InputStreamReader[] isr;// 入力ストリーム用の配列
    private static BufferedReader[] in;// バッファリングをによりテキスト読み込み用の配列
    private static PrintWriter[] out;// 出力ストリーム用の配列
    private static ClientProcThread[] myClientProcThread;// スレッド用の配列
    private static int member;// 接続しているメンバーの数
    public static int roomId = 0;// 0から昇順
    public static int maxRoomId = 3;// 最大のルームID(ルームID数-1)

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:tcp://localhost/~/PL7_server";

    // Database credentials
    static final String USER = "lemon";
    static final String PASS = "Fuu190523";

    public static boolean checkLoginInformation(Connection conn, Statement stmt, String cName, String cPass) {
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
                if (cName == rs.getString("name") && cPass == rs.getString("pass")) {
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

    public static int countPlayRecord(Statement stmt, Connection conn) {
        String sql;
        int rows = 0;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            sql = "SELECT COUNT(*) FROM results";
            ResultSet rs = stmt.executeQuery(sql);

            // STEP 4: Extract data from result set
            rows = rs.getInt("count(*)");
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
        return rows;
    }

    public static String serveName(int id, Statement stmt, Connection conn) {
        String sql;
        String name = "";
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            sql = "SELECT name FROM client WHERE id = ?";
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

    public static int[][] transferPlayRecord(PreparedStatement pstmt, Connection conn, int playerId) {// 対戦成績の転送
        String sql;
        int[][] playRecord = new int[1][3];
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            sql = "SELECT win,lose,draw FROM results where id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();

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
                if (pstmt != null)
                    pstmt.close();
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

    // 全員にメッセージを送る
    public static void SendAll(String str, String myName) {
        // 送られた来たメッセージを接続している全員に配る
        for (int i = 1; i <= member; i++) {
            if (flag[i] == true) {
                out[i].println(str);
                out[i].flush();// バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
                System.out.println("Send messages to client No." + i);
            }
        }
    }

    // フラグの設定を行う
    public static void SetFlag(int n, boolean value) {
        flag[n] = value;
    }

    public static void loginSubmit(Connection conn, PreparedStatement stmt, int id, String name, String pass) {// 工数1.0
        String sql;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            sql = "INSERT INTO rooms" + "VALUES (?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, pass);
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

    public static int makeRoom(Connection conn, PreparedStatement stmt) {// 工数1.0
        String sql;
        roomId = 0;
        for (int i = 0; i <= maxRoomId; i++) {
            roomId++;
            try {
                // STEP 1: Register JDBC driver
                Class.forName(JDBC_DRIVER);

                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                sql = "INSERT INTO rooms" + "VALUES (?)";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, roomId);
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
        return roomId;
    }

    public static boolean confirmRoom(int roomId, Statement stmt, Connection conn) {// 工数1.0
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

    public static void removeRoom(int roomId, Connection conn, PreparedStatement pstmt) {// 工数0.5
        String sql;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            sql = "DELETE FROM rooms WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, roomId);
            pstmt.executeUpdate(sql);

            // STEP 4: Clean-up environment
            pstmt.close();
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
                if (pstmt != null)
                    pstmt.close();
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

    public static void updateResults(int playerId, int win, int lose, PreparedStatement stmt, Connection conn) {// 工数4
        String sql;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            if (win == 1) {
                sql = "UPDATE Results" + "SET win = win + 1 WHERE playerId = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, playerId);
                stmt.executeUpdate(sql);

            } else if (lose == 1) {
                sql = "UPDATE Results" + "SET lose = lose + 1 WHERE playerId = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, playerId);
                stmt.executeUpdate(sql);

            } else {
                sql = "UPDATE Results" + "SET draw = draw + 1 WHERE playerId = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, playerId);
                stmt.executeUpdate(sql);
            }
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

    // mainプログラム
    public static void main(String[] args) {
        // 必要な配列を確保する
        incoming = new Socket[maxConnection];
        flag = new boolean[maxConnection];
        isr = new InputStreamReader[maxConnection];
        in = new BufferedReader[maxConnection];
        out = new PrintWriter[maxConnection];
        myClientProcThread = new ClientProcThread[maxConnection];

        int n = 1;

        try {
            System.out.println("The server has launched!");
            ServerSocket server = new ServerSocket(10000);// 10000番ポートを利用する
            while (true) {
                incoming[n] = server.accept();
                flag[n] = true;
                System.out.println("Accept client No." + n);
                // 必要な入出力ストリームを作成する
                isr[n] = new InputStreamReader(incoming[n].getInputStream());
                in[n] = new BufferedReader(isr[n]);
                out[n] = new PrintWriter(incoming[n].getOutputStream(), true);

                myClientProcThread[n] = new ClientProcThread(n, incoming[n], isr[n], in[n], out[n]);// 必要なパラメータを渡しスレッドを作成
                myClientProcThread[n].start();// スレッドを開始する
                member = n;// メンバーの数を更新する
                n++;
            }
        } catch (Exception e) {
            System.err.println("ソケット作成時にエラーが発生しました: " + e);
        }
    }
}