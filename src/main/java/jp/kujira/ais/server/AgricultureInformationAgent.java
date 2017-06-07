package jp.kujira.ais.server;

import jp.kujira.ais.information.Information;
import jp.kujira.ais.information.KnowHow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Kujira on 2017/04/03.
 */
public class AgricultureInformationAgent extends AbstractServerAgent implements ServerAgent {
    private final String name;
    private final List<Information> agricultureInformation;

    public AgricultureInformationAgent(final String name){
        this.name = name;
        this.agricultureInformation = new ArrayList<>();
    }

    public void register(final Information knowHow){
        knowHow.resetUsageCount();
        agricultureInformation.add(knowHow);
    }

    public void clear(){ agricultureInformation.clear();}

    public String getName(){
        return name;
    }

    public List<Information> getAgricultureInformation(){
        return agricultureInformation;
    }

    public BigDecimal getInformationWorth(final int providerID){
        List<Information> ai = agricultureInformation.stream()
                .map(i -> (KnowHow)i)
                .filter(i -> i.getProviderID() == providerID)
                .collect(Collectors.toList());
        return ai.stream()
                .map(i -> i.calculateWorth())
                .reduce((v1, v2) -> v1.add(v2))
                .orElseThrow(IllegalStateException::new);
    }

    public void consumeInformation(final int providerID){
        agricultureInformation.stream()
                .map(i -> (KnowHow)i)
                .filter(i -> i.getProviderID() == providerID)
                .forEach(i -> i.consume());
    }

}
