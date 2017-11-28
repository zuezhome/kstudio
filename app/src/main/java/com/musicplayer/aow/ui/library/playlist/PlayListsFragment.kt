package com.musicplayer.aow.ui.library.playlist


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.musicplayer.aow.R
import com.joaquimley.faboptions.FabOptions




class PlayListsFragment : Fragment(){

    @BindView(R.id.recycler_view)
    internal var recyclerView: RecyclerView? = null
    @BindView(R.id.progress_bar)
    internal var progressBar: ProgressBar? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_play_lists, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)

        //Fabb Option
//        val fabOptions = view.findViewById(R.id.fab_options) as FabOptions
//        fabOptions.setButtonsMenu(R.menu.fabb_option)

    }


    private var mListener: onFragmentInteractionListener? = null

    interface onFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is onFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
}
