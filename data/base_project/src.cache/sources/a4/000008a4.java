package android.media.videoeditor;

import android.provider.MediaStore;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: VideoEditorFactory.class */
public class VideoEditorFactory {
    public static VideoEditor create(String projectPath) throws IOException {
        File dir = new File(projectPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new FileNotFoundException("Cannot create project path: " + projectPath);
            }
            if (!new File(dir, MediaStore.MEDIA_IGNORE_FILENAME).createNewFile()) {
                throw new FileNotFoundException("Cannot create file .nomedia");
            }
        }
        return new VideoEditorImpl(projectPath);
    }

    public static VideoEditor load(String projectPath, boolean generatePreview) throws IOException {
        VideoEditor videoEditor = new VideoEditorImpl(projectPath);
        if (generatePreview) {
            videoEditor.generatePreview(null);
        }
        return videoEditor;
    }
}