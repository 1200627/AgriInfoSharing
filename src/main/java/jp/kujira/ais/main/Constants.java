package jp.kujira.ais.main;

import jp.kujira.ais.util.AISUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kujira on 2017/04/24.
 */
public class Constants {
    public static final BigDecimal INITIAL_PROVIDE_PROBABILITY = BigDecimal.valueOf(0.5d);
    public static final String[] AIA_ARRAY = {"ノウハウ"};
    public static final int BIG_DECIMAL_SCALE = 3;
    public static final BigDecimal MIN_PARAMETER = BigDecimal.ZERO;
    public static final BigDecimal MAX_PARAMETER = BigDecimal.ONE;
    public static final String FUNCTION_TYPE = "type2";

    // 各種設定パラメータ
    public static final Map<String, String> INITIAL_PROPERTIES = Collections.unmodifiableMap(new LinkedHashMap<String, String>(){{
        put("numberOfProviders", "100");
        put("numberOfUsers", "100");
        put("numberOfDiligentUsers", "100");
        put("numberOfKnowHow", "100");
        put("minRequiredIncentive", "60");
        put("maxRequiredIncentive", "100");
        put("delayOfTurn", "3");
        put("knowHowAvailableHours", "100");
    }});

    // 重み係数
    public static final Map<String, BigDecimal> WEIGHTING_FACTORS = Collections.unmodifiableMap(new LinkedHashMap<String, BigDecimal>(){{
        put("alpha", BigDecimal.valueOf(1.0d));
        put("beta", BigDecimal.valueOf(1.0d));
        put("gamma", BigDecimal.valueOf(1.0d));
        put("delta", BigDecimal.valueOf(1.0d));
        put("epsilon", BigDecimal.valueOf(1.0d));
        put("zeta", BigDecimal.valueOf(1.0d));
        put("eta", BigDecimal.valueOf(1.0d));
        put("theta", BigDecimal.valueOf(1.0d));
        put("iota", BigDecimal.valueOf(1.0d));
    }});

    // 鮮度関数
    private static final Function<Integer, BigDecimal> TAU = t -> BigDecimal.valueOf(t).divide(new BigDecimal(SimulatorModel.getInstance().getProperty("knowHowAvailableHours")), BIG_DECIMAL_SCALE, RoundingMode.HALF_UP);
    public static final Map<String, Function<Integer, BigDecimal>> FRESHNESS_FUNCTIONS = Collections.unmodifiableMap(new LinkedHashMap<String, Function<Integer, BigDecimal>>(){{
        put("type1", t -> BigDecimal.ONE.subtract(TAU.apply(t)));
        put("type2", t -> BigDecimal.ONE.subtract(TAU.apply(t).pow(2)));
        put("type3", t -> BigDecimal.ONE.subtract(AISUtil.sqrt(TAU.apply(t), BIG_DECIMAL_SCALE)));
        put("type4", t -> BigDecimal.valueOf(Math.cos(Math.PI * TAU.apply(t).doubleValue())).add(BigDecimal.ONE).divide(BigDecimal.valueOf(2), BIG_DECIMAL_SCALE, RoundingMode.HALF_UP));
        put("type5", t -> BigDecimal.valueOf(Math.acos(2 * TAU.apply(t).doubleValue() - 1.0d)).divide(BigDecimal.valueOf(Math.PI), BIG_DECIMAL_SCALE, RoundingMode.HALF_UP));
    }});
}
