package jp.kujira.ais.provider;

/**
 * Created by Kujira on 2017/04/03.
 */
public class Providers {
    public static Provider newRandomProvider(final int ID, final int minRI, final int maxRI){
        return new RandomProvider(ID, minRI, maxRI);
    }
}
