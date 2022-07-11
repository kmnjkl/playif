package com.lkjuhkmnop.textquest.playactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lkjuhkmnop.textquest.R;
import com.lkjuhkmnop.textquest.story.TwLink;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.ViewHolder> {
    private PlayActivity playActivity;
    private TwLink[] linksData;

    public LinksAdapter(PlayActivity playActivity, TwLink[] linksData) {
        this.playActivity = playActivity;
        this.linksData = linksData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button linkButton;

        public ViewHolder(View itemView) {
            super(itemView);
            linkButton = (Button) itemView.findViewById(R.id.link_button);
        }

        public void setLinkButtonText(String linkText) {
            linkButton.setText(linkText);
        }
    }

    public void setData(TwLink[] newData) {
        linksData = newData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item, parent, false);
        return new LinksAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TwLink link = linksData[position];
        holder.setLinkButtonText(link.name + " | " + link.pid + " : " + link.link);
        holder.linkButton.setOnClickListener(v -> {
            playActivity.goByLinkPosition(position);
        });
    }

    @Override
    public int getItemCount() {
        return linksData == null ? 0 : linksData.length;
    }

    public void cleanData() {
        linksData = null;
    }
}
