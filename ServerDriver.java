import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ServerDriver {
    private static String directoryPath;

    public static void setDirectoryPath(String path) {
        directoryPath = path.endsWith("/") ? path : path + "/";
    }

    private static void writeToFile(String fileName, List<String> content, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, append))) {
            for (String line : content) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeUserID(int id, String myName, String myPass, int myWin, int myLose, int myDraw) {
        String fileName = directoryPath + "User.txt";
        List<String> content = new ArrayList<>();
        content.add(id + "," + myName + "," + myPass + "," + myWin + "," + myLose + "," + myDraw);
        writeToFile(fileName, content, true);
    }

    public static void readUserID() {
        String fileName = directoryPath + "User.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) {
                    continue;
                }
                int id = Integer.parseInt(parts[0]);
                String myName = parts[1];
                String myPass = parts[2];
                int myWin = Integer.parseInt(parts[3]);
                int myLose = Integer.parseInt(parts[4]);
                int myDraw = Integer.parseInt(parts[5]);
                System.out.println("ID: " + id + ", Name: " + myName + ", Password: " + myPass + ", Win: " + myWin
                        + ", Lose: " + myLose + ", Draw: " + myDraw);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeRoomID(int roomId, String user1, String user2) {
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

    public static void makeRoom(String user1) {
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
    }

    public static void enterRoom(int roomId, String user2) {
        List<String> fileContent = readRoomIDFile();
        boolean roomFound = false;

        for (int i = 0; i < fileContent.size(); i++) {
            String[] parts = fileContent.get(i).split(",");
            if (parts.length != 3) {
                continue;
            }
            int currentRoomId = Integer.parseInt(parts[0]);
            if (currentRoomId == roomId) {
                if (!parts[2].isEmpty()) {
                    System.out.println("部屋は満員です");
                    return;
                }
                fileContent.set(i, parts[0] + "," + parts[1] + "," + user2);
                roomFound = true;
                break;
            }
        }

        if (!roomFound) {
            System.out.println("Room ID " + roomId + " not found.");
            return;
        }

        String fileName = directoryPath + "Room.txt";
        writeToFile(fileName, fileContent, false);

        System.out.println("User " + user2 + " entered room with ID: " + roomId);
    }

    public static void renewRecord(int result, int id) {
        String fileName = directoryPath + "User.txt";
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
                    int myWin = Integer.parseInt(parts[3]);
                    int myLose = Integer.parseInt(parts[4]);
                    int myDraw = Integer.parseInt(parts[5]);

                    if (result == 0)
                        myWin++;
                    else if (result == 1)
                        myLose++;
                    else if (result == 2)
                        myDraw++;

                    line = parts[0] + "," + parts[1] + "," + parts[2] + "," + myWin + "," + myLose + "," + myDraw;
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

    public static void deleteRoom(int roomId) {
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

    // mainプログラム
    public static void main(String[] args) {
        setDirectoryPath("C:/Users/shun0/university/PL7/");

        // 初期データの作成
        writeUserID(1, "Shun", "1234", 10, 5, 2);
        writeUserID(2, "Alice", "5678", 3, 2, 1);
        writeUserID(3, "Bob", "abcd", 4, 1, 3);

        // ユーザーデータの読み込み
        System.out.println("\n--- User Data ---");
        readUserID();

        // ルームデータの作成
        writeRoomID(1, "Shun", "");
        writeRoomID(2, "Alice", "Bob");

        // ルームデータの読み込み
        System.out.println("\n--- Room Data ---");
        readRoomID();

        // ルーム操作のテスト
        System.out.println("\n--- Room Operations Test ---");
        enterRoom(1, "Charlie"); // user1のみ存在するroomに入ろうとする
        enterRoom(2, "Charlie"); // user2が存在するroomに入ろうとする
        enterRoom(3, "Charlie"); // 存在しないroomに入ろうとする

        // ユーザーの勝敗情報更新のテスト
        System.out.println("\n--- Renew Record Test ---");
        renewRecord(0, 1); // 勝利数を更新
        renewRecord(1, 2); // 敗北数を更新
        renewRecord(2, 3); // 引き分け数を更新

        // 更新後のユーザーデータの読み込み
        System.out.println("\n--- Updated User Data ---");
        readUserID();

        System.out.println("\n--- Delete Room ---");
        deleteRoom(1);
        readRoomID();
    }
}