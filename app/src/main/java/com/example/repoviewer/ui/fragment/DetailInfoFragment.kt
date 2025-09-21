package com.example.repoviewer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.repoviewer.databinding.DetailInfoFragmentBinding
import com.example.repoviewer.ui.viewmodel.RepositoryInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.repoviewer.R
import io.noties.markwon.Markwon
import kotlin.getValue

@AndroidEntryPoint
class DetailInfoFragment : Fragment() {
    private var _binding: DetailInfoFragmentBinding? = null
    private val binding get() = _binding!!

    private var _markwon: Markwon? = null
    private val markwon get() = _markwon!!

    private var _repoId: String? = null
    private val repoId get() = _repoId!!

    private val args: DetailInfoFragmentArgs by navArgs()

    private val viewModel: RepositoryInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _repoId = args.repoId
        viewModel.loadRepositoryDetails(repoId)
        _markwon = Markwon.create(requireContext())

        val toolbar = binding.toolbarDetailInfo
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupWindowInsets()
        setupUiListeners()
        bindToViewModel()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutDetailInfoFragment) { v, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                v.paddingLeft,
                systemBarInsets.top,
                v.paddingRight,
                systemBarInsets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(binding.layoutDetailInfoFragment)
    }

    private fun setupUiListeners() {
        binding.buttonLogOutDetail.setOnClickListener {
            viewModel.logout()
        }

        binding.buttonRetryConnectionError.buttonRetry.setOnClickListener {
            viewModel.retryLoad(repoId)
        }

        binding.buttonRetryConnectionErrorReadme.buttonRetry.setOnClickListener {
            viewModel.retryLoad(repoId)
        }
    }

    private fun bindToViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateLoading(state)
            updateError(state)
            updateContent(state)
            updateReadme(state)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.action.collect { action ->
                    handleAction(action)
                }
            }
        }
    }

    private fun updateLoading(state: RepositoryInfoViewModel.State) {
        binding.layoutLoading.visibility =
            if (state is RepositoryInfoViewModel.State.Loading) View.VISIBLE else View.GONE
    }

    private fun updateError(state: RepositoryInfoViewModel.State) {
        binding.layoutConnectionError.visibility =
            if (state is RepositoryInfoViewModel.State.Error) View.VISIBLE else View.GONE
    }

    private fun updateContent(state: RepositoryInfoViewModel.State) {
        val loaded = state as? RepositoryInfoViewModel.State.Loaded
        binding.layoutRepositoryStatistics.visibility =
            if (loaded != null) View.VISIBLE else View.GONE
        loaded?.let {
            val repoDetail = it.githubRepo
            binding.textStarsCount.text = repoDetail.stargazersCount.toString()
            binding.textForksCount.text = repoDetail.forksCount.toString()
            binding.textWatchersCount.text = repoDetail.watchersCount.toString()
            binding.textLicenseValue.text =
                repoDetail.license?.name ?: getString(R.string.no_license)
            binding.textLink.text = repoDetail.htmlUrl

            (requireActivity() as AppCompatActivity).supportActionBar?.title = repoDetail.name
        }
    }

    private fun updateReadme(state: RepositoryInfoViewModel.State) {
        val readmeState = (state as? RepositoryInfoViewModel.State.Loaded)?.readmeState

        binding.layoutLoadingDetail.visibility =
            if (readmeState is RepositoryInfoViewModel.ReadmeState.Loading) View.VISIBLE else View.GONE

        binding.scrollReadme.visibility =
            if (readmeState is RepositoryInfoViewModel.ReadmeState.Loaded) View.VISIBLE else View.GONE

        if (readmeState is RepositoryInfoViewModel.ReadmeState.Loaded) {
            markwon.setMarkdown(binding.textReadme, readmeState.markdown)
        }

        binding.layoutConnectionErrorReadme.visibility =
            if (readmeState is RepositoryInfoViewModel.ReadmeState.Error) View.VISIBLE else View.GONE

        binding.layoutNoReadme.visibility =
            if (readmeState is RepositoryInfoViewModel.ReadmeState.Empty) View.VISIBLE else View.GONE
    }

    private fun handleAction(action: RepositoryInfoViewModel.Action) {
        when (action) {
            is RepositoryInfoViewModel.Action.NavigateToSignIn -> {
                val directions =
                    DetailInfoFragmentDirections.actionDetailInfoFragmentToAuthFragment()
                findNavController().navigate(directions)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}