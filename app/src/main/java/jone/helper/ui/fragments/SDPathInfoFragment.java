package jone.helper.ui.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jone.helper.R;
import jone.helper.bean.DeviceInfo;
import jone.helper.ui.activities.JoneAppManagerActivity;
import jone.helper.ui.fragments.base.BaseFragment;

/**
 * SD卡路径信息
 *
 * @doc android系统自身自带有存储，另外也可以通过sd卡来扩充存储空间。前者好比pc中的硬盘，后者好移动硬盘。 前者空间较小，后者空间大，但后者不一定可用。 开发应用，处理本地数据存取时，可能会遇到这些问题：
 * <p/>
 * 需要判断sd卡是否可用: 占用过多机身内部存储，容易招致用户反感，优先将数据存放于sd卡;
 * 应用数据存放路径，同其他应用应该保持一致，应用卸载时，清除数据:
 * <p/>
 * 标新立异在sd卡根目录建一个目录，招致用户反感
 * 用户卸载应用后，残留目录或者数据在用户机器上，招致用户反感
 * 需要判断两者的可用空间: sd卡存在时，可用空间反而小于机身内部存储，这时应该选用机身存储;
 * <p/>
 * 数据安全性，本应用数据不愿意被其他应用读写;
 * <p/>
 * 图片缓存等，不应该被扫描加入到用户相册等媒体库中去。
 * <p/>
 * 使用外部存储，需要的权限
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * 从API 19 / Andorid 4.4 / KITKAT开始，不再需要显式声明这两个权限，除非要读写其他应用的应用数据($appDataDir)
 * Created by jone.sun on 2015/12/11.
 * @link {http://liaohuqiu.net/cn/posts/storage-in-android/} Android存储使用参考
 * @link {http://hubingforever.blog.163.com/blog/static/1710405792012816103948907/ } Android中关于外部存储的一些重要函数
 */
public class SdPathInfoFragment extends BaseFragment<JoneAppManagerActivity> {
    private static SdPathInfoFragment instance = null;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public List<DeviceInfo> data = new ArrayList<>();

    public static SdPathInfoFragment getInstance() {
        if (instance == null) {
            instance = new SdPathInfoFragment();
        }
        return instance;
    }

    public SdPathInfoFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_sd_path_info;
    }

    @Override
    protected void findViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        initData();
        recyclerView.setAdapter(new MyAdapter(data));
    }

    /***
     * Context methods:
     * getFilesDir()
     * getCacheDir()
     * getDatabasePath()
     * getDir()
     * getNoBackupFilesDir()
     * getFileStreamPath()
     * getPackageCodePath()
     * getPackageResourcePath()
     * <p/>
     * ApplicationInfo fields:
     * dataDir
     * sourceDir
     * nativeLibraryDir
     * publicSourceDir
     * splitSourceDirs
     * splitPublicSourceDirs
     * <p/>
     * ($rootDir)
     * +- /data                -> Environment.getDataDirectory()
     * |   |
     * |   |   ($appDataDir)
     * |   +- data/com.srain.cube.sample
     * |       |
     * |       |   ($filesDir)
     * |       +- files            -> Context.getFilesDir() / Context.getFileStreamPath("")
     * |       |       |
     * |       |       +- file1    -> Context.getFileStreamPath("file1")
     * |       |   ($cacheDir)
     * |       +- cache            -> Context.getCacheDir()
     * |       |
     * |       +- app_$name        ->(Context.getDir(String name, int mode)
     * |
     * |   ($rootDir)
     * +- /storage/sdcard0     -> Environment.getExternalStorageDirectory()
     * |                       / Environment.getExternalStoragePublicDirectory("")
     * |
     * +- dir1             -> Environment.getExternalStoragePublicDirectory("dir1")
     * |
     * |   ($appDataDir)
     * +- Andorid/data/com.srain.cube.sample
     * |
     * |   ($filesDir)
     * +- files        -> Context.getExternalFilesDir("")
     * |   |
     * |   +- file1    -> Context.getExternalFilesDir("file1")
     * |   +- Music    -> Context.getExternalFilesDir(Environment.Music);
     * |   +- Picture  -> ... Environment.Picture
     * |   +- ...
     * |
     * |   ($cacheDir)
     * +- cache        -> Context.getExternalCacheDir()
     * |
     * +- ???
     */
    private void initData() {
        List<String> directories = new ArrayList<String>(){{
            add(Environment.DIRECTORY_ALARMS);
            add(Environment.DIRECTORY_DCIM);
            add(Environment.DIRECTORY_DOWNLOADS);
            add(Environment.DIRECTORY_MOVIES);
            add(Environment.DIRECTORY_MUSIC);
            add(Environment.DIRECTORY_NOTIFICATIONS);
            add(Environment.DIRECTORY_PICTURES);
            add(Environment.DIRECTORY_PODCASTS);
            add(Environment.DIRECTORY_RINGTONES);
            add("");
        }};
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            directories.add(Environment.DIRECTORY_DOCUMENTS);
        }

        List<String> externalStorageStates = new ArrayList<String>() {
            {
                add(Environment.MEDIA_BAD_REMOVAL);
                add(Environment.MEDIA_CHECKING);
                add(Environment.MEDIA_MOUNTED);
                add(Environment.MEDIA_MOUNTED_READ_ONLY);
                add(Environment.MEDIA_NOFS);
                add(Environment.MEDIA_REMOVED);
                add(Environment.MEDIA_SHARED);
                add(Environment.MEDIA_UNMOUNTABLE);
                add(Environment.MEDIA_UNMOUNTED);
            }
        };
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            externalStorageStates.add(Environment.MEDIA_UNKNOWN);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            externalStorageStates.add(Environment.MEDIA_EJECTING);
        }

        data = new ArrayList<>();
        data.add(new DeviceInfo("内部存储根目录Environment.getDataDirectory()",
                Environment.getDataDirectory().getPath()));
        data.add(new DeviceInfo("Context.getFilesDir()",
                getHostActivity().getFilesDir().getPath()));

        data.add(new DeviceInfo("Context.getCacheDir()",
                getHostActivity().getCacheDir().getPath()));

        data.add(new DeviceInfo("Context.getDatabasePath()",
                getHostActivity().getDatabasePath("db_jone").getPath()));
        data.add(new DeviceInfo("Context.getDir(joneDir)",
                getHostActivity().getDir("joneDir", Context.MODE_PRIVATE).getPath()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            data.add(new DeviceInfo("Context.getNoBackupFilesDir()",
                    getHostActivity().getNoBackupFilesDir().getPath()));
        }

        data.add(new DeviceInfo("Context.getFileStreamPath(joneDir)",
                getHostActivity().getFileStreamPath("joneDir").getPath()));
        data.add(new DeviceInfo("Context.getPackageResourcePath()",
                getHostActivity().getPackageResourcePath()));
        data.add(new DeviceInfo("Context.getPackageCodePath()",
                getHostActivity().getPackageCodePath()));

        /***
         * Environment.getExternalStorageDirectory():
         /storage/sdcard0

         // 同 $rootDir
         Environment.getExternalStoragePublicDirectory(""):
         /storage/sdcard0

         Environment.getExternalStoragePublicDirectory("folder1"):
         /storage/sdcard0/folder1
         */
        data.add(new DeviceInfo("外部存储根目录Environment.getExternalStorageDirectory()",
                Environment.getExternalStorageDirectory().getPath()));

        data.add(new DeviceInfo("Environment.getExternalStoragePublicDirectory()",
                Environment.getExternalStoragePublicDirectory("").getPath()));

        data.add(new DeviceInfo("Environment.getRootDirectory()",
                Environment.getRootDirectory().getPath()));

        data.add(new DeviceInfo("Environment.getDownloadCacheDirectory()",
                Environment.getDownloadCacheDirectory().getPath()));

        for (int i = 0; i < directories.size(); i++) {
            File file = getHostActivity().getExternalFilesDir(directories.get(i));
            if (file != null) {
                data.add(new DeviceInfo("Context.getExternalFilesDir()" + directories.get(i),
                        file.getPath()));
            }
        }

        File externalCacheDir = getHostActivity().getExternalCacheDir();
        if (externalCacheDir != null) {
            data.add(new DeviceInfo("Context.getExternalCacheDir()",
                    externalCacheDir.getPath()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] externalCacheDirs = getHostActivity().getExternalCacheDirs();
            for (int i = 0; i < externalCacheDirs.length; i++) {
                data.add(new DeviceInfo("Context.getExternalCacheDirs()" + i,
                        externalCacheDirs[i].getPath()));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            File[] externalMediaDirs = getHostActivity().getExternalMediaDirs();
            for (int i = 0; i < externalMediaDirs.length; i++) {
                data.add(new DeviceInfo("Context.getExternalMediaDirs()" + i,
                        externalMediaDirs[i].getPath()));
            }
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<DeviceInfo> data;

        public MyAdapter(List<DeviceInfo> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ViewHolder holder = new ViewHolder(LayoutInflater
                    .from(viewGroup.getContext()).inflate(R.layout.item_device_info_layout, viewGroup, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.text.setText(data.get(i).getName() + ":  " + data.get(i).getValue());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView text;

            public ViewHolder(View itemView) {
                // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }


    /**
     * 存储的用量情况
     * <p/>
     * 根据系统用户不同，所能占用的存储空间大小也有不同
     * <p/>
     * 在API level 9及其以上时，File对象的getFreeSpace()方法获取系统root用户可用空间；
     * <p/>
     * getUsableSpace()取非root用户可用空间
     * 当有多个存储可用时获取磁盘用量，根据当前系统情况选用合适的存储。
     * <p/>
     * 根据系统存储用量，合理设定app所用的空间大小；运行时，也可做动态调整。
     *
     * @param path
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static long getUsableSpace(File path) {
        if (path == null) {
            return -1;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        } else {
            if (!path.exists()) {
                return 0;
            } else {
                final StatFs stats = new StatFs(path.getPath());
                return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
            }
        }
    }

    /***
     * 特别注意, 对于外部存储，获取$cacheDir 或者 $filesDir及其下的路径
     * <p/>
     * 在API level 8 以下，或者空间不足，相关的方法获路径为空时，需要自己构造。
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File path = context.getExternalCacheDir();

            // In some case, even the sd card is mounted,
            // getExternalCacheDir will return null
            // may be it is nearly full.
            if (path != null) {
                return path;
            }
        }

        // Before Froyo or the path is null,
        // we need to construct the external cache folder ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }
}
