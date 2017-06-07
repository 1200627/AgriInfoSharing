package jp.kujira.ais.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;

/**
 * Created by Kujira on 2017/04/27.
 */
public class AISUtil {
    public static int randomInt(int min, int max){
        return min + new SecureRandom().nextInt(max - min + 1);
    }

    public static BigDecimal randomBigDecimal(BigDecimal min, BigDecimal max, int scale){
        BigDecimal t = BigDecimal.valueOf(Math.pow(10, scale));
        int r = randomInt(0, max.subtract(min).multiply(t).intValue());
        return min.add(BigDecimal.valueOf(r).divide(t, scale, RoundingMode.HALF_UP));
    }

    public static BigDecimal sqrt(BigDecimal a, int scale){
        //とりあえずdoubleのsqrtを求める
        BigDecimal x = new BigDecimal(
                Math.sqrt(a.doubleValue()), MathContext.DECIMAL64);
        if(scale < 17) return x;

        BigDecimal b2 = new BigDecimal(2);
        for(int tempScale = 16; tempScale < scale; tempScale *= 2){
            //x = x - (x * x - a) / (2 * x);
            x = x.subtract(
                    x.multiply(x).subtract(a).divide(
                            x.multiply(b2), scale, BigDecimal.ROUND_HALF_EVEN));
        }
        return x;
    }
}
