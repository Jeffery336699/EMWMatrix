package in.srain.cube.request;

public interface IRequest<T> {

    RequestData getRequestData();

    void onRequestSuccess(T data);

    void onRequestFail(FailData failData);

    RequestBase setFailData(FailData failData);

    FailData getFailData();

    /**
     * send request
     */
    void send();

    /**
     * request synchronously
     */
    T requestSync();

    void cancelRequest();

    T onDataFromServer(String data);

    /**
     * filter the origin data or convert its structure.
     *
     * @param jsonData
     * @return
     */
    T processOriginDataFromServer(JsonData jsonData);
}