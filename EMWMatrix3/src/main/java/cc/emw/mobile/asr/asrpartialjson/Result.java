package cc.emw.mobile.asr.asrpartialjson;

import java.util.ArrayList;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.asr.asrpartialjson
 * @data on 2018/9/20  16:21
 * @describe TODO
 */

public class Result {

    private ArrayList<String> uncertain_word;
    private ArrayList<String> word;

    public ArrayList<String> getUncertain_word() {
        return uncertain_word;
    }

    public ArrayList<String> getWord() {
        return word;
    }
}
