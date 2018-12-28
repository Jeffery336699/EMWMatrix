package cc.emw.mobile.chat.model.bean;

import com.farsunset.cim.client.model.Message;

import java.io.Serializable;

/**
 * Created by sunny.du on 2017/5/13.
 */

public class ImprotanceMessage extends Message implements Serializable {
   private  boolean isSecletor=false;
   public void setIsSelector(boolean isSecletor){
      this.isSecletor=isSecletor;
   }
   public boolean getIsSelector(){
      return isSecletor;
   }
}
