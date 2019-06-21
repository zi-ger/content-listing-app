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
    private StoneAdapter stoneAdapter;
    private CategoryAdapter categoryAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private FloatingActionButton fabCategory;
    private FloatingActionButton fabStone;

    private DBHelper sdb;

    private int CATEGORY_OR_STONE;

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

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Escolha entre categorias ou pedras primeiro!", Toast.LENGTH_SHORT).show();
            }
        });

        setFabCategory();
        setFabStone();
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
            if (CATEGORY_OR_STONE == 1) {
                categoryAdapter = new CategoryAdapter(sdb.getAllCategories(), this, this);
            } else if (CATEGORY_OR_STONE == 2) {
                stoneAdapter = new StoneAdapter(sdb.getAllStones(), this, this);
            }

        } else if (id == R.id.reloadDB){
            reloadDB();
        } else if (id == R.id.clearDB) {
            sdb.clearDatabase();

            stoneList.clear();
            categoryList.clear();

            if (CATEGORY_OR_STONE == 1) {
                categoryAdapter.setCategoryList(categoryList);
                recyclerView.setAdapter(categoryAdapter);
            } else if (CATEGORY_OR_STONE == 2) {
                stoneAdapter.setStoneList(stoneList);
                recyclerView.setAdapter(stoneAdapter);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadDB() {
        try {
            if (CATEGORY_OR_STONE == 1) {
                categoryList = sdb.getAllCategories();
                categoryAdapter = new CategoryAdapter(sdb.getAllCategories(), this, this);
                recyclerView.setAdapter(categoryAdapter);
            } else if (CATEGORY_OR_STONE == 2) {
                stoneList = sdb.getAllStones();
                stoneAdapter = new StoneAdapter(stoneList, this, this);
                recyclerView.setAdapter(stoneAdapter);
            }


        } catch (NullPointerException e) {
            Toast.makeText(this, "Nenhum registro no banco de dados.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setRecyclerViewCategory() {

        CATEGORY_OR_STONE = 1;

        try {
            categoryList = sdb.getAllCategories();
            categoryAdapter = new CategoryAdapter(categoryList, this, this);

        } catch (NullPointerException e) {
            categoryAdapter = new CategoryAdapter(categoryList, this, this);

            Toast.makeText(this, "Nenhum registro no banco de dados.", Toast.LENGTH_SHORT).show();
        }

        recyclerView = (RecyclerView) findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(categoryAdapter);
   }

    private void setRecyclerViewStone() {

        CATEGORY_OR_STONE = 2;

        try {
            stoneList = sdb.getAllStones();
            stoneAdapter = new StoneAdapter(stoneList, this, this);

        } catch (NullPointerException e) {
            stoneAdapter = new StoneAdapter(stoneList, this, this);

            Toast.makeText(this, "Nenhum registro no banco de dados.", Toast.LENGTH_SHORT).show();
        }

        recyclerView = (RecyclerView) findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stoneAdapter);
    }

    private void setFabCategory() {
        fabCategory = findViewById(R.id.fabCategory);
        fabCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setRecyclerViewCategory();
            }
        });
    }

    private void setFabStone() {
        fabStone = findViewById(R.id.fabStone);
        fabStone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRecyclerViewStone();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        insertStone();
                    }
                });
            }
        });
    }

    public void insertStone(){
        Bundle bundle = new Bundle();
        bundle.putInt("REQ_CODE", REQUEST_INSERT);

        ArrayList<Category> catArray = sdb.getAllCategories();
        bundle.putParcelableArrayList("catArray", catArray);

        Intent intent = new Intent(this, EditStoneActivity.class);
        intent.putExtras(bundle);

        startActivityForResult(intent, REQUEST_INSERT);
    }

    @Override
    public void editStone(int pos) {
        Stone st = stoneAdapter.getStoneList().get(pos);

        Bundle bundle = new Bundle();

        ArrayList<Category> catArray = sdb.getAllCategories();
        bundle.putParcelableArrayList("catArray", catArray);

        bundle.putParcelable("eStone", st);

        bundle.putInt("REQ_CODE", REQUEST_EDIT);
        bundle.putInt("position", pos);

        Intent intent = new Intent(this, EditStoneActivity.class);
        intent.putExtras(bundle);

        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public void editCategory(int pos) {
        Category cat;
    }

    @Override
    public void undo() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.constraintLayout),"Item removido.",Snackbar.LENGTH_LONG);

        snackbar.setAction("Desfazer", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stoneAdapter.restore();
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
                stoneAdapter.insert(newStone);
            } else{
                Toast.makeText(this,"Operação Cancelada!",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                Stone eStone = bundle.getParcelable("returnStone");

                int pos = bundle.getInt("position");

                stoneAdapter.update(eStone, pos);
                sdb.updateStone(eStone);
                reloadDB();
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

                        stone.setCategory((int)sdb.getCategoryFromName(jsonArray.getJSONObject(i).getString("category")).getId());

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
        }
    }

}