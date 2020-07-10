package com.evolitist.graphplotter.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.evolitist.graphplotter.R
import com.evolitist.graphplotter.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {
    private val viewModel by lazy { ViewModelProvider(this).get<HomeViewModel>() }
    private lateinit var binding: HomeFragmentBinding
    private var shortAnimationDuration = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        shortAnimationDuration = context?.resources?.getInteger(android.R.integer.config_shortAnimTime)?.toLong() ?: 0L
        binding = HomeFragmentBinding.inflate(inflater, container, false).apply {
            viewModel = this@HomeFragment.viewModel
            inputField.apply {
                setImeActionLabel(getString(R.string.go), EditorInfo.IME_ACTION_GO)
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        loadPoints()
                        true
                    } else false
                }
            }
            button.setOnClickListener { loadPoints() }
        }

        return binding.root
    }

    private fun loadPoints() {
        binding.run {
            val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
            val view = activity?.currentFocus ?: View(activity)
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            inputField.clearFocus()
            button.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .withEndAction {
                    button.visibility = View.GONE
                }
            loader.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration)
                    .setListener(null)
            }
        }
        viewModel.getPoints { points, e ->
            binding.run {
                loader.visibility = View.GONE
                button.alpha = 1f
                button.visibility = View.VISIBLE
            }
            if (!points.isNullOrEmpty()) {
                findNavController(this).navigate(
                    HomeFragmentDirections.actionHomeFragmentToResultFragment(points, points.size)
                )
            } else {
                Toast.makeText(
                    context,
                    e?.message ?: context?.getString(R.string.error_unknown),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
