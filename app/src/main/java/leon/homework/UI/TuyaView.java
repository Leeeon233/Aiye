package leon.homework.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import leon.homework.data.Const;

/*
 * Created by BC on 2017/3/21 0021.
 */

public class TuyaView extends View {
    private Context context;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;// 画布的画笔
    private Paint mPaint;// 真实的画笔
    private float mX, mY;// 临时点坐标
    private static final float TOUCH_TOLERANCE = 4;
    // 保存Path路径的集合
    private static List<DrawPath> savePath;
    // 保存已删除Path路径的集合
    private static List<DrawPath> deletePath;
    // 记录Path路径的对象
    private DrawPath dp;
    private int screenWidth, screenHeight;
    private int currentColor = Color.BLACK;
    private int currentSize = 5;
    private int currentStyle = 1;
    //private int[] paintColor;//颜色集合

    private class DrawPath {
        public Path path;// 路径
        Paint paint;// 画笔
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        screenWidth = getProperSize(500, widthMeasureSpec);
        // 获取合适的高度值
        screenHeight = getProperSize(500, heightMeasureSpec);
        // 设置宽高尺寸大小值，此方法决定view最终的尺寸大小
        setMeasuredDimension(screenWidth, screenHeight);
        Log.d("TUYA","width:"+screenWidth+"height:"+screenHeight);
        initCanvas();
    }

    private int getProperSize(int defaultSize, int measureSpec){
        int properSize = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode){
            case MeasureSpec.UNSPECIFIED:
                // 没有指定大小，设置为默认大小
                properSize = defaultSize;
                break;
            case MeasureSpec.EXACTLY:
                // 固定大小，无需更改其大小
                properSize = size;
                break;
            case MeasureSpec.AT_MOST:
                // 此处该值可以取小于等于最大值的任意值，此处取最大值的1/4
                properSize = size / 4;
        }

        return properSize;
    }

    public TuyaView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.context = context;
        /*paintColor = new int[]{
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.BLACK, Color.GRAY, Color.CYAN
        };*/
        setLayerType(LAYER_TYPE_SOFTWARE,null);//设置默认样式，去除dis-in的黑色方框以及clear模式的黑线效果

        savePath = new ArrayList<>();
        deletePath = new ArrayList<>();
    }
    public void initCanvas() {
        setPaintStyle();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        //画布大小
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.argb(0,0,0,0));
        mCanvas = new Canvas(mBitmap);  //所有mCanvas画的东西都被保存在了mBitmap中
        mCanvas.drawColor(Color.TRANSPARENT);
    }
    //初始化画笔样式
    private void setPaintStyle() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        if (currentStyle == 1) {
            mPaint.setStrokeWidth(5);
            mPaint.setColor(currentColor);
        } else {//橡皮擦
            mPaint.setAlpha(0);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setStrokeWidth(currentSize);
        }
    }
    @Override
    public void onDraw(Canvas canvas) {
        //canvas.drawColor(0xFFAAAAAA);
        // 将前面已经画过得显示出来
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        if (mPath != null) {
            // 实时的显示
            canvas.drawPath(mPath, mPaint);
        }
    }
    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(mY - y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用mPath.lineTo也可以)
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            //mPath.lineTo(mX,mY);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        //将一条完整的路径保存下来
        savePath.add(dp);
        mPath = null;// 重新置空
    }
    /**
     * 撤销
     * 撤销的核心思想就是将画布清空，
     * 将保存下来的Path路径最后一个移除掉，
     * 重新将路径画在画布上面。
     */
    public void undo() {
        if (savePath != null && savePath.size() > 0) {
            DrawPath drawPath = savePath.get(savePath.size() - 1);
            deletePath.add(drawPath);
            savePath.remove(savePath.size() - 1);
            redrawOnBitmap();
        }
    }
    /**
     * 重做
     */
    public void redo() {
        if (savePath != null && savePath.size() > 0) {
            savePath.clear();
            redrawOnBitmap();
        }
    }
    private void redrawOnBitmap() {
        /*mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布*/
        initCanvas();
        for (DrawPath drawPath : savePath) {
            mCanvas.drawPath(drawPath.path, drawPath.paint);
        }
        invalidate();// 刷新
    }
    /**
     * 恢复，恢复的核心就是将删除的那条路径重新添加到savapath中重新绘画即可
     */
    public void recover() {
        if (deletePath.size() > 0) {
            //将删除的路径列表中的最后一个，也就是最顶端路径取出（栈）,并加入路径保存列表中
            DrawPath dp = deletePath.get(deletePath.size() - 1);
            savePath.add(dp);
            //将取出的路径重绘在画布上
            mCanvas.drawPath(dp.path, dp.paint);
            //将该路径从删除的路径列表中去除
            deletePath.remove(deletePath.size() - 1);
            invalidate();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 每次down下去重新new一个Path
                mPath = new Path();
                //每一次记录的路径对象是不一样的
                dp = new DrawPath();
                dp.path = mPath;
                dp.paint = mPaint;
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
    //保存到sd卡
    public void saveToSDCard(String workid,String quesid) {
        File file = new File(Const.INSTANCE.getDATA_PATH() + workid+"/"+quesid+".png");
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdir();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

        //发送Sd卡的就绪广播,要不然在手机图库中不存在
        /*Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
        intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
        context.sendBroadcast(intent);*/
        Log.e("TAG", "图片已保存");
    }
    //以下为样式修改内容
    //设置画笔样式
    public void selectPaintStyle(int which) {
        if (which == 0) {
            currentStyle = 1;
            setPaintStyle();
        }
        //当选择的是橡皮擦时，设置颜色为白色
        if (which == 1) {
            currentStyle = 2;
            setPaintStyle();
        }
    }
    //选择画笔大小
    public void selectPaintSize(int which) {
        //int size = Integer.parseInt(this.getResources().getStringArray(R.array.paintsize)[which]);
        currentSize = which;
        setPaintStyle();
    }
    //设置画笔颜色
    /*public void selectPaintColor(int which) {
        currentColor = paintColor[which];
        setPaintStyle();
    }*/
}
