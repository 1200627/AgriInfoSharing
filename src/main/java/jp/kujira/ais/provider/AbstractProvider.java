package jp.kujira.ais.provider;

import javafx.beans.property.*;
import jp.kujira.ais.information.Information;
import jp.kujira.ais.server.AISServer;
import jp.kujira.ais.server.AgricultureInformationAgent;
import jp.kujira.ais.server.IncentiveNegotiationAgent;
import jp.kujira.ais.util.AISUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static jp.kujira.ais.main.Constants.BIG_DECIMAL_SCALE;
import static jp.kujira.ais.main.Constants.INITIAL_PROVIDE_PROBABILITY;

/**
 * Created by Kujira on 2017/04/03.
 */
public abstract class AbstractProvider implements Provider {
    private final int ID;
    private boolean canRequire;
    protected List<Information> knowHow;      // 所持ノウハウのリスト
    private int knowHowPointer = 0;         // 次に提供するノウハウのインデックス
    protected BigDecimal requiredIncentive; // 要求インセンティブ
    private BigDecimal earnedIncentive;     // 獲得済インセンティブ
    private BigDecimal provideProbability;  // 提供確率
    private BigDecimal contribution;        // 貢献度(インセンティブ獲得後に上昇)
    protected Function<BigDecimal, BigDecimal> evaluation = arg1 -> BigDecimal.ZERO;    // 篤農家自身のノウハウへの評価関数

    private IntegerProperty IDProperty;
    private IntegerProperty knowHowPointerProperty;
    private BooleanProperty canRequireProperty;
    private Property<BigDecimal> earnedIncentiveProperty;
    private Property<BigDecimal> contributionProperty;

    public AbstractProvider(final int ID){
        this.ID = ID;
        this.canRequire = false;
        this.earnedIncentive = BigDecimal.ZERO;
        this.provideProbability = INITIAL_PROVIDE_PROBABILITY;
        this.contribution = BigDecimal.ONE;

        this.IDProperty = new SimpleIntegerProperty(this.ID);
        this.knowHowPointerProperty = new SimpleIntegerProperty(this.knowHowPointer);
        this.canRequireProperty = new SimpleBooleanProperty(this.canRequire);
        this.earnedIncentiveProperty = new SimpleObjectProperty<>(this.earnedIncentive);
        this.contributionProperty = new SimpleObjectProperty<>(this.contribution);
    }

    public abstract void makeKnowHow(int size);

    public void evaluateKnowHow(){
        knowHow.stream().forEach(kh -> {
            BigDecimal hiddenWorth = kh.getHiddenWorth();
            kh.setProviderWorth(evaluation.apply(hiddenWorth));
        });
    }

    public final void provide(){
        Information kh = knowHow.get(knowHowPointer++);
        knowHowPointerProperty.setValue(knowHowPointer);
        AgricultureInformationAgent aia = (AgricultureInformationAgent) AISServer.getInstance().getAgricultureInformationAgent("ノウハウ");

        aia.register(kh);
        canRequire = true;
        canRequireProperty.setValue(canRequire);
    }

    public final void requireIncentive(){
        IncentiveNegotiationAgent ina = (IncentiveNegotiationAgent)AISServer.getInstance().newIncentiveNegotiationAgent();
        BigDecimal ei = ina.negotiate(ID, requiredIncentive);
        if(ei.equals(BigDecimal.ZERO)){
            return;     // インセンティブが与えられなかった場合はreturn
        }
        earnedIncentive = earnedIncentive.add(ei);
        earnedIncentiveProperty.setValue(earnedIncentive);
        canRequire = false;
        canRequireProperty.setValue(canRequire);
    }

    public final void decide(){
        // TODO: Decide the condition of providing know-how
        provideProbability = canRequire ? BigDecimal.ZERO : BigDecimal.valueOf(0.2d);
        if(AISUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.ONE, BIG_DECIMAL_SCALE).compareTo(provideProbability) < 0){
            provide();
        }
    }

    public final int getID(){
        return ID;
    }
    public final boolean canRequire(){
        return canRequire;
    }
    public final List<Information> getKnowHow(){ return knowHow; }
    public final BigDecimal getRequiredIncentive(){
        return requiredIncentive;
    }
    public final BigDecimal getContribution(){
        return contribution;
    }

    public final IntegerProperty providerIDProperty(){ return IDProperty; }
    public final IntegerProperty knowHowPointerProperty(){ return knowHowPointerProperty; }
    public final BooleanProperty canRequireProperty(){ return canRequireProperty; }
    public final Property<BigDecimal> earnedIncentiveProperty(){ return earnedIncentiveProperty; }
    public final Property<BigDecimal> contributionProperty(){ return contributionProperty; }
}
