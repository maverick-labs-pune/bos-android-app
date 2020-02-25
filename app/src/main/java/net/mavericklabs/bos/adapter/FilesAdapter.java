/*
 * Copyright (c) 2020. Maverick Labs
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
import android.net.Uri;
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
import net.mavericklabs.bos.activity.ImageViewActivity;
import net.mavericklabs.bos.activity.PDFActivity;
import net.mavericklabs.bos.activity.SelectAthleteActivity;
import net.mavericklabs.bos.activity.VideoActivity;
import net.mavericklabs.bos.object.File;
import net.mavericklabs.bos.realm.RealmEvaluationResource;
import net.mavericklabs.bos.realm.RealmGroup;
import net.mavericklabs.bos.realm.RealmHandler;
import net.mavericklabs.bos.realm.RealmResource;
import net.mavericklabs.bos.realm.RealmUser;
import net.mavericklabs.bos.utils.AlertCallback;
import net.mavericklabs.bos.utils.AlertUtil;
import net.mavericklabs.bos.utils.EvaluationResourceType;
import net.mavericklabs.bos.utils.Util;

import java.util.List;

import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_ACTIVITY_MODE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_EVALUATION_RESOURCE_TYPE;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_RESOURCE_KEY;
import static net.mavericklabs.bos.utils.Constants.BUNDLE_KEY_URI;
import static net.mavericklabs.bos.utils.Constants.CURRICULUM;
import static net.mavericklabs.bos.utils.Constants.FILE;
import static net.mavericklabs.bos.utils.Constants.READ;
import static net.mavericklabs.bos.utils.Constants.TRAINING_SESSION;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ResourceViewHolder> {
    private Context context;
    private List<File> files;
    private android.app.Activity activity;
    private RealmGroup group;

    public FilesAdapter(android.app.Activity activity, List<File> files) {
        this.context = activity.getApplicationContext();
        this.files = files;
        this.activity = activity;
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
        final File fileObject = files.get(position);
        holder.labelTextView.setText(fileObject.getLabel());
        holder.imageView.setImageResource(R.drawable.baseline_attachment_black_48);
        holder.resourceTypeTextView.setText("File");
        holder.assignResourceImageView.setVisibility(View.GONE);
        holder.viewResourceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.io.File file = Util.getFileInsideBOSDirectory(fileObject, context);
                String fileExtension = Util.getFileExtension(fileObject.getUrl());
                switch (fileExtension) {
                    case ".pdf": {
                        Intent intent = new Intent(context, PDFActivity.class);
                        intent.putExtra(BUNDLE_KEY_RESOURCE_KEY, fileObject.getKey());
                        intent.putExtra(BUNDLE_KEY_URI, Uri.fromFile(file).toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    }
                    case ".mp4": {
                        Intent intent = new Intent(context, VideoActivity.class);
                        intent.putExtra(BUNDLE_KEY_RESOURCE_KEY, fileObject.getKey());
                        intent.putExtra(BUNDLE_KEY_URI, Uri.fromFile(file).toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    }
                    case ".png":
                    case ".jpg":
                    case ".jpeg":
                        Intent intent = new Intent(context, ImageViewActivity.class);
                        intent.putExtra(BUNDLE_KEY_RESOURCE_KEY, fileObject.getKey());
                        intent.putExtra(BUNDLE_KEY_URI, Uri.fromFile(file).toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    default:
                        break;
                }


            }

        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class ResourceViewHolder extends RecyclerView.ViewHolder {

        private final TextView labelTextView;
        private final TextView resourceTypeTextView;
        private final ImageView imageView;
        private final CardView cardView;
        private final ImageView viewResourceImageView, assignResourceImageView;

        ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.text_view_label);
            resourceTypeTextView = itemView.findViewById(R.id.text_view_type);
            imageView = itemView.findViewById(R.id.image_view);
            cardView = itemView.findViewById(R.id.card_view);
            assignResourceImageView = itemView.findViewById(R.id.image_view_assign_resource);
            viewResourceImageView = itemView.findViewById(R.id.image_view_view_resource);
        }
    }

}


