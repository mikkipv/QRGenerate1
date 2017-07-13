package com.example.codemac.qrgenerate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class MainActivity extends AppCompatActivity {

    EditText inputString;
    ImageView qrImage;
    Button btnSubmit;
    Button btnSendEmail;
    Bitmap bitmap;

    public final static int QRcodeWidth = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputString = (EditText) findViewById(R.id.editTextInput);
        qrImage = (ImageView) findViewById(R.id.imageViewDisplay);

        btnSubmit = (Button) findViewById(R.id.buttonSubmit);
        btnSendEmail = (Button) findViewById(R.id.buttonEmail);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inpustStringValue = inputString.getText().toString();


                try {

                    bitmap = TextToImageEncode(inpustStringValue);

                    qrImage.setImageBitmap(bitmap);


                } catch (WriterException e) {

                    Log.e("QRGenerate", e.getMessage());
                }


            }
        });

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendEmail("mikkipv@gmail.com", "mikki");
            }
        });
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void sendEmail(String emailId, String student) {

        File myDir = new File(Environment.getExternalStorageDirectory() + "/req_images");
        myDir.mkdirs();

        DateFormat format = new SimpleDateFormat("yyyy_MM_dd_H_mm_ss", Locale.getDefault());
        Date curDate = new Date();
        String displayDate = format.format(curDate);
        String fname = displayDate + student + "_img.jpg"; // now this is dynamic

        // create the file in the directory
        File file = new File(myDir, fname);

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/image");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{emailId});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test Subject");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");

        emailIntent.setType("image/*"); // accept any image



        try {
            boolean fileCreated = file.createNewFile();
            if (fileCreated) {
                // write the bitmap to that file
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            }
        } catch (IOException ex) {
            Log.d("SAVE FAILED", "could not save file");
        }

        // then attach the file to the intent
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }



}