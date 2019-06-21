package com.ziger.content_listing_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class EditCategoryActivity extends AppCompatActivity {

    private List<Stone> stoneList;
    private StoneAdapter stoneAdapter;
    private RecyclerView recyclerView;

    private TextView categoryNameEditText;

    private static final int REQUEST_INSERT = 3;
    private static final int REQUEST_EDIT = 4;

    private Category eCategory;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        categoryNameEditText = findViewById(R.id.categoryNameEditText);

        Bundle bundle = getIntent().getExtras();

        final int REQ_CODE = bundle.getInt("REQ_CODE");

        FloatingActionButton fabSave = findViewById(R.id.fabCategorySave);
        FloatingActionButton fabRemove = findViewById(R.id.fabCategoryRemove);

        if (REQ_CODE == REQUEST_EDIT) {

            fabRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeCategory();
                }
            });

            eCategory = bundle.getParcelable("eCategory");
            categoryNameEditText.setText(eCategory.getName());

            stoneList = bundle.getParcelableArrayList("stArray");
            stoneAdapter = new StoneAdapter(stoneList, null, this);


            recyclerView = (RecyclerView) findViewById(R.id.categoryRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(stoneAdapter);
        } else {
            fabRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(EditCategoryActivity.this, "Operação impossível.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnCategory();
            }
        });
    }

    public void returnCategory() {

        Bundle bundle = getIntent().getExtras();
        int categoryPos = bundle.getInt("position");
        int REQ_CODE = bundle.getInt("REQ_CODE");

        Bundle returnBundle = new Bundle();
        Category returnCategory = new Category();


        returnCategory.setName(categoryNameEditText.getText().toString());


        if (REQ_CODE == REQUEST_EDIT) {
            returnBundle.putInt("position", categoryPos);
            returnCategory.setId(eCategory.getId());
        }
        returnBundle.putParcelable("returnCategory", returnCategory);
        returnBundle.putInt("REQ_CODE", -1);

        Intent returnIntent = new Intent();
        returnIntent.putExtras(returnBundle);
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }

    public void removeCategory() {

        Bundle bundle = getIntent().getExtras();
        int categoryPos = bundle.getInt("position");

        Bundle returnBundle = new Bundle();

        returnBundle.putInt("position", categoryPos);
        returnBundle.putInt("id", eCategory.getId());
        returnBundle.putInt("REQ_CODE", 0);

        Intent returnIntent = new Intent();
        returnIntent.putExtras(returnBundle);
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }

}
