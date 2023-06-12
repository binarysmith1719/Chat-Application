package com.codezilla.chatapp.ImageCompression;

import android.app.Activity;
import android.content.Context;

public interface ImageCompressionCompletionListener {
    public void uploadOnCompression();
    void getProgress(int p);
}
