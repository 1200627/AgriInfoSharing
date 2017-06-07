package jp.kujira.ais.information;

import jp.kujira.ais.user.User;

import java.math.BigDecimal;

/**
 * Created by Kujira on 2017/04/03.
 */
public interface Information {
    void use(BigDecimal userWorth, User user);
    BigDecimal calculateWorth();
    BigDecimal getHiddenWorth();
    void setProviderWorth(BigDecimal providerWorth);
    void resetUsageCount();
}
