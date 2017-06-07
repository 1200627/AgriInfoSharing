package jp.kujira.ais.information;

import jp.kujira.ais.util.AISUtil;

import java.math.BigDecimal;

import static jp.kujira.ais.main.Constants.*;

/**
 * Created by Kujira on 2017/04/03.
 */
public class KnowHow extends AbstractInformation {
    private final int providerID;

    public KnowHow(){
        super(BigDecimal.ZERO, BigDecimal.ZERO);
        this.providerID = -1;
    }

    public KnowHow(final int providerID, final BigDecimal providerContribution, final BigDecimal hiddenWorth){
        super(providerContribution, hiddenWorth);
        this.providerID = providerID;
    }

    public static KnowHow newRandomWorthKnowHow(final int providerID, final BigDecimal providerContribution, final BigDecimal minWorth, final BigDecimal maxWorth){
        return new KnowHow(providerID, providerContribution, AISUtil.randomBigDecimal(minWorth, maxWorth, BIG_DECIMAL_SCALE));
    }

    public int getProviderID(){
        return providerID;
    }

    public void consume(){
        setConsumed(true);
    }
}
