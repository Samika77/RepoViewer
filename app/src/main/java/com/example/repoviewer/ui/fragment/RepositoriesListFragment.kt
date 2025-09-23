package com.example.repoviewer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repoviewer.databinding.RepositoriesListFragmentBinding
import com.example.repoviewer.ui.adapter.RepoAdapter
import com.example.repoviewer.ui.viewmodel.RepositoriesListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepositoriesListFragment : Fragment() {

    private var _binding: RepositoriesListFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RepositoriesListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RepositoriesListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWindowInsets()

        binding.recyclerRepositories.layoutManager = LinearLayoutManager(requireContext())

        val repoAdapter = RepoAdapter()
        binding.recyclerRepositories.adapter = repoAdapter

        setupUiListeners(repoAdapter)

        bindToViewModel(repoAdapter)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutRepositoriesListFragment) { v, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                v.paddingLeft,
                systemBarInsets.top,
                v.paddingRight,
                systemBarInsets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(binding.layoutRepositoriesListFragment)
    }

    private fun setupUiListeners(adapter: RepoAdapter) {
        binding.buttonLogOutRepositories.setOnClickListener {
            viewModel.logout()
        }

        binding.buttonRetryConnectionError.buttonRetry.setOnClickListener {
            viewModel.retryLoad()
        }

        binding.buttonRetrySomethingError.buttonRetry.setOnClickListener {
            viewModel.retryLoad()
        }

        binding.buttonRefreshRepositories.setOnClickListener {
            viewModel.refreshList()
        }

        adapter.setOnItemClickListener { repoId ->
            viewModel.onRepositorySelected(repoId)
        }
    }

    private fun bindToViewModel(adapter: RepoAdapter) {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateLoading(state)
            updateRepositoryList(state, adapter)
            updateEmptyView(state)
            updateConnectionErrorView(state)
            updateGeneralErrorView(state)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.action.collect { action ->
                    handleAction(action)
                }
            }
        }
    }

    private fun updateLoading(state: RepositoriesListViewModel.State) {
        binding.layoutLoading.visibility =
            if (state is RepositoriesListViewModel.State.Loading) View.VISIBLE else View.GONE
    }

    private fun updateRepositoryList(state: RepositoriesListViewModel.State, adapter: RepoAdapter) {
        val visible = state is RepositoriesListViewModel.State.Loaded
        binding.recyclerRepositories.visibility = if (visible) View.VISIBLE else View.GONE
        if (visible) {
            adapter.submitList(state.repos)
        }
    }

    private fun updateEmptyView(state: RepositoriesListViewModel.State) {
        binding.layoutRepositoriesEmpty.visibility =
            if (state is RepositoriesListViewModel.State.Empty) View.VISIBLE else View.GONE
    }

    private fun updateConnectionErrorView(state: RepositoriesListViewModel.State) {
        binding.layoutConnectionError.visibility =
            if (state is RepositoriesListViewModel.State.Error.ConnectionError) View.VISIBLE else View.GONE
    }

    private fun updateGeneralErrorView(state: RepositoriesListViewModel.State) {
        binding.layoutSomethingError.visibility =
            if (state is RepositoriesListViewModel.State.Error.GeneralError) View.VISIBLE else View.GONE
    }

    private fun handleAction(action: RepositoriesListViewModel.Action) {
        when (action) {
            is RepositoriesListViewModel.Action.RouteToDetail -> navigateToDetail(action.repoId)
            RepositoriesListViewModel.Action.NavigateToSignIn -> navigateToAuth()
        }
    }

    private fun navigateToDetail(repoId: String) {
        val directions =
            RepositoriesListFragmentDirections.actionRepositoriesListFragmentToDetailInfoFragment(
                repoId
            )
        findNavController().navigate(directions)
    }

    private fun navigateToAuth() {
        val directions =
            RepositoriesListFragmentDirections.actionRepositoriesListFragmentToAuthFragment()
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}