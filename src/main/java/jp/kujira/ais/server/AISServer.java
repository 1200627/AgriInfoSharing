package jp.kujira.ais.server;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static jp.kujira.ais.main.Constants.*;

/**
 * Created by Kujira on 2017/04/03.
 */
public final class AISServer {
    private static final AISServer INSTANCE = new AISServer();
    private AISServer(){
        agricultureInformationAgents = Arrays.stream(AIA_ARRAY)
                .map(AgricultureInformationAgent::new)
                .collect(Collectors.toList());
    }
    public static AISServer getInstance(){
        return INSTANCE;
    }

    private List<ServerAgent> agricultureInformationAgents;

    public ServerAgent getAgricultureInformationAgent(final String name){
        return agricultureInformationAgents.stream()
                .filter(a -> ((AgricultureInformationAgent)a).getName().equals(name))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public static ServerAgent newIncentiveNegotiationAgent(){
        // TODO: Implement method
        return new IncentiveNegotiationAgent();
    }

    public static ServerAgent newInformationNegotiationAgent(){
        // TODO: Implement method
        return new InformationNegotiationAgent();
    }
}
