package com.example.kotlininstagram.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagram.databinding.RecyclerRowBinding
import com.example.kotlininstagram.model.Post
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(private val postList : ArrayList<Post>): RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedRecyclerAdapter.PostHolder {
        // bağlama işlemi
       val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedRecyclerAdapter.PostHolder, position: Int) {
        holder.binding.recyclerEmailText.text = postList.get(position).email
        holder.binding.recyclerCommentText.text = postList.get(position).comment
        // picasso kütüphanesini kullandık burada görselleri yazdırmak için
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

}