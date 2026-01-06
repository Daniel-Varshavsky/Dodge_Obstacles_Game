package com.example.dodge_obstacles_game.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dodge_obstacles_game.adapters.HighScoreAdapter
import com.example.dodge_obstacles_game.databinding.FragmentHighScoreBinding
import com.example.dodge_obstacles_game.interfaces.Callback_HighScoreClicked
import com.example.dodge_obstacles_game.utilities.Constants
import com.example.dodge_obstacles_game.utilities.SharedPreferencesManager

class HighScoreFragment : Fragment() {

    companion object {
        var highScoreItemClicked: Callback_HighScoreClicked? = null
    }

    private var _binding: FragmentHighScoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHighScoreBinding.inflate(inflater, container, false)

        binding.highScoreRV.layoutManager = LinearLayoutManager(requireContext())

        setupButtons()
        loadLeaderboard(Constants.GAME_MODE.BUTTONS_NORMAL)

        return binding.root
    }

    private fun setupButtons() {
        binding.btnButtons.setOnClickListener {
            binding.highScoreLAYDifficulties.visibility = View.VISIBLE
        }

        binding.btnTilt.setOnClickListener {
            loadLeaderboard(Constants.GAME_MODE.TILT)
            binding.highScoreLAYDifficulties.visibility = View.INVISIBLE
        }

        binding.btnEasy.setOnClickListener {
            loadLeaderboard(Constants.GAME_MODE.BUTTONS_EASY)
        }

        binding.btnNormal.setOnClickListener {
            loadLeaderboard(Constants.GAME_MODE.BUTTONS_NORMAL)
        }

        binding.btnHard.setOnClickListener {
            loadLeaderboard(Constants.GAME_MODE.BUTTONS_HARD)
        }
    }

    private fun loadLeaderboard(mode: String) {
        val leaderboard =
            SharedPreferencesManager.getInstance().getLeaderboard(mode)

        val adapter = HighScoreAdapter(leaderboard)
        adapter.callback = highScoreItemClicked

        binding.highScoreRV.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
