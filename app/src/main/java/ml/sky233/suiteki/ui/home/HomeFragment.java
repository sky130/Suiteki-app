package ml.sky233.suiteki.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ml.sky233.suiteki.InstallActivity;
import ml.sky233.suiteki.adapter.WatchFaceAdapter;
import ml.sky233.suiteki.bean.WatchFaceObject;
import ml.sky233.suiteki.databinding.FragmentHomeBinding;
import ml.sky233.suiteki.util.Eson.EsonUtils;
import ml.sky233.suiteki.util.MsgUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        new Thread(() -> {
            Request request = new Request.Builder().url("https://sky233.ml/update/Suiteki/watchface-demo/index.json").build();
            OkHttpClient okClient = new OkHttpClient();
            try {
                Response response = okClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    handler.sendMessage(MsgUtils.build(Objects.requireNonNull(response.body()).string(), 0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return root;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Object obj = EsonUtils.getArray(EsonUtils.toObject((String) msg.obj), "watchfaces");
                    ArrayList<WatchFaceObject> list = new ArrayList<>();
                    for (int i = 0; i < EsonUtils.getArrayLength(obj); i++) {
                        Object object = EsonUtils.getArrayObject(obj, i);
                        list.add(new WatchFaceObject(EsonUtils.getObjectText(object, "title"),
                                EsonUtils.getObjectText(object, "img"),
                                EsonUtils.getObjectText(object, "user"),
                                EsonUtils.getObjectText(object, "url")));
                    }
                    WatchFaceAdapter adapter = new WatchFaceAdapter(getContext(), list);
                    adapter.setOnItemClickListener((v, i) -> {
                        Toast.makeText(getContext(), "正在下载,待会会自动安装", Toast.LENGTH_SHORT).show();
                        new Thread(() -> {
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(list.get(i).url)
                                    .build();
                            Response response = null;
                            try {
                                response = client.newCall(request).execute();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            ResponseBody body = response.body();
                            try {
                                byte[] bytes = body.bytes();
                                handler.sendMessage(MsgUtils.build(bytes, 1));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            response.close();
                            body.close();
                        }).start();
                    });
                    binding.recycler.setAdapter(adapter);
                    break;
                case 1:
                    InstallActivity.bytes = (byte[]) msg.obj;
                    startActivity(new Intent(getActivity(), InstallActivity.class));
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}