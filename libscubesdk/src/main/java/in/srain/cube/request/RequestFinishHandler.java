package in.srain.cube.request;

public interface RequestFinishHandler<T> {

    void onRequestFinish(T data);
}