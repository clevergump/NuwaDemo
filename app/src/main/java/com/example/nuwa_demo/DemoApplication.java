package com.example.nuwa_demo;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import cn.jiajixin.nuwa.Nuwa;

public class DemoApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Nuwa.init(this);
        Nuwa.loadPatch(this, getPatchFilePath());
    }

    private String getPatchFilePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath().concat("/NuwaDemoPatch.jar");
    }
}
