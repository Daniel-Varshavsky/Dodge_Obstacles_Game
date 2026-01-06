package com.example.dodge_obstacles_game.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dodge_obstacles_game.databinding.HighScoreRowBinding
import com.example.dodge_obstacles_game.interfaces.Callback_HighScoreClicked
import com.example.dodge_obstacles_game.model.leaderboardEntry

class HighScoreAdapter(
    private val items: List<leaderboardEntry>
) : RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder>() {

    var callback: Callback_HighScoreClicked? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HighScoreViewHolder {
        val binding = HighScoreRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HighScoreViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HighScoreViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class HighScoreViewHolder(
        private val binding: HighScoreRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: leaderboardEntry) {
            binding.highScoreLBLName.text = entry.name
            binding.highScoreLBLScore.text = entry.score.toString()
            binding.highScoreLBLTime.text = entry.time

            binding.root.setOnClickListener {
                if (entry.hasLocation()) {
                    callback?.highScoreItemClicked(
                        entry.latitude!!,
                        entry.longitude!!
                    )
                }
            }
        }
    }
}
