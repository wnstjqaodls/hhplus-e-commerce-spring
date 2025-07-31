package ecommerce.point.domain;

import java.util.List;

public class ActivityWindow {

    private List<Activity> activities;

    public ActivityWindow(List<Activity> activities) {
        this.activities = activities;
    }

    public long calculateBalance() {
        return activities.stream()
                .mapToLong(Activity::getAmount)
                .sum();
    }
    
    public void addActivity(Activity activity) {
        activities.add(activity);
    }
    
    public List<Activity> getActivities() {
        return activities;
    }
}
