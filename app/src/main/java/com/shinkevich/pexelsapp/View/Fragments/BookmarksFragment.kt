package com.shinkevich.pexelsapp.View.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shinkevich.pexelsapp.R
import com.shinkevich.pexelsapp.View.Adapters.BookmarksRecyclerViewAdapter
import com.shinkevich.pexelsapp.View.Adapters.SpacesItemDecoration
import com.shinkevich.pexelsapp.ViewModel.BookmarksViewModel
import com.shinkevich.pexelsapp.ViewModel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class BookmarksFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmarks, container, false)
    }

    val viewModel by viewModels<BookmarksViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var imagesAdapter: BookmarksRecyclerViewAdapter
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var exploreTextView : TextView
    private lateinit var noBookmarksSavedStub : View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingProgressBar = requireView().findViewById(R.id.loadingProgressBarBookmarks)
        exploreTextView = requireView().findViewById(R.id.exploreTextViewBookmarks)
        noBookmarksSavedStub = requireView().findViewById(R.id.noResultsStubBookmarks)

        imagesRecyclerView = requireView().findViewById(R.id.imagesRecyclerViewBookmarks)
        imagesRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        imagesRecyclerView.addItemDecoration(
            SpacesItemDecoration(
                requireView().resources.getDimension(R.dimen.image_spacing).roundToInt()
            )
        )
        imagesAdapter = BookmarksRecyclerViewAdapter(mutableListOf(), viewModel)
        imagesRecyclerView.adapter = imagesAdapter

        viewModel.bookmarks.observe(viewLifecycleOwner) { bookmarksList ->
            if (bookmarksList.isNotEmpty()) {
                noBookmarksSavedStub.visibility = View.GONE
                imagesAdapter.setBookmarksList(bookmarksList)
            } else {
                noBookmarksSavedStub.visibility = View.VISIBLE
                viewModel.updateDataIsLoading(false)
            }

        }

        viewModel.dataIsLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                loadingProgressBar.visibility = View.VISIBLE
            } else {
                loadingProgressBar.visibility = View.GONE
            }
        }

        viewModel.imageForDetails.observe(viewLifecycleOwner){imageId ->
            if (!viewModel.imageForDetailsHandled){
                viewModel.imageForDetailsHandled = true
                val bundle = Bundle()
                bundle.putLong(IMAGE_ID_PARAM, imageId)
                bundle.putBoolean(SEARCH_IMAGE_LOCALLY_PARAM, true)
                findNavController().navigate(R.id.detailsFragment, bundle)
            }
        }

        viewModel.getBookmarks()

        exploreTextView.setOnClickListener { view ->
            sharedViewModel.searchPopularImages = true
            view.findNavController().navigate(R.id.homeFragment)
        }
    }
}