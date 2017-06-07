package jp.kujira.ais.user;

import jp.kujira.ais.information.Information;
import jp.kujira.ais.information.KnowHow;
import jp.kujira.ais.server.AISServer;
import jp.kujira.ais.server.AgricultureInformationAgent;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Kujira on 2017/04/26.
 */
public class AbstractUser implements User {
    private BigDecimal contribution = BigDecimal.ONE;

    protected Function<BigDecimal, BigDecimal> evaluation = arg1 -> BigDecimal.ZERO;    // ユーザのノウハウへの評価関数
    protected Function<List<Information>, Information> selectInformation = arg1 -> new KnowHow();

    public void requireKnowHow(){
        List<Information> knowHow = ((AgricultureInformationAgent)AISServer.getInstance().getAgricultureInformationAgent("ノウハウ")).getAgricultureInformation();
        Information kh = selectInformation.apply(knowHow);
        kh.use(evaluation.apply(kh.getHiddenWorth()).multiply(contribution), this); // c_i * e_i
    }
}
