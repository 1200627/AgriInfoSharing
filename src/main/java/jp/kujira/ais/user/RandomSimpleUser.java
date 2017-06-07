package jp.kujira.ais.user;

import jp.kujira.ais.util.AISUtil;

/**
 * Created by Kujira on 2017/04/26.
 * 全ノウハウからランダムに情報を選択し，hiddenWorthと同じ評価値をつけるユーザ
 */
public class RandomSimpleUser extends SimpleUser{
    public RandomSimpleUser(){
        super.selectInformation = arg -> {
            final int size = arg.size();
            final int index = AISUtil.randomInt(0, size-1);
            return arg.get(index);
        };
        super.evaluation = arg -> arg;
    }
}
