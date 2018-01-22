package com.example.crowdfireassign.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.crowdfireassign.R;
import com.example.crowdfireassign.data.Contract;
import com.example.crowdfireassign.data.ImageHelper;
import com.example.crowdfireassign.receivers.AlarmReceiver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;



public class MainActivity extends BaseActivity implements View.OnClickListener{

    private ViewPager mShirtViewPager, mPantViewPager;
    private ImageView mAddShirtImageView, mAddPantImageView;
    private ImageView mFavImageView, mShuffleImageView;
    private Dialog dialog;

    public static final int MEDIA_TYPE_IMAGE = 1;

    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 101;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private Uri fileUri; // file url to store image/video

    private ArrayList<Bitmap> mShirtBitmapList;
    private ArrayList<Bitmap> mPantBitmapList;
    private int selection = -1, favShirt = -1, favPant = -1, backPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        boolean isExtra = false;
        if(getIntent() != null) {
            isExtra = getIntent().getBooleanExtra("Extras", false);

        }
        checkPermission(mActivity);
        setAlarmForNotification();
        mShirtViewPager = findViewById(R.id.shirtViewPager);
        mPantViewPager = findViewById(R.id.pantViewPager);
        mAddPantImageView = findViewById(R.id.addPants);
        mAddShirtImageView = findViewById(R.id.addShirts);
        mFavImageView = findViewById(R.id.fav);
        mShuffleImageView = findViewById(R.id.shuffle);

        mAddShirtImageView.setOnClickListener(this);
        mAddPantImageView.setOnClickListener(this);
        mFavImageView.setOnClickListener(this);
        mShuffleImageView.setOnClickListener(this);
        if(isExtra){
            shuffle();
        } else {
            checkDataInDb();
        }
    }

    public void checkDataInDb(){
        mShirtBitmapList = helper.getBitmaps(mActivity, Contract.Tables.SHIRT_TABLE_NAME);
        mPantBitmapList = helper.getBitmaps(mActivity, Contract.Tables.PANT_TABLE_NAME);

        if(mShirtBitmapList == null || mShirtBitmapList.size() == 0) {
            mShirtBitmapList = new ArrayList<>();
        } else {
            setShirtAdapter(mShirtBitmapList);
        }

        if(mPantBitmapList == null || mPantBitmapList.size() == 0) {
            mPantBitmapList = new ArrayList<>();
        } else {
            setPantsAdapter(mPantBitmapList);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission(Activity context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                return true;
            } else {
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_fav){
            backPressed = 1;
            mPantBitmapList = helper.getFavPantsBitmap(mActivity);
            mShirtBitmapList = helper.getFavShirtBitmaps(mActivity);
            setShirtAdapter(mShirtBitmapList);
            setPantsAdapter(mPantBitmapList);
        } else if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(backPressed == 1){
            checkDataInDb();
            backPressed = 0;
            Toast.makeText(mActivity, "All Pants and Shirts will appear, press back again to exit", Toast.LENGTH_SHORT).show();
            return;
        } else {
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addPants :
                selection = 1;
                showPopUp();
                break;
            case R.id.addShirts :
                selection = 0;
                showPopUp();
                break;
            case R.id.fav :
                if(favPant > -1 && favShirt > -1){
                    helper.insertFav(favPant, favShirt);
                } else {
                    Toast.makeText(mActivity, "Pant or Shirt missing", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.shuffle :
                shuffle();
                break;
            case R.id.cameraBtn :
                dialog.dismiss();
                cameraIntent();
                break;
            case R.id.galleryBtn :
                dialog.dismiss();
                galleryIntent();
                break;
        }
    }

    private void shuffle(){
        backPressed = 1;
        int shirtPosition = mUtils.randInt(mShirtBitmapList.size());
        int pantPosition = mUtils.randInt(mPantBitmapList.size());
        if(shirtPosition < mShirtBitmapList.size() &&
                pantPosition < mPantBitmapList.size()){
            ArrayList<Bitmap> shirtTempList = new ArrayList<>();
            ArrayList<Bitmap> pantTempList = new ArrayList<>();
            shirtTempList.add(mShirtBitmapList.get(shirtPosition));
            pantTempList.add(mPantBitmapList.get(pantPosition));
            setPantsAdapter(pantTempList);
            setShirtAdapter(shirtTempList);
        }
    }

    //dialog to select image from camera or gallery
    public void showPopUp(){
        dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_select_image);
        dialog.show();

        Button cameraBtn = dialog.findViewById(R.id.cameraBtn);
        Button galleryBtn = dialog.findViewById(R.id.galleryBtn);

        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
    }

    public void cameraIntent(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void galleryIntent(){
        /*Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(i, GALLERY_IMAGE_REQUEST_CODE);*/
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_IMAGE_REQUEST_CODE);;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    /**
     * ------------ Helper Methods ----------------------
     * */

	/*
	 * Creating file uri to store image
	 */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if(requestCode == GALLERY_IMAGE_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewGalleryImage(data);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled gallery", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void previewCapturedImage() {
        try {

            final Bitmap bitmap = mUtils.getBitmap(mActivity, fileUri);
            if(selection == 0) {
                mShirtBitmapList.add(bitmap);
                helper.insertUri(fileUri, Contract.Tables.SHIRT_TABLE_NAME);
                setShirtAdapter(mShirtBitmapList);
            } else {
                mPantBitmapList.add(bitmap);
                helper.insertUri(fileUri, Contract.Tables.PANT_TABLE_NAME);
                setPantsAdapter(mPantBitmapList);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void previewGalleryImage(Intent data) {
        //String fileName;
        Bitmap bitmap = null;
        if (data != null) {

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                if(selection == 0) {
                    mShirtBitmapList.add(bitmap);
                    helper.insertUri(data.getData(), Contract.Tables.SHIRT_TABLE_NAME);
                    setShirtAdapter(mShirtBitmapList);
                } else {
                    mPantBitmapList.add(bitmap);
                    helper.insertUri(data.getData(), Contract.Tables.PANT_TABLE_NAME);
                    setPantsAdapter(mPantBitmapList);
                }

            } catch (Exception ae) {
                ae.printStackTrace();
            }
        }

    }

    public void setShirtAdapter(final ArrayList<Bitmap> mShirtBitmapList){
        Collections.reverse(mShirtBitmapList);
        ViewPageAdapter adapter1 = new ViewPageAdapter(mShirtBitmapList, mActivity);
        mShirtViewPager.setAdapter(adapter1);

        //_ID is primary key starts from 1, position starts from 0. to match initializing by 1 instead of 0 and
        // adding 1 in position
        favShirt = 1;
        mShirtViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position < mShirtBitmapList.size()){
                    favShirt = position + 1;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setPantsAdapter(final ArrayList<Bitmap> mPantBitmapList){
        Collections.reverse(mPantBitmapList);
        ViewPageAdapter adapter2 = new ViewPageAdapter(mPantBitmapList, mActivity);
        mPantViewPager.setAdapter(adapter2);
        favPant = 1;
        mPantViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position < mPantBitmapList.size()){
                    favPant = position + 1;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setAlarmForNotification(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        //set time 6 am
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 0);

        //setting intent to class where alarm broadcast message will be handled
        Intent intent = new Intent(mActivity, AlarmReceiver.class);
        //start alarm pending intent
        PendingIntent alarmIntent = PendingIntent.getBroadcast(mActivity, AlarmManager.ELAPSED_REALTIME_WAKEUP, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //get instance of alarm manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //Inexact alarm everyday since device is booted up. This is a better choice and
        //scales well when device time settings/locale is changed
        //We're setting alarm to fire notification
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                , SystemClock.elapsedRealtime() + (1000 * 60 * 2)
                ,AlarmManager.INTERVAL_DAY
                ,  alarmIntent);

    }
}
