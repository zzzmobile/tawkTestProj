package com.test.tawktest.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.test.tawktest.R
import com.test.tawktest.di.Injection
import com.test.tawktest.model.GitUser
import com.test.tawktest.viewmodel.GitUserListViewModel
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.activity_users.progressBar


/**
 * user list activity
 */
class GitUsersActivity : AppCompatActivity() {

    private lateinit var listViewModel: GitUserListViewModel
    private lateinit var adapter: GitUserAdapter

    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    var currentPage: Int = 0    // last user's id in user list
    var currentSearchText: String = ""      // search text

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        // connectivity manager for checking network available
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {    // network available
                    Snackbar.make(
                        contentView,
                        getString(R.string.network_connected),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    runOnUiThread {
                        onResume()
                    }
                }

                override fun onLost(network: Network) {     // network unavailable
                    Snackbar.make(
                        contentView,
                        getString(R.string.network_unavailable),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        )

        setupViewModel()
        setupUI()

        // load users
        currentPage = 0
        listViewModel.clearUsers()
        listViewModel.loadGitUsers(0)
    }

    // view
    private fun setupUI() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.hide()

        // search bar textchange listener
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

        adapter = GitUserAdapter(listViewModel.searchUsers.value ?: emptyList(), listViewModel.liveDataNote?.value ?: emptyList())
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter
        rvUsers.addOnScrollListener(object :
            PaginationScrollListener(rvUsers.layoutManager as LinearLayoutManager) {
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
        // item click listener for recyclerview
        rvUsers.addOnItemTouchListener(RecyclerTouchListener(this, object : ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        listViewModel.searchUsers.value?.let { onUserDetails(it.get(position)) }
                    }
                })
        )
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
        adapter.update(it)
        listViewModel.searchUserWithName(currentSearchText)
        // load notes
        listViewModel.getAllUserNotes(this)!!.observe(this, Observer { notes ->
            adapter.updateNotes(notes)
        })
    }

    private val isViewLoadingObserver = Observer<Boolean> {
        val visibility = if (it) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    private val onMessageErrorObserver = Observer<Any> {
        Snackbar.make(
            contentView,
            getString(R.string.error_fetch_users),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private val emptyListObserver = Observer<Boolean> {
        Snackbar.make(
            contentView,
            getString(R.string.no_users),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    // load users from github
    fun loadMoreUsers() {
        if (listViewModel.users.value.isNullOrEmpty())
            return

        val user = listViewModel.users.value?.last() as GitUser
        currentPage = user.id
        isLoading = false
        listViewModel.loadGitUsers(currentPage)
    }

    fun onUserDetails(user: GitUser) {
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