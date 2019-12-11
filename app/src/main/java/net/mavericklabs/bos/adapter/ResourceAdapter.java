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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.mavericklabs.bos.R;
import net.mavericklabs.bos.activity.CurriculumActivity;
import net.mavericklabs.bos.realm.RealmResource;

import java.util.List;

import static net.mavericklabs.bos.utils.Constants.CURRICULUM;
import static net.mavericklabs.bos.utils.Constants.FILE;
import static net.mavericklabs.bos.utils.Constants.TRAINING_SESSION;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder> {
    private Context context;
    private List<RealmResource> resources;

    public ResourceAdapter(Context context, List<RealmResource> resources) {
        this.context = context;
        this.resources = resources;
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_resource, parent, false);
        return new ResourceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        final RealmResource realmResource = resources.get(position);
        holder.getLabel().setText(realmResource.getLabel());
        switch (realmResource.getType()) {
            case CURRICULUM:
                holder.getImageView().setImageResource(R.drawable.baseline_calendar_today_black_48);
                holder.getResourceType().setText("Curriculum");
                holder.getCardView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CurriculumActivity.class);
                        intent.putExtra("resourceKey",realmResource.getKey());
                        context.startActivity(intent);
                    }
                });
                break;

            case FILE:
                holder.getImageView().setImageResource(R.drawable.baseline_attachment_black_48);
                holder.getResourceType().setText("File");
                break;

            case TRAINING_SESSION:
                holder.getImageView().setImageResource(R.drawable.baseline_calendar_today_black_48);
                holder.getResourceType().setText("Training session");

                break;

            default:
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

        ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.text_view_label);
            resourceType = itemView.findViewById(R.id.text_view_type);
            imageView = itemView.findViewById(R.id.image_view);
            cardView = itemView.findViewById(R.id.card_view);
        }

        TextView getLabel() {
            return label;
        }

        public TextView getResourceType() {
            return resourceType;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public CardView getCardView() {
            return cardView;
        }
    }

}


