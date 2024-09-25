package com.ahmedsaidgulsen.risaletabu;

import static com.ahmedsaidgulsen.risaletabu.GameActivity.pas_count1;
import static com.ahmedsaidgulsen.risaletabu.GameActivity.pas_count2;
import static com.ahmedsaidgulsen.risaletabu.GameActivity.score1;
import static com.ahmedsaidgulsen.risaletabu.GameActivity.score2;
import static com.ahmedsaidgulsen.risaletabu.GameActivity.taboo_count1;
import static com.ahmedsaidgulsen.risaletabu.GameActivity.taboo_count2;
import static com.ahmedsaidgulsen.risaletabu.GameActivity.true_count1;
import static com.ahmedsaidgulsen.risaletabu.GameActivity.true_count2;
import static com.ahmedsaidgulsen.risaletabu.GameActivity.tourCount;
import static com.ahmedsaidgulsen.risaletabu.MainActivity.gameTourCount;
import static com.ahmedsaidgulsen.risaletabu.MainActivity.rightOfPas;
import static com.ahmedsaidgulsen.risaletabu.MainActivity.gameTime;
import static com.ahmedsaidgulsen.risaletabu.GameActivity.counter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.ahmedsaidgulsen.risaletabu.databinding.ActivityScoreBinding;

public class ScoreActivity extends AppCompatActivity {

    private ActivityScoreBinding binding;
    String firstTeamName;
    String secondTeamName;
    private static float tour_counter = 0;
    AlertDialog menuDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityScoreBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting team names from the game activity
        Intent intent = getIntent();
        firstTeamName = intent.getStringExtra("firstTeamName");
        secondTeamName = intent.getStringExtra("secondTeamName");

        // setting the team names
        binding.team1NameView.setText(firstTeamName);
        binding.team2NameView.setText(secondTeamName);

        // setting the scores
        binding.dogru1ScoreView.setText("Doğru: " + true_count1);
        binding.dogru2ScoreView.setText("Doğru: " + true_count2);
        binding.pas1ScoreView.setText("Pas: " + pas_count1);
        binding.pas2ScoreView.setText("Pas: " + pas_count2);
        binding.tabu1ScoreView.setText("Tabu: " + taboo_count1);
        binding.tabu2ScoreView.setText("Tabu: " + taboo_count2);
        binding.sonuc1ScoreView.setText("Skor: " + score1);
        binding.sonuc2ScoreView.setText("Skor: " + score2);


        // make invisible the game result until the game is finished
        binding.gameResultView.setVisibility(View.INVISIBLE);


        // setting the button text and showing the game result when the game finished
        tour_counter += 0.5;
        if (tour_counter == gameTourCount) {
            binding.button.setText("Tekrar Oyna");
            binding.homeButton.setVisibility(View.INVISIBLE); // oyun bitince zaten tekrar oyna ile ana ekrana donuyor, menu butonunun da olmasi anlamsiz

            if (score1 > score2) {
                binding.gameResultView.setText(firstTeamName + " kazandı, tebrikler!");
                binding.gameResultView.setVisibility(View.VISIBLE);
            } else if (score2 > score1) {
                binding.gameResultView.setText(secondTeamName + " kazandı, tebrikler!");
                binding.gameResultView.setVisibility(View.VISIBLE);
            } else {
                binding.gameResultView.setText("Oyun bitti, dostluk kazandı!");
                binding.gameResultView.setVisibility(View.VISIBLE);
            }
        }


    }


    public static void refresh() { // kullanici tekrar oynaya bastiginda veya oyun aninda menuye dondugunde bu statik degiskenlerin refresh edilmesi lazim
        tour_counter = 0;
        true_count1 = 0;
        true_count2 = 0;
        pas_count1 = 0;
        pas_count2 = 0;
        taboo_count1 = 0;
        taboo_count2 = 0;
        score1 = 0;
        score2 = 0;
        tourCount = 1;
        gameTourCount = 3; // main activity'de default olarak 3 basliyor, kullanici kendisi ayarlarsa ona gore degisiyor
        rightOfPas = 3;
        gameTime = 60;
    }

    public void home(View view) {
        if (menuDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog); // 2.parametre cihaz ile uyumlu tema uretiyor
            builder.setTitle("Menüye Dön");
            builder.setMessage("Menüye dönerseniz oyun sıfırlanacak, emin misiniz?");

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    counter = 0; // bu game activity'de olcut olarak kullandigimiz counter, takim ismi ve skor belirleme icin
                    refresh();
                    Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // herhangi bir sey olmayacak Hayir'a basarsa
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

    public void continueGame(View view) {
        if (tour_counter != gameTourCount) {
            Intent intent = new Intent(ScoreActivity.this, GameActivity.class);
            intent.putExtra("firstTeamName", firstTeamName);
            intent.putExtra("secondTeamName", secondTeamName);
            startActivity(intent);
            finish();
        } else { // the game is finished, restart the game and go to main activity
            refresh();

            Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


}