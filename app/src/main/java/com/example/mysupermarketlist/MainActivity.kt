package com.example.mysupermarketlist

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
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

        val recyclerView: RecyclerView = findViewById(R.id.recycler_first)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listAdapter

        var datasetSecond = databaseHelper.readFromDatabaseOther()
        val listAdapterSecond = ListAdapter(datasetSecond, listener)

        val recyclerViewSecond: RecyclerView = findViewById(R.id.recycler_second)
        recyclerViewSecond.layoutManager = LinearLayoutManager(this)
        recyclerViewSecond.adapter = listAdapterSecond

        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)
        fabAdd.setOnClickListener{
            onAddClick(dataset, listAdapter, datasetSecond, listAdapterSecond)
        }

        val fabDelete: FloatingActionButton = findViewById(R.id.fab_delete)
        fabDelete.setOnClickListener{
            databaseHelper.deleteFromDatabase()

            dataset = databaseHelper.readFromDatabase()
            datasetSecond = databaseHelper.readFromDatabaseOther()

            listAdapter.updateAdapter(dataset)
            listAdapterSecond.updateAdapter((datasetSecond))
        }
    }

    private fun onAddClick(dataset: MutableList<ListAdapter.ListItem>, listAdapter: ListAdapter, datasetSecond: MutableList<ListAdapter.ListItem>, listAdapterSecond: ListAdapter) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog, null)
        val editText = view.findViewById<EditText>(R.id.editText)
        val checkBox = view.findViewById<CheckBox>(R.id.checkbox)

        builder.setTitle("Add")
            .setView(view)
            .setPositiveButton("OK") { dialog, _ ->
                if (editText.text.toString().isNotEmpty()) {
                    databaseHelper.addToDatabase(editText.text.toString(), checkBox.isChecked)

                    if(!checkBox.isChecked) {
                        dataset.add(dataset.size, ListAdapter.ListItem(editText.text.toString(), false))

                        listAdapter.notifyItemInserted(dataset.size)
                    }
                    else{
                        datasetSecond.add(datasetSecond.size, ListAdapter.ListItem(editText.text.toString(), false))

                        listAdapterSecond.notifyItemInserted(datasetSecond.size)
                    }
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