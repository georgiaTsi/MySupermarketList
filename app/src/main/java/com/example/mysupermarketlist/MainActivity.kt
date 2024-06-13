package com.example.mysupermarketlist

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    val databaseHelper = SupermarketDatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listener = object : OnItemClickListener {
            override fun onItemClick(item: ListAdapter.ListItem) {
                databaseHelper.updateDatabase(item)
            }
        }

        var dataset = databaseHelper.readFromDatabase()
        val listAdapter = ListAdapter(dataset, listener)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listAdapter

        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)
        fabAdd.setOnClickListener{
            onAddClick(dataset, listAdapter)
        }

        val fabDelete: FloatingActionButton = findViewById(R.id.fab_delete)
        fabDelete.setOnClickListener{
            databaseHelper.deleteFromDatabase()

            dataset = databaseHelper.readFromDatabase()

            listAdapter.updateAdapter(dataset)
        }
    }

    private fun onAddClick(dataset: MutableList<ListAdapter.ListItem>, listAdapter: ListAdapter) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog, null)
        val editText = view.findViewById<EditText>(R.id.editText)

        builder.setTitle("Add")
            .setView(view)
            .setPositiveButton("OK") { dialog, _ ->
                if (editText.text.toString().isNotEmpty()) {
                    databaseHelper.addToDatabase(editText.text.toString())

                    dataset.add(dataset.size, ListAdapter.ListItem(editText.text.toString(), false))

                    listAdapter.notifyItemInserted(dataset.size)
                }

                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onDestroy() {
        databaseHelper.dbHelper.close()

        super.onDestroy()
    }
}