
import java.util.Scanner;

public class OthelloDriver {
    int disc; // 石の色．黒：1，白：2，緑：0
    int board[][] = new int[8][8];
    int winLose;// 勝ちの場合は1，負けの場合は0，引き分けの場合は2
    int x, y; // 受け取った座標
    int stone;// 受け取った色の情報

    // 盤面を表示する
    public static void printBoard(int board[][]) {
        System.out.println("  0 1 2 3 4 5 6 7"); // 列番号を表示
    
        for (int i = 0; i < 8; i++) {
            System.out.print(i + " "); // 行番号を表示
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " "); // 盤面の各マスの値を表示
            }
            System.out.println(); // 改行して次の行に移る
        }
    }

public static void main(String args[]){
    Othello othello = new Othello(0, 0);
    Scanner scanner = new Scanner(System.in); // Scannerのインスタンスを作成
    int x = 3, y = 3;
    int fB = 0, fW = 0;

    System.out.println("ゲームを始めます");
    printBoard(othello.board);
    while(true){
        fB = 0; fW = 0;// フラグの初期化
        //黒の手番
        System.out.println("黒の手番です");
        
        while (othello.checkBoard(x,y,1) != true) {
            System.out.println("searchBoard出力:" + othello.searchBoard(othello.board, 1));
            if(othello.searchBoard(othello.board, 1) == false){
            System.out.println("黒の置ける場所はありません．");
            fB++;
            break;
            }
            System.out.print("x = ");
            x = scanner.nextInt();
            System.out.print("y = ");
            y = scanner.nextInt();
            System.out.println("checkBoard出力:" + othello.checkBoard(x,y,1));
            if(othello.checkBoard(x,y,1) != true)
                System.out.println("その位置には置けません");
        }
        if(fB != 1){
            othello.calcBoard(x, y, 1);
            printBoard(othello.board); // 盤面を表示する
        }
        
        //白の手番
        System.out.println("白の手番です");
        
        while (othello.checkBoard(x,y,2) != true) {
            System.out.println("searchBoard出力:" + othello.searchBoard(othello.board, 2));
            if(othello.searchBoard(othello.board, 2) == false){
            System.out.println("白の置ける場所はありません．");
            fW++;
            break;
            }
            System.out.print("x = ");
            x = scanner.nextInt();
            System.out.print("y = ");
            y = scanner.nextInt();
            System.out.println("checkBoard出力:" + othello.checkBoard(x,y,2));
            if(othello.checkBoard(x,y,2) != true)
                System.out.println("その位置には置けません");
        }
        if(fW != 1 ){
            othello.calcBoard(x, y, 2);
            printBoard(othello.board); // 盤面を表示する
        }
        
        if(fB == 1 && fW == 1){
            System.out.println("どちらも置けなくなりました．");
            break;
        }
    }
    System.out.println("ゲームを終了します．");
    scanner.close();
}

}