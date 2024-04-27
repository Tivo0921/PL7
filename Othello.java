/*だて　編集 */
//座標を受け取って，配列を返す

//工数6
public class othelo {
    int disc; // 石の色．黒：1，白：2，緑：0
    int board[][] = new int[8][8];
    int winLose;
    public int X, Y; // 受け取った座標
    public int stone;// 受け取った色の情報

    public othelo(int X, int Y) {
        if (X > 7 || X < 0 || Y > 7 || Y < 0) {
            throw new IllegalStateException("盤面の位置を正しく指定できていません");
        }
    }

    // 盤面を初期化する:Void
    public void Setup() {// 工数 0.1 進捗0.1
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = 0;
            }
        }
        board[3][3] = 1;
        board[3][4] = 2;
        board[4][3] = 2;
        board[4][4] = 1;
    }

    // 石を置ける場所があるか判定する(配列):Boolean
    public Boolean Search_Board(int Board[][]) {// 工数1，進捗0

        return true;
    }

    // 石を置く場所を指定する(配列):配列
    public int[][] Select_Board(int[][] Board) {// 工数1，進捗0

        return Board;
    }

    // 石を置けるか判定する(配列):boolean
    public Boolean Check_Board(int board[][]){//工数1，進捗0.1
        if(board[Y][X] != 0){ //指定した場所に石が置いてある場合
            return false;
        }

        if(){//指定した場所が

        }
        return true;
    }

    // 石を置く(配列):void
    public void put_Disc(int Board[][]) {// 工数0.5，進捗0

    }

    // 石を裏返す(配列)：配列
    public int[][] Reverse_Disc(int Board[][]) {// 工数0.5，進捗0

        return Board;
    }

    // 8方向の確認
    public void turnLeftUp(int x, int y) {// 左上方向
        int next = board[y - 1][x - 1];// 左上の石

        if (next != stone) {
            for (int i = 2; true; i++) {
                if (x - i < 0 || y - i < 0 || board[y - i][x - i] == 0) {// 石がない場合
                    break;
                } else if (board[y - i][x - i] == stone) {// 自分の石と同じ場合
                    for (int t = 1; t < i; t++) {// 間の石を全てひっくり返す
                        board[y - t][x - t] = stone;
                    }
                    break;
                }
            }
        }
    }

    public void turnUp(int x, int y) {// 上方向
        if (y > 1) {// 盤面の上から2行目より下で行う処理
            int next = board[y - 1][x];// 1つ上の石
            if (next != stone) {// 1つ上の石が違う色の場合
                for (int i = 2; true; i++) {
                    if (y - i < 0 || board[y - i][x] == 0) {// 石がない場合は終了
                        break;
                    } else if (board[y - i][x] == stone) {// 自分の石と同じ場合
                        for (int t = 1; t < i; t++) {// 間の自分の石を全てひっくり返す
                            board[y - t][x] = stone;// 盤面の石の色を上書き
                        }
                        break;
                    }
                }
            }
        }
    }

    public void turnRightUp(int x, int y){//右上
        if(y > 1 && )
    }

    // 黒の個数を数える(配列):int
    public int Count_Disc(int Board[][]) {// 工数0.5，進捗0
        int count = 0;
        return count;
    }

    // 石がすべて片方の色かどうか判定(配列):int
    public int Check_Color(int Board[][]) {// 工数0.5，進捗0
        int count = 0;
        return count;
    }

    // 勝敗判定(配列,int)：int
    public int Judge(int Board[][], int winLose) {// 工数0.5，進捗0
        return winLose;
    }

}