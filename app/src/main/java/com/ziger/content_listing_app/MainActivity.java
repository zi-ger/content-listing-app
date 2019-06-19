package com.ziger.content_listing_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
    private List<Category> categoryList;
    private StoneAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private DBHelper sdb;


    private static final int REQUEST_INSERT = 1;
    private static final int REQUEST_EDIT = 2;

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sdb = new DBHelper(this);

        setRecyclerView();
        setFloatActionButton();
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
        if (id == R.id.readFromJson) {
            new GetFromJson().execute();
//            adapter = new StoneAdapter(sdb.getAllStones(), this, this);

        } else if (id == R.id.reloadDB){
            try {
                stoneList = sdb.getAllStones();
                adapter = new StoneAdapter(stoneList, this, this);
                recyclerView.setAdapter(adapter);

            } catch (NullPointerException e) {
                Toast.makeText(this, "Nenhum registro no banco de dados.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.clearDB) {
            sdb.clearDatabase();
            stoneList.clear();
            adapter.setStoneList(stoneList);
            recyclerView.setAdapter(adapter);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRecyclerView() {

        try {
            stoneList = sdb.getAllStones();
            adapter = new StoneAdapter(stoneList, this, this);

        } catch (NullPointerException e) {
            adapter = new StoneAdapter(stoneList, this, this);

            Toast.makeText(this, "Nenhum registro no banco de dados.", Toast.LENGTH_SHORT).show();
        }

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

        bundle.putParcelable("eStone", st);

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

                Stone newStone = bundle.getParcelable("returnStone");

                sdb.insertStone(newStone);
                adapter.insert(newStone);
            } else{
                Toast.makeText(this,"Operação Cancelada!",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_EDIT) {
            if (resultCode == Activity.RESULT_OK) {

                Bundle bundle = data.getExtras();
                Stone eStone = bundle.getParcelable("returnStone");
                int pos = bundle.getInt("position");

                adapter.updateName(eStone.getName(), pos);
                adapter.updateColor(eStone.getColor(), pos);
                adapter.updateUrl(eStone.getUrl(), pos);
                adapter.updateImage(eStone.getImage(), pos);

          } else {
                Toast.makeText(this,"Operação Cancelada!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private class GetFromJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            stoneList = new ArrayList<Stone>();
            categoryList = new ArrayList<Category>();


            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall("https://my-json-server.typicode.com/zi-ger/jsonServer/db");
            if (jsonStr != null) {
                try {
                    JSONObject object = new JSONObject(jsonStr);
                    JSONArray jsonArray = object.getJSONArray("categories");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        Category category = new Category();
                        category.setName(jsonArray.getJSONObject(i).getString("name"));

                        sdb.insertCategory(category);
                    }
                    object = new JSONObject(jsonStr);
                    jsonArray = object.getJSONArray("stones");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Stone stone = new Stone();
                        stone.setName(jsonArray.getJSONObject(i).getString("name"));
                        stone.setColor(jsonArray.getJSONObject(i).getString("color"));
                        stone.setCategory(sdb.getCategoryFromName(jsonArray.getJSONObject(i).getString("category")).getId());
                        stone.setUrl(jsonArray.getJSONObject(i).getString("url"));

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        new HttpHandler().getBitmap(stone.getUrl()).compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] imageBytes = stream.toByteArray();

                        stone.setImage(imageBytes);

                        sdb.insertStone(stone);
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
//            setRecyclerView();
//            setFloatActionButton();
        }
    }

}