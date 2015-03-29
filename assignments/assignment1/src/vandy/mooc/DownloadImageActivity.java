package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import static vandy.mooc.DownloadUtils.downloadImage;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        // @@ TODO -- you fill in here.
        super.onCreate(savedInstanceState);

        // Get the URL associated with the Intent data.
        // @@ TODO -- you fill in here.
        Intent intent = getIntent();
        final String url = (intent != null) ? intent.getStringExtra("imageUrl") : "";

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.

        // @@ TODO -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.

        Thread backgroundThread = new Thread(new Runnable() {

            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                Uri downloadedImage = downloadImage(getApplicationContext(), Uri.parse(url));

                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                if (downloadedImage != null) {
                    bundle.putString("imageUrl", downloadedImage.toString());
                } else {
                    bundle.putString("imageUrl", "");
                }
                message.setData(bundle);
                handler.sendMessage(message);
            }

            Handler handler = new Handler() {

                @Override
                public void handleMessage(Message msg) {

                    String imageUrl = msg.getData().getString("imageUrl", "");
                    int result = (!imageUrl.isEmpty()) ? RESULT_OK : RESULT_CANCELED;

                    Intent intent = new Intent();
                    intent.putExtra("imageUrl", imageUrl);
                    setResult(result, intent);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            };
        });

        backgroundThread.start();
    }

}