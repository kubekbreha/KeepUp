package smart.tuke.sk.keepup;

/**
 * Created by kubek on 11/3/2017.
 */

public class TabMessage {
    public static String get(int menuItemId, boolean isReselection) {
        String message = "Content for ";

        switch (menuItemId) {
            case R.id.tab_friends:
                message += "friends";
                break;
            case R.id.tab_history:
                message += "history";
                break;
            case R.id.tab_run:
                message += "run";
                break;

        }

        if (isReselection) {
            message += " WAS RESELECTED! YAY!";
        }

        return message;
    }
}
