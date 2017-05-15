package fourthquadrant.chileme;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Created by Euphoria on 2017/5/15.
 */

@Aspect
public class AspectTest {
    private static final String TAG = "xuyisheng";

    @Before("execution(* fourthquadrant.chileme.Activity.**Activity.on**(..))")
    public void onActivityMethodBefore(JoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "onActivityMethodBefore: " + key);
    }



}
