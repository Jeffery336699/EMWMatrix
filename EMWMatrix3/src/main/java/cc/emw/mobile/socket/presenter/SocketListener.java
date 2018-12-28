package cc.emw.mobile.socket.presenter;

import io.socket.client.Socket;

/**
 * @author fmc
 * @package cc.emw.mobile.socket.presenter
 * @data on 2018/5/21  18:15
 * @describe TODO
 */

public interface SocketListener {
    void doOnAction(Socket socket);
}
