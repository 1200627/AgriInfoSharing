package jp.kujira.ais.provider;

import jp.kujira.ais.information.KnowHow;
import jp.kujira.ais.util.AISUtil;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static jp.kujira.ais.main.Constants.*;

/**
 * Created by Kujira on 2017/04/03.
 * ランダムにインセンティブを要求し，ランダムな価値のノウハウを生成し，ランダムに提供者評価をつけるプロバイダ
 */
public class RandomProvider extends AbstractProvider implements Provider {
    public RandomProvider(final int ID, final int minRI, final int maxRI){
        super(ID);
        super.requiredIncentive = BigDecimal.valueOf(AISUtil.randomInt(minRI, maxRI));  // 最小値と最大値の間の乱数を要求インセンティブに設定
        super.evaluation = arg -> AISUtil.randomBigDecimal(MIN_PARAMETER, MAX_PARAMETER, BIG_DECIMAL_SCALE);    // 評価値を[MIN_PARAMETER, MAX_PARAMETER]の範囲でランダムに設定
    }

    @Override
    public void makeKnowHow(int size) {
        super.knowHow = IntStream.range(0, size)
                .mapToObj(i -> KnowHow.newRandomWorthKnowHow(super.getID(), getContribution(), BigDecimal.ZERO, BigDecimal.ONE))
                .collect(Collectors.toList());
    }
}
