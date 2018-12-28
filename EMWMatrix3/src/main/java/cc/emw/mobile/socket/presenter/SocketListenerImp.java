package cc.emw.mobile.socket.presenter;

import cc.emw.mobile.socket.action.SocketServiceInter;
import cc.emw.mobile.socket.model.SocketActionModel;
import cc.emw.mobile.socket.model.SocketActionModelImp;
import io.socket.client.Socket;

/**
 * @author fmc
 * @package cc.emw.mobile.socket.presenter
 * @data on 2018/5/21  18:40
 * @describe TODO
 */

public class SocketListenerImp  implements SocketListener{
    private SocketActionModel socketActionModel;
    private SocketServiceInter serviceInter;
    public SocketListenerImp (SocketServiceInter serviceInter){
        this.serviceInter = serviceInter;
        socketActionModel = new SocketActionModelImp();
    }

    @Override
    public void doOnAction(Socket socket) {
       // socketActionModel.doOnSocket(serviceInter,socket);
    }
}
