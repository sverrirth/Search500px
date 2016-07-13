package ir.sverr.gridsearch;

import android.content.Context;
import android.widget.ImageView;

public class SquareImageView extends ImageView {   //For keeping the images as a square without disproportionate stretching

    public SquareImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

}