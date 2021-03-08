package com.test.tawktest.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.tawktest.R
import com.test.tawktest.di.Injection
import com.test.tawktest.model.GitUser
import com.test.tawktest.viewmodel.GitUserListViewModel
import kotlinx.android.synthetic.main.activity_users.*

/**
 * user list activity
 */
class GitUsersActivity : AppCompatActivity(), OnUserItemClickListener {

    private lateinit var listViewModel: GitUserListViewModel
    private lateinit var adapter: GitUserAdapter

    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    var currentPage: Int = 0
    var currentSearchText: String = ""

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

        searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //SEARCH FILTER
                currentSearchText = charSequence.toString()
                listViewModel.searchUserWithName(currentSearchText)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        adapter = GitUserAdapter(listViewModel.searchUsers.value ?: emptyList(), this)
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
        listViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory()
        ).get(GitUserListViewModel::class.java)

        listViewModel.searchUsers.observe(this, renderUsers)
        listViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        listViewModel.onMessageError.observe(this, onMessageErrorObserver)
        listViewModel.isEmptyList.observe(this, emptyListObserver)
    }

    //observers
    private val renderUsers = Observer<List<GitUser>> {
        Log.v(TAG, "data updated $it")
//        layoutError.visibility = View.GONE
//        layoutEmpty.visibility = View.GONE
        adapter.update(it)
        listViewModel.searchUserWithName(currentSearchText)
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
        listViewModel.clearUsers()
        listViewModel.loadGitUsers(0)
    }

    companion object {
        const val TAG = "CONSOLE"
    }

    // load users from github
    fun loadMoreUsers() {
        val user = listViewModel.users.value?.last() as GitUser
        currentPage = user.id
        isLoading = false
        listViewModel.loadGitUsers(currentPage)
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