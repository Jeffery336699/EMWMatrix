package cc.emw.mobile.chat.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sunnyDu on 3/7/17.
 */

public class MKLoader extends View {
  private Circle[] circles;
  private int circlesSize;
  private float radius;
  private int[] transformations;
  protected int color;
  protected int width, height;
  protected int desiredWidth, desiredHeight;
  protected PointF center;


  private void initialize(Context context, AttributeSet attrs) {
    circlesSize = 3;
    transformations = new int[]{-1, 0,1};
    this.desiredWidth = 150;
    this.color=Color.parseColor("#ffffff");
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int measuredWidth = resolveSize(this.desiredWidth, widthMeasureSpec);
    final int measuredHeight = resolveSize(this.desiredWidth, heightMeasureSpec);
    setMeasuredDimension(measuredWidth, measuredHeight);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    this.width = getWidth();
    this.height = getHeight();
    this.center = new PointF(width / 2.0f, height / 2.0f);
    circles = new Circle[circlesSize];
    radius = 5f;
    for (int i = 0; i < circlesSize; i++) {
      circles[i] = new Circle();
      circles[i].setColor(color);
      circles[i].setRadius(radius);
      circles[i].setCenter(center.x, center.y);
    }
    for (int i = 0; i < circlesSize; i++) {
      final int index = i;
      ValueAnimator translateAnimator = ValueAnimator.ofFloat(center.y,center.y+5f,center.y-5f, center.y,center.y-5f,center.y+5f,center.y);
      translateAnimator.setDuration(1500);
      translateAnimator.setStartDelay(index * 120);
      translateAnimator.setRepeatCount(1);
      translateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          circles[index].setCenter(center.x, (float)animation.getAnimatedValue());
          invalidate();
        }
      });
      if(i==0) {
        translateAnimator.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            if (listener != null)
              listener.doEnd();
          }
        });
      }
      translateAnimator.start();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    for (int i = 0; i < circlesSize; i++) {
      canvas.save();
      canvas.translate(7f * radius * transformations[i], 0);
      circles[i].draw(canvas);
      canvas.restore();
    }
  }



  public OnAnimEndListener listener;
  public void setOnAnimEndListener(OnAnimEndListener listener){
    this.listener=listener;
  }
  public interface OnAnimEndListener{
    void doEnd();
  }
  public MKLoader(Context context) {
    this(context,null);
  }

  public MKLoader(Context context, AttributeSet attrs) {
    this(context, attrs,0);
  }

  public MKLoader(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context, attrs);
  }
}
