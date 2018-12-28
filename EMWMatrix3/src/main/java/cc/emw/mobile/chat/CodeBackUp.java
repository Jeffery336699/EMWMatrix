package cc.emw.mobile.chat;

/**
 * Created by sunny.du on 2017/6/22.
 */

public class CodeBackUp {
    /**
     * ChatActivity滑动监听  已重写    暂时保留备份，后续稳定后删除
     */
    //        mListview.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (!mListview.getScrollFlag()) {
//                    switch (newState) {
//                        case RecyclerView.SCROLL_STATE_DRAGGING:
//                            if (mRlRoot4.getVisibility() == View.VISIBLE) {
//                                closeRootView(mRlRoot4, false, false);
////                                mItvChatAddMore.setBackground(getResources().getDrawable(R.drawable.chat_more_app));
//                            }
//                            if (mRlExpressionRoot.getVisibility() == View.VISIBLE) {
//                                closeRootView(mRlExpressionRoot, true, true);
//                            }
//                            if (mLlParams.leftMargin < 0 && mTvOpenChatAiButton2.getVisibility() == View.GONE) {//键盘打开的时机关闭功能栏
//                                showMultimediaOpen();
//                            }
//                            if (mRlLine.getVisibility() == View.VISIBLE && photoAndCameraAdapter != null) {
//                                photoAndCameraAdapter.getPreSurfaceView().closeCamera();
//                                closeRootView(mRlLine, true, true);
////                                mIvChatAddPhoto.setBackground(getResources().getDrawable(R.drawable.chat_icon_camera));
//                            }
//                            if (mRlFileViewGroup.getVisibility() == View.VISIBLE) {
//                                mEtFileListSearch.setText("");
//                                closeRootView(mRlFileViewGroup, true, true);
//                                mRlChatMsgRoot.setVisibility(View.VISIBLE);
//                                mRlFileRoot.setVisibility(View.GONE);
//                                showMultimediaOpen();
//                            }
////                            if (keybordStateFlag) {
////                                keybordStateFlag = KeyBoardUtil.closeKeyboard(ChatActivity.this);
////                            }
//                            break;
//                        case RecyclerView.SCROLL_STATE_IDLE:
//                            break;
//                        case RecyclerView.SCROLL_STATE_SETTLING:
//                            break;
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
}
