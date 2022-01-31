package de.semesterprojekt.paf_android_quiz_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import de.semesterprojekt.paf_android_quiz_client.util.Helper;
import de.semesterprojekt.paf_android_quiz_client.R;
import de.semesterprojekt.paf_android_quiz_client.model.Highscore;
import de.semesterprojekt.paf_android_quiz_client.config.ServerConfig;

/**
 * Adapter class for the recyclerview in the Highscore Activity Layout
 */
public class HighscoreAdapter extends RecyclerView.Adapter<HighscoreAdapter.HighscoreViewHolder> {

    Context context;
    ArrayList<Highscore> highscoreArrayList;

    public HighscoreAdapter(Context context, ArrayList<Highscore> highscoreArrayList) {
        this.context = context;
        this.highscoreArrayList = highscoreArrayList;
    }

    @NonNull
    @Override
    public HighscoreAdapter.HighscoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_highscores, parent, false);

        return new HighscoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HighscoreAdapter.HighscoreViewHolder holder, int position) {
        Highscore currentHighscore = highscoreArrayList.get(position);

        String date = Helper.formatDate(currentHighscore.getTimeStamp()) + " Uhr";
        String username = "User: " + currentHighscore.getUser().getUserName();
        String score = "Score: " + currentHighscore.getUserScore();
        String url = ServerConfig.PROFILE_IMAGE_API + currentHighscore.getUser().getUserName();
        String pos = Integer.toString(position + 1);
        holder.tv_hsPos.setText(pos);
        holder.tv_hsDate.setText(date);
        holder.tv_hsUserName.setText(username);
        holder.tv_hsUserScore.setText(score);
        Picasso.get().load(url).fit().centerInside().into(holder.iv_hsUserImage);
    }

    @Override
    public int getItemCount() {
        return highscoreArrayList.size();
    }

    /**
     * Holds the views from the Highscore List Layout
     */
    public static class HighscoreViewHolder extends RecyclerView.ViewHolder {

        TextView tv_hsDate, tv_hsUserName, tv_hsUserScore, tv_hsPos;
        CircleImageView iv_hsUserImage;


        public HighscoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_hsPos = itemView.findViewById(R.id.tv_hs_pos);
            tv_hsDate = itemView.findViewById(R.id.tv_hs_date);
            tv_hsUserName = itemView.findViewById(R.id.tv_hs_user_name);
            tv_hsUserScore = itemView.findViewById(R.id.tv_hs_user_score);
            iv_hsUserImage = itemView.findViewById(R.id.iv_hs_user_image);
        }
    }
}
