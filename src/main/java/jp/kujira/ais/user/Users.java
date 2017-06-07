package jp.kujira.ais.user;

/**
 * Created by Kujira on 2017/04/03.
 */
public class Users {
    public static User newRandomSimpleUser(){
        return new RandomSimpleUser();
    }

    public static User newRandomDiligentUser(){
        return new RandomDiligentUser();
    }
}
