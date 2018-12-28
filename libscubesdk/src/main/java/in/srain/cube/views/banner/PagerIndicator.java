package in.srain.cube.views.banner;

public interface PagerIndicator {

    void setNum(int num);

    int getTotal();

    void setSelected(int index);

    int getCurrentIndex();
}
