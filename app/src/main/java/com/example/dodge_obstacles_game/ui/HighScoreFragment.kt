package com.example.dodge_obstacles_game.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dodge_obstacles_game.R
import com.example.dodge_obstacles_game.model.leaderboardEntry
import com.example.dodge_obstacles_game.utilities.SharedPreferencesManager
import com.example.dodge_obstacles_game.interfaces.Callback_HighScoreClicked

class HighScoreFragment : Fragment() {

    companion object {
        var highScoreItemClicked: Callback_HighScoreClicked? = null
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HighScoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_high_score, container, false)
        recyclerView = v.findViewById(R.id.highScore_RV)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val leaderboard: List<leaderboardEntry> = SharedPreferencesManager.getInstance().getLeaderboard()
        adapter = HighScoreAdapter(leaderboard)
        recyclerView.adapter = adapter

        return v
    }

    inner class HighScoreAdapter(private val items: List<leaderboardEntry>) :
        RecyclerView.Adapter<HighScoreAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameText = view.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.highScore_LBL_name)
            val scoreText = view.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.highScore_LBL_score)
            val timeText = view.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.highScore_LBL_time)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.high_score_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val entry = items[position]
            holder.nameText.text = entry.name
            holder.scoreText.text = entry.score.toString()
            holder.timeText.text = entry.time

            holder.itemView.setOnClickListener {
                entry.latitude?.let { lat ->
                    entry.longitude?.let { lon ->
                        highScoreItemClicked?.highScoreItemClicked(lat, lon)
                    }
                }
            }
        }

        override fun getItemCount(): Int = items.size
    }
}
