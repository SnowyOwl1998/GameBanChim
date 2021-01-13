package com.example.gamebanchim;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.gamebanchim.GameView.screenRatioX;
import static com.example.gamebanchim.GameView.screenRatioY;

public class Bullet {
    int x,y,width,height, damage = 1;
    Bitmap bullet;

    Bullet (Resources res){
        bullet = BitmapFactory.decodeResource(res, R.drawable.dan);

        width = bullet.getWidth();
        height = bullet.getHeight();

        width /= 2;
        height /= 2;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);
    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }
}
