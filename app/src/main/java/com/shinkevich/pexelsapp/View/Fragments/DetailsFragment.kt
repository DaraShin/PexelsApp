package com.shinkevich.pexelsapp.View.Fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.shinkevich.pexelsapp.Model.Image
import com.shinkevich.pexelsapp.Model.RequestResult
import com.shinkevich.pexelsapp.R
import com.shinkevich.pexelsapp.ViewModel.DetailsViewModel
import com.shinkevich.pexelsapp.ViewModel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

const val IMAGE_ID_PARAM = "image_id"
const val SEARCH_IMAGE_LOCALLY_PARAM = "search_image_locally"

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private var imageId: Long? = null
    private var searchImageLocally: Boolean? = null
    private val viewModel by viewModels<DetailsViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var imageView: ImageView
    private lateinit var authorTextView: TextView
    private lateinit var downloadBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var addToBookmarksBtn: ImageButton
    private lateinit var noImageFoundStub: View
    private lateinit var imageButtonsLayout: View
    private lateinit var imageScrollView: ScrollView
    private lateinit var progresBar: ProgressBar
    private lateinit var exploreTextView: TextView

    private lateinit var image: Image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageId = it.getLong(IMAGE_ID_PARAM)
            searchImageLocally = it.getBoolean(SEARCH_IMAGE_LOCALLY_PARAM)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = requireView().findViewById(R.id.imgViewDetails)
        authorTextView = requireView().findViewById(R.id.authorTextViewDetails)
        downloadBtn = requireView().findViewById(R.id.downloadBtn)
        backBtn = requireView().findViewById(R.id.backBtn)
        imageScrollView = requireView().findViewById(R.id.imageScrollViewDetails)
        noImageFoundStub = requireView().findViewById(R.id.noImageFoundStub)
        imageButtonsLayout = requireView().findViewById(R.id.imageButtonsLayout)
        progresBar = requireView().findViewById(R.id.progressBarDetails)
        addToBookmarksBtn = requireView().findViewById(R.id.addToBookmarksBtn)
        exploreTextView = requireView().findViewById(R.id.exploreTextViewDetails)


        viewModel.photoResult.observe(viewLifecycleOwner) { photoResult ->
            when (photoResult) {
                is RequestResult.Success -> {
                    val imageToShow = photoResult.data
                    if (imageToShow != null) {

                        Glide.with(requireContext())
                            .load(imageToShow.url)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    showNoImageFoundStub()
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    progresBar.visibility = View.GONE
                                    return false
                                }

                            })
                            .into(imageView)
                        authorTextView.text = imageToShow.author
                        image = imageToShow
                    } else {
                        showNoImageFoundStub()
                    }
                }
                is RequestResult.Failure -> {
                    showNoImageFoundStub()
                }
                is RequestResult.NoInternetConnectionFailure -> {
                    showNoImageFoundStub()
                }
            }
        }

        viewModel.bookmarkResult.observe(viewLifecycleOwner) { bookmarkResult ->
            when (bookmarkResult) {
                is RequestResult.Success -> {
                    val imageToShow = bookmarkResult.data
                    if (imageToShow != null) {

                        Glide.with(requireContext())
                            .load(File(imageToShow.filePath))
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    showNoImageFoundStub()
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    progresBar.visibility = View.GONE
                                    return false
                                }

                            })
                            .into(imageView)
                        authorTextView.text = imageToShow.author
                    } else {
                        showNoImageFoundStub()
                    }
                }
                is RequestResult.Failure -> {
                    showNoImageFoundStub()
                }
                is RequestResult.NoInternetConnectionFailure -> {
                    showNoImageFoundStub()
                }
            }

        }

        exploreTextView.setOnClickListener { view ->
            sharedViewModel.searchPopularImages  = true
            view.findNavController().navigate(R.id.homeFragment)
        }

        if (imageId != null) {
            if(!searchImageLocally!!){
                viewModel.getPhoto(imageId!!)
            } else{
                viewModel.getBookmark(imageId!!)
            }

        }

        if(searchImageLocally!!){
            addToBookmarksBtn.setImageResource(R.drawable.bookmarks_icon_active)
            addToBookmarksBtn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
        }

        downloadBtn.setOnClickListener { view: View ->
            if(!searchImageLocally!!) {
                if (image != null) {
                    viewModel.downloadPhoto(image)
                }
            }
        }

        backBtn.setOnClickListener { view ->
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        addToBookmarksBtn.setOnClickListener { btnView ->
            if(!searchImageLocally!!){
                viewModel.savePhotoToBookmarks(image)
                (btnView as ImageView).setImageResource(R.drawable.bookmarks_icon_active)
                (btnView as ImageView).setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
            }
        }

    }

    private fun showNoImageFoundStub() {
        noImageFoundStub.visibility = View.VISIBLE
        imageScrollView.visibility = View.GONE
        authorTextView.visibility = View.GONE
        imageButtonsLayout.visibility = View.GONE
        progresBar.visibility = View.GONE
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: Long, param2: Boolean) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong(IMAGE_ID_PARAM, param1)
                    putBoolean(SEARCH_IMAGE_LOCALLY_PARAM, param2)
                }
            }
    }
}