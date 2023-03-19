package ml.sky233.suiteki.ui.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeviceViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DeviceViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}