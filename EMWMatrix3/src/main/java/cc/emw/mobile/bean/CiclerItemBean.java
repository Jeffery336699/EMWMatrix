package cc.emw.mobile.bean;

import java.util.List;

public class CiclerItemBean {
    private String title;
    private List<ItemData> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ItemData> getList() {
        return list;
    }

    public void setList(List<ItemData> list) {
        this.list = list;
    }

    public static class ItemData{
        private String imageIcon;
        private String time;
        private String content;
        private String num;

        public String getImageIcon() {
            return imageIcon;
        }

        public void setImageIcon(String imageIcon) {
            this.imageIcon = imageIcon;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }
    }
}
