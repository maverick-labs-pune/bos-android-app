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

import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.activity.CurriculumActivity;
import net.mavericklabs.bos.object.Curriculum;
import net.mavericklabs.bos.object.Day;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.utils.AppLogger;
import net.mavericklabs.bos.utils.DateUtil;
import net.mavericklabs.bos.utils.EvaluationResourceType;
import net.mavericklabs.bos.utils.Util;

import java.util.List;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_ACTIVITY_MODE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_EVALUATION_RESOURCE_TYPE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_EVALUATION_RESOURCE_UUID;
import static net.mavericklabs.bos.utils.Constants.CURRICULUM;
import static net.mavericklabs.bos.utils.Constants.EVALUATION;
import static net.mavericklabs.bos.utils.Constants.TRAINING_SESSION;

public class DailyPlannerAdapter extends RecyclerView.Adapter<DailyPlannerAdapter.EvaluationResourceViewHolder> {
    private Context context;
    private List<RealmEvaluationResource> realmEvaluationResources;
    private AppLogger appLogger = new AppLogger(getClass().toString());

    public DailyPlannerAdapter(Context context, List<RealmEvaluationResource> realmEvaluationResources) {
        this.context = context;
        this.realmEvaluationResources = realmEvaluationResources;
    }

    @NonNull
    @Override
    public EvaluationResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_evaluated_resource, parent, false);
        return new EvaluationResourceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EvaluationResourceViewHolder holder, int position) {
        final RealmEvaluationResource realmEvaluationResource = realmEvaluationResources.get(position);
        final EvaluationResourceType type = Util.getEvaluationResourceType(realmEvaluationResource.getType());
        switch (type) {
            case USER:
                holder.resourceLabelTextView.setText(realmEvaluationResource.getUser().getFullName());

                break;
            case GROUP:
                holder.resourceLabelTextView.setText(realmEvaluationResource.getResource().getLabel());
                holder.entityTextView.setText(realmEvaluationResource.getGroup().getLabel());
                break;
            case UNKNOWN:
        }
        String data = realmEvaluationResource.getData();
        final RealmResource realmResource = realmEvaluationResource.getResource();
        switch (realmResource.getType()) {
            case CURRICULUM:
                Curriculum curriculum = Util.convertRealmResourceToCurriculum(realmEvaluationResource);
                appLogger.logInformation("realmEvaluationResource");
                appLogger.logInformation(realmEvaluationResource.getUuid());
                int totalNumberOfDays = curriculum.getDays().size();
                int completedDays = 0;
                for (Day day : curriculum.getDays()) {
                    appLogger.logInformation(day.getUuid());
                    appLogger.logInformation(String.valueOf(day.isEvaluated()));
                    if (day.isEvaluated()) {
                        completedDays++;
                    }
                }

                holder.imageView.setImageResource(R.drawable.ic_training_session);
                holder.resourceTypeTextView.setText("Curriculum");
                holder.lastUpdatedTextView.setText("Last changed : " + DateUtil.dateToString(curriculum.getLastModificationTime()));
                int progress = (int) ((double) completedDays / totalNumberOfDays * 100);
                holder.progressBar.animateProgress(1000, 0, progress);
//                holder.progressTextView.setText("Progress : " + completedDays+ "/" + totalNumberOfDays );
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CurriculumActivity.class);
                        intent.putExtra(BUNDLE_KEY_ACTIVITY_MODE, EVALUATION);
                        intent.putExtra(BUNDLE_KEY_EVALUATION_RESOURCE_TYPE, type.label);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(BUNDLE_KEY_EVALUATION_RESOURCE_UUID, realmEvaluationResource.getUuid());
                        context.startActivity(intent);
                    }
                });
                break;
            case TRAINING_SESSION:
                holder.resourceTypeTextView.setText("Training Session");
                holder.imageView.setImageResource(R.drawable.ic_training_session);

                break;

            default:
                break;
        }

    }

    @Override
    public int getItemCount() {
        return realmEvaluationResources.size();
    }

    class EvaluationResourceViewHolder extends RecyclerView.ViewHolder {

        private final TextView resourceLabelTextView, entityTextView, resourceTypeTextView,
                lastUpdatedTextView;
        private final RoundedHorizontalProgressBar progressBar;
        private final CardView cardView;
        private final ImageView imageView;

        EvaluationResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            resourceLabelTextView = itemView.findViewById(R.id.text_view_resource_label);
            resourceTypeTextView = itemView.findViewById(R.id.text_view_resource_type);
            entityTextView = itemView.findViewById(R.id.text_view_entity_name);
            progressBar = itemView.findViewById(R.id.progress_bar);
            lastUpdatedTextView = itemView.findViewById(R.id.text_view_last_updated);
            cardView = itemView.findViewById(R.id.card_view);
            imageView = itemView.findViewById(R.id.image_view);
        }

    }

}


