/*だて　編集 */
//座標を受け取って，配列を返す

//工数6
public class Othello {
    int disc; // 石の色．黒：1，白：2，緑：0
    int board[][] = new int[8][8];
    int winLose;
    public int X, Y; // 受け取った座標
    public int stone;// 受け取った色の情報

    public Othello(int X, int Y) {
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
    public Boolean searchBoard(int Board[][]) {// 工数1，進捗0

        return true;
    }

    // 石を置く場所を指定する(配列):配列
    public int[][] selectBoard(int[][] Board) {// 工数1，進捗0

        return Board;
    }

    // 石を置けるか判定する(配列):boolean
    public Boolean checkBoard(int x, int y, int stone) {// 工数1，進捗1，引数を指定された場所，石の色に変更
        if (board[y][x] != 0) { // 指定した場所に石が置いてある場合
            return false;
        } else {// 指定した場所から8方向を確認し，ひっくり返せる石があるかを確認
            int check = 0;
            if (y > 1 && x > 1 && board[y - 1][x - 1] != stone && board[y - 1][x - 1] != 0)// 左上に石が存在する場合
                check++;
            if (y > 1 && board[y - 1][x] != stone && board[y - 1][x] != 0)// 上に石が存在する場合
                check++;
            if (y > 1 && x < 6 && board[y - 1][x + 1] != stone && board[y - 1][x + 1] != 0)// 右上に石が存在する場合
                check++;
            if (y > 6 && board[y + 1][x] != stone && board[y + 1][x] != 0)// 下に石が存在する場合
                check++;
            if (x < 6 && board[y][x + 1] != stone && board[y][x + 1] != 0)// 右に石が存在する場合
                check++;
            if (y < 6 && x > 1 && board[y + 1][x - 1] != stone && board[y + 1][x - 1] != 0)// 左下に石が存在する場合
                check++;
            if (x > 1 && board[y][x - 1] != stone && board[y][x - 1] != 0)// 左に石が存在する場合
                check++;
            if (y < 6 && x < 6 && board[y + 1][x + 1] != stone && board[y + 1][x + 1] != 0)// 右下に石が存在する場合
                check++;
            if (check == 0) {// 置ける場所がなかった場合
                return false;
            } else {// 置ける場所が一つでもあった場合
                return true;
            }
        }
    }

    // 石を置く(配列):void
    public void putDisc(int Board[][]) {// 工数0.5，進捗0

    }

    // 石を裏返す(配列)：配列
    public int[][] reverseDisc(int Board[][]) {// 工数0.5，進捗0

        return Board;
    }

    // 以下の八つのメソッドで，おいた場所から8方向の確認を行なっています
    public void turnLeftUp(int x, int y) {// 左上方向
        int next = board[y - 1][x - 1];// 左上の石

        if (next != stone && next != 0) {
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
            if (next != stone && next != 0) {// 1つ上の石が違う色の場合
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

    public void turnRightUp(int x, int y) {// 右上
        if (y > 1 && x < 6) {
            // 隣の石
            int next = board[y - 1][x + 1];
            // 隣の石が裏の場合
            if (next != stone && next != 0) {
                // さらにその1つ隣の石から順に確認
                for (int i = 2; true; i++) {
                    if (x + i > 7 || y - i < 0 || board[y - i][x + i] == 0) {
                        // 石がない場合
                        break;
                    } else if (board[y - i][x + i] == stone) {
                        // 自分の石の場合
                        for (int t = 1; t < i; t++) {
                            // 上書き
                            board[y - t][x + t] = stone;
                        }
                        break;
                    }
                }
            }
        }
    }

    public void turnDown(int x, int y) {// 下
        // 下の石
        int next = board[y + 1][x];
        // 下の石が裏の場合
        if (next != stone && next != 0) {
            // さらにその一つ下から順に確認
            for (int i = 2; true; i++) {
                if (y + i > 7 || board[y + i][x] == 0) {
                    // 石がない場合，終了
                    break;
                } else if (board[y + i][x] == stone) {
                    // 自分の石の場合
                    for (int t = 1; t < i; t++) {
                        // 上書き
                        board[y + t][x] = stone;
                    }
                    break;
                }
            }
        }
    }

    public void turnRight(int x, int y) {// 右
        if (x < 6) {
            // 右隣の石
            int next = board[y][x + 1];
            // 右隣の石が裏の場合
            if (next != stone && next != 0) {
                // さらにその右隣から順に確認
                for (int i = 2; true; i++) {
                    if (x + i > 7 || board[y][x + i] == 0) {
                        // 石がない場合
                        break;
                    } else if (board[y][x + i] == stone) {
                        // 自分の石の場合
                        for (int t = 1; t < i; t++) {// 全てひっくり返す
                            board[y][x + t] = stone;
                        }
                        break;
                    }
                }
            }
        }
    }

    public void turnLeftDown(int x, int y) {// 左下
        if (y < 6 && x > 1) {
            // 左下の石
            int next = board[y + 1][x - 1];
            // 左下の石が裏の場合
            if (next != stone && next != 0) {
                // さらに左下の石から順に確認
                for (int i = 2; true; i++) {
                    if (x - i < 0 || y + i > 7 || board[y + i][x - i] == 0) {
                        // 石がない場合
                        break;
                    } else if (board[y + i][x - i] == stone) {
                        // 自分の石の場合
                        for (int t = i; t < i; t++) {// 間の石をひっくり返す
                            // 上書き
                            board[y + t][x - t] = stone;
                        }
                        break;
                    }
                }
            }
        }
    }

    public void turnLeft(int x, int y) {// 左
        if (x > 1) {
            // 左の石
            int next = board[y][x - 1];
            // 左の石が裏の場合
            if (next != stone && next != 0) {
                // 左隣から順に確認
                for (int i = 2; true; i++) {
                    if (x - i < 0 || board[y][x - i] == 0) {
                        // 石がない場合
                        break;
                    } else if (board[y][x - i] == stone) {
                        // 自分の石の場合
                        for (int t = 1; t < i; t++) {// 間の石を全てひっくり返す
                            board[y][x - t] = stone;
                        }
                        break;
                    }
                }
            }
        }
    }

    public void turnRightDown(int x, int y) {// 左下
        if (y < 6 && x < 6) {
            // 左下の石
            int next = board[y + 1][x + 1];
            // 左下の石が裏の場合
            if (next != stone && next != 0) {
                // さらに左下から順に確認
                for (int i = 2; true; i++) {
                    if (x + i > 7 || y + i > 7 || board[y + i][x + i] == 0) {
                        // 石がない場合
                        break;
                    } else if (board[y + i][x + i] == stone) {
                        // 自分の石の場合
                        for (int t = 1; t < i; t++) {// 間の石を全てひっくり返す
                            board[y + t][x + t] = stone;
                        }
                        break;
                    }
                }
            }
        }
    }

    // 黒の個数を数える(配列):int
    public int countDisc(int board[][]) {// 工数0.5，進捗0.5
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[j][i] == 1)
                    count++;
            }
        }
        return count;
    }

    // 石がすべて片方の色かどうか判定(配列):int
    public Boolean checkColor(int board[][]) {// 工数0.5，進捗0.5，戻り値をbooleanに変更．片方の色の時はtrue
        int countB = 0, countW = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[j][i] == 1)
                    countB++;
                else if (board[j][i] == 2)
                    countW++;
            }
        }
        if (countB == 0 || countW == 0)// 片方だけの場合
            return true;
        else // 両方の色がある場合
            return false;
    }

    // 勝敗判定(配列,int)：int
    public int Judge(int Board[][], int winLose) {// 工数0.5，進捗0
        return winLose;
    }
}