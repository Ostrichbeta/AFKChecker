package com.jeder.afkChecker;
import com.jeder.afkChecker.extra.RamdomString;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Main extends JavaPlugin implements Listener {

    //HashMap聲明區域

    public Map< String , Boolean > isNeedCaptcha = new HashMap<>() ; //用於儲存玩家的輸入時間
    public Map< String , Integer > wrongTime = new HashMap<>() ; //玩家的輸入錯誤次數
    //public Map< String , Float > inputVelocity = new HashMap<>() ; //用於偵測玩家的手速

    String captcha ;
    long deadLine = 9223372036854775807l ;
    @Override
    public void onEnable() {
        //當外掛被加載 發送訊息
        getLogger().info(ChatColor.AQUA + "歡迎使用AFKCHecker!");
        getServer().getPluginManager().registerEvents(this, this);
        //以下為config文件的設定部分
        FileConfiguration fc = getConfig() ;
        fc.addDefault( "checkPeriod" , 900 ); //檢查週期 單位為秒
        fc.addDefault( "captchaLength" , 7 ); //驗證碼的長度
        fc.addDefault( "enterTimer" , 30 ); //驗證計時器，設定為auto是彈性的時間，設定為數值單位為秒
        fc.addDefault( "maxWrongTime" , 3 ); //允許輸入錯誤的次數
        fc.addDefault( "punishMode" , "kick" ); //設定驗證碼輸入錯誤後的懲罰，目前只有kick(踢出)這一個選項
        fc.addDefault( "showCaptchaTimer" , true ); //是否顯示輸入倒數計時(如果輸入時簡短的話要關掉，不然會影響玩家的遊戲體驗)
        fc.addDefault( "captchaMode" , 0 ); //設定生成的驗證碼的樣式，設定為0是英文和數字混合，設定為1為免混淆模式（不包含0,1,I,l,O,o,q），設定為2則是純數字（強烈不建議此選項）
        getConfig().options().copyDefaults( true ) ;
        saveConfig() ; //保存配置

    }

    class sendCAPTCHA extends BukkitRunnable {
        private final Main plugin;

        sendCAPTCHA (Main plugin) {
            this.plugin = plugin;
        }

        @Override
        public void run() {
            //此為程式運行部分
            int length = getConfig().getInt( "captchaLength" ) ; //獲取生成字串的長度
            int mode = getConfig().getInt( "captchaMode" ) ; //獲取產生的模式
            captcha = new RamdomString().RandomString( length , mode ) ; //獲取一個驗證碼
            Collection < ? extends Player > plrList = Bukkit.getOnlinePlayers() ; //獲取所有的線上玩家
            Date d = new Date() ; //獲取一個日期變數
            deadLine = d.getTime() + (long)(getConfig().getInt( "enterTimer" ) * 1000) ; //更新deadline
            for ( Player p : plrList
                 ) {
                //開始遍歷
                //給每個玩家發送一個訊息
                String name = p.getName() ;
                isNeedCaptcha.put( name , true ) ; //加入玩家的姓名
                wrongTime.put( name , 0 ) ; //將玩家的輸入錯誤次數設定為0
                p.sendMessage( ChatColor.YELLOW + "[JCF掛機判定] " + ChatColor.GREEN + "為了節約伺服器的運算資源，現在您需要直接輸入 " + ChatColor.GOLD + ChatColor.BOLD + captcha + ChatColor.RESET + ChatColor.GREEN + " 來證明您當前沒有在掛機，如果您在 " + ChatColor.GOLD + ChatColor.BOLD + getConfig().getInt("enterTimer") + ChatColor.RESET + ChatColor.GREEN + " 秒內仍沒有完成輸入，或者輸入錯誤的次數達到 " + ChatColor.GOLD + ChatColor.BOLD + getConfig().getInt("maxWrongTime") + ChatColor.RESET + ChatColor.GREEN + " 次，您將會被移出伺服器。" ) ;
            }
        }
    }

    class checkCAPTCHA extends BukkitRunnable {
        private final Main plugin ;

        checkCAPTCHA ( Main plugin ) {
            this.plugin = plugin ;
        }

        @Override
        public void run() {
            //當玩家達到規定的時間 移出伺服器
            Date d = new Date() ;
            long nowTime = d.getTime() ; //獲取當前時間
            if ( nowTime > deadLine ) {
                Collection < ? extends Player > plrList = Bukkit.getOnlinePlayers() ; //獲取在線玩家的名單並進行foreach
                for ( Player p  : plrList
                        ) {
                    String name = p.getName() ;
                    boolean isInTheTable = isNeedCaptcha.containsKey( name ) ; //判斷該玩家是否需要輸入一個驗證碼
                    if ( isInTheTable ) {
                        //玩家沒有在規定的時間內輸入驗證碼
                        p.kickPlayer( ChatColor.YELLOW + "[JCF掛機判定] " + ChatColor.GREEN + "您沒有在規定的時間內輸入驗證碼，您已經被移出伺服器。" );
                        isNeedCaptcha.remove( p.getName() ) ;
                        wrongTime.remove( p.getName() ) ;
                        //將該玩家存儲的HashMap移除
                    }

                }
            }

        }
    }

}

