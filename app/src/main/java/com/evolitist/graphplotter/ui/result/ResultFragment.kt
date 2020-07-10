package com.evolitist.graphplotter.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.evolitist.graphplotter.R
import com.evolitist.graphplotter.databinding.ResultFragmentBinding
import com.evolitist.graphplotter.model.Point

class ResultFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(this, ResultViewModelFactory(points)).get<ResultViewModel>()
    }
    private lateinit var points: Array<Point>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        arguments?.getParcelableArray("points")?.let {
            points = it.filterIsInstance<Point>().toTypedArray()
        }
        val binding = ResultFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        ConstraintSet().run {
            clone(context, R.layout.result_fragment_constraints)
            applyTo(binding.constraints)
        }
        binding.saveButton.setOnClickListener {
            viewModel.saveImage(context ?: return@setOnClickListener, binding.graph.bitmap) {
                Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    class ResultViewModelFactory(private val points: Array<Point>) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass == ResultViewModel::class.java) {
                ResultViewModel(points) as T
            } else {
                throw UnsupportedOperationException("This factory can only create ViewModels of type ResultViewModel")
            }
        }
    }
}
