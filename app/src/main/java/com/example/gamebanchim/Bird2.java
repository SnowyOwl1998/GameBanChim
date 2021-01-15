package com.example.gamebanchim;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.gamebanchim.GameView.screenRatioX;
import static com.example.gamebanchim.GameView.screenRatioY;
import static com.example.gamebanchim.GameView.levelUp;

public class Bird2 {
    public int speed = 30;
    public boolean wasShot = true;
    int x = 0,y, width, height, birdCounter = 1, deadCounter = 1, health = (int) (2 + levelUp*0.05);
    Bitmap bird1, bird2, bird3, bird4, dead1, dead2, dead3, dead4;

    Bird2(Resources res) {
        bird1 = BitmapFactory.decodeResource(res, R.drawable.bird2_1);
        bird2 = BitmapFactory.decodeResource(res, R.drawable.bird2_2);
        bird3 = BitmapFactory.decodeResource(res, R.drawable.bird2_3);
        bird4 = BitmapFactory.decodeResource(res, R.drawable.bird2_4);
//        dead1 = BitmapFactory.decodeResource(res, R.drawable.dead1);
//        dead2 = BitmapFactory.decodeResource(res, R.drawable.dead2);
//        dead3 = BitmapFactory.decodeResource(res, R.drawable.dead3);
//        dead4 = BitmapFactory.decodeResource(res, R.drawable.dead4);

        width = bird1.getWidth();
        height = bird1.getHeight();

        width /= 9;
        height /= 9;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false);
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false);
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false);
        bird4 = Bitmap.createScaledBitmap(bird4, width, height, false);
//        dead1 = Bitmap.createScaledBitmap(dead1, width, height, false);
//        dead2 = Bitmap.createScaledBitmap(dead2, width, height, false);
//        dead3 = Bitmap.createScaledBitmap(dead3, width, height, false);
//        dead4 = Bitmap.createScaledBitmap(dead4, width, height, false);

        y = -height;
    }

    Bitmap getBird (){
        if(birdCounter == 1){
            birdCounter++;
            return bird1;
        } else if(birdCounter == 2){
            birdCounter++;
            return bird2;
        } else if(birdCounter == 3){
            birdCounter++;
            return bird3;
        }
        birdCounter = 1;
        return bird4;
    }

//    Bitmap birdDead (){
//        if(deadCounter == 1){
//            deadCounter++;
//            return dead1;
//        } else if(deadCounter == 2){
//            deadCounter++;
//            return dead2;
//        } else if(deadCounter == 3){
//            deadCounter++;
//            return dead3;
//        }
//        deadCounter = 1;
//        return dead4;
//    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }
}
