package jp.kujira.ais.provider;

import jp.kujira.ais.information.Information;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Kujira on 2017/04/03.
 */
public abstract class AbstractProvider implements Provider {
    private final int ID;
    private List<Information> knowHow;      // 所持ノウハウのリスト
    private BigDecimal requiredIncentive;   // 要求インセンティブ
    private BigDecimal earnedIncentive;     // 獲得済インセンティブ
    private BigDecimal provideProbability;  // 提供確率

    public AbstractProvider(final int ID){
        this.ID = ID;
    }

    public abstract void provide();
}
