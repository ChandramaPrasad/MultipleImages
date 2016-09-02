package com.example.administrator.taskproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

;

public class MainActivity extends AppCompatActivity {

    private Button camera;
    private Button sendMail;
    Document document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        registerEvent();

    }

    /**
     * Method use for register the events when user click on buttons.
     */
    private void registerEvent() {
        // TODO Auto-generated method stub

        //Open camera when user click on camera button.
        camera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            }
        });
        //Send direct email when user click on email button.
        sendMail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                File[] listFile;
                File collectImages = new File(Environment.getExternalStorageDirectory(), "/collection.pdf");
                if (!collectImages.exists()) {
                    try {
                        collectImages.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }else{
                    collectImages.delete();
                }

                File fileImages = new File(Environment.getExternalStorageDirectory() + "/saved_images");

                if (fileImages.isDirectory()) {
                    listFile = fileImages.listFiles();
                    List<File> tempList = new ArrayList<File>(Arrays.asList(listFile));
                    try {
                        Image img = Image.getInstance(tempList.get(0).toString());
                        document = new Document(img);
                        PdfWriter.getInstance(document, new FileOutputStream(collectImages));
                        document.open();
                        for (int i = 0; i < tempList.size(); i++) {
                            img = Image.getInstance(tempList.get(i).toString());
                            document.setPageSize(img);
                            document.newPage();
                            img.setAbsolutePosition(0, 0);
                            document.add(img);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    document.close();
                }

                String[] mailto = { "" };
                Uri uri = Uri.fromFile(
                        new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/collection.pdf"));
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Image PDF Report");
                emailIntent.setType("application/pdf");
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(emailIntent, "email sending.."));

            }
        });

    }

    /**
     * This method use to initialise the variables.
     */
    private void initComponent() {
        // TODO Auto-generated method stub
        camera = (Button) findViewById(R.id.cameraButton);
        sendMail = (Button) findViewById(R.id.emailSend);
    }

    //When user click image vai camera then directly store to saveimage folder.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/saved_images");
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image_" + n + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
