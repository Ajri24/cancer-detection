package com.dicoding.picodiploma.mycamera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.adapter.Hadapter
import com.example.myapplication.db.HistoryDB
import com.example.myapplication.db.RepositoryClass

class History : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Hadapter
    private lateinit var repository: RepositoryClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history)

        repository = RepositoryClass(this)

        recyclerView = findViewById(R.id.recyclerViewFavoritePerson)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val favoriteFollowersList: List<HistoryDB> = repository.getAllHistory()

        adapter = Hadapter(this, repository)
        adapter.setData(favoriteFollowersList)
        recyclerView.adapter = adapter
    }
}

