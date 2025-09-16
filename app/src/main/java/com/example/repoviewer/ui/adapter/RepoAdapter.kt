package com.example.repoviewer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.repoviewer.data.network.Repo
import com.example.repoviewer.databinding.ItemRepositoryBinding
import com.example.repoviewer.R

class RepoAdapter : ListAdapter<Repo, RepoAdapter.RepoViewHolder>(DIFF_CALLBACK) {
    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRepositoryBinding.inflate(inflater, parent, false)
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = getItem(position)
        holder.bind(repo, onItemClickListener)
    }

    class RepoViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: Repo, clickListener: ((String) -> Unit)?) {
            binding.textRepoName.text = repo.name
            binding.textRepoLanguage.text = repo.language
            binding.textRepoDescription.text = repo.description ?: "No description"

            val color = when (repo.language.lowercase()) {
                "kotlin" -> ContextCompat.getColor(binding.root.context, R.color.purple)
                "java" -> ContextCompat.getColor(binding.root.context, R.color.red)
                "javascript" -> ContextCompat.getColor(binding.root.context, R.color.yellow)
                else -> ContextCompat.getColor(binding.root.context, R.color.white)
            }

            binding.textRepoLanguage.setTextColor(color)

            binding.root.setOnClickListener {
                clickListener?.invoke(repo.repoId)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem == newItem
        }
    }
}