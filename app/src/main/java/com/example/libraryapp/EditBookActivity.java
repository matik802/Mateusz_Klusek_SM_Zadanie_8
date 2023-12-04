package com.example.libraryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditBookActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_BOOK_TITLE = "pb.edu.pl.EDIT_BOOK_TITLE";
    public static final String EXTRA_EDIT_BOOK_AUTHOR = "pb.edu.pl.EDIT_BOOK_AUTHOR";
    public static final String EXTRA_EDIT_BOOK_ID = "pb.edu.pl.EDIT_BOOK_ID";
    private EditText editTitleEditText;
    private EditText editAuthorEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        editTitleEditText = findViewById(R.id.edit_book_title);
        editAuthorEditText = findViewById(R.id.edit_book_author);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_EDIT_BOOK_TITLE))
            editTitleEditText.setText(intent.getStringExtra(EXTRA_EDIT_BOOK_TITLE));
        if (intent.hasExtra(EXTRA_EDIT_BOOK_AUTHOR))
            editAuthorEditText.setText(intent.getStringExtra(EXTRA_EDIT_BOOK_AUTHOR));
        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                if (editTitleEditText.getText().toString().isEmpty()
                        || editAuthorEditText.getText().toString().isEmpty()) {
                    setResult(RESULT_CANCELED, replyIntent);
                }
                else {
                    String title = editTitleEditText.getText().toString();
                    replyIntent.putExtra(EXTRA_EDIT_BOOK_TITLE, title);
                    String author = editAuthorEditText.getText().toString();
                    replyIntent.putExtra(EXTRA_EDIT_BOOK_AUTHOR, author);
                    if (intent.hasExtra(EXTRA_EDIT_BOOK_ID)) {
                        replyIntent.putExtra(EXTRA_EDIT_BOOK_ID, intent.getIntExtra(EXTRA_EDIT_BOOK_ID, 0));
                    }
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}