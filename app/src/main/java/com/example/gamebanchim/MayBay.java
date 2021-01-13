package com.example.gamebanchim;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.logging.Handler;

import static com.example.gamebanchim.GameView.screenRatioX;
import static com.example.gamebanchim.GameView.screenRatioY;

public class MayBay {
    public boolean isGoingUp = false, isGoingDown = false;
    public int toShoot = 0;
    int x,y,width,height,wingCounter = 0, shootCounter = 0, bangCounter = 0;
    Bitmap Mb1, Mb2, Mb3, shoot1, shoot2, shoot3, shoot4, shoot5, bang1, bang2, bang3, bang4;
    private GameView gameView;


    MayBay (GameView gameView, int screenY, Resources res){
        this.gameView = gameView;
        Mb1 = BitmapFactory.decodeResource(res, R.drawable.mb1);
        Mb2 = BitmapFactory.decodeResource(res, R.drawable.mb2);
        Mb3 = BitmapFactory.decodeResource(res, R.drawable.mb3);
        shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot1);
        shoot2 = BitmapFactory.decodeResource(res, R.drawable.shoot2);
        shoot3 = BitmapFactory.decodeResource(res, R.drawable.shoot3);
        bang1 = BitmapFactory.decodeResource(res, R.drawable.bang1);
        bang2 = BitmapFactory.decodeResource(res, R.drawable.bang2);
        bang3 = BitmapFactory.decodeResource(res, R.drawable.bang3);
        bang4 = BitmapFactory.decodeResource(res, R.drawable.bang4);


        width = Mb1.getWidth();
        height = Mb1.getHeight();

        width /= 4;
        height /= 4;

        width = (int)(width*screenRatioX);
        height = (int)(height*screenRatioY) ;

        Mb1 = Bitmap.createScaledBitmap(Mb1, width, height, false);
        Mb2 = Bitmap.createScaledBitmap(Mb2, width, height, false);
        Mb3 = Bitmap.createScaledBitmap(Mb3, width, height, false);
        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, false);
        shoot2 = Bitmap.createScaledBitmap(shoot2, width, height, false);
        shoot3 = Bitmap.createScaledBitmap(shoot3, width, height, false);
        bang1 = Bitmap.createScaledBitmap(bang1, width, height, false);
        bang2 = Bitmap.createScaledBitmap(bang2, width, height, false);
        bang3 = Bitmap.createScaledBitmap(bang3, width, height, false);
        bang4 = Bitmap.createScaledBitmap(bang4, width, height, false);

        y = screenY/2;
        x = (int) (64*screenRatioX);
    }

    Bitmap getMayBay () {

        if (toShoot != 0){
            if (shootCounter == 1){
                shootCounter++;
                return shoot1;
            } else if (shootCounter == 2) {
                shootCounter++;
                return shoot2;
            }

            shootCounter = 1;
            toShoot--;
            gameView.newBullet();
            return shoot3;
        }

        if (wingCounter == 0){
            wingCounter++;
            return Mb1;
        } else if (wingCounter == 1) {
            wingCounter++;
            return Mb2;
        }
        wingCounter-=2;
        return Mb3;
    }
    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }

    Bitmap getBang () {
        if(bangCounter == 1){
            bangCounter++;
            return bang1;
        } else if(bangCounter == 2){
            bangCounter++;
            return bang2;
        } else if(bangCounter == 3){
            bangCounter++;
            return bang3;
        } else
        bangCounter = 1;
        return bang4;
    }
}
