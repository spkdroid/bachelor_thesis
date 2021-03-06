package hochschule.de.bachelorthesis.view.food;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import hochschule.de.bachelorthesis.R;
import hochschule.de.bachelorthesis.adapter.AdapterMeasurements;
import hochschule.de.bachelorthesis.databinding.FragmentMeasurementListBinding;
import hochschule.de.bachelorthesis.enums.MeasurementType;
import hochschule.de.bachelorthesis.enums.SortType;
import hochschule.de.bachelorthesis.loadFromDb.MeasurementObject;
import hochschule.de.bachelorthesis.room.tables.Food;
import hochschule.de.bachelorthesis.room.tables.Measurement;
import hochschule.de.bachelorthesis.room.tables.UserHistory;
import hochschule.de.bachelorthesis.utility.MySnackBar;
import hochschule.de.bachelorthesis.utility.Samples;
import hochschule.de.bachelorthesis.viewmodels.FoodViewModel;

/**
 * View for the measurement list.
 * <p>
 * All existing measurements for the selected food will be displayed here.
 * <p>
 * The user is able to create a new measurement by clicking on the floating action button.
 * <p>
 * Also, the list can be sorted, either alphanumeric or for the GI value.
 * <p>
 * The user is able to delete all measurements in this list by pressing the Delete all measurements
 * in the action bar.
 * <p>
 * KNOWN BUG:
 * Sometimes, if clicking too fast and creating new measurements, the text size of a random
 * measurement may lowers.
 */
public class MeasurementListFragment extends Fragment {

    private AdapterMeasurements mAdapter;

    private FoodViewModel mViewModel;

    private int mFoodId;

    private List<MeasurementObject> mAllMeasurementObjects = new ArrayList<>();


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View model
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(FoodViewModel.class);

        setHasOptionsMenu(true);

        // get passed food id
        assert getArguments() != null;
        mFoodId = getArguments().getInt("food_id");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Init data binding
        FragmentMeasurementListBinding mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_measurement_list, container, false);
        mBinding.setLifecycleOwner(getViewLifecycleOwner());
        mBinding.setViewModel(mViewModel);

        // RecyclerView
        final RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setHasFixedSize(true);

        NavController navController = Navigation
                .findNavController(Objects.requireNonNull(getActivity()), R.id.main_activity_fragment_host);

        mAdapter = new AdapterMeasurements(getContext(), navController);
        recyclerView.setAdapter(mAdapter);

        handleListeners();

        // fab
        Bundle bundle = new Bundle();
        bundle.putInt("food_id", mFoodId);
        mBinding.add.setOnClickListener(Navigation
                .createNavigateOnClickListener(R.id.action_foodInfoFragment_to_addMeasurement, bundle));

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.measurement_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_gi:
                mViewModel.updateFoodListModel(SortType.GI);
                sortMeasurementObjectsAndPassResultToAdapter(mViewModel.getFoodListModel().getSortType());
                return true;

            case R.id.sort_glucose_max:
                mViewModel.updateFoodListModel(SortType.GLUCOSE_MAX);
                sortMeasurementObjectsAndPassResultToAdapter(mViewModel.getFoodListModel().getSortType());
                return true;

            case R.id.add_unfinished_gi_measurement:
                createTemplateMeasurement(false, null);
                return true;

            case R.id.add_low_gi_measurement:
                createTemplateMeasurement(true, MeasurementType.LOW);
                return true;

            case R.id.add_mid_gi_measurement:
                createTemplateMeasurement(true, MeasurementType.MID);
                return true;

            case R.id.add_high_gi_measurement:
                createTemplateMeasurement(true, MeasurementType.HIGH);
                return true;

            case R.id.add_ref_gi_measurement:
                createTemplateMeasurement(true, MeasurementType.REF);
                return true;

            case R.id.delete_measurements:
                deleteMeasurements();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleListeners() {
        // LOAD ALL MEASUREMENTS
        mViewModel.getAllMeasurementsById(mFoodId).observe(getViewLifecycleOwner(), new Observer<List<Measurement>>() {
            @Override
            public void onChanged(final List<Measurement> measurements) {

                mAllMeasurementObjects.clear();

                // LOAD REF PRODUCT
                final LiveData<Food> ldf = mViewModel.getFoodByFoodNameAndBrandName("Glucose", "Reference Product");
                ldf.observe(getViewLifecycleOwner(), new Observer<Food>() {
                    @Override
                    public void onChanged(Food refFood) {
                        ldf.removeObserver(this);

                        // Add a header element and set it to the start on the list,
                        // so the adapter can use index 0 and build a header line with.
                        measurements.add(0, Samples.getEmptyMeasurement());

                        if (refFood == null) {
                            for (Measurement m : measurements) {
                                mAllMeasurementObjects.add(new MeasurementObject(m, null));
                            }

                            sortMeasurementObjectsAndPassResultToAdapter(mViewModel.getMeasurementListModel().getSortType());
                            return;
                        }

                        // LOAD REF MEASUREMENTS
                        final LiveData<List<Measurement>> ldm = mViewModel.getAllMeasurementsById(refFood.id);
                        ldm.observe(getViewLifecycleOwner(), new Observer<List<Measurement>>() {
                            @Override
                            public void onChanged(List<Measurement> refMeasurements) {
                                ldm.removeObserver(this);

                                Measurement.removeNonGiMeasurements(refMeasurements);

                                if (refMeasurements.size() == 0) {
                                    for (Measurement m : measurements) {
                                        mAllMeasurementObjects.add(new MeasurementObject(m, null));
                                    }

                                    sortMeasurementObjectsAndPassResultToAdapter(mViewModel.getMeasurementListModel().getSortType());
                                    return;
                                }

                                for (Measurement m : measurements) {
                                    mAllMeasurementObjects.add(new MeasurementObject(m, refMeasurements));
                                    sortMeasurementObjectsAndPassResultToAdapter(mViewModel.getMeasurementListModel().getSortType());
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Creates a random GI measurement
     *
     * @param finished - Measurement unfinished or not
     */
    private void createTemplateMeasurement(final boolean finished, final MeasurementType type) {

        // Load all measurements to check later if all has been finished
        final LiveData<List<Measurement>> ldMeasurements = mViewModel.getAllMeasurements();
        ldMeasurements.observe(getViewLifecycleOwner(), new Observer<List<Measurement>>() {
            @Override
            public void onChanged(List<Measurement> allMeasurements) {
                ldMeasurements.removeObserver(this);

                // Only insert if no other measurement is active right now
                for (Measurement m : allMeasurements) {
                    if (m.isActive()) {
                        snackBar("Cannot add measurement because another one is still active!");
                        return;
                    }
                }

                // Load user data
                final LiveData<UserHistory> ldu = mViewModel.getUserHistoryLatest();
                ldu.observe(getViewLifecycleOwner(), new Observer<UserHistory>() {
                    @Override
                    public void onChanged(final UserHistory userHistory) {
                        ldu.removeObserver(this);

                        // Leave if no user data has been set yet
                        if (userHistory == null) {
                            snackBar("Enter user data first!");
                            return;
                        }

                        // Load the food
                        final LiveData<Food> ldf = mViewModel.getFoodById(mFoodId);
                        ldf.observe(getViewLifecycleOwner(), new Observer<Food>() {
                            @Override
                            public void onChanged(Food food) {
                                ldf.removeObserver(this);

                                Measurement templateMeasurement;

                                // Create either an unfinished or a finished measurement
                                // If food is ref product use another sample to get higher values
                                if (finished) {
                                    templateMeasurement = Samples.getRandomMeasurement(
                                            Objects.requireNonNull(getContext()), mFoodId, userHistory.id, true, type, food.getFoodName());
                                } else {
                                    templateMeasurement = Samples
                                            .getRandomMeasurementUnfinished(Objects.requireNonNull(getContext()), mFoodId,
                                                    userHistory.id, true, food.getFoodName());
                                }

                                mViewModel.insertMeasurement(templateMeasurement);
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Deletes all current measurements for the food.
     */
    private void deleteMeasurements() {
        final LiveData<Food> ldf = mViewModel.getFoodById(mFoodId);
        ldf.observe(getViewLifecycleOwner(), new Observer<Food>() {
            @Override
            public void onChanged(Food food) {
                ldf.removeObserver(this);

                new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                        .setTitle("Delete Confirmation")
                        .setMessage(
                                "You are about to delete this measurement.\n\nIt cannot be restored at a later time!\n\nContinue?")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(android.R.string.yes, new OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (whichButton == -1) {
                                    mViewModel.deleteAllMeasurementFromFoodWithId(mFoodId);
                                    snackBar("Deleted all measurements successfully!");
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    /**
     * Create a comparator depending on how to sort the list, sort the list and pass the list to the
     * adapter, which will cause the list to be visible.
     *
     * @param sortType How to sort the list
     */

    private void sortMeasurementObjectsAndPassResultToAdapter(SortType sortType) {
        Comparator<MeasurementObject> comparator;

        // Create a comparator depending of the given parameter
        switch (sortType) {
            case GLUCOSE_MAX:
                comparator = new Comparator<MeasurementObject>() {
                    @Override
                    public int compare(MeasurementObject mo1, MeasurementObject mo2) {
                        return mo1.getGlucoseMax().compareTo(mo2.getGlucoseMax());
                    }
                };
                break;

            case GI:
                comparator = new Comparator<MeasurementObject>() {
                    @Override
                    public int compare(MeasurementObject mo1, MeasurementObject mo2) {
                        return mo1.getGi().compareTo(mo2.getGi());
                    }
                };
                break;

            default:
                throw new IllegalStateException("Unexpected state at list sorting!");
        }

        if (mAllMeasurementObjects == null || mAllMeasurementObjects.size() == 0) {
            return;
        }

        // Remove the first element (header), sort the list and put the header back in
        MeasurementObject tmp = mAllMeasurementObjects.get(0);
        mAllMeasurementObjects.remove(0);

        Collections.sort(mAllMeasurementObjects, comparator);

        mAllMeasurementObjects.add(0, tmp);

        mAdapter.setMeasurementObjects(mAllMeasurementObjects);
    }

    /**
     * Helper function for faster SnackBar creation
     *
     * @param msg The message to display in the SnackBar
     */
    private void snackBar(String msg) {
        MySnackBar.createSnackBar(Objects.requireNonNull(getContext()), Objects.requireNonNull(getView()), msg);
    }
}

