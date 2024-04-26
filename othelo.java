/*だて　編集 */

public class othelo {
    int Disc;
    int Board[][];
    int Win_Lose;

    //石を置ける場所があるか判定する(配列):Boolean
    Boolean Search_Board(int Board[][]){
        return true;
    }

    //石を置く場所を指定する(配列):配列
    int[][] Select_Board(int[][] Board){
        return Board;
    }

    //石を置けるか判定する(配列):boolean
    Boolean Check_Board(int Board[][]){
        return true;
    }
    //石を置く(配列):void
    void put_Disc(int Board[][]){

    }

    //石を裏返す(配列)：配列
    int[][] Reverse_Disc(int Board[][]){
        return Board;
    }

    //黒の個数を数える(配列):int
    int Count_Disc(int Board[][]){
        int count = 0;
        return count;
    }

    //石がすべて片方の色かどうか判定(配列):int
    int Check_Color(int Board[][]){
        int count = 0;
        return count;
    }

    //勝敗判定(配列,int)：int
    int Judge(int Board[][], int Win_Lose){
        return Win_Lose;
    }
}
