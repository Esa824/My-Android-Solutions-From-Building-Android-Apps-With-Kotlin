package com.esa.buildingprimaryandsecondaryappnavigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation

class SportsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sports, container, false)
        view.findViewById<Button>(R.id.basketball)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.nav_sports_to_basketball, null)
        )
        view.findViewById<Button>(R.id.football)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.nav_sports_to_football, null)
        )
        view.findViewById<Button>(R.id.hockey)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.nav_sports_to_hockey, null)
        )

        return view
    }

}