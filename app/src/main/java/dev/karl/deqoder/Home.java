package dev.karl.deqoder;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Home extends AppCompatActivity {

    ConstraintLayout scan, gen;
    String barcodeStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setStatusBarColor(ContextCompat.getColor(Home.this, R.color.light_gray));
        getWindow().setNavigationBarColor(ContextCompat.getColor(Home.this, R.color.light_gray));

        ActivityCompat.requestPermissions(Home.this, new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
        }, PackageManager.PERMISSION_GRANTED);

        scan = findViewById(R.id.clScanBtn);
        gen = findViewById(R.id.clGenBtn);

        scan.setOnClickListener(view -> {
            //
            scanCode();
        });

        gen.setOnClickListener(view -> {
            //
            Intent intent = new Intent(Home.this, QrGen.class);
            startActivity(intent);
        });

        AdView mAdView = findViewById(R.id.adViewHome);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(getIntent().hasExtra("shortcut")){

            scanCode();
        }

    }

    public void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Press volume up button to turn on flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{
        barcodeStr = "-";
        if(result.getContents() != null){
            //result.getContents();
            //Toast.makeText(Home.this, "" + result.getContents(), Toast.LENGTH_SHORT).show();
            barcodeStr = result.getContents();
            //
            alertResult(barcodeStr);

        }else{
            //alertDia("Error", "Scan failed. Try again.");

            if(getIntent().hasExtra("shortcut")){
                finishAffinity();
            }
        }
    });
    public void alertResult(String textStr){

        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        View v = getLayoutInflater().inflate(R.layout.dialog_result_viewer, null);

        TextView text = v.findViewById(R.id.tvDiaResultText);
        TextView closeBtn = v.findViewById(R.id.tvClose);
        TextView copyBtn = v.findViewById(R.id.tvCopyToCB);
        TextView gotoLink = v.findViewById(R.id.tvGotoLink);

        text.setText(textStr);

        builder.setView(v);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        closeBtn.setOnClickListener(view -> {
            alertDialog.dismiss();
            //

        });

        copyBtn.setOnClickListener(view -> {
            //
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("link", textStr);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(Home.this, "Link copied to clipboard" , Toast.LENGTH_SHORT).show();


        });
        gotoLink.setOnClickListener(view -> {
            //
            if (isValidURL(textStr)){
                try {
                    Uri uri = Uri.parse(textStr);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(Home.this, "Not able to open link" , Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Home.this, "Not able to open link" , Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setOnDismissListener(dialogInterface -> {
            if(getIntent().hasExtra("shortcut")){
                finishAffinity();
            }
        });

    }

    private static boolean isValidURL(String url) {
        String regex = "^(https?|ftp):\\/\\/[\\w\\.-]+(:\\d+)?(\\/\\S*)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

}