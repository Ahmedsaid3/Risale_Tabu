package com.ahmedsaidgulsen.risaletabu;

import static com.ahmedsaidgulsen.risaletabu.MainActivity.gameTime;
import static com.ahmedsaidgulsen.risaletabu.MainActivity.rightOfPas;
import static com.ahmedsaidgulsen.risaletabu.ScoreActivity.refresh;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ahmedsaidgulsen.risaletabu.databinding.ActivityGameBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    String firstTeamName;
    String secondTeamName;
    public static int counter = 0; // to set the teams
    public static float tourCount = 1;
    public static int pasCounter = 1;

    public static int taboo_count1 = 0, taboo_count2 = 0;
    public static int pas_count1 = 0, pas_count2 = 0;
    public static int true_count1 = 0, true_count2 = 0;
    public static int score1 = 0, score2 = 0;

    AlertDialog menuDialog;

    // time
    CountDownTimer countDownTimer;
    long timeLeft;
    AlertDialog pauseDialog;

    // database
    DatabaseHelper databaseHelper;
    Cursor cursor;
    static ArrayList<WordGroup> arrayList;
    static int wordGroupIx = 0;

    // Note: yukarida kullandigim degiskenlerden bazilarini static yapma sebebim, bu aktivite her acildiginda tekrar olusturulmasinlar(sifirlanmasinlar) -> yani kaldiklari yerden devam etsinler


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // getting the team names from the main activity(at the beginning) and score activity
        Intent intent = getIntent();
        firstTeamName = intent.getStringExtra("firstTeamName");
        secondTeamName = intent.getStringExtra("secondTeamName");


        // setting the tour count
        binding.tourView.setText((int)tourCount + ".Tur");
        tourCount += 0.5;


        // setting the team name
        if (counter % 2 == 0) {
            binding.teamNameView.setText(firstTeamName);
        } else {
            binding.teamNameView.setText(secondTeamName);
        }
        counter++;


        // setting the remainPasView
        binding.remainPasView.setText("0 / " + rightOfPas);

        // count down timer
        startTimer(gameTime);


        // database
        if (arrayList == null) { // 1 defa yapsin bu islemleri
            databaseHelper = new DatabaseHelper(this);
            arrayList = new ArrayList<>();

            transmitDatafromDatabasetoArrayList(databaseHelper); // bunu yaptiktan sonra artik isim arrayList ile
            shuffleArrayList(arrayList);
        }

        // setting the word views
        showWordViews(arrayList);

    }



    // database'i guncelleyip asset folderina tekrar yuklemis olsam da, emulatorde app'i silmeden yeni verilere erisemem, cunku ->
    // app silinmediginde internal storage'inda hala eski database dosyasinin kopyasi duruyor, dolayisiyla createDatabase(); metodu calismiyor ->
    // direk openDatabase(); metodu ile eski dosyayi aciyor ve ArrayList'e ekliyor. (bahsedilen metodlar DatabaseHelper class'inda, gerektiginde orayi incele)
    public void transmitDatafromDatabasetoArrayList(DatabaseHelper databaseHelper) {
        try {
            databaseHelper.createDatabase(); // assets folder'indaki database'imizi internal storage'a copy ediyoruz aslinda
        } catch (IOException e) {
            e.printStackTrace();
        }

        databaseHelper.openDatabase(); // internal storage'a gelmis olan database'imizi aciyoruz

        cursor = databaseHelper.getAllWords(); // bu metod database'imize erisen cursor donuyordu, biz de onu cursor'e esitledik

        if (cursor.moveToFirst()) { // cursor'un ilk satira gitmesini sagliyor, eger satir bos degilse de if'in icine giriyor(kontrol amacli)
            do {
                WordGroup wordGroup = new WordGroup(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                arrayList.add(wordGroup);
            } while (cursor.moveToNext());
        }

        cursor.close();

        databaseHelper.closeDatabase();
    }


    public void shuffleArrayList(ArrayList<WordGroup> arrayList) {
        Collections.shuffle(arrayList);
    }

    public void showWordViews(ArrayList<WordGroup> arrayList) {
        if (wordGroupIx < arrayList.size()) {
            binding.mainWordView.setText(arrayList.get(wordGroupIx).mainWord);
            binding.tabuWord1View.setText(arrayList.get(wordGroupIx).tabuWord1);
            binding.tabuWord2View.setText(arrayList.get(wordGroupIx).tabuWord2);
            binding.tabuWord3View.setText(arrayList.get(wordGroupIx).tabuWord3);
        } else { // yani aslinda bu else kismi database'de az kelime olma senaryosu icin yazildi, kelimeler bittiginde uygulama cokmesin diye, ayni kelimeleri farkli sirayla tekrar gosteriyoruz
            // ix sayisi listedeki eleman sayisini gecince(kelimeler bitince) reshuffle yapip wordGroupIx counter'ini 0'lamamiz lazim
            shuffleArrayList(arrayList);
            wordGroupIx = 0;
            binding.mainWordView.setText(arrayList.get(wordGroupIx).mainWord);
            binding.tabuWord1View.setText(arrayList.get(wordGroupIx).tabuWord1);
            binding.tabuWord2View.setText(arrayList.get(wordGroupIx).tabuWord2);
            binding.tabuWord3View.setText(arrayList.get(wordGroupIx).tabuWord3);
        }

        // debug islemi icin kullandim, kelimeler dogru bi sekilde shuffle ediliyor mu baktim:
        // Log.d("Kelimeler",  wordGroupIx + ": " + arrayList.get(wordGroupIx).mainWord);

        wordGroupIx++;
    }


    private void startTimer(long time) {
        countDownTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.timeView.setText("Süre: " + String.valueOf(millisUntilFinished / 1000));
                timeLeft = millisUntilFinished / 1000; // Update the remaining time
            }

            @Override
            public void onFinish() {
                pasCounter = 1;

                Intent intent = new Intent(GameActivity.this, ScoreActivity.class);
                intent.putExtra("firstTeamName", firstTeamName);
                intent.putExtra("secondTeamName", secondTeamName);
                startActivity(intent);
                finish();
            }
        }.start();
    }


    public void pause(View view) {
        countDownTimer.cancel();

        if (pauseDialog == null) { // yani eger dialog yoksa olustur, create edilmisse tekrarlamasina gerek yok bu islemi
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog); // 2.parametre cihaz ile uyumlu tema uretiyor
            builder.setTitle("Oyun Durdu");
            builder.setMessage("Devam etmek için Evet'e basın");

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // resuming the timer
                    startTimer(timeLeft);
                }
            });

            builder.setCancelable(false); // kullanicinin yanlislikla ekrana dokunmasu veya geri tusuna basmasini onlemek icin eklendi

            pauseDialog = builder.create();
        }

        pauseDialog.show();

        // emulatorde alert dialog ortada olmasina ragmen telefonlarda ortada degil asagida cikabiliyor, onu duzeltmek icin ekledim
        Window window = pauseDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;  // Center the dialog
            window.setAttributes(layoutParams);
        }

    }

    public void home(View view) {

        countDownTimer.cancel();

        if (menuDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog); // 2.parametre cihaz ile uyumlu tema uretiyor
            builder.setTitle("Menüye Dön");
            builder.setMessage("Menüye dönerseniz oyun sıfırlanacak, emin misiniz?");

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    counter = 0; // private statik bir degisken, takim isimlerini gostermek ve skorlari belirlemek icin kullandigim bi olcut(counter tek mi cift mi mantigi)
                    refresh();
                    Intent intent = new Intent(GameActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startTimer(timeLeft);
                }
            });

            builder.setCancelable(false);

            menuDialog = builder.create();
        }

        menuDialog.show();

        // emulatorde alert dialog ortada olmasina ragmen telefonlarda ortada degil asagida cikabiliyor, onu duzeltmek icin ekledim
        Window window = menuDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;  // Center the dialog
            window.setAttributes(layoutParams);
        }

    }

    public void clickTrue(View view) {
        showWordViews(arrayList);

        if (counter % 2 != 0) {
            // first team
            true_count1++;
            score1++;
        } else {
            // second team
            true_count2++;
            score2++;
        }
    }


    public void clickPas(View view) {
        showWordViews(arrayList);

        // setting the remainPasView
        binding.remainPasView.setText(pasCounter + " / " + rightOfPas);
        if (pasCounter == rightOfPas) {
            binding.pasButton.setEnabled(false);
            binding.pasButton.setAlpha(0.5f); // to make it look blurred (0.0 to 1.0)
        } else {
            pasCounter++;
        }

        // setting the pas count
        if (counter % 2 != 0) {
            // first team
            pas_count1++;
        } else {
            // second team
            pas_count2++;
        }
    }

    public void clickTaboo(View view) {
        showWordViews(arrayList);

        if (counter % 2 != 0) {
            // first team
            taboo_count1++;
            score1--;
        } else {
            // second team
            taboo_count2++;
            score2--;
        }
    }


}

