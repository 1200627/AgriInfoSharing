package jp.kujira.ais.information;

import java.math.BigDecimal;

/**
 * Created by Kujira on 2017/04/03.
 */
public class ICTInformation extends AbstractInformation implements Information {
    private BigDecimal worth = BigDecimal.ZERO;

    public ICTInformation(){
        super(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public BigDecimal getWorth(){
        return worth;
    }

    public void setWorth(final BigDecimal worth){
        this.worth = worth;
    }
}
