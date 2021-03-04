package com.test.tawktest.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.tawktest.R
import com.test.tawktest.di.Injection
import com.test.tawktest.model.GitUser
import com.test.tawktest.viewmodel.GitUserViewModel
import kotlinx.android.synthetic.main.activity_users.*

/**
 * user list activity
 */
class GitUsersActivity : AppCompatActivity(), OnUserItemClickListener {

    private lateinit var viewModel: GitUserViewModel
    private lateinit var adapter: GitUserAdapter

    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    var currentPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        setupViewModel()
        setupUI()
    }

    // view
    private fun setupUI() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.hide()
        adapter = GitUserAdapter(viewModel.users.value ?: emptyList(), this)
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter
        rvUsers.addOnScrollListener(object : PaginationScrollListener(rvUsers.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                //you have to call loadmore items to get more data
                loadMoreUsers()
            }
        })
    }

    // view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory()
        ).get(GitUserViewModel::class.java)

        viewModel.users.observe(this, renderUsers)
        viewModel.isViewLoading.observe(this, isViewLoadingObserver)
        viewModel.onMessageError.observe(this, onMessageErrorObserver)
        viewModel.isEmptyList.observe(this, emptyListObserver)
    }

    //observers
    private val renderUsers = Observer<List<GitUser>> {
        Log.v(TAG, "data updated $it")
//        layoutError.visibility = View.GONE
//        layoutEmpty.visibility = View.GONE
        adapter.update(it)
    }

    private val isViewLoadingObserver = Observer<Boolean> {
        Log.v(TAG, "isViewLoading $it")
        val visibility = if (it) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    private val onMessageErrorObserver = Observer<Any> {
        Log.v(TAG, "onMessageError $it")
//        layoutError.visibility = View.VISIBLE
//        layoutEmpty.visibility = View.GONE
//        textViewError.text = "Error $it"
    }

    private val emptyListObserver = Observer<Boolean> {
        Log.v(TAG, "emptyListObserver $it")
//        layoutEmpty.visibility = View.VISIBLE
//        layoutError.visibility = View.GONE
    }

    //If you require updated data, you can call the method "loadGitUsers" here
    override fun onResume() {
        super.onResume()
        currentPage = 0
        viewModel.clearUsers()
        viewModel.loadGitUsers(0)
    }

    companion object {
        const val TAG = "CONSOLE"
    }

    // load users from github
    fun loadMoreUsers() {
        val user = viewModel.users.value?.last() as GitUser
        currentPage = user.id
        isLoading = false
        viewModel.loadGitUsers(currentPage)
    }

    override fun onUserItemClick(user: GitUser) {
        // show user details activity
        val intent = Intent(this, UserDetailsActivity::class.java)
        intent.putExtra("username", user.login)
        startActivity(intent)
    }

    // pagination listener for user recyclerview
    abstract class PaginationScrollListener(var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
        abstract fun isLastPage(): Boolean

        abstract fun isLoading(): Boolean

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val totalItemCount = layoutManager.itemCount
            if (!isLoading() && !isLastPage()) {
                if (layoutManager.findLastCompletelyVisibleItemPosition() == totalItemCount - 1) {
                    loadMoreItems()
                }
            }
        }

        abstract fun loadMoreItems()
    }
}