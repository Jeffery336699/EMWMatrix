package in.srain.cube.photos;

public interface PhotoReadyHandler {

    static int FROM_CAMERA = 1;
    static int FROM_ALBUM = 2;
    static int FROM_CROP = 3;

    void onPhotoReady(int from, String imgPath);
}
