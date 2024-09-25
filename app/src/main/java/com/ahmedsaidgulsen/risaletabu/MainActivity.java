package com.ahmedsaidgulsen.risaletabu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ahmedsaidgulsen.risaletabu.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static int gameTourCount = 3;
    public static int rightOfPas = 3;
    public static int gameTime = 60;
    private ActivityMainBinding binding;
    int stepSize = 30;
    AlertDialog howToPlayDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // setting the tour seekbar
        binding.tourSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.tourCountText.setText("Tur sayısı: " + (progress + 1));
                gameTourCount = progress + 1;
            }

            // bu iki metod kullanici ilk bastiginda ve biraktiginda ne olmasi ile ilgili, gerek olmadigi icin kullanmadim
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // setting the pas seekbar
        binding.pasSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.pasCountText.setText("Pas hakkı: " + (progress + 1));
                rightOfPas = progress + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // setting the time seekbar
        binding.timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.timeCountText.setText("Süre: " + stepSize * (progress + 1)); // 30'ar 30'ar artacak
                gameTime = stepSize * (progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }


    public void howToPlay(View view) {

        if (howToPlayDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
            builder.setTitle("Nasıl Oynanır?");

            // mesajima style eklemek icin text view kullaniyorum alert dialog icinde (setMessage yerine setView kullaniyorum yani)
            TextView messageView = new TextView(this);
            messageView.setText(Html.fromHtml(
                    "<br/>2 Takım kurulur.<br/><br/>" +
                            "Takım isimleri belirlenir.<br/><br/>" +
                            "Her takım anlatacak kişiyi seçer.<br/><br/>" +
                            "Oyun başlar.<br/><br/>" +
                            "Rakip takımdan bir oyuncu anlatan kişiyi kontrol eder.<br/><br/>" +
                            "Yasaklı kelimeler alt kısımda yer alır.<br/><br/>" +
                            "Bu kelimeler kullanılırsa tabu olur ve takım puan kaybeder.<br/><br/>" +
                            "Kelime tabu yapılmadan anlatılmaya çalışılır.<br/><br/>" +
                            "En yüksek puanı elde eden takım oyunu kazanır."
            ));

            messageView.setPadding(50, 20, 50, 20);
            messageView.setTextSize(16);

            builder.setView(messageView);

            builder.setPositiveButton("Anladım", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            howToPlayDialog = builder.create();
        }

        howToPlayDialog.show();

        // emulatorde alert dialog ortada olmasina ragmen telefonlarda ortada degil asagida cikabiliyor, onu duzeltmek icin ekledim
        Window window = howToPlayDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);
        }

    }


    public void play(View view) {
        // getting the team names from the user
        String firstTeamName = binding.editText1.getText().toString();
        String secondTeamName = binding.editText2.getText().toString();


        if (firstTeamName.matches("") || secondTeamName.matches("")) {
            Toast.makeText(this, "Takım isimleri boş bırakılamaz", Toast.LENGTH_LONG).show();
        } else {
            // going to second activity
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("firstTeamName", firstTeamName);
            intent.putExtra("secondTeamName", secondTeamName);
            startActivity(intent); // yani aslinda her start activity yaptigimizda gitmek istedigimiz aktivite yeniden olusuyor, eger finish'lemeden gidersek tamamen gereksiz yere arka planda yer kapliyor ->
            // sadece back tusu ile geri dondugumuzde bu aktiviteyi oldugu gibi gormek istiyorsak finish yapmadan gideriz, ama benim uygulamamda boyle bi ihtiyac yok
            finish(); // that activity will not hold on the back stack unnecessarily
        }
    }


}