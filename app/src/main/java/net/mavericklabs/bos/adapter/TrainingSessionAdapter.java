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
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.activity.TrainingSessionActivity;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.Day;
import net.mavericklabs.bos.object.TrainingSession;
import net.mavericklabs.bos.utils.ActivityMode;
import net.mavericklabs.bos.utils.EvaluationResourceType;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_ACTIVITY_MODE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_EVALUATION_RESOURCE_TYPE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_EVALUATION_RESOURCE_UUID;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_TRAINING_SESSION;

public class TrainingSessionAdapter extends RecyclerView.Adapter<TrainingSessionAdapter.TrainingSessionViewHolder> {
    private Context context;
    private Curriculum curriculum;
    private Day day;
    private ActivityMode activityMode;
    private EvaluationResourceType evaluatingResourceType;

    TrainingSessionAdapter(Context context, Curriculum curriculum, Day day,
                           ActivityMode activityMode,
                           EvaluationResourceType evaluatingResourceType) {
        this.context = context;
        this.curriculum = curriculum;
        this.day = day;
        this.activityMode = activityMode;
        this.evaluatingResourceType = evaluatingResourceType;
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
        holder.labelTextView.setText(trainingSession.getLabel());
        switch (activityMode) {
            case EVALUATION:
                holder.evaluatedImageView.setVisibility(View.VISIBLE);
                if (day.isEvaluated()) {
                    holder.evaluatedImageView.setImageResource(R.drawable.baseline_check_black_18);
                } else {
                    holder.evaluatedImageView.setImageResource(R.drawable.baseline_close_black_18);
                }
                holder.imageViewOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TrainingSessionActivity.class);
                        intent.putExtra(BUNDLE_KEY_TRAINING_SESSION, trainingSession);
                        intent.putExtra(BUNDLE_KEY_ACTIVITY_MODE, activityMode.label);
                        intent.putExtra(BUNDLE_KEY_EVALUATION_RESOURCE_TYPE, evaluatingResourceType.label);
                        intent.putExtra(BUNDLE_KEY_EVALUATION_RESOURCE_UUID, curriculum.getUuid());
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                break;
            case READ:
                holder.evaluatedImageView.setVisibility(View.GONE);
                holder.imageViewOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TrainingSessionActivity.class);
                        intent.putExtra(BUNDLE_KEY_TRAINING_SESSION, trainingSession);
                        intent.putExtra(BUNDLE_KEY_ACTIVITY_MODE, activityMode.label);
                        intent.putExtra(BUNDLE_KEY_EVALUATION_RESOURCE_TYPE, evaluatingResourceType.label);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                break;

            case UNKNOWN:
        }
    }

    @Override
    public int getItemCount() {
        return day.getSessions().size();
    }

    class TrainingSessionViewHolder extends RecyclerView.ViewHolder {

        private final TextView labelTextView;
        private final ImageView imageView, expandedImageView, evaluatedImageView;
        private final ImageView imageViewOpen;

        TrainingSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.text_view_label);
            imageView = itemView.findViewById(R.id.image_view);
            imageViewOpen = itemView.findViewById(R.id.image_view_open);
            expandedImageView = itemView.findViewById(R.id.image_view_expand);
            evaluatedImageView = itemView.findViewById(R.id.image_view_evaluated);
        }
    }

}


