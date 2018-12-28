package cc.emw.mobile.asr.asrfinishjson;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.asr.asrfinishjson
 * @data on 2018/9/20  16:23
 * @describe TODO
 */

public class AsrFinishJsonData {

    private String error;
    private String sub_error;
    private String desc;
    private OriginResult origin_result;

    public String getError() {
        return error;
    }

    public String getSub_error() {
        return sub_error;
    }

    public String getDesc() {
        return desc;
    }

    public OriginResult getOrigin_result() {
        return origin_result;
    }
}
