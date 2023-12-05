package com.example.libraryapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private BookViewModel bookViewModel;
    public static final int NEW_BOOK_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_BOOK_ACTIVITY_REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        setSupportActionBar(binding.toolbar);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
//        bookViewModel.findAll().observe(this, adapter::setBooks);
        bookViewModel.findAll().observe(this, books -> {
            adapter.setBooks(books);
                });
        FloatingActionButton addBookButton = findViewById(R.id.add_button);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditBookActivity.class);
                startActivityForResult(intent, NEW_BOOK_ACTIVITY_REQUEST_CODE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == NEW_BOOK_ACTIVITY_REQUEST_CODE) {
            Book book = new Book(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE),
                    data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR));
            bookViewModel.insert(book);
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.book_added),
                    Snackbar.LENGTH_LONG).show();
        }
        else if (resultCode == RESULT_OK && requestCode == EDIT_BOOK_ACTIVITY_REQUEST_CODE) {
            Book book = new Book(
                    data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE),
                    data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR)
            );
            book.setId(data.getIntExtra(EditBookActivity.EXTRA_EDIT_BOOK_ID,0));
           // Book book = bookViewModel.findById(data.getIntExtra(EditBookActivity.EXTRA_EDIT_BOOK_ID,0));
//            Book book = bookViewModel.findBookWithTitle(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE)).get(0);
            //Log.e("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",String.valueOf(book.getId()));
            bookViewModel.update(book);
            Snackbar.make(findViewById(R.id.coordinator_layout),
                            getString(R.string.book_edited),
                            Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.empty_not_saved),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private Book book;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
//            itemView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
//                public void onSwipeTop() {
//                    Snackbar.make(findViewById(R.id.coordinator_layout),
//                            getString(R.string.book_archived),
//                            Snackbar.LENGTH_LONG).show();
//                }
//                public void onSwipeRight() {
//                    Snackbar.make(findViewById(R.id.coordinator_layout),
//                            getString(R.string.book_archived),
//                            Snackbar.LENGTH_LONG).show();
//                }
//                public void onSwipeLeft() {
//                    Snackbar.make(findViewById(R.id.coordinator_layout),
//                            getString(R.string.book_archived),
//                            Snackbar.LENGTH_LONG).show();
//                }
//                public void onSwipeBottom() {
//                    Snackbar.make(findViewById(R.id.coordinator_layout),
//                            getString(R.string.book_archived),
//                            Snackbar.LENGTH_LONG).show();
//                }
//
//            });

            bookTitleTextView = itemView.findViewById(R.id.book_title);
            bookAuthorTextView = itemView.findViewById(R.id.book_author);
        }

        public void bind(Book book) {
            bookTitleTextView.setText(book.getTitle());
            bookAuthorTextView.setText(book.getAuthor());
            this.book = book;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, EditBookActivity.class);
            intent.putExtra(EditBookActivity.EXTRA_EDIT_BOOK_ID, book.getId());
            intent.putExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE, book.getTitle());
            intent.putExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR, book.getAuthor());
            startActivityForResult(intent, EDIT_BOOK_ACTIVITY_REQUEST_CODE);
        }

        @Override
        public boolean onLongClick(View v) {
            MainActivity.this.bookViewModel.delete(this.book);
            return true;
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> books;

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            if (books != null) {
                Book book = books.get(position);
                holder.bind(book);
            }
            else
                Log.d("MainActivity", "No books");
        }

        @Override
        public int getItemCount() {
            if (books != null)
                return books.size();
            return 0;
        }

        void setBooks(List<Book> books) {
            this.books = books;
            notifyDataSetChanged();
        }
    }
}