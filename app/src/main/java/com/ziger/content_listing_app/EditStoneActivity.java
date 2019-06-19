package com.ziger.content_listing_app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EditStoneActivity extends AppCompatActivity {

    private ImageView stoneImageView;
    private EditText urlTextView;
    private Bitmap bitmap;
    private int cont;

    TextView nameEditText;
    TextView colorEditText;

    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_EDIT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stone);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();

        final int REQ_CODE = bundle.getInt("REQ_CODE");

        cont = 0;

        nameEditText = findViewById(R.id.nameEditText);
        colorEditText = findViewById(R.id.colorEditText);

        stoneImageView = (ImageView) findViewById(R.id.stoneImageView);
        stoneImageView.setImageResource(R.drawable.image);

        urlTextView = (EditText) findViewById(R.id.urlEditText);


        if (REQ_CODE == REQUEST_EDIT) {

//            nameEditText.setText(bundle.getString("name"));
//            colorEditText.setText(bundle.getString("color"));
//            urlTextView.setText(bundle.getString("url"));

            Stone eStone = bundle.getParcelable("eStone");

            nameEditText.setText(eStone.getName());
            colorEditText.setText(eStone.getColor());
            urlTextView.setText(eStone.getUrl());

//            byte[] imageBytes = bundle.getByteArray("imageBytes");
            stoneImageView.setImageBitmap(BitmapFactory.decodeByteArray(eStone.getImage(), 0, eStone.getImage().length));
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnStone();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0)
            if (resultCode == Activity.RESULT_OK){
                Uri targetUri = data.getData();
                Bitmap bitmap;
                try{
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                    stoneImageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        if (requestCode == 1)
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                stoneImageView.setImageBitmap(imageBitmap);
            }
    }

    public void returnStone() {

        Bundle bundle = getIntent().getExtras();
        int stonePos = bundle.getInt("position");
        int REQ_CODE = bundle.getInt("REQ_CODE");

        Bundle returnBundle = new Bundle();
        Stone returnStone = new Stone();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ((BitmapDrawable)stoneImageView.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream); // para evitar NullPointerException
        byte[] bpmBytes = stream.toByteArray();

        returnStone.setName(nameEditText.getText().toString());
        returnStone.setColor(colorEditText.getText().toString());
        returnStone.setUrl(urlTextView.getText().toString());

        returnStone.setImage(bpmBytes);

        returnBundle.putParcelable("returnStone", returnStone);

        if (REQ_CODE == REQUEST_EDIT) {
            returnBundle.putInt("position", stonePos);
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtras(returnBundle);
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }

    public void saveImage(View view) {
        Bitmap bitmap = ((BitmapDrawable)stoneImageView.getDrawable()).getBitmap();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        } else {

            String image_name = "Image_" + cont++;

            String root = Environment.getExternalStorageDirectory().toString() + File.separator + "DCIM";
            File myDir = new File(root);
            myDir.mkdirs();
            String fname = image_name + ".jpg";
            File file = new File(myDir, fname);

            if (file.exists()) file.delete();

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                MediaStore.Images.Media.insertImage(getContentResolver()
                        ,file.getAbsolutePath(),file.getName(),file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getFromGallery(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    public void getFromCamera(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        }
    }

    public void getFromURL(View view){
        new getBitmalURL().execute(urlTextView.getText().toString());
    }

    private class getBitmalURL extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            bitmap = new HttpHandler().getBitmap(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (bitmap!=null) {
                stoneImageView.setImageBitmap(bitmap);
            }else
                Toast.makeText(getApplicationContext(),"Não foi possivel fazer download da imagem",Toast.LENGTH_LONG).show();
        }
    }
}