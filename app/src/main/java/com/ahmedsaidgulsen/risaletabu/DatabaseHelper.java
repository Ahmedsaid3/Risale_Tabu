package com.ahmedsaidgulsen.risaletabu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "risaleTabuWords.db";
    public static final int databaseVersion = 1;

    private SQLiteDatabase database;
    private final Context context; // database path'e erismek icin kullandik

    // constructor
    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, databaseVersion);
        this.context = context;
    }

    // biz database'imizi disaridan(db browser for sqlite) ile aldigimiz icin bu iki metodu duzenlememize gerek yok
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    // bu metod version degisikligi yaptigimizda calisiyor (ama uygulamadan app'i silip tekrar yuklemek daha kolay bi cozum, bu kismi yine kullanmayabiliriz yani)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // When upgrading, delete the old database and copy the new one from assets.
//        context.deleteDatabase(databaseName); // internal storage'dan siliyor anladigim kadariyla
//        try {
//            copyDatabase(); // bosalan internal storage'a direk kopyalama islemi yapiyoruz
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    // bu 3 metodu alttaki 3 metottan sonra kullaniyoruz aslinda, database'in internal storage'da olmasi lazim bunlarin calisabilmesi icin ->
    // onun icin de oncelikle database copy logic'inin calismasi lazim, yani alttaki 3 metod (benim anladigim mantik bu sekilde)
    public void openDatabase() {
        String databasePath = context.getDatabasePath(databaseName).getPath(); // "data/data/packageName/datasets" gibi bir path aslinda, bu internal storoge oluyor, packageName: com.ahmedsaidgulsen.risaletabu
        if (database != null && database.isOpen()) { // eger database onceden acilmissa tekrar acma islemi yapilmasin (bu metottan direk ciksin)
            return;
        }
        database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public Cursor getAllWords() {
        String query = "SELECT * FROM easyWords";
        return database.rawQuery(query, null);
    }

    public void closeDatabase() {
        if (database != null) {
            database.close();
        }
    }

    // database copy logic: assets folder'inda olan database'imizi internal storage'a aktarmamiz lazim, bu 3 metod bu ise yariyor
    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();
        if (!dbExist) {
            this.getReadableDatabase();  // This creates an empty database in the default location(internal storage dedigimiz kisim)
            copyDatabase();
        }
    }

    private boolean checkDatabase() { // internal storage'da yoksa create database'in if kismina gir diyoruz aslinda
        String dbPath = context.getDatabasePath(databaseName).getPath();
        File dbFile = new File(dbPath);
        return dbFile.exists();
    }

    private void copyDatabase() throws IOException {
        InputStream input = context.getAssets().open(databaseName);
        String outFileName = context.getDatabasePath(databaseName).getPath();
        OutputStream output = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024]; // 1024 byte ala ala ilerliyor, memory efficiency sagliyor, tum dosyayi birden alsa sorun olur mesela
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush(); // buffer'da kalmis data varsa onu da output stream'ine aktarmamizi sagliyor, veri kaybini onluyor yani
        output.close();
        input.close();
    }



}
