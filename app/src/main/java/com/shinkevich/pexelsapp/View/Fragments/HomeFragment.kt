package com.shinkevich.pexelsapp.View.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shinkevich.pexelsapp.Model.Image
import com.shinkevich.pexelsapp.Model.RequestResult
import com.shinkevich.pexelsapp.R
import com.shinkevich.pexelsapp.View.Adapters.ImagesRecyclerViewAdapter
import com.shinkevich.pexelsapp.View.Adapters.PopularQueryRecyclerViewAdapter
import com.shinkevich.pexelsapp.View.Adapters.SpacesItemDecoration
import com.shinkevich.pexelsapp.ViewModel.HomeViewModel
import com.shinkevich.pexelsapp.ViewModel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel by viewModels<HomeViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var progressBar: ProgressBar
    private lateinit var noNetworkStub: View
    private lateinit var noResultsStub: View
    private lateinit var tryAgainTextView: TextView
    private lateinit var exploreTextView: TextView
    private lateinit var searchView: SearchView

    private lateinit var navController: NavController

    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var imagesAdapter: ImagesRecyclerViewAdapter

    private lateinit var popularQueryRecyclerView: RecyclerView
    private lateinit var popularQueryListAdapter: PopularQueryRecyclerViewAdapter

    private var searchCoroutine: Job = lifecycleScope.launch{}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tryAgainTextView = requireView().findViewById(R.id.tryAgainTextViewHome)
        exploreTextView = requireView().findViewById(R.id.exploreTextViewHome)
        progressBar = requireView().findViewById(R.id.searchImagesProgressBar)
        noNetworkStub = requireView().findViewById(R.id.noNetworkStub)
        noResultsStub = requireView().findViewById(R.id.noResultsStub)
        searchView = requireView().findViewById(R.id.imageSearchView)

        navController = requireActivity().findNavController(R.id.navHostFragment)

        imagesRecyclerView =
            requireView().findViewById(R.id.imagesRecyclerViewHome)
        imagesRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        imagesRecyclerView.addItemDecoration(
            SpacesItemDecoration(
                requireView().resources.getDimension(R.dimen.image_spacing).roundToInt()
            )
        )
        imagesAdapter = ImagesRecyclerViewAdapter(arrayListOf<Image>(), viewModel)
        imagesRecyclerView.adapter = imagesAdapter

        popularQueryRecyclerView =
            requireView().findViewById(R.id.popularQueryRecyclerView)
        val popularQueryList = mutableListOf<String>()
        popularQueryRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        popularQueryListAdapter =
            PopularQueryRecyclerViewAdapter(popularQueryList, viewModel, requireContext())
        popularQueryRecyclerView.adapter = popularQueryListAdapter
        popularQueryListAdapter.setSelectedItemPosition(viewModel.selectedPopularQueryPosition)

        viewModel.dataIsLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }

        viewModel.featuredCollections.observe(viewLifecycleOwner) { featuredCollectionsResult ->
            when (featuredCollectionsResult) {
                is RequestResult.Success -> {
                    if(featuredCollectionsResult is RequestResult.SuccessFromCache){
                        Toast.makeText(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
                        viewModel.updateDataIsLoading(false)
                    }
                    val featuredCollections = featuredCollectionsResult.data!!
                    if (featuredCollections.isNotEmpty()) {
                        popularQueryListAdapter.setPopularQueryList(featuredCollections)
                    } else {
                        Toast.makeText(
                            activity,
                            getString(R.string.no_popular_queries),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is RequestResult.Failure -> {
                    Toast.makeText(
                        activity,
                        getString(R.string.failed_popular_queries),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is RequestResult.NoInternetConnectionFailure -> {
                    viewModel.updateDataIsLoading(false)
                    imagesRecyclerView.visibility = View.GONE
                    noNetworkStub.visibility = View.VISIBLE
                    noResultsStub.visibility = View.GONE
                }
            }
        }

        viewModel.popularQuerySearch.observe(viewLifecycleOwner) { query ->
            if (!viewModel.popularQuerySearchHandled) {
                viewModel.popularQuerySearchHandled = true
                searchView.setQuery(query, true)
            }
        }

        viewModel.imagesToShowResult.observe(viewLifecycleOwner) { imagesToShowResult ->
            when (imagesToShowResult) {
                is RequestResult.Success -> {
                    if(imagesToShowResult is RequestResult.SuccessFromCache){
                        Toast.makeText(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
                        viewModel.updateDataIsLoading(false)
                    }
                    val imagesToShow = imagesToShowResult.data!!
                    if (imagesToShow.isNotEmpty()) {
                        imagesRecyclerView.visibility = View.VISIBLE
                        noNetworkStub.visibility = View.GONE
                        noResultsStub.visibility = View.GONE
                        imagesAdapter.setImageList(imagesToShow as ArrayList<Image>)
                    } else {
                        viewModel.updateDataIsLoading(false)
                        imagesRecyclerView.visibility = View.GONE
                        noNetworkStub.visibility = View.GONE
                        noResultsStub.visibility = View.VISIBLE
                    }
                }
                is RequestResult.Failure -> {
                    Toast.makeText(
                        activity,
                        getString(R.string.error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.updateDataIsLoading(false)
                }
                is RequestResult.NoInternetConnectionFailure -> {
                    viewModel.updateDataIsLoading(false)
                    imagesRecyclerView.visibility = View.GONE
                    noNetworkStub.visibility = View.VISIBLE
                    noResultsStub.visibility = View.GONE
                }
            }
        }

        viewModel.imageForDetails.observe(viewLifecycleOwner) { imageId ->
            if (!viewModel.imageForDetailsHandled) {
                viewModel.imageForDetailsHandled = true
                val bundle = Bundle()
                bundle.putLong(IMAGE_ID_PARAM, imageId)
                bundle.putBoolean(SEARCH_IMAGE_LOCALLY_PARAM, false)
                navController.navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
            }

        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    viewModel.querySearchHandled = true
                    progressBar.visibility = View.VISIBLE
                    val selectedQueryIdx = popularQueryListAdapter.setSearchQuery(query)
                    if (selectedQueryIdx != -1) {
                        (popularQueryRecyclerView.layoutManager as LinearLayoutManager).scrollToPosition(
                            selectedQueryIdx
                        )
                    }
                    viewModel.searchImages(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchCoroutine.cancel()
                if(newText != null && newText.isNotEmpty()){
                    var searchQuery = newText!!
                    searchCoroutine = CoroutineScope(Dispatchers.IO).launch(){
                        delay(5000)
                        viewModel.searchImages(searchQuery)
                    }
                }
                return false
            }

        })

        if (viewModel.isFirstLoad) {
            viewModel.isFirstLoad = false
            viewModel.getFeaturedCollections()
            viewModel.getPopularImages()
        }

        if(sharedViewModel.searchPopularImages){
            sharedViewModel.searchPopularImages = false
            viewModel.getPopularImages()
            popularQueryListAdapter.setSelectedItemPosition(-1)
            searchView.setQuery("", false)
        }

        tryAgainTextView.setOnClickListener { view ->
            if (searchView.query.isEmpty()) {
                viewModel.getPopularImages()
                viewModel.getFeaturedCollections()
            } else {
                viewModel.searchImages(searchView.query.toString())
            }
        }

        exploreTextView.setOnClickListener { view ->
            viewModel.getPopularImages()
            searchView.setQuery("", false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.selectedPopularQueryPosition = popularQueryListAdapter.getSelectedPosition()
    }

}