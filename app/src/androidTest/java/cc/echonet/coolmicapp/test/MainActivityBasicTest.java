package cc.echonet.coolmicapp.test;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.echonet.coolmicapp.MainActivity;
import cc.echonet.coolmicapp.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityBasicTest {

    private static final String STRING_LISTENER = "N/A(N/A)";
    private static final String STRING_TIMER = "00:00:00";

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Test
    public void basicFields_Test() {
        activityRule.getScenario().onActivity(activity -> executor.execute(() -> {
            onView(withId(R.id.txtListeners)).check(matches(withText(STRING_LISTENER)));
            onView(withId(R.id.timerValue)).check(matches(withText(STRING_TIMER)));
            onView(withId(R.id.start_recording_button)).check(matches(withText("START LIVESTREAM")));
            onView(withId(R.id.start_recording_button)).perform(click());
            onView(withId(R.id.start_recording_button)).check(matches(withText("START LIVESTREAM")));
        }));
    }
}
