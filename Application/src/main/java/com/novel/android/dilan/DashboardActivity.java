package com.novel.android.dilan;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ano on 2/22/2018.
 */

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private NovelAdapter mAdapter;

    Typeface face;
    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    TextView quote;
    TextView notes;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initiate
        MobileAds.initialize(this, "ca-app-pub-6793022751700770~7532674547");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ImageButton shareBtn = (ImageButton) findViewById(R.id.shareButton);

        // Font
        face = Typeface.createFromAsset(getAssets(), "fonts/mileadilan.otf");
        quote = (TextView) findViewById(R.id.quote);
        notes = (TextView) findViewById(R.id.pesan_dilan);
        TextView collection = (TextView) findViewById(R.id.collectionText);

        quote.setTypeface(face);
        notes.setTypeface(face);
        collection.setTypeface(face);
        notes.setText(randomQuote());

        // Define
        mRecyclerView = (RecyclerView) findViewById(R.id.recycvleNovel);
        Drawable[] animals = {
                getDrawable(R.drawable.dilan_satu),
                getDrawable(R.drawable.dilan_dua),
                getDrawable(R.drawable.dilan_tiga)
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
       // mRecyclerView.addItemDecoration(new ItemDecorationAlbumColumns(5,1));
        mAdapter = new NovelAdapter(this,animals);
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    //mAdapter.getItemId(position);
                openNovel(position);
                Log.e("Posisi",String.valueOf(position));

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        doTimerTask();

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp(String.valueOf(notes.getText()));
            }
        });


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }



    private void shareApp(String note) {
        String shareBody = note+" - baca novelnya https://play.google.com/store/apps/details?id=" + getPackageName();

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.dilan_quote));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    public void doTimerTask(){

        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        notes.setText(randomQuote());
                    }
                });
            }};

        // public void schedule (TimerTask task, long delay, long period)
        t.schedule(mTimerTask, 30000, 30000);  //

    }

    public void stopTask(){

        if(mTimerTask!=null){
            mTimerTask.cancel();
        }

    }

    String randomQuote(){
        Random random = new Random();

        String[] quote = {"Jangan jauh' Dik, jangan pergi jauh-jauh kan ada darahku ditubuhmu",
                "Selamat ulang tahun, Milea. ini hadiah untukmu, cuma TTS, tapi sudah kuisi semua. aku sayang kamu, aku tidak mau kamu pusing, karena harus mengisinya .dilan!",
                "Meski yang dikatakannya bukan kata-kata cinta, tapi mampu menumbuhkan rasa cinta",
                "Nanti kalau kamu mau tidur, percayalah aku sedang mengucapkan selamat tidur dari jauh, kamu gak akan denger",
                "Cinta itu indah, jika bagimu tida, mungkin kamu salah milih pasangan",
                "Aku tidak ingin mengekangmu. Terserah! bebas kemana engkau pergi! asal aku ikut",
                "Kalau suatu saat nanti kau rindu padaku, maukah kau memberitahuku? agar aku bisa langsung berlari menemuimu",
                "Milea, jangan bilang ke aku ada yang menyakitimu, nanti besoknya, orang itu akan hilang",
                "Milea, kamu cantik, tapi aku belum mencintaimu. Enggak tahu kalau sore. Tunggu aja",
                "Katakan sekarang kalau kue kau anggap apa dirimu? roti cokelat? roti keju? martabak? kroket? bakwan? ayolah! aku ingin memesannya untuk malam ini aku mau kamu",
                "Aku rindu kamu! itu, akan selalu",
                "Ada kamu aku malu, tapi kalau gak ada kamu, aku suka rindu",
                "Aku mencintaumu, biarlah, ini urusanku. Bagaimana kamu kepadaku, terserah, itu urusanmu",
                "Bukan tuhan yang harus kau cari. Tapi jawaban, mengapa kau bodoh mencari yang sudah bersamamu",
                "Aku selalu berdoa berharap kamu mau denganku. Kukira Tuhan lebih kuasa daripada kau!",
                "Musik adalah perasaan yang bisa didengar",
                "Aku, adalah hujan. Kalau gak suka, silahkan berteduh",
                "Aku ingin meniup rambutmu. Aku, untuk mencintaimu",
                "Hatiku milikmu, kamu Tuannya, mau merasa senang atau tidak, kamulah yang menentukan",
                "Jika doa bukan sebuah Permintaan, setidaknya itu adalah sebuah Pengakuan atas Kelemahan diri manusia dihadapan Tuhannya",
                "Kita ingin senang, walau itu sederhana",
                "Tujuan pacaran adalah untuk putus. Bisa karena berpisah, bisa karena menikah",
                "Kesenangan bukan dicari, tapi dicipkatan",
                "Dia baik, jadi aku takut menyakitinya",
                "Jangan rindu. Ini berat, kau takkan kuat, biar aku saja",
                "Mendoakan adalah mencintai seseorang paling rahasia",
                "Tak semua yang kita kira indah akan menjadi indah pada akhirnya, andai saja dapat ku ulang kembali",
                "Kalau kamu bohong, itu hak kamu. Asal jangan aku yang bohong ke kamu",
                "Aku lahir, dibarengin kamu lahir. Kayak sengaja mau bikin aku seneng di Bumi",
                "Pemberitahuan: Sejak sore kemarin aku sudah mencintaimu",
                "Sesorang yang tepat tak selalu datang tepat waktu, kadang ia datang setelah kau lelah disakiti oleh seseorang yang tidak tahu cara menghargaimu",
                "Dan sekarang yang tetap di dalam diriku adalah kenangan, disanalah kamu selalu",
                "Cinta itu seperti kamu, lembut, menghangatkan dan aku selalu rindu",
                "Masa lalu adalah masa lalu, tak usah dihindari atau kau tolak. Masa lalu akan menjadi penasihat baik. Tidak ada gunanya kau sesali. Biarlah itu hadir sebagai aliran yang membawamu pergi ke tujuan yang lebih baik",
                "Cinta itu dirasakan bukan dipikirkan, ia lebih butuh balasan daripada alasan"};
        Log.e("Jumlah",String.valueOf(quote.length));
        int randomNumber = random.nextInt(quote.length - 0 ) + 0;
        return quote[randomNumber];
    }

    private void openNovel(int position){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("novel",position);
        startActivity(intent);
    }
}
