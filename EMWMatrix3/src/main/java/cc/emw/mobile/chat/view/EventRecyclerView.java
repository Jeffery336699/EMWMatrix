package cc.emw.mobile.chat.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by sunny.du on 2017/5/11.
 */

public class EventRecyclerView extends RecyclerView {
    public EventRecyclerView(Context context) {
        this(context, null);
    }

    public EventRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    boolean eventFlag=false;
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent e) {
//        Log.d("sunny----->", "=11111111111111111111");
//        addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                int lastPosition = -1;
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                    if (layoutManager instanceof GridLayoutManager) {
//                        lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
//                    } else if (layoutManager instanceof LinearLayoutManager) {
//                        lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
//                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//                        int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
//                        ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
//                        lastPosition = findMax(lastPositions);
//                    }
//                    if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 1 || lastPosition == 8) {
//                        eventFlag = true;
//                    } else {
//                        eventFlag = false;
//                    }
//                }
//            }
//
//            //找到数组中的最大值
//            private int findMax(int[] lastPositions) {
//                int max = lastPositions[0];
//                for (int value : lastPositions) {
//                    if (value > max) {
//                        max = value;
//                    }
//                }
//                return max;
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
//        return true;
//    }
//    private boolean   eventFlag=false;
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                    int lastPosition = -1;
//                    if(newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                        if (layoutManager instanceof GridLayoutManager) {
//                            lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
//                        } else if (layoutManager instanceof LinearLayoutManager) {
//                            lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
//                        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//                            int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
//                            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
//                            lastPosition = findMax(lastPositions);
//                        }
//                        Log.d("sunny----->","lastPosition="+lastPosition);
//                        Log.d("sunny----->"," recyclerView.getLayoutManager().getItemCount()="+ recyclerView.getLayoutManager().getItemCount());
//                        if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 1 ||lastPosition==0) {
//                            eventFlag=true;
//                        }else{
//                            eventFlag=false;
//                        }
//                    }
//                }
//                //找到数组中的最大值
//                private int findMax(int[] lastPositions) {
//                    int max = lastPositions[0];
//                    for (int value : lastPositions) {
//                        if (value > max) {
//                            max = value;
//                        }
//                    }
//                    return max;
//                }
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                }
//            });
//        }
//        return eventFlag;
//    }
}
