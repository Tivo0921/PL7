//Othello.java
/*だて　編集 */
//座標を受け取って，配列を返す
//石を置いて，その後に裏返したりするメソッドを動かす

//工数6
public class Othello {
    int disc; // 石の色．黒：1，白：2，緑：0
    int board[][] = new int[8][8];
    int winLose;// 勝ちの場合は1，負けの場合は0，引き分けの場合は2
    int x, y; // 受け取った座標
    int stone;// 受け取った色の情報

    public Othello(int x, int y) {
        if (x > 7 || x < 0 || y > 7 || y < 0) {
            throw new IllegalStateException("盤面の位置を正しく指定できていません");
        }
        Setup();// 盤面の初期化を行なっておく．
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
    // ターンが回ってきて，石をおく前にどこか置けるかどうかを判断
    public Boolean searchBoard(int board[][], int stone) {// 工数1，進捗0
        int f = 0; // 置ける場所があるか否かをチェックするためのフラグ

        if (checkColor(board) == true) {// 盤面が全て白 or 黒の場合
            // ループを抜ける
            // break;<-エラー起きたから一旦コメントアウト
        }

        for (int i = 0; i < 8; i++) {// 盤面の，石が置かれていない場所全てをチェックする
            for (int j = 0; j < 8; j++) {
                if (checkBoard(i, j, stone) == true)// その位置に石を置いて，一つでもひっくり返る石がある場合
                    f++;
            }
        }

        if (f == 0) // 置ける場所が一つもなかった場合
            return false;
        else // 置ける場所が一つでもあった場合
            return true;
    }

    // 石を置けるか判定する(配列):boolean
    public Boolean checkBoard(int x, int y, int stone) {// 工数1，進捗1，引数を指定された場所，石の色に変更
        if (board[y][x] != 0) { // 指定した場所に石が置いてある場合
            return false;
        } else {// 指定した場所から8方向を確認し，ひっくり返せる石があるかを確認
            int check = 0;
            if (y > 0 && x > 0) {// 左上に石が存在する場合
                if (board[y - 1][x - 1] != stone && board[y - 1][x - 1] != 0) {
                    for (int i = 2; true; i++) {
                        if (x - i < 0 || y - i < 0 || board[y - i][x - i] == 0) {// 石がない場合
                            // System.out.println("左上:NG");
                            break;
                        } else if (board[y - i][x - i] == stone) {// 自分の石と同じ場合
                            check++;
                            // System.out.println("左上:OK");
                            break;
                        }
                    }
                }
            }

            if (y > 0) {// 上に石が存在する場合
                if (board[y - 1][x] != stone && board[y - 1][x] != 0) {
                    for (int i = 2; true; i++) {
                        if (y - i < 0 || board[y - i][x] == 0) {// 石がない場合は終了
                            // System.out.println("上 :NG");
                            break;
                        } else if (board[y - i][x] == stone) {// 自分の石と同じ場合
                            check++;
                            // System.out.println("上 :OK");
                            break;
                        }
                    }
                }
            }

            if (y > 0 && x < 7) {// 右上に石が存在する場合
                if (board[y - 1][x + 1] != stone && board[y - 1][x + 1] != 0) {
                    for (int i = 2; true; i++) {
                        if (x + i > 7 || y - i < 0 || board[y - i][x + i] == 0) {// 石がない場合
                            // System.out.println("右下:NG");
                            break;
                        } else if (board[y - i][x + i] == stone) {// 自分の石の場合
                            check++;
                            // System.out.println("右下:OK");
                            break;
                        }
                    }
                }
            }

            if (y < 7) {// 下に石が存在する場合
                if (board[y + 1][x] != stone && board[y + 1][x] != 0) {
                    for (int i = 2; true; i++) {
                        if (y + i > 7 || board[y + i][x] == 0) {// 石がない場合，終了
                            // System.out.println("下 :NG");
                            break;
                        } else if (board[y + i][x] == stone) {// 自分の石の場合
                            check++;
                            // System.out.println("下 :OK");
                            break;
                        }
                    }
                }
            }

            if (x < 7) {// 右に石が存在する場合
                if (board[y][x + 1] != stone && board[y][x + 1] != 0) {
                    for (int i = 2; true; i++) {
                        if (x + i > 7 || board[y][x + i] == 0) {// 石がない場合
                            // System.out.println("右 :NG");
                            break;
                        } else if (board[y][x + i] == stone) {// 自分の石の場合
                            check++;
                            // System.out.println("右 :OK");
                            break;
                        }
                    }
                }
            }

            if (y < 7 && x > 0) {// 左下に石が存在する場合． x < 7 消去
                if (board[y + 1][x - 1] != stone && board[y + 1][x - 1] != 0) {
                    for (int i = 2; true; i++) {
                        if (x - i < 0 || y + i > 7 || board[y + i][x - i] == 0) {// 石がない場合
                            // System.out.println("左下:NG");
                            break;
                        } else if (board[y + i][x - i] == stone) {// 自分の石の場合
                            check++;
                            // System.out.println("左下:OK");
                            break;
                        }
                    }
                }
            }

            if (x > 0) {// 左に石が存在する場合
                if (board[y][x - 1] != stone && board[y][x - 1] != 0) {
                    for (int i = 2; true; i++) {
                        if (x - i < 0 || board[y][x - i] == 0) {// 石がない場合
                            // System.out.println("左 :NG");
                            break;
                        } else if (board[y][x - i] == stone) {// 自分の石の場合
                            check++;
                            // System.out.println("左 :OK");
                            break;
                        }
                    }
                }
            }

            if (y < 7 && x < 7) {// 右下に石が存在する場合
                if (board[y + 1][x + 1] != stone && board[y + 1][x + 1] != 0) {
                    for (int i = 2; true; i++) {
                        if (x + i > 7 || y + i > 7 || board[y + i][x + i] == 0) {// 石がない場合
                            // System.out.println("右下:NG");
                            break;
                        } else if (board[y + i][x + i] == stone) {// 自分の石の場合
                            check++;
                            // System.out.println("右下:OK");
                            break;
                        }
                    }
                }
            }

            if (check == 0) {// 置ける場所がなかった場合
                return false;
            } else {// 置ける場所が一つでもあった場合
                return true;
            }
        }
    }

    public int[][] calcBoard(int x, int y, int stone) {// 盤面チェック後の計算を行い，盤面情報を返す
        putStone(x, y, stone);// 石を置く
        turnStone(x, y, stone);// 石を裏返す
        return board;
    }

    // 石を置く(配列):void
    public void putStone(int x, int y, int stone) {// 工数0.5，進捗0
        if (checkBoard(x, y, stone) == true) {// 石を置ける場合
            board[y][x] = stone;
        } else {// 石を置けない場合
        }
    }

    // 石を裏返す
    public void turnStone(int x, int y, int stone) {
        // 8方向を確認してひっくり返す．
        turnLeftUp(x, y, stone);
        turnUp(x, y, stone);
        turnRightUp(x, y, stone);
        turnLeft(x, y, stone);
        turnRight(x, y, stone);
        turnLeftDown(x, y, stone);
        turnDown(x, y, stone);
        turnRightDown(x, y, stone);
    }

    // 以下の八つのメソッドで，おいた場所から8方向の確認を行なっています
    public void turnLeftUp(int x, int y, int stone) {// 左上方向
        if (y > 1 && x > 1) {
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
    }

    public void turnUp(int x, int y, int stone) {// 上方向
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

    public void turnRightUp(int x, int y, int stone) {// 右上
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

    public void turnDown(int x, int y, int stone) {// 下
        // 下の石
        if (y < 6) {
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
    }

    public void turnRight(int x, int y, int stone) {// 右
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

    public void turnLeftDown(int x, int y, int stone) {// 左下
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
                        for (int t = 1; t < i; t++) {// 間の石をひっくり返す
                            // 上書き
                            board[y + t][x - t] = stone;
                        }
                        break;
                    }
                }
            }
        }
    }

    public void turnLeft(int x, int y, int stone) {// 左
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

    public void turnRightDown(int x, int y, int stone) {// 左下
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
    public int Judge(int board[][], int winLose) {// 工数0.5，進捗0
        int countB = 0, countW = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[j][i] == 1)
                    countB++;
                else if (board[j][i] == 2)
                    countW++;
            }
        }
        if (countB > countW) {
            winLose = 1;
        } else if (countB == countW) {
            winLose = 2;
        } else if (countB < countW) {
            winLose = 0;
        }
        return winLose;
    }

}