package com.example.crowdfireassign.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.crowdfireassign.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chitra on 16/1/18.
 */

public class ImageHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wardrobe.db";
    private static final int DATABASE_VERSION = 1;

    public ImageHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Contract.Tables.SHIRT_TABLE_NAME
                + " (" + Contract.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.Columns.IMAGE_COLUMN_NAME +" TEXT)");

        db.execSQL("CREATE TABLE " + Contract.Tables.PANT_TABLE_NAME
                + " (" + Contract.Columns._ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.Columns.IMAGE_COLUMN_NAME + " TEXT)");

        db.execSQL("CREATE TABLE " + Contract.Tables.FAV_TABLE_NAME
                + "(" + Contract.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.Columns.SHIRT_ID_COULUMN_NAME + " INTEGER,"
                + Contract.Columns.PANT_ID_COLUMN_NAME + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + Contract.Tables.SHIRT_TABLE_NAME);
        db.execSQL("DROP TABLE " + Contract.Tables.PANT_TABLE_NAME);
        db.execSQL("DROP TABLE " + Contract.Tables.FAV_TABLE_NAME);
        onCreate(db);
    }

    public void insertUri(Uri uri, String tableName){
        ContentValues values = new ContentValues();
        values.put(Contract.Columns.IMAGE_COLUMN_NAME, String.valueOf(uri));
        getWritableDatabase().insert(tableName, null, values);
    }

    public ArrayList<Bitmap> getBitmaps(Context context, String tableName){
        Cursor cursor = getReadableDatabase().rawQuery("Select * from " + tableName, null);
        ArrayList<Bitmap> bitmapList = new ArrayList<>();

        if(cursor != null){
            while (cursor.moveToNext()){
                String uri = cursor.getString(cursor.getColumnIndex(Contract.Columns.IMAGE_COLUMN_NAME));
                //System.out.println(uri);
                Bitmap bitmap = new Utils().getBitmap(context, Uri.parse(uri));
                if(bitmap != null) {
                    bitmapList.add(bitmap);
                }
            }
        }
        return bitmapList;
    }

    public void insertFav(int shirtId, int pantId){
        Cursor cursor = getReadableDatabase().rawQuery("select * from " + Contract.Tables.FAV_TABLE_NAME + " where " +
                Contract.Columns.SHIRT_ID_COULUMN_NAME + "=" + (shirtId + 1) + " and "
                + Contract.Columns.PANT_ID_COLUMN_NAME + "=" + (pantId + 1), null);
        ContentValues values = new ContentValues();
        values.put(Contract.Columns.SHIRT_ID_COULUMN_NAME, shirtId);
        values.put(Contract.Columns.PANT_ID_COLUMN_NAME, pantId);
        if(cursor == null || cursor.getCount() == 0) {
           long i = getWritableDatabase().insert(Contract.Tables.FAV_TABLE_NAME, null, values);
        } else {
            getWritableDatabase().update(Contract.Tables.FAV_TABLE_NAME, values,
                    Contract.Columns.SHIRT_ID_COULUMN_NAME + "=? and "
                            + Contract.Columns.PANT_ID_COLUMN_NAME + "=?",
                    new String[]{String.valueOf(shirtId), String.valueOf(pantId)});
        }
    }

    public ArrayList<Bitmap> getFavShirtBitmaps(Context context){
        String query = "select * from " + Contract.Tables.FAV_TABLE_NAME +
                " as a, " + Contract.Tables.SHIRT_TABLE_NAME + " as b where b."
                + Contract.Columns._ID + "= a." + Contract.Columns.SHIRT_ID_COULUMN_NAME;
        //System.out.println(query);

        return getFavBitmaps(context, getWritableDatabase().rawQuery(query, null));

    }

    public ArrayList<Bitmap> getFavPantsBitmap(Context context){
        String query = "select * from " + Contract.Tables.FAV_TABLE_NAME +
                " as a, " + Contract.Tables.PANT_TABLE_NAME + " as b where b."
                + Contract.Columns._ID + "= a." + Contract.Columns.PANT_ID_COLUMN_NAME;
        //System.out.println(query);

        return getFavBitmaps(context, getWritableDatabase().rawQuery(query, null));

    }

    public ArrayList<Bitmap> getFavBitmaps(Context context, Cursor cursor){
        ArrayList<Bitmap> bitmapList = new ArrayList<>();
        if(cursor != null && cursor.getCount()> 0){
            while (cursor.moveToNext()){
                String uri = cursor.getString(cursor.getColumnIndex(Contract.Columns.IMAGE_COLUMN_NAME));
                //System.out.println(uri);
                Bitmap bitmap = new Utils().getBitmap(context, Uri.parse(uri));
                if(bitmap != null) {
                    bitmapList.add(bitmap);
                }
            }
        }
        return bitmapList;
    }
}
