package hochschule.de.bachelorthesis.fragments_main_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import hochschule.de.bachelorthesis.R;
import hochschule.de.bachelorthesis.activities.AddFoodActivity;
import hochschule.de.bachelorthesis.adapter.FoodAdapter;
import hochschule.de.bachelorthesis.lifecycle.ActivityMainObserver;
import hochschule.de.bachelorthesis.lifecycle.FragmentFoodObserver;
import hochschule.de.bachelorthesis.room.Food;
import hochschule.de.bachelorthesis.view_model.FoodViewModel;

public class FoodFragment extends Fragment {

    private FoodViewModel foodViewModel;
    private FloatingActionButton fab;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle("My Food");
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_food, container, false);

        fab = rootView.findViewById(R.id.button_add_note);

        getLifecycle().addObserver(new FragmentFoodObserver());

        // RecyclerView
        RecyclerView rv = rootView.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);

        // Adapter
        NavController navController = Navigation.findNavController(getActivity(), R.id.main_activity_fragment);
        final FoodAdapter adapter = new FoodAdapter(getContext(), navController);
        rv.setAdapter(adapter);

        // View model
        foodViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);

        foodViewModel.getAllFood().observe(this, new Observer<List<Food>>() {
            @Override
            public void onChanged(List<Food> foods) {
                adapter.setFoods(foods);
            }
        });

       return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_main_activity_food_fragment_to_addFoodActivity));
    }
}

