 package com.example.gamebanchim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying, isGameOver = false, isFlyUp = false;
    private int screenX, screenY, counter = 0, level = 1, score = 0, upgradeMax = 25, upgradeSpeed = 0, randomUpgrade = 0;
    private Bird1[] bird1s;
    private Bird2[] bird2s;
    public static float screenRatioX, screenRatioY;
    private BackGround background1, background2;
    private Paint paintScore, paintSpeed, paintLevel;
    private MayBay mb;
    private List<Bullet> bullets;
    private Random random;
    private SharedPreferences prefs;
    private GameActivity activity;
    private SoundPool soundPool;
    private int shootSound;
    private Troll[] trolls;


    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        shootSound = soundPool.load(activity, R.raw.shoot, 1);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        background1 = new BackGround(screenX, screenY, getResources());
        background2 = new BackGround(screenX, screenY, getResources());

        mb = new MayBay(this, screenY, getResources());

        bullets = new ArrayList<>();

        background2.x = screenX;

        paintScore = new Paint();
        paintScore.setTextSize(128);
        paintScore.setColor(Color.WHITE);
        paintSpeed = new Paint();
        paintSpeed.setTextSize(64);
        paintSpeed.setColor(Color.BLACK);
        paintLevel = new Paint();
        paintLevel.setTextSize(64);
        paintLevel.setColor(Color.RED);
        bird1s = new Bird1[4];
        bird2s = new Bird2[4];
        trolls = new Troll[2];

        for(int i = 0;i < 4; i++){
            Bird1 bird1 = new Bird1(getResources());
            Bird2 bird2 = new Bird2(getResources());
            bird1s[i] = bird1;
            bird2s[i] = bird2;
        }
        for(int i = 0; i < 2; i++){
            Troll troll = new Troll(getResources());
            trolls[i] = troll;
        }

        random = new Random();

    }

    @Override
    public void run() {
        while (isPlaying){
            update();
            draw();
            sleep();
        }
    }

    private void update(){
        background1.x -= 10*screenRatioX;
//        background2.x -= 10*screenRatioX;

        if (background1.x + this.screenX < 0){
            background1.x = screenX;
        }

        if (background1.x < 0){
            background2.x = background1.x + this.screenX;
        } else {
            background2.x = background1.x - this.screenX;
        }

        counter++;
        if(counter > 15 - upgradeSpeed/2){
            counter = 0;
            mb.toShoot++;
        }
        if(mb.isGoingUp){
            mb.y -= 30*screenRatioY;
        }
        else if (mb.isGoingDown) {
            mb.y += 30 * screenRatioY;
        }

        if(mb.y < 0)
            mb.y = 0;

        if(mb.y > screenY - mb.height)
            mb.y = screenY - mb.height;

        List<Bullet> trash = new ArrayList<>();

        for(Bullet bullet : bullets){
            if(bullet.x > screenX)
                trash.add(bullet);
            bullet.x += 50 * screenRatioX;

            for (Bird1 bird1 : bird1s){
                if (Rect.intersects(bird1.getCollisionShape(), bullet.getCollisionShape())){
                    score++;
                    bird1.x = -500;
                    bullet.x = screenX + 500;
                    bird1.wasShot =  true;
                    randomUpgrade = random.nextInt(19);
                }
            }

            for (Bird2 bird2 : bird2s){
                if (Rect.intersects(bird2.getCollisionShape(), bullet.getCollisionShape())){
                    bird2.health -= 1;
                    bird2.x += 50 ;
                    bullet.x = screenX + 500;
                    if (bird2.health == 0){
                        score += 2;
                        bird2.x = -500;
                        bullet.x = screenX + 500;
                        bird2.wasShot =  true;
                        randomUpgrade = random.nextInt(12);
                    }
                }
            }

            for (Troll troll : trolls){
                if (Rect.intersects(troll.getCollisionShape(), bullet.getCollisionShape())){
                        score--;
                        troll.x = -500;
                        bullet.x = screenX + 500;
                        troll.wasShot =  true;
                        randomUpgrade = 13;
                    }
                }
        }

        if (score > 100){
            level = 2;
        } else if (score > 200){
            level = 3;
        }

        if (randomUpgrade == 1 && upgradeSpeed < upgradeMax) {
            upgradeSpeed++;
            randomUpgrade = 0;
        } else if (randomUpgrade == 13 && upgradeSpeed > 0) {
            upgradeSpeed--;
            randomUpgrade = 0;
        }

        for(Bullet bullet : trash)
            bullets.remove(bullet);

        for(Bird1 bird1 : bird1s){
            bird1.x -= bird1.speed;
            if (bird1.x + bird1.width < 0){

                if(!bird1.wasShot) {
                    isGameOver = true;
                    return;
                }

                int bound = (int) (30 * screenRatioX);
                bird1.speed = random.nextInt(bound);

                if (bird1.speed < 10 * screenRatioX)
                    bird1.speed = (int) (10 * screenRatioX);

                bird1.x = screenX;
                bird1.y = random.nextInt(screenY - bird1.height);

                bird1.wasShot = false;
            }
        }
        if(level >= 2)
        for(Bird2 bird2 : bird2s){
            bird2.x -= bird2.speed;
            int upOrDown = random.nextInt(100);
            if(upOrDown>50)
                isFlyUp = false;
            else
                isFlyUp = true;
            if(isFlyUp)
                bird2.y += bird2.speed/2;
            else
                bird2.y -= bird2.speed/2;


            if (bird2.x + bird2.width < 0){

                if(!bird2.wasShot) {
                    isGameOver = true;
                    return;
                }

                int bound = (int) (20 * screenRatioX);
                bird2.speed = random.nextInt(bound);

                if (bird2.speed < 5 * screenRatioX)
                    bird2.speed = (int) (5 * screenRatioX);

                bird2.x = screenX;
                bird2.y = random.nextInt(screenY - bird2.height);

                bird2.wasShot = false;
            }
        }
        if(level >= 3)
            for(Troll troll : trolls){
                troll.x -= troll.speed;
                if (troll.x + troll.width < 0){

                    int bound = (int) (50 * screenRatioX);
                    troll.speed = random.nextInt(bound);

                    if (troll.speed < 35 * screenRatioX)
                        troll.speed = (int) (35 * screenRatioX);

                    troll.x = screenX;
                    troll.y = random.nextInt(screenY - troll.height);

                    troll.wasShot = false;
                }
            }



//        if (Rect.intersects(bird.getCollisionShape(), mb.getCollisionShape())){
//            isGameOver = true;
//            return;
//        }

    }

    private void draw(){

        if(getHolder().getSurface().isValid()){

            final Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paintScore);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paintScore);
            if(isGameOver){
                isPlaying = false;
                canvas.drawBitmap(mb.getBang(), mb.x, mb.y, paintScore);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                waitBeforeExiting();
                return;
            }

            canvas.drawText(score + "", screenX/2f - 30, 164, paintScore);
            canvas.drawText("Shoot speed: "+upgradeSpeed, 100, 164, paintSpeed);
            canvas.drawText("Level: "+level, screenX/2f - 64, 250, paintLevel);


            for(Bird1 bird1 : bird1s)
                canvas.drawBitmap(bird1.getBird(), bird1.x, bird1.y, paintScore);

            for(Bird2 bird2 : bird2s)
                canvas.drawBitmap(bird2.getBird(), bird2.x, bird2.y, paintScore);

            for(Troll troll: trolls)
                canvas.drawBitmap(troll.getBird(), troll.x, troll.y, paintScore);

            canvas.drawBitmap(mb.getMayBay(), mb.x,mb.y, paintScore);
            for(Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paintScore);



            getHolder().unlockCanvasAndPost(canvas);
        }

    }

    private void waitBeforeExiting() {
        try {
            Thread.sleep(1000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveIfHighScore() {
        if(prefs.getInt("highscore", 0) < score){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }
    }

    private void sleep(){
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }
    public void pause(){
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()){
            case  MotionEvent.ACTION_DOWN:
                if(event.getY() < mb.y) {
                    mb.isGoingUp = true;
                }
                if(event.getY() > mb.y) {
                    mb.isGoingDown = true;
                };
                break;
            case MotionEvent.ACTION_UP:
                mb.isGoingUp = false;
                mb.isGoingDown = false;
        }
        return true;
    }

    public void newBullet() {

        if(!prefs.getBoolean("isMuteSound", false))
            soundPool.play(shootSound, 1, 1, 0, 0, 1);

        Bullet bullet = new Bullet(getResources());
        bullet.x = mb.x + mb.width;
        bullet.y = (int) (mb.y + (mb.height / 2));
        bullets.add(bullet);
    }


}
