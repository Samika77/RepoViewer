package com.example.repoviewer.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.repoviewer.R
import com.example.repoviewer.databinding.AuthFragmentBinding
import com.example.repoviewer.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : Fragment() {
    private var _binding: AuthFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AuthFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutAuthFragment) { v, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                v.paddingLeft,
                systemBarInsets.top,
                v.paddingRight,
                systemBarInsets.bottom
            )
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            binding.buttonSignIn.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = imeInsets.bottom
            }
            insets
        }
        ViewCompat.requestApplyInsets(binding.layoutAuthFragment)

        binding.editTextToken.setOnFocusChangeListener { _, _ ->
            viewModel.refreshState()
        }

        binding.editTextToken.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                viewModel.token.value = text?.toString()
            }
        )

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBarSignIn.visibility =
                if (state is AuthViewModel.State.Loading) View.VISIBLE else View.GONE
            binding.textInvalidToken.visibility =
                if (state is AuthViewModel.State.InvalidInput) View.VISIBLE else View.GONE
            binding.textInvalidToken.text =
                if (state is AuthViewModel.State.InvalidInput) {
                    when (state.reasonCode) {
                        AuthViewModel.ErrorReason.EMPTY_TOKEN -> getString(R.string.empty_token)
                        AuthViewModel.ErrorReason.INVALID_TOKEN -> getString(R.string.invalid_token)
                        AuthViewModel.ErrorReason.UNKNOWN_ERROR -> getString(R.string.unknown_error)
                    }
                } else null
            binding.buttonSignIn.isEnabled = state !is AuthViewModel.State.Loading
            binding.buttonSignIn.text =
                if (state is AuthViewModel.State.Loading) "" else getString(R.string.sign_in)

            val colorRes =
                if (state is AuthViewModel.State.InvalidInput) R.color.error
                else if (binding.editTextToken.hasFocus()) R.color.blue
                else R.color.gray
            val heightDp = if (colorRes == R.color.gray) 1 else 2
            setUnderline(colorRes, heightDp)

            val hintColorRes =
                if (state is AuthViewModel.State.InvalidInput) R.color.error
                else if (binding.editTextToken.hasFocus()) R.color.blue
                else R.color.white_50_percent
            val hintColor = ContextCompat.getColor(requireContext(), hintColorRes)
            val colorStateList = ColorStateList.valueOf(hintColor)
            binding.inputLayoutToken.defaultHintTextColor = colorStateList
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    when (action) {
                        is AuthViewModel.Action.ShowError -> {
                            showAlert(action.message)
                        }

                        is AuthViewModel.Action.RouteToMain -> {
                            val directions =
                                AuthFragmentDirections.actionAuthFragmentToRepositoriesListFragment()
                            findNavController().navigate(directions)
                        }
                    }
                }
            }
        }
        binding.buttonSignIn.setOnClickListener {
            viewModel.onSignButtonPressed()
        }
    }

    private fun setUnderline(@ColorRes colorResId: Int, heightDp: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        binding.viewTokenUnderline.setBackgroundColor(color)
        binding.viewTokenUnderline.layoutParams = binding.viewTokenUnderline.layoutParams.apply {
            height = (heightDp * resources.displayMetrics.density).toInt()
        }
        binding.viewTokenUnderline.requestLayout()
    }

    private fun showAlert(message: String) {
        val formattedMessage = getString(R.string.error_message, message)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setTitle(getString(R.string.error_title))
            .setMessage(formattedMessage)
            .setPositiveButton(getString(R.string.ok_button), null)
            .show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.blue)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}