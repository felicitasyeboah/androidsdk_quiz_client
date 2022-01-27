package de.semesterprojekt.paf_android_quiz_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import de.semesterprojekt.paf_android_quiz_client.model.Highscore;

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

        Highscore highscore = highscoreArrayList.get(position);
        holder.tv_hs_date.setText(highscore.date);
        holder.iv_hs_userImage.setImageResource(highscore.userImage);

    }

    @Override
    public int getItemCount() {
        return highscoreArrayList.size();
    }

    public static class HighscoreViewHolder extends RecyclerView.ViewHolder {

        TextView tv_hs_date;
        ShapeableImageView iv_hs_userImage;

        public HighscoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_hs_date = itemView.findViewById(R.id.tv_hs_date);
            iv_hs_userImage = itemView.findViewById(R.id.iv_hs_user_image);
        }
    }
}
