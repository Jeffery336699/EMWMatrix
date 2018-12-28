package cc.emw.mobile.chat.model.bean;

import java.util.List;

/**
 * Created by sunny.du on 2017/3/20.
 *
 */
public class AiMsgBean {
    private String Type;
    private String Text;
    private String[] Images;
    private String Url;
    private List<Foods> Foods;
    @Override
    public String toString() {
        return "{Type="+Type+"},{Text="+Text+"},{Images="+Images+"},{Url="+Url+"}----end";
    }

    public AiMsgBean(){}
    public AiMsgBean(String Type,String Text){
        this.Type=Type;
        this.Text=Text;

    }
    public AiMsgBean(String Type,String[] Images,String Url){
        this.Type=Type;
        this.Images=Images;
        this.Url=Url;
    }
    public AiMsgBean(String Type,List<Foods> Foods,String Url){
        this.Type=Type;
        this.Foods=Foods;
        this.Url=Url;
    }

    public void setFoods(List<AiMsgBean.Foods> foods) {
        Foods = foods;
    }

    public List<AiMsgBean.Foods> getFoods() {
        return Foods;
    }

    public String getType() {
        return Type;
    }

    public String getText() {
        return Text;
    }

    public void setCode(String Type) {
        this.Type = Type;
    }

    public void setText(String Text) {
        this.Text = Text;
    }

    public void setImages(String[] images) {
        Images = images;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String[] getImages() {
        return Images;
    }

    public String getUrl() {
        return Url;
    }

    public class Foods{
        private String Name;
        private String PriceText;
        private String DefaultPic;
        private String Url;
        public Foods(){}
        public Foods(String Name,String PriceText,String DefaultPic,String Url){
            this.Name=Name;
            this.PriceText=PriceText;
            this.DefaultPic=DefaultPic;
            this.Url=Url;
        }
        public String getUrl() {
            return Url;
        }

        public String getDefaultPic() {
            return DefaultPic;
        }

        public String getName() {
            return Name;
        }

        public String getPriceText() {
            return PriceText;
        }

        public void setUrl(String url) {
            Url = url;
        }

        public void setDefaultPic(String defaultPic) {
            DefaultPic = defaultPic;
        }

        public void setName(String name) {
            Name = name;
        }

        public void setPriceText(String priceText) {
            PriceText = priceText;
        }
    }
}
