package com.example.mysupermarketlist

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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

        var isOtherTabOpen = true

        val pair = initFirstRecyclerView(listener)
        val dataset = pair.first
        val listAdapter = pair.second

        var datasetSecond = databaseHelper.readFromDatabaseOther()
        val listAdapterSecond = ListAdapter(datasetSecond, listener)

        val recyclerViewSecond: RecyclerView = findViewById(R.id.recycler_second)
        recyclerViewSecond.layoutManager = LinearLayoutManager(this)
        recyclerViewSecond.adapter = listAdapterSecond

        val arrowImageView = findViewById<ImageView>(R.id.imageview_arrow)

        val secondLinearLayout = findViewById<LinearLayout>(R.id.linearlayout_second)
        secondLinearLayout.setOnClickListener{ v ->
            if(isOtherTabOpen){
                arrowImageView.setImageDrawable(getDrawable(R.drawable.ic_arrow_down))
            }
            else{
                arrowImageView.setImageDrawable(getDrawable(R.drawable.ic_arrow_up))
            }

            isOtherTabOpen = !isOtherTabOpen

            recyclerViewSecond.isVisible = isOtherTabOpen
        }

        initFabs(dataset, listAdapter, datasetSecond, listAdapterSecond)
    }

    private fun initFabs(
        dataset: MutableList<ListAdapter.ListItem>,
        listAdapter: ListAdapter,
        datasetSecond: MutableList<ListAdapter.ListItem>,
        listAdapterSecond: ListAdapter
    ) {
        var dataset1 = dataset
        var datasetSecond1 = datasetSecond
        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)
        fabAdd.setOnClickListener {
            onAddClick(dataset1, listAdapter, datasetSecond1, listAdapterSecond)
        }

        val fabDelete: FloatingActionButton = findViewById(R.id.fab_delete)
        fabDelete.setOnClickListener {
            databaseHelper.deleteFromDatabase()

            dataset1 = databaseHelper.readFromDatabase()
            datasetSecond1 = databaseHelper.readFromDatabaseOther()

            listAdapter.updateAdapter(dataset1)
            listAdapterSecond.updateAdapter((datasetSecond1))
        }
    }

    private fun initFirstRecyclerView(listener: OnItemClickListener): Pair<MutableList<ListAdapter.ListItem>, ListAdapter> {
        val dataset = databaseHelper.readFromDatabase()
        val listAdapter = ListAdapter(dataset, listener)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_first)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listAdapter
        return Pair(dataset, listAdapter)
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
                    databaseHelper.addToDatabase(editText.text.toString(), !checkBox.isChecked)

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