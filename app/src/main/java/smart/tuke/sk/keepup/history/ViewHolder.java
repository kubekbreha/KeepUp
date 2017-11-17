package smart.tuke.sk.keepup.history;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import smart.tuke.sk.keepup.R;


/**
 * Created by kubek on 11/17/2017.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    CardView cv;
    TextView title;
    TextView description;
    ImageView imageView;

    public ViewHolder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView);
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
    }
}