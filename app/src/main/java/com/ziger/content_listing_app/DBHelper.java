package com.ziger.content_listing_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data.db";
    public static final String STONES_TABLE_NAME        = "stones";
        public static final String STONES_COLUMN_ID         = "id";
        public static final String STONES_COLUMN_NAME       = "name";
        public static final String STONES_COLUMN_COLOR      = "color";
        public static final String STONES_COLUMN_CATEGORY   = "category";
        public static final String STONES_COLUMN_URL        = "url";
        public static final String STONES_COLUMN_IMAGE      = "image";

    public static final String CATEGORIES_TABLE_NAME    = "categories";
        public static final String CATEGORIES_COLUMN_ID     = "id";
        public static final String CATEGORIES_COLUMN_NAME   = "name";

    public DBHelper(Context context){
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS "+ STONES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ CATEGORIES_TABLE_NAME);

        createDatabaseTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ STONES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ CATEGORIES_TABLE_NAME);
        onCreate(db);
    }

    public void clearDatabase() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+ STONES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ CATEGORIES_TABLE_NAME);

        createDatabaseTables(db);
        db.close();
    }

    public void createDatabaseTables(SQLiteDatabase db) {

        db.execSQL("create table " + CATEGORIES_TABLE_NAME
                + "("+  CATEGORIES_COLUMN_ID + " integer primary key, "
                +       CATEGORIES_COLUMN_NAME + " text)");

        db.execSQL("create table " + STONES_TABLE_NAME
                + "("+  STONES_COLUMN_ID + " integer primary key, "
                +       STONES_COLUMN_NAME + " text, "
                +       STONES_COLUMN_COLOR + " text, "
                +       STONES_COLUMN_CATEGORY + " integer, "
                +       STONES_COLUMN_URL + " text, "
                +       STONES_COLUMN_IMAGE + " blob, "
                +       "FOREIGN KEY("+ STONES_COLUMN_CATEGORY +") REFERENCES "+CATEGORIES_TABLE_NAME+"("+CATEGORIES_COLUMN_ID+"))");
    }

    public long insertStone(Stone stone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STONES_COLUMN_NAME, stone.getName());
        contentValues.put(STONES_COLUMN_COLOR, stone.getColor());
        contentValues.put(STONES_COLUMN_CATEGORY, stone.getCategory());
        contentValues.put(STONES_COLUMN_URL, stone.getUrl());
        contentValues.put(STONES_COLUMN_IMAGE, stone.getImage());
        long i = db.insert(STONES_TABLE_NAME, null, contentValues);
        db.close();
        return i;
    }

    public Stone getStone(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ STONES_TABLE_NAME +" where "
                + STONES_COLUMN_ID +"="+id+"", null );
        res.moveToFirst();

        Stone stone = new Stone(res.getInt(res.getColumnIndex(STONES_COLUMN_ID)),
                res.getString(res.getColumnIndex(STONES_COLUMN_NAME)),
                res.getString(res.getColumnIndex(STONES_COLUMN_COLOR)),
                res.getInt(res.getColumnIndex(STONES_COLUMN_CATEGORY)),
                res.getString(res.getColumnIndex(STONES_COLUMN_URL)),
                res.getBlob(res.getColumnIndex(STONES_COLUMN_IMAGE)));
        db.close();
        return stone;
    }

    public int numberOfRows(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, tableName);
        db.close();
        return numRows;
    }

    public void updateStone(Stone stone) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(  "update '"  + STONES_TABLE_NAME + "' set "
                                + STONES_COLUMN_NAME    + "='"+ stone.getName() +"',"
                                + STONES_COLUMN_COLOR   + "='"+ stone.getColor() +"',"
                                + STONES_COLUMN_CATEGORY   + "='"+ stone.getCategory() +"',"
                                + STONES_COLUMN_URL   + "='"+ stone.getUrl() +"',"
                                + STONES_COLUMN_IMAGE   + "='"+ stone.getImage() +"'"
                                + " where " + STONES_COLUMN_ID + "=" + stone.getId()+";");
    }

    public Integer deleteStone (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i =  db.delete(STONES_TABLE_NAME,
                STONES_COLUMN_ID +" = ?",
                new String[] { Integer.toString(id) });
        db.close();
        return i;
    }

    public ArrayList<Stone> getAllStones(){
        ArrayList<Stone> stones = new ArrayList<Stone>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+STONES_TABLE_NAME, null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            stones.add(new Stone(res.getInt(res.getColumnIndex(STONES_COLUMN_ID)),
                    res.getString(res.getColumnIndex(STONES_COLUMN_NAME)),
                    res.getString(res.getColumnIndex(STONES_COLUMN_COLOR)),
                    res.getInt(res.getColumnIndex(STONES_COLUMN_CATEGORY)),
                    res.getString(res.getColumnIndex(STONES_COLUMN_URL)),
                    res.getBlob(res.getColumnIndex(STONES_COLUMN_IMAGE))));
            res.moveToNext();
        }
        db.close();
        return stones;
    }

    public long insertCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORIES_COLUMN_NAME, category.getName());
        long i = db.insert(CATEGORIES_TABLE_NAME, null, contentValues);
        db.close();
        return i;
    }

    public Category getCategory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ CATEGORIES_TABLE_NAME +" where "
                + CATEGORIES_COLUMN_ID +"="+id+"", null );
        res.moveToFirst();
        Category category = new Category(res.getInt(res.getColumnIndex(CATEGORIES_COLUMN_ID)),
                res.getString(res.getColumnIndex(CATEGORIES_COLUMN_NAME)));
        db.close();
        return category;
    }

    public Category getCategoryFromName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ CATEGORIES_TABLE_NAME +" where "
                + CATEGORIES_COLUMN_NAME +"='"+name+"'", null );
        res.moveToFirst();
        Category category = new Category(res.getInt(res.getColumnIndex(CATEGORIES_COLUMN_ID)),
                res.getString(res.getColumnIndex(CATEGORIES_COLUMN_NAME)));
        db.close();
        return category;
    }

    public int updateCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORIES_COLUMN_NAME, category.getName());
        db.close();
        return db.update(CATEGORIES_TABLE_NAME, contentValues, CATEGORIES_COLUMN_ID +" = ?", new String[] {Integer.toString(category.getId())} );
    }

    public ArrayList<Category> getAllCategories(){
        ArrayList<Category> categories = new ArrayList<Category>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ CATEGORIES_TABLE_NAME, null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            categories.add(new Category(res.getInt(res.getColumnIndex(CATEGORIES_COLUMN_ID)),
                    res.getString(res.getColumnIndex(CATEGORIES_COLUMN_NAME))));
            res.moveToNext();
        }
        db.close();
        return categories;
    }
}