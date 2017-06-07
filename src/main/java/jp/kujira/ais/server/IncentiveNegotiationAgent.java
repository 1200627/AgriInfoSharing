package jp.kujira.ais.server;

import java.math.BigDecimal;

/**
 * Created by Kujira on 2017/04/03.
 */
public class IncentiveNegotiationAgent extends AbstractServerAgent implements ServerAgent {
    public BigDecimal negotiate(final int providerID, final BigDecimal requiredIncentive){
        AgricultureInformationAgent aia = (AgricultureInformationAgent)AISServer.getInstance().getAgricultureInformationAgent("ノウハウ");

        BigDecimal worth = aia.getInformationWorth(providerID);
        // TODO: Decide the threshold
        BigDecimal threshold = BigDecimal.valueOf(50.0d);
        // インセンティブ付与時に情報を消費(価値を0に設定する)
        if(worth.compareTo(threshold) > 0){
            aia.consumeInformation(providerID);
            return worth;
        }
        return BigDecimal.ZERO;
    }
}
