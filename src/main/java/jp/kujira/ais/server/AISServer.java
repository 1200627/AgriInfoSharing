package jp.kujira.ais.server;

import java.util.List;

/**
 * Created by Kujira on 2017/04/03.
 */
public final class AISServer {
    private static final AISServer INSTANCE = new AISServer();
    private AISServer(){}
    public static AISServer getInstance(){
        return INSTANCE;
    }

    private List<ServerAgent> agricultureInformationAgents;

    public ServerAgent newIncentiveNegotiationAgent(){
        // TODO: Implement method
        return new IncentiveNegotiationAgent();
    }

    public ServerAgent newInformationNegotiationAgent(){
        // TODO: Implement method
        return new InformationNegotiationAgent();
    }
}
