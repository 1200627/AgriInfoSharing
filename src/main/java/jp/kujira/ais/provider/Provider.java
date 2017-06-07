package jp.kujira.ais.provider;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import jp.kujira.ais.information.Information;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Kujira on 2017/04/03.
 */
public interface Provider {
    void makeKnowHow(int size);
    void evaluateKnowHow();
    void provide();
    void requireIncentive();
    void decide();
    int getID();
    boolean canRequire();
    List<Information> getKnowHow();
    BigDecimal getContribution();
    BigDecimal getRequiredIncentive();

    IntegerProperty providerIDProperty();
    BooleanProperty canRequireProperty();
    Property<BigDecimal> earnedIncentiveProperty();
    Property<BigDecimal> contributionProperty();
}
