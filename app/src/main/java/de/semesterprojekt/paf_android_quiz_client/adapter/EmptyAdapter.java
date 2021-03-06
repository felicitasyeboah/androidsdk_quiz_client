package de.semesterprojekt.paf_android_quiz_client.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * EmptyAdapter class to assing to the recyclerview to avoid "no adapter attached "
 * warning when starting recyclerview
 */
public class EmptyAdapter extends RecyclerView.Adapter<EmptyAdapter.EmptyHolder> {
    @Override
    public EmptyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(EmptyHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
}