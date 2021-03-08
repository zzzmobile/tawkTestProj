package com.test.tawktest.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.tawktest.R
import com.test.tawktest.model.GitUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_useritem.view.*

interface OnUserItemClickListener {
    fun onUserItemClick(user: GitUser)
}

class GitUserAdapter(private var gitUsers: List<GitUser>, private val clickListener : OnUserItemClickListener) :
    RecyclerView.Adapter<GitUserAdapter.MViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_useritem, parent, false)
        return MViewHolder(view)
    }

    override fun onBindViewHolder(vh: MViewHolder, position: Int) {
        vh.bind(gitUsers[position], clickListener)
    }

    override fun getItemCount(): Int {
        return gitUsers.size
    }

    fun update(data: List<GitUser>) {
        gitUsers = data
        notifyDataSetChanged()
    }

    class MViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val avatarView: CircleImageView = view.ivUser
        private val textViewName: AppCompatTextView = view.txtUser
        private val itemView: CardView = view.itemView

        fun bind(gitUser: GitUser, listener : OnUserItemClickListener) {
            // set avatar image
            Glide.with(avatarView.context).load(gitUser.avatar_url).into(avatarView)
            // set avatar login
            textViewName.text = gitUser.login
            itemView.setOnClickListener {
                listener.onUserItemClick(gitUser)
            }
        }
    }
}
