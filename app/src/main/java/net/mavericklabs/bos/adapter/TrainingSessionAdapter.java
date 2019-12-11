/*
 * Copyright (c) 2019. Maverick Labs
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as,
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package net.mavericklabs.bos.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.activity.TrainingSessionActivity;
import net.mavericklabs.bos.object.Day;
import net.mavericklabs.bos.object.TrainingSession;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_TRAINING_SESSION;

public class TrainingSessionAdapter extends RecyclerView.Adapter<TrainingSessionAdapter.TrainingSessionViewHolder> {
    private Context context;
    private Day day;

    public TrainingSessionAdapter(Context context, Day day) {
        this.context = context;
        this.day = day;
    }

    @NonNull
    @Override
    public TrainingSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_training_session, parent, false);
        return new TrainingSessionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingSessionViewHolder holder, int position) {
        final TrainingSession trainingSession = this.day.getSessions().get(position);
        holder.getLabel().setText(trainingSession.getLabel());
        holder.getImageViewOpen().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, TrainingSessionActivity.class);
                intent.putExtra(BUNDLE_KEY_TRAINING_SESSION,trainingSession);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return day.getSessions().size();
    }

    class TrainingSessionViewHolder extends RecyclerView.ViewHolder {

        private final TextView label;
        private final ImageView imageView, expandedImageView;
        private final ImageView imageViewOpen;

        TrainingSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.text_view_label);
            imageView = itemView.findViewById(R.id.image_view);
            imageViewOpen = itemView.findViewById(R.id.image_view_open);
            expandedImageView = itemView.findViewById(R.id.image_view_expand);
        }

        TextView getLabel() {
            return label;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public ImageView getExpandedImageView() {
            return expandedImageView;
        }

        public ImageView getImageViewOpen() {
            return imageViewOpen;
        }
    }

}


