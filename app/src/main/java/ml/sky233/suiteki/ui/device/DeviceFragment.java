package ml.sky233.suiteki.ui.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import ml.sky233.suiteki.DeviceActivity;
import ml.sky233.suiteki.InstallActivity;
import ml.sky233.suiteki.R;
import ml.sky233.suiteki.GraphActivity;
import ml.sky233.suiteki.TipsActivity;
import ml.sky233.suiteki.adapter.AppAdapter;
import ml.sky233.suiteki.bean.AppObject;
import ml.sky233.suiteki.callback.StatusCallback;
import ml.sky233.suiteki.databinding.FragmentDeviceBinding;
import ml.sky233.suiteki.service.ble.BleService;
import ml.sky233.suiteki.service.ble.HuamiService;

import static ml.sky233.suiteki.MainApplication.TAG;
import static ml.sky233.suiteki.MainApplication.devicesList;
import static ml.sky233.suiteki.MainApplication.mService;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Objects;

public class DeviceFragment extends Fragment implements StatusCallback {

    private FragmentDeviceBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DeviceViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DeviceViewModel.class);
        BleService.setCallback(DeviceFragment.this);
        binding = FragmentDeviceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.deviceChange.setOnClickListener((v) -> {
            getActivity().startActivity(new Intent(getActivity(), DeviceActivity.class));
        });
        ArrayList<AppObject> arrayList = new ArrayList<>();
        arrayList.add(new AppObject("自定义安装", R.drawable.ic_app_install));
        arrayList.add(new AppObject("使用帮助", R.drawable.ic_app_help));
        arrayList.add(new AppObject("应用设置(未开放)", R.drawable.ic_app_setting));

        AppAdapter adapter = new AppAdapter(getContext(), arrayList);
        adapter.setOnItemClickListener((v, i) -> {
            switch (i) {
                case 0:
                    if (BleService.ble_status == HuamiService.STATUS_BLE_NORMAL) {
                        new Handler().postDelayed(() -> this.startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("application/*"), 1), 120);
//                        new Handler().postDelayed(() ->, 120);
                    } else
                        Toast.makeText(getContext(), "等待连接完毕", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    startActivity(new Intent(getContext(), TipsActivity.class));
                    break;
            }
        });
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.recycler.setAdapter(adapter);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultData == null) return;
        Log.d(TAG, resultData.getData().toString());
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            DocumentFile df = DocumentFile.fromSingleUri(requireContext(), resultData.getData());
            try {
                InstallActivity.setInputStream(requireContext().getContentResolver().openInputStream(Objects.requireNonNull(df).getUri()));
                startActivity(new Intent(getContext(), InstallActivity.class));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        setText();
    }

    public void setText() {
        if (devicesList.getList().size() == 0 | devicesList.getDeviceInfo() == null) {
            binding.deviceMac.setText("未添加设备");
            binding.deviceName.setText("尚未添加设备");
        } else if (devicesList.getDeviceInfo() != null) {
            binding.deviceName.setText(devicesList.getDeviceInfo().Name);
            binding.deviceMac.setText(devicesList.getDeviceInfo().Mac);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStatusChange(int status) {
        switch (status) {
            case HuamiService.STATUS_BLE_CONNECT_FAILURE:
            case HuamiService.STATUS_BLE_DISCONNECT:
                binding.statusImg.setImageResource(R.drawable.ic_status_ble_disconnect);
                break;
            case HuamiService.STATUS_BLE_NORMAL:
                binding.statusImg.setImageResource(R.drawable.ic_status_ble_connected);
                break;
            default:
                binding.statusImg.setImageResource(R.drawable.ic_status_ble_connecting);
        }
    }
}