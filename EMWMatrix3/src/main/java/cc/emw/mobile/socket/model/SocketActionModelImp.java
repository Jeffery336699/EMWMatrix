package cc.emw.mobile.socket.model;



import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.socket.action.SocketServiceInter;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * @author fmc
 * @package cc.emw.mobile.socket.model
 * @data on 2018/5/21  17:13
 * @describe TODO
 */

public class SocketActionModelImp implements SocketActionModel{

    public Socket initSocket(EMWApplication application) {
        Socket socket = application.getAppIOSocket();
        try{
            if(socket != null){
                socket = IO.socket(Const.SOCKET_URL);
            }
        }catch (Exception ex){

        }
        return socket;
    }
    //@Override
    public void doRegSocket(Socket socket,String regGson) {
        if(socket.connected()){
            socket.emit(EMWApplication.SOCKET_EVENT_REG, regGson, new Ack() {
                @Override
                public void call(Object... args) {

                }
            });
        }
    }
    // @Override
    public void doConnectSocket(EMWApplication application) {
        Socket socket = initSocket(application);
        socket.connect();
    }

    @Override
    public void doOnSocket(SocketServiceInter serviceInter, Socket socket,String socket_tag) {
        emit = new MyEmitter(serviceInter);
        socket.on(socket_tag, emit);
    }

    @Override
    public void doOffSocket(Socket socket,String socket_tag) {
        if(emit != null){
            socket.off(socket_tag,emit);
        }

    }
    private MyEmitter emit;
    public class MyEmitter implements Emitter.Listener{
        private SocketServiceInter socketServiceInter;
        public MyEmitter(SocketServiceInter socketServiceInter){
            this.socketServiceInter = socketServiceInter;
        }
        @Override
        public void call(Object... args) {
            socketServiceInter.doDialogShow();
        }
    }

}
