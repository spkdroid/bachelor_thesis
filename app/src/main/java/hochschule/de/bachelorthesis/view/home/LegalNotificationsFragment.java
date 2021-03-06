package hochschule.de.bachelorthesis.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import hochschule.de.bachelorthesis.R;
import hochschule.de.bachelorthesis.databinding.FragmentLegalNotificationsBinding;

/**
 * @author Maik Thielen
 * <p>
 * View class for the legal notifications.
 * <p>
 * Currently, just having some temp datas.
 */
public class LegalNotificationsFragment extends Fragment {

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Init data binding
        FragmentLegalNotificationsBinding mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_legal_notifications, container, false);
        mBinding.setLifecycleOwner(this);

        return mBinding.getRoot();
    }
}

