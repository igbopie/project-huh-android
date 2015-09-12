package com.huhapp.android.welcome;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.huhapp.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeToHuhFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeToHuhFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeToHuhFragment extends Fragment {
    public static final int SCREEN_WELCOME = 0;
    public static final int SCREEN_LOCATION = 1;
    public static final int SCREEN_NOTIFICATIONS = 2;
    public static final int SCREEN_RULES = 3;
    public static final int SCREEN_THANKS = 4;
    public static final int N_SCREENS = 5;

    private static final String SCREEN_NUMBER = "screen-number";

    // TODO: Rename and change types of parameters
    private int screenNumber;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment WelcomeToHuhFragment.
     */
    public static WelcomeToHuhFragment newInstance(int screenNumber) {
        WelcomeToHuhFragment fragment = new WelcomeToHuhFragment();
        Bundle args = new Bundle();
        args.putInt(SCREEN_NUMBER, screenNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public WelcomeToHuhFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            screenNumber = getArguments().getInt(SCREEN_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        switch (screenNumber) {
            case SCREEN_WELCOME:
                return inflater.inflate(R.layout.fragment_welcome_to_huh, container, false);
            case SCREEN_LOCATION:
                return inflater.inflate(R.layout.fragment_welcome_location, container, false);
            case SCREEN_NOTIFICATIONS:
                return inflater.inflate(R.layout.fragment_welcome_notifications, container, false);
            case SCREEN_RULES:
                return inflater.inflate(R.layout.fragment_welcome_rules, container, false);
            case SCREEN_THANKS:
                return inflater.inflate(R.layout.fragment_welcome_thanks, container, false);
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = (Button) view.findViewById(R.id.closeButton);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }
    }
}
