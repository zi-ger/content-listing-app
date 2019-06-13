package com.ziger.content_listing_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Actions {

    private List<Stone> stoneList;
    private StoneAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private static final int REQUEST_INSERT = 1;
    private static final int REQUEST_EDIT = 2;

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new GetStonesJson().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void setRecyclerView() {

        adapter = new StoneAdapter(stoneList, this, this);

        recyclerView = (RecyclerView) findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new TouchHelp(adapter));
        touchHelper.attachToRecyclerView(recyclerView);

    }

    private void setFloatActionButton() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertStone();
            }
        });
    }

    public void insertStone(){
        Bundle bundle = new Bundle();
        bundle.putInt("REQ_CODE", REQUEST_INSERT);

        Intent intent = new Intent(this, EditStoneActivity.class);
        intent.putExtras(bundle);

        startActivityForResult(intent, REQUEST_INSERT);
    }

    @Override
    public void editStone(int pos) {
        Stone st = adapter.getStoneList().get(pos);

        Bundle bundle = new Bundle();

        Bitmap bmp = st.getImage();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        bundle.putByteArray("imageBytes", imageBytes);
        bundle.putString("name", st.getName());
        bundle.putString("color", st.getColor());
        bundle.putString("url", st.getUrl());

        bundle.putInt("REQ_CODE", REQUEST_EDIT);
        bundle.putInt("position", pos);

        Intent intent = new Intent(this, EditStoneActivity.class);
        intent.putExtras(bundle);

        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public void undo() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.constraintLayout),"Item removido.",Snackbar.LENGTH_LONG);

        snackbar.setAction("Desfazer", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.restore();
            }
        });
        snackbar.show();
    }

    @Override
    public void toast(Stone stone) {
        Toast.makeText(this, stone.getName()+" "+ stone.getColor(),Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INSERT){
            if (resultCode == Activity.RESULT_OK){
                Bundle bundle = data.getExtras();
                Stone newStone = new Stone();

                newStone.setName(bundle.getString("name"));
                newStone.setColor(bundle.getString("color"));
                newStone.setUrl(bundle.getString("url"));

                byte[] imageBytes = bundle.getByteArray("imageBytes");
                Bitmap bpm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                newStone.setImage(bpm);

                adapter.insert(newStone);
            } else{
                Toast.makeText(this,"Operação Cancelada!",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_EDIT) {
            if (resultCode == Activity.RESULT_OK) {

                Bundle bundle = data.getExtras();

                int pos = bundle.getInt("position");

                adapter.updateName(bundle.getString("name"), pos);
                adapter.updateColor(bundle.getString("color"), pos);
                adapter.updateUrl(bundle.getString("url"), pos);

                byte[] imageBytes = bundle.getByteArray("imageBytes");
                Bitmap newStoneImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                adapter.updateImage(newStoneImage, pos);

            } else {
                Toast.makeText(this,"Operação Cancelada!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private class GetStonesJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            stoneList = new ArrayList<Stone>();

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall("https://my-json-server.typicode.com/zi-ger/jsonServer/db");
            if (jsonStr != null) {
                try {
                    JSONObject object = new JSONObject(jsonStr);
                    JSONArray jsonArray = object.getJSONArray("stones");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Stone st = new Stone();
                        st.setName(jsonArray.getJSONObject(i).getString("name"));
                        st.setColor(jsonArray.getJSONObject(i).getString("color"));
                        st.setUrl(jsonArray.getJSONObject(i).getString("url"));
                        st.setImage(new HttpHandler().getBitmap(st.getUrl()));

                        stoneList.add(st);
                    }
                }  catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setRecyclerView();
            setFloatActionButton();
        }
    }

}