package jp.kujira.ais.information;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.XYChart;
import jp.kujira.ais.main.SimulatorModel;
import jp.kujira.ais.user.DiligentUser;
import jp.kujira.ais.user.SimpleUser;
import jp.kujira.ais.user.User;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static jp.kujira.ais.main.Constants.FRESHNESS_FUNCTIONS;
import static jp.kujira.ais.main.Constants.FUNCTION_TYPE;
import static jp.kujira.ais.main.Constants.WEIGHTING_FACTORS;

/**
 * Created by Kujira on 2017/04/03.
 */
public abstract class AbstractInformation implements Information {
    private BigDecimal evaluation = BigDecimal.ZERO;
    private BigDecimal providerContribution = BigDecimal.ZERO;      // c
    private BigDecimal rarity = BigDecimal.ZERO;                    // r
    private BigDecimal usageCount = BigDecimal.valueOf(-1);         // u'
    private BigDecimal diligentUsageCount = BigDecimal.valueOf(-1); // u''
    private BigDecimal freshness = BigDecimal.ONE;
    private AtomicInteger livingTime = new AtomicInteger(0);

    private BigDecimal providerWorth;
    private BigDecimal hiddenWorth;
    private BigDecimal userWorth = BigDecimal.ZERO;         // SUM(c'_i * e'_i)
    private BigDecimal diligentUserWorth = BigDecimal.ZERO; // SUM(c''_i * e''_i)

    protected boolean isConsumed;

    private Property<BigDecimal> evaluationProperty;
    private Property<BigDecimal> rarityProperty;
    private Property<BigDecimal> usageCountProperty;
    private Property<BigDecimal> diligentUsageCountProperty;
    private Property<BigDecimal> freshnessProperty;
    private Property<BigDecimal> providerWorthProperty;
    private Property<BigDecimal> hiddenWorthProperty;
    private Property<BigDecimal> userWorthProperty;
    private Property<BigDecimal> diligentUserWorthProperty;
    protected BooleanProperty isConsumedProperty;

    private XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
    private Property<XYChart.Series<String, BigDecimal>> seriesProperty;

    public AbstractInformation(final BigDecimal providerContribution, final BigDecimal hiddenWorth){
        this.providerContribution = providerContribution;
        this.hiddenWorth = hiddenWorth;
        this.series.setName("evaluation");
        this.evaluationProperty = new SimpleObjectProperty<>(this.evaluation);
        this.rarityProperty = new SimpleObjectProperty<>(this.rarity);
        this.usageCountProperty = new SimpleObjectProperty<>(this.usageCount);
        this.diligentUsageCountProperty = new SimpleObjectProperty<>(this.diligentUsageCount);
        this.freshnessProperty = new SimpleObjectProperty<>(this.freshness);
        this.providerWorthProperty = new SimpleObjectProperty<>();
        this.hiddenWorthProperty = new SimpleObjectProperty<>(this.hiddenWorth);
        this.userWorthProperty = new SimpleObjectProperty<>();
        this.diligentUserWorthProperty = new SimpleObjectProperty<>();
        this.isConsumedProperty = new SimpleBooleanProperty(this.isConsumed);
        this.seriesProperty = new SimpleObjectProperty<>(this.series);
    }

    public void use(final BigDecimal userWorth, final User user){
        if(user instanceof SimpleUser) {
            this.usageCount = usageCount.add(BigDecimal.ONE);
            this.usageCountProperty.setValue(usageCount);
            this.userWorth = this.userWorth.add(userWorth);
            this.userWorthProperty.setValue(this.userWorth);
        }else if(user instanceof DiligentUser){
            this.diligentUsageCount = diligentUsageCount.add(BigDecimal.ONE);
            this.diligentUsageCountProperty.setValue(diligentUsageCount);
            this.diligentUserWorth = this.diligentUserWorth.add(userWorth);
            this.diligentUserWorthProperty.setValue(this.diligentUserWorth);
        }
    }

    public BigDecimal calculateWorth(){
        // TODO: Build the worth function
        if(isConsumed){
            evaluation = BigDecimal.ZERO;
            return evaluation;
        }
        int lt = livingTime.incrementAndGet() - 1;
        if(lt >= Integer.valueOf(SimulatorModel.getInstance().getProperty("knowHowAvailableHours")) + 1){
            return evaluation;
        }
        BigDecimal g = FRESHNESS_FUNCTIONS.get(FUNCTION_TYPE).apply(lt);
        freshness = g.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : g;
        freshnessProperty.setValue(freshness);

        BigDecimal alpha = WEIGHTING_FACTORS.get("alpha");
        BigDecimal beta = WEIGHTING_FACTORS.get("beta");
        BigDecimal gamma = WEIGHTING_FACTORS.get("gamma");
        BigDecimal delta = WEIGHTING_FACTORS.get("delta");
        BigDecimal epsilon = WEIGHTING_FACTORS.get("epsilon");
        BigDecimal zeta = WEIGHTING_FACTORS.get("zeta");
        BigDecimal eta = WEIGHTING_FACTORS.get("eta");
        BigDecimal theta = WEIGHTING_FACTORS.get("theta");
        BigDecimal iota = WEIGHTING_FACTORS.get("iota");

        BigDecimal P = alpha.multiply(providerContribution).add(beta.multiply(rarity));
        BigDecimal J = gamma.multiply(usageCount).add(delta.multiply(userWorth));
        BigDecimal D = epsilon.multiply(diligentUsageCount).add(zeta.multiply(diligentUserWorth));
        evaluation = eta.multiply(P).add(theta.multiply(J).add(iota.multiply(D))).multiply(freshness);
        evaluationProperty.setValue(evaluation);
        Platform.runLater(() -> series.getData().add(new XYChart.Data<>(String.valueOf(lt), evaluation)));
        return evaluation;
    }

    public BigDecimal getHiddenWorth(){
        return hiddenWorth;
    }

    public void resetUsageCount(){
        this.usageCount = BigDecimal.ZERO;
        usageCountProperty.setValue(this.usageCount);
        this.diligentUsageCount = BigDecimal.ZERO;
        diligentUsageCountProperty.setValue(this.diligentUsageCount);
    }

    public void setRarity(final BigDecimal rarity){
        this.rarity = rarity;
        rarityProperty.setValue(this.rarity);
    }

    public void setProviderWorth(final BigDecimal providerWorth){
        this.providerWorth = providerWorth;
        providerWorthProperty.setValue(this.providerWorth);
    }

    public void setConsumed(final boolean isConsumed){
        if(isConsumed){
            evaluation = BigDecimal.ZERO;
            evaluationProperty.setValue(evaluation);
        }
        this.isConsumed = isConsumed;
        isConsumedProperty.setValue(this.isConsumed);
    }

    public Property<BigDecimal> evaluationProperty(){return evaluationProperty;}
    public Property<BigDecimal> rarityProperty(){return rarityProperty;}
    public Property<BigDecimal> usageCountProperty(){return usageCountProperty;}
    public Property<BigDecimal> diligentUsageCountProperty(){return diligentUsageCountProperty;}
    public Property<BigDecimal> freshnessProperty(){return freshnessProperty;}
    public Property<BigDecimal> providerWorthProperty(){return providerWorthProperty;}
    public Property<BigDecimal> hiddenWorthProperty(){return hiddenWorthProperty;}
    public Property<BigDecimal> userWorthProperty(){return userWorthProperty;}
    public Property<BigDecimal> diligentUserWorthProperty(){return diligentUserWorthProperty;}
    public BooleanProperty isConsumedProperty(){ return isConsumedProperty; }
    public Property<XYChart.Series<String, BigDecimal>> seriesProperty(){return seriesProperty;}
}
