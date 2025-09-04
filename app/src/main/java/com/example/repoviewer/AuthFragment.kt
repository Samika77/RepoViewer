package com.example.repoviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import androidx.core.view.updateLayoutParams
import com.example.repoviewer.databinding.AuthFragmentBinding

class AuthFragment : Fragment() {
    private var _binding: AuthFragmentBinding? = null
    private val binding get() = _binding!!

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

        binding.buttonSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_repositoriesListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}