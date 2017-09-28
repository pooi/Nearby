package cf.nearby.nearby.util;

import android.content.Intent;

/**
 * Created by tw on 2017-09-28.
 */

public interface OnAdapterSupport {
    void showView();
    void hideView();

    void redirectActivityForResult(Intent intent);

    void redirectActivity(Intent intent);
}
