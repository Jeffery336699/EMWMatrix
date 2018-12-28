package cc.emw.mobile.chat.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by sunnyDu on 3/7/17.
 */

public class Circle {
  private PointF center;
  private float radius;
  private Paint paint;
  public Circle() {
    center = new PointF();
    paint = new Paint();
    paint.setAntiAlias(true);
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  public void setCenter(float x, float y) {
    center.set(x, y);
  }

  public void setColor(int color) {
    paint.setColor(color);
  }

  public void setAlpha(int alpha) {
    paint.setAlpha(alpha);
  }

  public void setWidth(float width) {
    paint.setStrokeWidth(width);
  }

  public void setStyle(Paint.Style style) {
    paint.setStyle(style);
  }
   public void draw(Canvas canvas) {
    canvas.drawCircle(center.x, center.y, radius, paint);
  }
}
