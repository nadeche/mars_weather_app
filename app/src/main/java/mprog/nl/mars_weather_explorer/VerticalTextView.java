package mprog.nl.mars_weather_explorer;

/**
 * VerticalTextView.java
 *
 * Created Pointer Null edited on 24 oct. 2011, found on stackoverflow.com
 * link: http://stackoverflow.com/questions/1258275/vertical-rotated-label-in-android/7855852#7855852
 * */
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * This class is an implementation of a custom view.
 * It converts a textView to a vertical textView.
 */
public class VerticalTextView extends TextView {

    private final boolean topDown;

    public VerticalTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        final int gravity = getGravity();

        // When the textView has a gravity attribute of bottom the start letter will be on the bottom after rotating
        if(Gravity.isVertical(gravity) && (gravity&Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
            setGravity((gravity&Gravity.HORIZONTAL_GRAVITY_MASK) | Gravity.TOP);
            topDown = false;
        }else{
            topDown = true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas){
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();

        // rotate the textView with the start letter at the top
        if(topDown){
            canvas.translate(getWidth(), 0);
            canvas.rotate(90);
        }
        // rotate the textView with the start letter at the bottom
        else {
            canvas.translate(0, getHeight());
            canvas.rotate(-90);
        }

        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());

        getLayout().draw(canvas);
        canvas.restore();
    }
}
