package cf.nearby.nearby.util;

import java.io.Serializable;

import cf.nearby.nearby.obj.Patient;

/**
 * Created by tw on 2017. 10. 1..
 */

public interface OnSearchPatientSupport extends Serializable {

    void onPatientSelected(Patient patient);

}
