import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class PlayerDriver extends JFrame {

    public static void main(String[] args) throws Exception{

        Player player = new Player();

        System.out.println("setupAppでアプリを立ち上げる");
        player.setupApp();

        System.out.println("displayMatchScreenでマッチ画面を描画");
        player.displayMatchScreen();

        System.out.println("対戦成績の閲覧");
        player.displayPlayRecord();

        System.out.println("ルームの作成");
        String playerName = "プレイヤ名";
        player.makeRoom(playerName);

        System.out.println("マッチ確認画面描画");
        player.displayMatching();

    }

}
