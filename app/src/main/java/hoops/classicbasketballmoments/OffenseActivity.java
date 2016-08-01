package hoops.classicbasketballmoments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by singerm on 7/20/2016.
 */
public class OffenseActivity extends Activity {

    final static String TAG = "OffenseActivity";

    private RelativeLayout mFrame;
    private ImageView mCourt;
    private TextView mBlurb, mLink;
    private ImageButton mPlayPause, mStep, mBackstep, mFaster, mSlower;
    private int mDisplayWidth, mDisplayHeight, shotClockX, shotClockY, REFRESH_RATE = 500;
    private float addToX, addToY;
    float factor;
    boolean isPlaying = false;
    PlayerView o1, o2, o3, o4, o5, d1, d2, d3, d4, d5, ballV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.offense_activity);

        mFrame = (RelativeLayout) findViewById(R.id.frame);
        mCourt = (ImageView) findViewById(R.id.court);
        mLink = (TextView) findViewById(R.id.link);
        mBlurb = (TextView) findViewById(R.id.blurb);
        mPlayPause = (ImageButton) findViewById(R.id.play_pause);
        mStep = (ImageButton) findViewById(R.id.step);
        mBackstep = (ImageButton) findViewById(R.id.backstep);
        mFaster = (ImageButton) findViewById(R.id.faster);
        mSlower = (ImageButton) findViewById(R.id.slower);

        //mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        mDisplayWidth = width;
        mDisplayHeight = height;
        Log.v(TAG, "mDisplayWidth: " + mDisplayWidth + ", mDisplayHeight: " + mDisplayHeight);

        adjustPositioningForScreen();

        Intent mIntent = getIntent();
        String offenseName = mIntent.getStringExtra("oName");
        Log.v(TAG, "This offense is: " + offenseName);

        PlayXMLParser mParser = new PlayXMLParser();
        ArrayList<String> playActions = new ArrayList<>();
        try {
            playActions = mParser.getPlay(offenseName, 0, getApplicationContext());
            //String text = Html.fromHtml("<center>" + mParser.mUrl + "</center>" +  "<br/>");
            mLink.setText(mParser.mUrl);
            mBlurb.setText(Html.fromHtml(mParser.mPlayName + "<br/><br/>" + mParser.mBlurb));
            if (mParser.mCourt.equals("right")) {
                shotClockX = 20;
                shotClockY = 40;
                mCourt.setImageResource(R.drawable.basket_right);
            }
            else {
                shotClockX = 450;
                shotClockY = 40;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        ArrayList<String> playActionsO1 = new ArrayList<>();
        ArrayList<String> playActionsO2 = new ArrayList<>();
        ArrayList<String> playActionsO3 = new ArrayList<>();
        ArrayList<String> playActionsO4 = new ArrayList<>();
        ArrayList<String> playActionsO5 = new ArrayList<>();

        ArrayList<String> playActionsD1 = new ArrayList<>();
        ArrayList<String> playActionsD2 = new ArrayList<>();
        ArrayList<String> playActionsD3 = new ArrayList<>();
        ArrayList<String> playActionsD4 = new ArrayList<>();
        ArrayList<String> playActionsD5 = new ArrayList<>();

        ArrayList<String> playActionsBall = new ArrayList<>();

        for (String action : playActions) {
            Log.v(TAG, "ACTION IS: " + action);

            if (action.split(",")[0].equals("opg"))
                //playActionsO1.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsO1.add(action);
            else if (action.split(",")[0].equals("osg"))
                //playActionsO2.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsO2.add(action);
            else if (action.split(",")[0].equals("osf"))
                //playActionsO3.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsO3.add(action);
            else if (action.split(",")[0].equals("opf"))
                //playActionsO4.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsO4.add(action);
            else if (action.split(",")[0].equals("oc"))
                //playActionsO5.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsO5.add(action);

            else if (action.split(",")[0].equals("dpg"))
                //playActionsD1.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsD1.add(action);
            else if (action.split(",")[0].equals("dsg"))
                //playActionsD2.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsD2.add(action);
            else if (action.split(",")[0].equals("dsf"))
                //playActionsD3.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsD3.add(action);
            else if (action.split(",")[0].equals("dpf"))
                //playActionsD4.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsD4.add(action);
            else if (action.split(",")[0].equals("dc"))
                //playActionsD5.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsD5.add(action);

            else if (action.split(",")[0].equals("ball"))
                //playActionsBall.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                //playActionsBall.add(action.split(",")[1] + "," + action.split(",")[2] + "," + action.split(",")[3]);
                playActionsBall.add(action);
        }

        adjustPositioningForScreen();

        int oPlayer = R.drawable.odot;
        int dPlayer = R.drawable.ddot;
        int ball = R.drawable.ball;

        o1 = new PlayerView(getApplicationContext(), playActionsO1, oPlayer);
        o2 = new PlayerView(getApplicationContext(), playActionsO2, oPlayer);
        o3 = new PlayerView(getApplicationContext(), playActionsO3, oPlayer);
        o4 = new PlayerView(getApplicationContext(), playActionsO4, oPlayer);
        o5 = new PlayerView(getApplicationContext(), playActionsO5, oPlayer);

        d1 = new PlayerView(getApplicationContext(), playActionsD1, dPlayer);
        d2 = new PlayerView(getApplicationContext(), playActionsD2, dPlayer);
        d3 = new PlayerView(getApplicationContext(), playActionsD3, dPlayer);
        d4 = new PlayerView(getApplicationContext(), playActionsD4, dPlayer);
        d5 = new PlayerView(getApplicationContext(), playActionsD5, dPlayer);

        ballV = new PlayerView(getApplicationContext(), playActionsBall, ball);

        mFrame.addView(o1);
        mFrame.addView(o2);
        mFrame.addView(o3);
        mFrame.addView(o4);
        mFrame.addView(o5);

        mFrame.addView(d1);
        mFrame.addView(d2);
        mFrame.addView(d3);
        mFrame.addView(d4);
        mFrame.addView(d5);

        mFrame.addView(ballV);

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isPlaying) {
                    pause();
                }else{
                    play();
                }
                isPlaying = !isPlaying;
            }
        });

        mStep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stepForward();
            }
        });

        mBackstep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stepBack();
            }
        });

        mSlower.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (REFRESH_RATE < 2000) {
                    REFRESH_RATE += 500;
                    Toast.makeText(getApplicationContext(), "Slowed down, frames switch at " + REFRESH_RATE + " ms. Normal speed is 500ms", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "C'mon man, I'm not gonna let you go slower than this.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mFaster.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (REFRESH_RATE > 500) {
                    REFRESH_RATE -= 500;
                    Toast.makeText(getApplicationContext(), "Sped up, frames switch at " + REFRESH_RATE + " ms. Normal speed is 500ms", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't go any faster bro!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void stepBack() {

        o1.stepBack();
        o2.stepBack();
        o3.stepBack();
        o4.stepBack();
        o5.stepBack();

        d1.stepBack();
        d2.stepBack();
        d3.stepBack();
        d4.stepBack();
        d5.stepBack();

        ballV.stepBack();

    }

    public void stepForward() {

        o1.stepForward();
        o2.stepForward();
        o3.stepForward();
        o4.stepForward();
        o5.stepForward();

        d1.stepForward();
        d2.stepForward();
        d3.stepForward();
        d4.stepForward();
        d5.stepForward();

        ballV.stepForward();

    }

    public void pause() {

        mPlayPause.setImageResource(R.drawable.play);

        o1.stop();
        o2.stop();
        o3.stop();
        o4.stop();
        o5.stop();

        d1.stop();
        d2.stop();
        d3.stop();
        d4.stop();
        d5.stop();

        ballV.stop();

    }

    public void play() {

        mPlayPause.setImageResource(R.drawable.pause);

        o1.start();
        o2.start();
        o3.start();
        o4.start();
        o5.start();

        d1.start();
        d2.start();
        d3.start();
        d4.start();
        d5.start();

        ballV.start();

    }

    public void adjustPositioningForScreen() {
        int mCourtWidth = 470, mCourtHeight = 500;

        if (mFrame.getWidth() != 0 && mFrame.getHeight() != 0) {
            mDisplayWidth = mFrame.getWidth();
            mDisplayHeight = mFrame.getHeight();
        }

        // Upright view
        // Court width  == DisplayWidth
        // Court height == (DisplayWidth / 470) * 500
        if (mDisplayHeight > mDisplayWidth) {
            factor = (float)mDisplayWidth/(float)mCourtWidth;
            addToY = (((float)mDisplayHeight - factor*(float)mCourtHeight)/2);
            Log.v(TAG, "0, 0 for image is: 0, " + addToY);
        }
        // Landscape view
        // Court height == DisplayHeight
        // Court width  == (DisplayHeight / 500) * 470
        else {
            factor = (float)mDisplayHeight/(float)mCourtHeight;
            addToX = (((float)mDisplayWidth - factor*(float)mCourtWidth)/2);
            Log.v(TAG, "0, 0 for image is: " + addToX + ", 0");
        }
        Log.v(TAG, "FACTOR is: " + factor);

        int orientation = getResources().getConfiguration().orientation;
        Log.v(TAG, "Orientation is... " + orientation);

        if (orientation == 2) {
            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(mCourtWidth*factor), Math.round(mCourtHeight*factor));
            //mCourt.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Math.round(mCourtWidth*factor), Math.round(mCourtHeight*factor));
            mCourt.setLayoutParams(layoutParams);
        }
        //
        //touched_image_view.setLayoutParams(layoutParams);



        addToX = 3;
        addToY = 3;

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            adjustPositioningForScreen();
        }
    }

    public class PlayerView extends View {

        private float rawX = 0, rawY = 0;
        private float x = 0, y = 0;
        private int frameNum = 1;
        private ArrayList<String> playList;
        private Bitmap mBitmap;
        private Bitmap mScaledBitmap;
        private final Paint mPainter = new Paint();
        private ScheduledFuture<?> mMoverFuture;
        private int xEdge = 0, yEdge = 0;
        //private int REFRESH_RATE = 500;
        private int bitmapResId;
        private int playerSize;

        PlayerView(Context context, ArrayList<String> playList, int bitmapResId) {
            super(context);

            mBitmap = BitmapFactory.decodeResource(getResources(), bitmapResId);
            this.bitmapResId = bitmapResId;
            playerSize = Math.round(mBitmap.getHeight() * factor);
            mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, playerSize, playerSize, false);
            this.playList = playList;
            Log.v(TAG, "playList: " + playList.get(0));

            this.rawX = Float.valueOf(playList.get(0).split(",")[1]);
            this.rawY = Float.valueOf(playList.get(0).split(",")[2]);

            this.x = xCalc();
            this.y = yCalc();

            Log.v(TAG, "initial x and y pos: " + x + " and " + y);

            mPainter.setAntiAlias(true);
        }

        @Override
        protected synchronized void onDraw(Canvas canvas) {

            canvas.save();

            canvas.drawBitmap(mScaledBitmap, x, y, mPainter);

            Log.v(TAG, "Should be a name: " + playList.get(0).split(",")[3]);

            mPainter.setTextSize(48f);

            // player
            if (this.bitmapResId != R.drawable.ball) {
                if (frameNum < playList.size()) {
                    //mPainter.setStyle(Paint.Style.STROKE);
                    //mPainter.setStrokeWidth(10);
                    //canvas.drawLine(x, y, x + playerSize, y, mPainter);
                    //mPainter.setStrokeWidth(1);
                    // Write the name so it shows in the screen
                    if (isOutOfScreen())
                        canvas.drawText(playList.get(frameNum).split(",")[3], x, y + playerSize, mPainter);
                    else
                        canvas.drawText(playList.get(frameNum).split(",")[3], x, y, mPainter);
                }
                else if (frameNum == playList.size()) {
                    //mPainter.setStrokeWidth(10);
                    //canvas.drawLine(x, y, x, y - playerSize, mPainter);
                    //mPainter.setStrokeWidth(1);
                    if (isOutOfScreen())
                        canvas.drawText(playList.get(0).split(",")[3], x, y + playerSize, mPainter);
                    else
                        canvas.drawText(playList.get(0).split(",")[3], x, y, mPainter);
                }
            }
            // ball
            else if (frameNum <= playList.size()) {
                //this.rawX = 470;
                //this.rawY = 0;
                Log.v(TAG, "x calc equals: " + xCalc(450) + " and yCalc equals: " + yCalc(40));
                canvas.drawText(playList.get(frameNum-1).split(",")[3], xCalc(shotClockX), yCalc(shotClockY), mPainter);
            }

            canvas.restore();

        }

        private float xCalc() {
            return rawX*factor + addToX - (factor*mBitmap.getHeight()/2);
        }

        private float xCalc(int x) {
            return x*factor + addToX - (factor*mBitmap.getHeight()/2);
        }

        private float yCalc() {
            return rawY*factor + addToY - (factor*mBitmap.getHeight()/2);
        }

        private float yCalc(int y) {
            return y*factor + addToY - (factor*mBitmap.getHeight()/2);
        }

        private boolean isOutOfScreen() {
            if (rawY < 30) {
                return true;
            }

            return false;
        }

        private void start() {

            // Creates a WorkerThread
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

            // Execute the run() in Worker Thread every REFRESH_RATE
            // milliseconds
            // Save reference to this job in mMoverFuture
            mMoverFuture = executor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {

                    // TODO - implement movement logic.
                    // Each time this method is run the BubbleView should
                    // move one step. If the BubbleView exits the display,
                    // stop the BubbleView's Worker Thread.
                    // Otherwise, request that the BubbleView be redrawn.
                    if (frameNum == playList.size())
                        stop();
                    else
                        moveWhileOnScreen();

                    PlayerView.this.postInvalidate();

                }
            }, 1500, REFRESH_RATE, TimeUnit.MILLISECONDS);

        }

        private void stop() {

            if (null != mMoverFuture && !mMoverFuture.isDone()) {
                mMoverFuture.cancel(true);
            }

            // This work will be performed on the UI Thread
            /*mFrame.post(new Runnable() {
                @Override
                public void run() {

                    // TODO - Remove the BubbleView from mFrame
                    mFrame.removeView(PlayerView.this);

                }
            });*/
        }

        private void stepForward() {
            Log.v(TAG, "IN stepForward!");
            if (frameNum == playList.size())
                return;

            stop();
            //frameNum++;
            moveWhileOnScreen();
            PlayerView.this.postInvalidate();

        }

        private void stepBack() {
            Log.v(TAG, "IN stepBack!");
            if (frameNum <= 1)
                return;

            stop();
            frameNum=frameNum-2;
            moveWhileOnScreen();
            PlayerView.this.postInvalidate();

        }

        private synchronized void moveWhileOnScreen() {

            Log.v(TAG, "In moveWhileOnScreen() frameNum: " + frameNum + " old x and y:" + x + " and " + y);
            //x += 25;
            //y += 25;


            if (frameNum < playList.size()) {
                this.rawX = Float.valueOf(playList.get(frameNum).split(",")[1]);
                this.rawY = Float.valueOf(playList.get(frameNum).split(",")[2]);
                this.x = xCalc();
                this.y = yCalc();
                //x = (Float.valueOf(playList.get(frameNum).split(",")[0])*factor) + addToX;
                //y = (Float.valueOf(playList.get(frameNum).split(",")[1])*factor) + addToY;
                //this.x -= (factor*mBitmap.getHeight())/2;
                //this.y -= (factor*mBitmap.getHeight())/2;
                frameNum++;

            }
            Log.v(TAG, "In moveWhileOnScreen() new x and y:" + x + " and " + y);

            //postInvalidate();
            //return true;
        }


    }

}
