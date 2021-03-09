package com.test.tawktest.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.test.tawktest.R
import com.test.tawktest.di.Injection
import com.test.tawktest.model.GitUser
import com.test.tawktest.viewmodel.GitUserViewModel
import kotlinx.android.synthetic.main.activity_user_details.*
import kotlinx.android.synthetic.main.activity_user_details.progressBar
import kotlinx.android.synthetic.main.activity_users.*

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
                Snackbar.make(
                    contentView,
                    context.getText(R.string.success_input_note),
                    Snackbar.LENGTH_SHORT
                ).show()
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
        if (it != null) {
            // set user avatar image
            Picasso.get().load(it.avatar_url).into(ivUser)
            // set user name
            txtName.text = it.name
            // set user company
            txtCompany.text = it.company
            // set user blog
            txtBlog.text = it.blog

            // followers count
            txtFollowers.text = it.followers.toString()
            // following count
            txtFollowing.text = it.following.toString()
        }
    }

    private val isViewLoadingObserver = Observer<Boolean> {
        val visibility = if (it) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    private val onMessageErrorObserver = Observer<Any> {
        Snackbar.make(
            contentView,
            getString(R.string.error_fetch_user),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()
        // load current user details
        viewModel.loadGitUser(username)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}