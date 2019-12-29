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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.Day;
import net.mavericklabs.bos.utils.ActivityMode;
import net.mavericklabs.bos.utils.EvaluationResourceType;

public class CurriculumDayAdapter extends RecyclerView.Adapter<CurriculumDayAdapter.DayViewHolder> {
    private Context context;
    private Curriculum curriculum;
    private ActivityMode activityMode;
    private EvaluationResourceType evaluatingResourceType;

    public CurriculumDayAdapter(Context context, Curriculum curriculum, ActivityMode activityMode,
                                EvaluationResourceType evaluatingResourceType) {
        this.context = context;
        this.curriculum = curriculum;
        this.activityMode = activityMode;
        this.evaluatingResourceType = evaluatingResourceType;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_day, parent, false);
        return new DayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DayViewHolder holder, int position) {
        final Day day = curriculum.getDays().get(position);
        holder.labelTextView.setText(day.getLabel());
        final RecyclerView recyclerView = holder.trainingSessionsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new TrainingSessionAdapter(context, curriculum,day,activityMode,
                evaluatingResourceType));
        holder.expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getVisibility() == View.GONE) {
                    // it's collapsed - expand it
                    recyclerView.setVisibility(View.VISIBLE);
                    holder.expandedImageView.setImageResource(R.drawable.baseline_expand_less_black_24);
                } else {
                    // it's expanded - collapse it
                    recyclerView.setVisibility(View.GONE);
                    holder.expandedImageView.setImageResource(R.drawable.baseline_expand_more_black_24);
                }

//                ObjectAnimator animation = ObjectAnimator.of(mItemDescription, "maxLines", mItemDescription.getMaxLines());
//                animation.setDuration(200).start();
            }
        });

        switch (activityMode){
            case EVALUATION:
                holder.evaluatedImageView.setVisibility(View.VISIBLE);
                if (day.isEvaluated()){
                    holder.evaluatedImageView.setImageResource(R.drawable.baseline_check_black_18);
                }else{
                    holder.evaluatedImageView.setImageResource(R.drawable.baseline_close_black_18);
                }
                break;
            case READ:
                holder.evaluatedImageView.setVisibility(View.GONE);
                break;
            case UNKNOWN:
        }
    }

    @Override
    public int getItemCount() {
        return curriculum.getDays().size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder {

        private final TextView labelTextView;
        private final ImageView imageView, expandedImageView,evaluatedImageView;
        private final CardView cardView;
        private final RecyclerView trainingSessionsRecyclerView;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.text_view_label);
            imageView = itemView.findViewById(R.id.image_view);
            evaluatedImageView = itemView.findViewById(R.id.image_view_evaluated);
            expandedImageView = itemView.findViewById(R.id.image_view_expand);
            cardView = itemView.findViewById(R.id.card_view);
            trainingSessionsRecyclerView = itemView.findViewById(R.id.recycler_view_training_sessions);
        }
    }

}


