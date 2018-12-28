package cc.emw.mobile.asr.asrpartialjson;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.asr.asrpartialjson
 * @data on 2018/9/20  16:21
 * @describe TODO
 */

public class OriginResult {
    private String corpus_no;
    private String err_no;
    private String sn;
    private Result result;

    public Result getResult() {
        return result;
    }

    public String getCorpus_no() {
        return corpus_no;
    }

    public String getErr_no() {
        return err_no;
    }

    public String getSn() {
        return sn;
    }
}
