package dev.karl.deqoder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.UUID;
public class QrGen extends AppCompatActivity {

    TextView genQR, genBC, save, label;
    EditText editText;
    ImageView imageView;
    ConstraintLayout cl;
    Switch aSwitch;
    String plainText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_gen);

        getWindow().setStatusBarColor(ContextCompat.getColor(QrGen.this, R.color.blueish));
        getWindow().setNavigationBarColor(ContextCompat.getColor(QrGen.this, R.color.blueish));

        editText = findViewById(R.id.etTextToGenerate);

        genQR = findViewById(R.id.tvGenQRBtn);
        genBC = findViewById(R.id.tvGenBCBtn);
        save = findViewById(R.id.tvSaveBtn);

        imageView = findViewById(R.id.ivQRBC);
        label = findViewById(R.id.tvTextlabel);
        cl = findViewById(R.id.clQRBC);
        aSwitch = findViewById(R.id.switch1);

        genQR.setOnClickListener(view -> {
            //
            plainText = editText.getText().toString();

            MultiFormatWriter writer = new MultiFormatWriter();
            try {
                BitMatrix matrix = writer.encode(plainText, BarcodeFormat.QR_CODE, 350, 350);
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.createBitmap(matrix);
                imageView.setImageBitmap(bitmap);
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);

                if (aSwitch.isChecked()){
                    label.setText(plainText);
                } else label .setText("");
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(QrGen.this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
            }
        });
        genBC.setOnClickListener(view -> {
            //
            plainText = editText.getText().toString();

            MultiFormatWriter writer = new MultiFormatWriter();
            try {
                BitMatrix matrix = writer.encode(plainText, BarcodeFormat.CODE_128, 350, 200);
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.createBitmap(matrix);
                imageView.setImageBitmap(bitmap);
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);

                if (aSwitch.isChecked()){
                    label.setText(plainText);
                } else label .setText("");
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(QrGen.this, "Failed to generate barcode", Toast.LENGTH_SHORT).show();
            }
        });
        save.setOnClickListener(view -> {
            //
            cl = findViewById(R.id.clQRBC);
            cl.setDrawingCacheEnabled(false);
            cl.setDrawingCacheEnabled(true);
            cl.buildDrawingCache();
            Bitmap bitmap = cl.getDrawingCache();

            try {
                saveImageToStorage(bitmap, (plainText.length() >= 10 ? plainText.substring(0,10) : plainText));
            } catch (IOException e) {
                Toast.makeText(QrGen.this, "Failed. IOException", Toast.LENGTH_SHORT).show();
            }

        });
        aSwitch.setChecked(false);

        AdView mAdView = findViewById(R.id.adViewQRBC);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    public void saveImageToStorage(Bitmap bitmapObject, String plainTxt) throws IOException {

        // Generate a random UUID
        UUID uuid = UUID.randomUUID();

        // Convert UUID to String
        String uuidString = uuid.toString();

        OutputStream imageOutStream;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "deqoder-"+plainTxt+"-"+uuidString+".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Deqoder Generated/");
            Uri uri = QrGen.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            imageOutStream = QrGen.this.getContentResolver().openOutputStream(uri);
        } else {
            String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Deqoder Generated/";
            Toast.makeText(QrGen.this, "Saved to "+imagePath, Toast.LENGTH_SHORT).show();
            File image = new File(imagePath, "deqoder-"+plainTxt+"-"+uuidString+".jpg");
            imageOutStream = new FileOutputStream(image);
        }

        try {
            bitmapObject.compress(Bitmap.CompressFormat.JPEG, 100, imageOutStream);
        } finally {
            imageOutStream.close();
        }

        Toast.makeText(QrGen.this, "Image saved.", Toast.LENGTH_SHORT).show();

    }
}