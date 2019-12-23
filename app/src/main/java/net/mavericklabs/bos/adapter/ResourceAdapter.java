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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.activity.CurriculumActivity;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmGroup;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.utils.Activity;
import net.mavericklabs.bos.utils.AlertCallback;
import net.mavericklabs.bos.utils.AlertUtil;

import java.util.List;

import io.realm.Realm;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_ACTIVITY_MODE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_RESOURCE_KEY;
import static net.mavericklabs.bos.utils.Constants.CURRICULUM;
import static net.mavericklabs.bos.utils.Constants.FILE;
import static net.mavericklabs.bos.utils.Constants.READ;
import static net.mavericklabs.bos.utils.Constants.TRAINING_SESSION;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder> {
    private Context context;
    private List<RealmResource> resources;
    private Activity activityType;
    private android.app.Activity activity;
    private RealmGroup group;

    public ResourceAdapter(android.app.Activity activity, List<RealmResource> resources,
                           Activity activityType) {
        this.context = activity.getApplicationContext();
        this.resources = resources;
        this.activity = activity;
        this.activityType = activityType;
    }

    public ResourceAdapter(android.app.Activity activity, List<RealmResource> resources,
                           Activity activityType, RealmGroup group) {
        this.context = activity.getApplicationContext();
        this.resources = resources;
        this.activity = activity;
        this.activityType = activityType;
        this.group = group;
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        switch (activityType) {
            case Group:
            case Resource:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_resource, parent, false);
                return new ResourceViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        final RealmResource realmResource = resources.get(position);
        holder.getLabel().setText(realmResource.getLabel());
        switch (realmResource.getType()) {
            case CURRICULUM:
                holder.getImageView().setImageResource(R.drawable.baseline_calendar_today_black_48);
                holder.getResourceType().setText("Curriculum");
                setOnClickListener(holder, realmResource);
                break;

            case FILE:
                holder.getImageView().setImageResource(R.drawable.baseline_attachment_black_48);
                holder.getResourceType().setText("File");
                break;

            case TRAINING_SESSION:
                holder.getImageView().setImageResource(R.drawable.baseline_calendar_today_black_48);
                holder.getResourceType().setText("Training session");
                setOnClickListener(holder, realmResource);
                break;

            default:
                break;
        }

    }

    private void setOnClickListener(ResourceViewHolder holder, final RealmResource realmResource) {
        switch (activityType) {
            case Resource:
                holder.getViewResourceButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CurriculumActivity.class);
                        intent.putExtra(BUNDLE_KEY_ACTIVITY_MODE,READ);
                        intent.putExtra(BUNDLE_KEY_RESOURCE_KEY, realmResource.getKey());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                break;
            case Group:
                holder.getViewResourceButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CurriculumActivity.class);
                        intent.putExtra(BUNDLE_KEY_ACTIVITY_MODE,READ);
                        intent.putExtra(BUNDLE_KEY_RESOURCE_KEY, realmResource.getKey());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                holder.getAssignResourceButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertUtil.showAlert(activity, "Are you sure", new AlertCallback() {
                            @Override
                            public void positiveButton() {
                                RealmEvaluationResource realmEvaluationResource =
                                        new RealmEvaluationResource(realmResource, group);
                                RealmHandler.copyToRealm(realmEvaluationResource);
                            }

                            @Override
                            public void negativeButton() {

                            }
                        });
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    class ResourceViewHolder extends RecyclerView.ViewHolder {

        private final TextView label;
        private final TextView resourceType;
        private final ImageView imageView;
        private final CardView cardView;
        private final Button viewResourceButton, assignResourceButton;

        ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.text_view_label);
            resourceType = itemView.findViewById(R.id.text_view_type);
            imageView = itemView.findViewById(R.id.image_view);
            cardView = itemView.findViewById(R.id.card_view);
            assignResourceButton = itemView.findViewById(R.id.button_assign_resource);
            viewResourceButton = itemView.findViewById(R.id.button_view_resource);
        }

        TextView getLabel() {
            return label;
        }

        TextView getResourceType() {
            return resourceType;
        }

        ImageView getImageView() {
            return imageView;
        }

        CardView getCardView() {
            return cardView;
        }

        public Button getAssignResourceButton() {
            return assignResourceButton;
        }

        public Button getViewResourceButton() {
            return viewResourceButton;
        }
    }

}


