package com.pixelsmith.pixelview;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class AccentBackgroundView extends View {

    private Paint paint;
    private Random random;
    private Path[] blobs;

    public AccentBackgroundView(Context context) {
        super(context);
        init();
    }

    public AccentBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        random = new Random();
        generateBlobs();
    }

    private void generateBlobs() {
        blobs = new Path[5]; 
        for (int i = 0; i < blobs.length; i++) {
            blobs[i] = createRandomBlob();
        }
    }

    private Path createRandomBlob() {
        Path path = new Path();
        float x = random.nextInt(800);
        float y = random.nextInt(800);
        float r = 100 + random.nextInt(100);
        path.addCircle(x, y, r, Path.Direction.CW);
        return path;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK); 

        paint.setColor(Color.parseColor("#FF69B4")); 
        paint.setAlpha(40); 

        for (Path blob : blobs) {
            canvas.drawPath(blob, paint);
        }
    }
}
