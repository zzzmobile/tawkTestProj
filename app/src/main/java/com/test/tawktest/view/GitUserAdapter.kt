package com.test.tawktest.view

import android.graphics.Color
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.test.tawktest.R
import com.test.tawktest.model.GitUser
import com.test.tawktest.model.UserNoteTableModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_useritem.view.*

class GitUserAdapter(
    private var gitUsers: List<GitUser>,
    private var userNotes: List<UserNoteTableModel>?
) :
    RecyclerView.Adapter<GitUserAdapter.MViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_useritem,
            parent,
            false
        )
        return MViewHolder(view)
    }

    override fun onBindViewHolder(vh: MViewHolder, position: Int) {
        vh.bind(gitUsers[position], position, userNotes)
    }

    override fun getItemCount(): Int {
        return gitUsers.size
    }

    fun update(data: List<GitUser>) {
        gitUsers = data
        notifyDataSetChanged()
    }

    fun updateNotes(data: List<UserNoteTableModel>) {
        userNotes = data
        notifyDataSetChanged()
    }

    class MViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val avatarView: CircleImageView = view.ivUser
        private val textViewName: AppCompatTextView = view.txtUser
        private val noteMarkView: ImageView = view.imgNoteMark

        fun bind(gitUser: GitUser, position: Int, notes: List<UserNoteTableModel>?) {
            if ((position + 1) % 4 == 0) { // invert image color every fourth avatar
                val negative = floatArrayOf(
                    -1.0f, 0f, 0f, 0f, 255f,
                    0f, -1.0f, 0f, 0f, 255f,
                    0f, 0f, -1.0f, 0f, 255f,
                    0f, 0f, 0f, 1.0f, 0f
                )
                avatarView.colorFilter = ColorMatrixColorFilter(negative)
            } else {
                avatarView.setColorFilter(Color.TRANSPARENT)
            }
            // set avatar image
            Picasso.get().load(gitUser.avatar_url).into(avatarView)

            // set user login string
            textViewName.text = gitUser.login

            // check this user has note
            val matches = notes?.filter {
                it.login == gitUser.login && it.note.isNotEmpty()
            }

            if (matches != null && matches.isNotEmpty())
                noteMarkView.visibility = View.VISIBLE
            else
                noteMarkView.visibility = View.GONE
        }
    }
}
