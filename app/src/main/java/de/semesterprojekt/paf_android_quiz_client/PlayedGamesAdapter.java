package de.semesterprojekt.paf_android_quiz_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.semesterprojekt.paf_android_quiz_client.model.PlayedGames;
import de.semesterprojekt.paf_android_quiz_client.model.ServerData;
import de.semesterprojekt.paf_android_quiz_client.model.SessionManager;

/**
 * Adapter class for recyclerview in the Profile Activity Layout
 */
public class PlayedGamesAdapter extends RecyclerView.Adapter<PlayedGamesAdapter.PlayedGamesViewHolder> {
    Context context;
    ArrayList<PlayedGames> playedGamesArrayList;
    SessionManager sessionManager;
    public PlayedGamesAdapter(Context context, ArrayList<PlayedGames> playedGamesArrayList) {
        this.context = context;
        this.playedGamesArrayList = playedGamesArrayList;
        sessionManager = new SessionManager(context);

    }

    @NonNull
    @Override
    public PlayedGamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_history, parent, false);
        return new PlayedGamesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayedGamesViewHolder holder, int position) {
        PlayedGames current = playedGamesArrayList.get(position);

        String date = Helper.formatDate(current.getTimeStamp()) + " Uhr";
        String username = sessionManager.getUserDatafromSession().get(context.getString(R.string.username));
        String userscore = Integer.toString(current.getUserScore());
        String urlUserImage = ServerData.PROFILE_IMAGE_API + username;
        String opponentname = current.getOpponent().getUserName();
        String opponentscore = Integer.toString(current.getOpponentScore());
        String urlOpponentImage = ServerData.PROFILE_IMAGE_API + opponentname;

        holder.tv_historyDate.setText(date);
        holder.tv_historyUserName.setText(username);
        holder.tv_historyUserScore.setText(userscore);
        Picasso.get().load(urlUserImage).fit().centerInside().into(holder.iv_historyUserImage);
        holder.tv_historyOpponentName.setText(opponentname);
        holder.tv_historyOpponentScore.setText(opponentscore);
        Picasso.get().load(urlOpponentImage).fit().centerInside().into(holder.iv_historyOpponentImage);

    }

    @Override
    public int getItemCount() {
        return playedGamesArrayList.size();
    }

    /**
     * Holds the view from the PlayedGames List Layout
     */
    public static class PlayedGamesViewHolder extends RecyclerView.ViewHolder {
        TextView tv_historyDate, tv_historyUserName, tv_historyUserScore,
                tv_historyOpponentName, tv_historyOpponentScore;
        ShapeableImageView iv_historyUserImage, iv_historyOpponentImage;

        public PlayedGamesViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_historyDate = itemView.findViewById(R.id.tv_history_date);
            tv_historyUserName = itemView.findViewById(R.id.tv_history_user_name);
            tv_historyUserScore = itemView.findViewById(R.id.tv_history_user_score);
            tv_historyOpponentName = itemView.findViewById(R.id.tv_history_opponent_name);
            tv_historyOpponentScore = itemView.findViewById(R.id.tv_history_opponent_score);
            iv_historyUserImage = itemView.findViewById(R.id.iv_history_user_image);
            iv_historyOpponentImage = itemView.findViewById(R.id.iv_history_opponent_image);
        }
    }
}

