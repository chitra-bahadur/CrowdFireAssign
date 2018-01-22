package com.example.crowdfireassign.data;

import android.provider.BaseColumns;

/**
 * Created by chitra on 16/1/18.
 */

public class Contract {

    public interface Columns extends BaseColumns {
        String IMAGE_COLUMN_NAME = "image_uri";
        String SHIRT_ID_COULUMN_NAME = "shirt_id";
        String PANT_ID_COLUMN_NAME = "pant_id";
    }

    public interface Tables {
        String SHIRT_TABLE_NAME = "shirts";
        String PANT_TABLE_NAME = "pants";
        String FAV_TABLE_NAME = "favourites";
    }
}
