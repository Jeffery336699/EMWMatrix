package cc.emw.mobile.socket.model;

import cc.emw.mobile.socket.action.SocketServiceInter;
import io.socket.client.Socket;

/**
 * @author fmc
 * @package cc.emw.mobile.socket.model
 * @data on 2018/5/21  17:02
 * @describe TODO
 */

public interface SocketActionModel {
    void doOnSocket(SocketServiceInter serviceInter, Socket socket,String socket_tag);
    void doOffSocket(Socket socket, String socket_tag);

}
