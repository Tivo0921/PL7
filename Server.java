import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//スレッド部（各クライアントに応じて）
class ClientProcThread extends Thread {
	private int number;//自分の番号
	private BufferedReader myIn;
    private InputStreamReader myIsr;
	private PrintWriter myOut;
	private String myName;//接続者の名前
	private String myPass;
	private String myRoom;


	public ClientProcThread(int n, Socket i,InputStreamReader isr,BufferedReader in, PrintWriter out) {
		number = n;
        myIsr = isr;
		myIn = in;
		myOut = out;
	}

	public void run() {
		try {
			myOut.println("Hello, client No." + number);//初回だけ呼ばれる
			while(true){
                String receivedLogin = myIn.readLine();

            // カンマを基に名前とパスワードに分割
                String[] splitReceivedLogin = receivedLogin.split(",");

			    myName = splitReceivedLogin[0];//初めて接続したときの一行目は名前
			    myPass = splitReceivedLogin[1];
			    if(!Server.readUserID1(myName, myPass)){
                    Server.writeUserID(number, myName, myPass);
                    break;
			    }
                else if(Server.readUserID2(myName, myPass)){
                    //myOut.println("名前かパスワードが間違っています。");
                }
            }
            while (true) {
                myRoom = myIn.readLine();
                if(myRoom.equals("View Results")){
                    myOut.println(Server.countPlayRecord());
                    for(int i = 1;i<=Server.countPlayRecord();i++){
                        int[][] result = Server.transferPlayRecord(i);
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
                        myOut.println(Server.serveName(i)+",id:"+resultString);
                        myOut.flush();
                    }
                }
                else if(myRoom.equals("make a room")){
                    System.out.println("enter");
                    int roomId = Server.makeRoom(myName);
                    String connect = "";
                    myOut.println(roomId);
                    myOut.flush();

                    while(true){
                        connect = myIn.readLine();
                        if(connect.equals("connect")){
                            myOut.println("対戦相手が接続しました");
                            myOut.flush();
                            break;
                        }
                        else if(connect.equals("delete")){
                            Server.deleteRoom(Integer.parseInt(myRoom));
                            break;
                        }
                    }
                    
                    if(connect.equals("delete")){
                        continue;
                    }
                    else{
                        break;
                    }
                }
                else if(Server.enterRoom(Integer.parseInt(myRoom), myName) == 1){
                        myOut.println(myRoom);
                        myOut.flush();
                        String check = "connected";
                        Server.SendAll(check, myName, number);
                        break;
                }
                else if(Server.enterRoom(Integer.parseInt(myRoom), myName) == 2){
                        myOut.println("ルームが満員です。ルームを作成してください");
                        myOut.flush();
                        int room = Server.makeRoom(myName);
                        myOut.println(room);
                        myOut.flush();
                        break;
                }
                else if(Server.enterRoom(Integer.parseInt(myRoom), myName) == 0){
                    System.out.println("enter");
                    int roomId = Server.makeRoom(myName);
                    String connect = "";
                    myOut.println(roomId);
                    myOut.flush();

                    while(true){
                        connect = myIn.readLine();
                        if(connect.equals("connect")){
                            myOut.println("対戦相手が接続しました");
                            myOut.flush();
                            break;
                        }
                        else if(connect.equals("delete")){
                            Server.deleteRoom(Integer.parseInt(myRoom));
                            break;
                        }
                    }
                    
                    if(connect.equals("delete")){
                        continue;
                    }
                    else{
                        break;
                    }
                }
            }
			System.out.println("broke");


			while (true) {//無限ループで，ソケットへの入力を監視する
				String str = myIn.readLine();
                //match endを受け取ったら対戦終了
                if(str.equals("match end")){
                    String receivedResults1 = myIn.readLine();
                    String[] splitReceivedResults1 = receivedResults1.split(",");
                    String resultId1 = splitReceivedResults1[0];
			        String result1 = splitReceivedResults1[1];
                    Server.renewRecord(Integer.parseInt(result1), Integer.parseInt(resultId1));

                    String receivedResults2 = myIn.readLine();
                    String[] splitReceivedResults2 = receivedResults2.split(",");
                    String resultId2 = splitReceivedResults2[0];
			        String result2 = splitReceivedResults2[1];
                    Server.renewRecord(Integer.parseInt(result2),Integer.parseInt(resultId2));

                    Server.deleteRoom(Integer.parseInt(myRoom));

                    break;
                }

				System.out.println("Received from client No."+number+"("+myName+"), Messages: "+str);
				if (str != null) {//このソケット（バッファ）に入力があるかをチェック
					Server.SendAll(str, myName, number);//サーバに来たメッセージは接続しているクライアント全員に配る
				}
			}
		} catch (Exception e) {
			//ここにプログラムが到達するときは，接続が切れたとき
			System.out.println("Disconnect from client No."+number+"("+myName+")");
            e.printStackTrace();
			Server.SetFlag(number, false);//接続が切れたのでフラグを下げる
		}
	}
}

class Server{
	
	private static int maxConnection=100;//最大接続数
	private static Socket[] incoming;//受付用のソケット
	private static boolean[] flag;//接続中かどうかのフラグ
	private static InputStreamReader[] isr;//入力ストリーム用の配列
	private static BufferedReader[] in;//バッファリングをによりテキスト読み込み用の配列
	private static PrintWriter[] out;//出力ストリーム用の配列
	private static ClientProcThread[] myClientProcThread;//スレッド用の配列
	private static int member;//接続しているメンバーの数
	public static int roomId = 0;// 0から昇順
    public static int maxRoomId = 3;// 最大のルームID(ルームID数-1)

	


    private static String directoryPath;

    public static void setDirectoryPath(String path) {
        directoryPath = path.endsWith("/") ? path : path + "/";
    }

    private synchronized static void writeToFile(String fileName, List<String> content, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, append))) {
            for (String line : content) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void writeUserID(int id, String myName, String myPass) {
        String fileName = directoryPath + "User.txt";
        List<String> content = new ArrayList<>();
        content.add(id + "," + myName + "," + myPass);
        writeToFile(fileName, content, true);
    }

    public static boolean readUserID1(String myName, String myPass) {
        String fileName = directoryPath + "User.txt";
        boolean existlogin = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    continue;
                }
                String Name = parts[1];
                String Pass = parts[2];
                if(myName.equals(Name) && myPass.equals(Pass)){
                    existlogin = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return existlogin;
    }


    
    public static boolean readUserID2(String myName, String myPass) {
        String fileName = directoryPath + "User.txt";
        boolean loginFalse = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    continue;
                }
                String Name = parts[1];
                String Pass = parts[2];
                if(myName.equals(Name) && !myPass.equals(Pass)){
                    loginFalse = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loginFalse;
    }
    

    
    public static String serveName(int id) {
        String fileName = directoryPath + "User.txt";
        String uName = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    continue;
                }
                int uid = Integer.parseInt(parts[0]);
                String Name = parts[1];
                if(uid == id){
                    uName = Name;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uName;
    }



   
    public static int countPlayRecord() {
        String fileName = directoryPath + "Result.txt";
        int cnt = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
               cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cnt;
        
    }



    public static int[][] transferPlayRecord(int id) {
        String fileName = directoryPath + "Result.txt";
        List<int[]> resultsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    continue;
                }
                int uid = Integer.parseInt(parts[0]);
                if (uid == id) {
                    int[] result = new int[3];
                    result[0] = Integer.parseInt(parts[2]);
                    result[1] = Integer.parseInt(parts[3]);
                    result[2] = Integer.parseInt(parts[4]);
                    resultsList.add(result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultsList.toArray(new int[resultsList.size()][]);
    }




	//全員にメッセージを送る
	public static void SendAll(String str, String myName, int num){
		//送られた来たメッセージを接続している全員に配る
		for(int i=num-1;i<=num;i++){
			if(flag[i] == true){
				out[i].println(str);
				out[i].flush();//バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
				System.out.println("Send messages to client No."+i);
			}
		}	
	}
	
	//フラグの設定を行う
	public static void SetFlag(int n, boolean value){	
		flag[n] = value;
	}



	

    public synchronized static void writeRoomID(int roomId, String user1, String user2) {
        String fileName = directoryPath + "Room.txt";
        List<String> content = new ArrayList<>();
        content.add(roomId + "," + user1 + "," + user2);
        writeToFile(fileName, content, true);
    }

    public static List<String> readRoomIDFile() {
        String fileName = directoryPath + "Room.txt";
        List<String> fileContent = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    public static void readRoomID() {
        List<String> fileContent = readRoomIDFile();
        for (String line : fileContent) {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                continue;
            }
            int roomId = Integer.parseInt(parts[0]);
            String user1 = parts[1];
            String user2 = parts[2];
            System.out.println("ID: " + roomId + ", User1: " + user1 + ", User2: " + user2);
        }
    }

    public synchronized static int makeRoom(String user1) {
        List<String> fileContent = readRoomIDFile();
        Set<Integer> roomIds = new HashSet<>();

        for (String line : fileContent) {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                continue;
            }
            int roomId = Integer.parseInt(parts[0]);
            roomIds.add(roomId);
        }

        int newRoomId = 1;
        while (roomIds.contains(newRoomId)) {
            newRoomId++;
        }

        writeRoomID(newRoomId, user1, "");

        System.out.println("Created room with ID: " + newRoomId + " for user: " + user1);
        return newRoomId;
    }

    public synchronized static int enterRoom(int roomId, String user2) {
        List<String> fileContent = readRoomIDFile();
        boolean roomFound = false;
        int room = 0;

        for (int i = 0; i < fileContent.size(); i++) {
            String[] parts = fileContent.get(i).split(",");
            if (parts.length != 3) {
                continue;
            }
            int currentRoomId = Integer.parseInt(parts[0]);
            if (currentRoomId == roomId) {
                if (!parts[2].isEmpty()) {
                    System.out.println("部屋は満員です");
                    room = 2;
                    return room;
                }
                fileContent.set(i, parts[0] + "," + parts[1] + "," + user2);
                roomFound = true;
                break;
            }
        }

        if (!roomFound) {
            System.out.println("Room ID " + roomId + " not found.");
            return room;
        }

        String fileName = directoryPath + "Room.txt";
        writeToFile(fileName, fileContent, false);

        room = 1;


        System.out.println("User " + user2 + " entered room with ID: " + roomId);
        return room;
    }


    
    public synchronized static void deleteRoom(int roomId) {
        String fileName = directoryPath + "Room.txt";
        List<String> fileContent = new ArrayList<>();
        boolean roomFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    continue;
                }
                int currentRoomId = Integer.parseInt(parts[0]);
                if (currentRoomId == roomId) {
                    roomFound = true;
                    continue;
                }
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    public synchronized static void renewRecord(int result, int id) {
        String fileName = directoryPath + "Result.txt";
        List<String> fileContent = new ArrayList<>();
        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    continue;
                }
                int currentId = Integer.parseInt(parts[0]);
                if (currentId == id) {
                    int myWin = Integer.parseInt(parts[2]);
                    int myLose = Integer.parseInt(parts[3]);
                    int myDraw = Integer.parseInt(parts[4]);

                    if (result == 0)
                        myWin++;
                    else if (result == 1)
                        myLose++;
                    else if (result == 2)
                        myDraw++;

                    line = parts[0] + "," + parts[1] + "," +"," + myWin + "," + myLose + "," + myDraw;
                    userFound = true;
                }
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!userFound) {
            System.out.println("User ID " + id + " not found.");
            return;
        }

        writeToFile(fileName, fileContent, false);
        System.out.println("User ID " + id + " record updated.");
    }




	
	//mainプログラム
	public static void main(String[] args) {
		//必要な配列を確保する
		incoming = new Socket[maxConnection];
		flag = new boolean[maxConnection];
		isr = new InputStreamReader[maxConnection];
		in = new BufferedReader[maxConnection];
		out = new PrintWriter[maxConnection];
		myClientProcThread = new ClientProcThread[maxConnection];

        setDirectoryPath("C:/Users/kou15/PL7");

		
		int n = 1;

		try {
			System.out.println("The server has launched!");
			ServerSocket server = new ServerSocket(10000);//10000番ポートを利用する
			while (true) {
				incoming[n] = server.accept();
				flag[n] = true;
				System.out.println("Accept client No." + n);
				//必要な入出力ストリームを作成する
				isr[n] = new InputStreamReader(incoming[n].getInputStream());
				in[n] = new BufferedReader(isr[n]);
				out[n] = new PrintWriter(incoming[n].getOutputStream(), true);
				
				myClientProcThread[n] = new ClientProcThread(n, incoming[n], isr[n], in[n], out[n]);//必要なパラメータを渡しスレッドを作成
				myClientProcThread[n] .start();//スレッドを開始する
				member = n;//メンバーの数を更新する
				n++;
			}
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}
}