package com.example.zenegep;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME="Zenegep.db";
    public static final String TABLE_SERVER="Szerver";
    public static final String TABLE_CLIENT="Kliens";

    @Override
    public void onCreate(SQLiteDatabase db){}

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SERVER);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CLIENT);
        onCreate(db);
    }

    public DatabaseHelper(Context context ){
        super(context,DATABASE_NAME, null,1);
        SQLiteDatabase db=this.getWritableDatabase();
    }

    private boolean checkIfTableExists(SQLiteDatabase db, String table){
        String check = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='"+table+"'";
        Cursor c = db.rawQuery(check, null);

        if (!c.moveToFirst()) {
            c.close();
            return false;
        }

        int count = c.getInt(0);
        c.close();
        return count>0;
    }

    public void initDatabase(String table){
        SQLiteDatabase db = this.getWritableDatabase();

        //TODO: ezt a két sort ne felejtsük el majd kikommentelni, mert igy mindig dropolja a tablet
        String dropdatabase="DROP TABLE IF EXISTS "+table;
        db.execSQL(dropdatabase);

        String databaseCreate;
        String dataToInsert;
        if(!checkIfTableExists(db, table)) {
            databaseCreate = "CREATE TABLE IF NOT EXISTS "+table +
                    "(Video_ID TEXT PRIMARY KEY, " +
                    "Video_NAME TEXT, " +
                    "SentCount INTEGER DEFAULT 0, " +
                    "Timestamp TEXT DEFAULT '2020-04-29 00:00:00')";

            db.execSQL(databaseCreate);
            //TODO: feltölteni a zenékkel, amit akarunk, hogy benne legyen
            dataToInsert = "INSERT INTO "+table+" (Video_ID, Video_NAME) VALUES" +
                    "('fJ9rUzIMcZQ', 'Queen - Bohemian Rhapsody')," +
                    "('NyOGIsds2C4','Yung Gravy, bbno$ - Whip A Tesla')," +
                    "('zSISvlwYweI','BSW - $oha nem elég')";

            db.execSQL(dataToInsert);
            db.close();

            getLastDayPopularList(table);
        }
    }

    private Cursor getData(String sql){
        SQLiteDatabase dbR=this.getReadableDatabase();
        return dbR.rawQuery(sql,null);
    }

    private void Query(String sql){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public ArrayList getMusicList(String table){
        ArrayList<String> musicList = new ArrayList<String>();
        String sql = "SELECT Video_NAME FROM "+table;
        Cursor c = getData(sql);
        c.moveToFirst();

        while(!c.isAfterLast()) {
            musicList.add(c.getString(c.getColumnIndex("Video_NAME")));
            c.moveToNext();
        }
        c.close();
        return musicList;
    }

    public ArrayList getVideoIDList(String table)
    {
        ArrayList<String> musicIDs = new ArrayList<String>();
        String sql = "SELECT Video_ID FROM "+table;
        Cursor c = getData(sql);
        c.moveToFirst();

        while(!c.isAfterLast()) {
            musicIDs.add(c.getString(c.getColumnIndex("Video_ID")));
            c.moveToNext();
        }

        c.close();
        return musicIDs;
    }

    public ArrayList getAllTimePopularList(String table){
        ArrayList<String> list = new ArrayList<String>();
        String sql = "SELECT Video_NAME, SentCount FROM "+table+" ORDER BY SentCount DESC";
        Cursor c = getData(sql);
        c.moveToFirst();

        while(!c.isAfterLast()){
            String videoname = c.getString(c.getColumnIndex("Video_NAME"));
            int count = c.getInt(c.getColumnIndex("SentCount"));
            String stringToAdd = videoname + " - "+count;
            list.add(stringToAdd);
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public ArrayList getLastDayPopularList(String table){
        ArrayList<String> list = new ArrayList<String>();
        String sql = "SELECT Video_NAME, SentCount FROM "+table+" WHERE Timestamp >= datetime('now', '-1 days') AND Timestamp < datetime('now')" +
                " ORDER BY SentCount DESC";
        Cursor c = getData(sql);
        c.moveToFirst();

        while(!c.isAfterLast()){
            String videoname = c.getString(c.getColumnIndex("Video_NAME"));
            int count = c.getInt(c.getColumnIndex("SentCount"));
            String stringToAdd = videoname + " - "+count;
            list.add(stringToAdd);
            c.moveToNext();
        }
        c.close();
        return list;
    }

    public void updateSql(String table, String videoid){
        String updateSql = "UPDATE "+table+" SET SentCount = SentCount + 1," +
                "Timestamp = datetime('now')" +
                "WHERE Video_ID = '"+videoid+"'";

        Query(updateSql);
    }

    public String suggestMusic(){
        Random rnd = new Random();
        String musicIdToSuggest;
        String[] musicList = new String[10];
        int i = 0;
        String sql = "SELECT Video_NAME FROM "+TABLE_CLIENT+" ORDER BY SentCount DESC LIMIT 10";
        Cursor c = getData(sql);
        c.moveToFirst();
        while(!c.isAfterLast()) {
            musicList[i] = c.getString(c.getColumnIndex("Video_NAME"));
            c.moveToNext();
            i++;
        }
        c.close();
        musicIdToSuggest=musicList[rnd.nextInt(10)];

        //TODO: rendes visszatérési értéket adni, mert most így hirtelen nem tudom mit kellene
        return musicIdToSuggest;
    }

    public boolean isInDatabase(String musicId, String table){
        ArrayList<String> idList = getVideoIDList(table);
        return idList.contains(musicId);
    }
}
