package com.example.android.pets.data;


import android.provider.BaseColumns;

public final class PetsContract {

    private PetsContract(){

    }

    private final class pets implements BaseColumns {
        /**
         * Defining Constants for the pets database
         */
        public static final String TABLE_NAME = "pets";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BREED = "breed";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_WEIGHT = "weight";

        /**
         * Defining constant values for genders
         */
        public static final int GENDER_MALE = 0;
        public static final int GENDER_FEMALE = 1;
        public static final int GENDER_UNKNOWN = 2;



    }
}
