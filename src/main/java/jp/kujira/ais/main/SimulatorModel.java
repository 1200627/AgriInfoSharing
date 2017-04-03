package jp.kujira.ais.main;

/**
 * Created by Kujira on 2017/04/03.
 */
public final class SimulatorModel {
    private static final SimulatorModel INSTANCE = new SimulatorModel();
    private SimulatorModel(){}
    public static SimulatorModel getInstance(){
        return INSTANCE;
    }
}
