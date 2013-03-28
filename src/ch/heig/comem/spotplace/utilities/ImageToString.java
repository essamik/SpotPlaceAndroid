package ch.heig.comem.spotplace.utilities;

import java.io.ByteArrayOutputStream;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.FROYO)
public class ImageToString extends AsyncTask<String, Void, String> {
	ImageView image;
	private final int size = 480;
     
    @Override
	protected  String doInBackground(String... path) {
		// TODO Auto-generated method stub
    	//String outPut = null;
        
         for (String sdPath : path) {
          
             //Bitmap bitmapOrg = BitmapFactory.decodeFile(sdPath);
         Bitmap bitmapOrg = BitmapFactory.decodeFile(sdPath);
             ByteArrayOutputStream bao = new ByteArrayOutputStream();
              
             //Resize the image
             double width = bitmapOrg.getWidth();
             double height = bitmapOrg.getHeight();
             double ratio = size/width;
             int newheight = (int)(ratio*height);
              
//             System.out.println("———-width" + width);
//             System.out.println("———-height" + height);
              
             bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, size, newheight, true);
              
             //Here you can define .PNG as well
             bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 50, bao);
             byte[] ba = bao.toByteArray();
             
  
             //String ba1 = Base64.encodeToString(ba, 0);
             String ba1 = Base64.encodeToString(ba, 1);
              
//             System.out.println("uploading image now ——–" + ba1);
             
             
              
             return ba1;
             
             ///A PARTIR D'ICI IL FAUT FAIRE LE POST
             
         }
         return null;
	}
       
    

}

	
