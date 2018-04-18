package com.jeder.afkChecker.extra;
import java.util.Random;

public class RamdomString {

    public String RandomString ( int length , int mode ) {
        String str ;
        int let ;
        switch ( mode ) {

            case 0 :
                str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                let = 62 ;
                //默認模式
                break ;

            case 1 :
                str = "abcdefghijkmnprstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";
                let = 55 ;
                //防止混淆模式
                break ;

            case 2 :
                str = "1234567890";
                let = 10 ;
                //純數字模式
                break ;

                default :
                    str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                    let = 62 ;
                    //對於dummy設為默認的模式
                    break ;
        }
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt( let );
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

}
