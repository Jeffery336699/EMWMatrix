package in.srain.cube.request;

public interface RequestHandler<T> extends RequestFinishHandler<T> {

    T processOriginData(JsonData jsonData);

    void onRequestFail(FailData failData);
}