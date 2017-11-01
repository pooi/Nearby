package cf.nearby.nearby.util;

import java.io.Serializable;

import cf.nearby.nearby.obj.Patient;

/**
 * Created by tw on 2017. 10. 31..
 */

public interface SearchPatientSupporter extends Serializable {
    void redirectNextActivity(Patient patient);
}
