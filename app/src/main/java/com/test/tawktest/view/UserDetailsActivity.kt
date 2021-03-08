package com.test.tawktest.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.test.tawktest.R
import com.test.tawktest.di.Injection
import com.test.tawktest.model.GitUser
import com.test.tawktest.viewmodel.GitUserViewModel
import kotlinx.android.synthetic.main.activity_user_details.*

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: GitUserViewModel
    private lateinit var context: Context

    private lateinit var username: String
    private lateinit var strNote: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        context = this@UserDetailsActivity

        // extract username
        username = ""
        intent.getStringExtra("username").also {
            if (it != null) {
                username = it
            }
        }

        setupViewModel()
        setupUI()
    }

    // view
    private fun setupUI() {
        viewModel.isViewLoading.observe(this, isViewLoadingObserver)

        // title
        supportActionBar?.title = username
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // load notes
        viewModel.getUserNote(context, username)!!.observe(this, Observer {

            if (it != null) {
                txtNotes.setText(it.note)
            }
        })

        // save note button
        btnSave.setOnClickListener {
            // save notes
            strNote = txtNotes.text.toString().trim()

            if (strNote.isEmpty()) {
                txtNotes.error = context.getString(R.string.error_input_note)
            } else {
                viewModel.insertData(context, username, strNote)
                Toast.makeText(context, context.getText(R.string.success_input_note), Toast.LENGTH_SHORT).show()
            }

        }
    }

    // view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory()
        ).get(GitUserViewModel::class.java)

        viewModel.user.observe(this, renderUser)
        viewModel.isViewLoading.observe(this, isViewLoadingObserver)
        viewModel.onMessageError.observe(this, onMessageErrorObserver)
    }

    //observers
    private val renderUser = Observer<GitUser> {
        Log.v(GitUsersActivity.TAG, "data updated $it")
//        layoutError.visibility = View.GONE
//        layoutEmpty.visibility = View.GONE
        if (it != null) {
            Glide.with(ivUser.context).load(it.avatar_url).into(ivUser)
            txtName.text = it.name
            txtCompany.text = it.company
            txtBlog.text = it.blog

            txtFollowers.text = it.followers.toString()
            txtFollowing.text = it.following.toString()
        }
    }

    private val isViewLoadingObserver = Observer<Boolean> {
        Log.v(GitUsersActivity.TAG, "isViewLoading $it")
        val visibility = if (it) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    private val onMessageErrorObserver = Observer<Any> {
        Log.v(GitUsersActivity.TAG, "onMessageError $it")
//        layoutError.visibility = View.VISIBLE
//        layoutEmpty.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadGitUser(username)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}