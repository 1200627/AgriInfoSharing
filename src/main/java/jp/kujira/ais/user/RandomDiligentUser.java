package jp.kujira.ais.user;

import jp.kujira.ais.util.AISUtil;

/**
 * Created by Kujira on 2017/06/01.
 */
public class RandomDiligentUser extends DiligentUser{
    public RandomDiligentUser(){
        super.selectInformation = arg -> {
            final int size = arg.size();
            final int index = AISUtil.randomInt(0, size-1);
            return arg.get(index);
        };
        super.evaluation = arg -> arg;
    }
}
