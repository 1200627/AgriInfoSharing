package jp.kujira.ais.server;

/**
 * Created by Kujira on 2017/04/03.
 */
public final class AISServer {
    private static final AISServer INSTANCE = new AISServer();
    private AISServer(){}
    public static AISServer getInstance(){
        return INSTANCE;
    }
}
