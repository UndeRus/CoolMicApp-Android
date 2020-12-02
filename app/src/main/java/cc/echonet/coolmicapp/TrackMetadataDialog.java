/*
 *      Copyright (C) Jordan Erickson                     - 2014-2020,
 *      Copyright (C) Löwenfelsen UG (haftungsbeschränkt) - 2015-2020
 *       on behalf of Jordan Erickson.
 *
 * This file is part of Cool Mic.
 *
 * Cool Mic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Mic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Mic.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cc.echonet.coolmicapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import cc.echonet.coolmicapp.Configuration.Profile;

public class TrackMetadataDialog {
    private final static class Adapter extends ArrayAdapter<String> {
        private final @NotNull Profile profile;

        public Adapter(@NonNull Context context, @NotNull Profile profile) {
            super(context, R.layout.metadata_list_entry, R.id.metadata_key, profile.getTrack().getKeys());
            this.profile = profile;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final @NonNull View view = super.getView(position, convertView, parent);
            final @NonNull String key = Objects.requireNonNull(super.getItem(position));
            final @NonNull String value = profile.getTrack().getValue(key, "");

            ((TextView)view.findViewById(R.id.metadata_key)).setText(key);
            ((TextView)view.findViewById(R.id.metadata_value)).setText(value);

            view.setOnClickListener(v -> PromptDialog.prompt(getContext(), key, value, result -> setMetadata(key, result)));

            view.findViewById(R.id.metadata_delete_key).setOnClickListener(v -> setMetadata(key, null));

            return view;
        }

        public void reloadData() {
            clear();
            addAll(profile.getTrack().getKeys());
            notifyDataSetChanged();
        }

        public void setMetadata(@NotNull String key, @Nullable String value) {
            profile.edit();
            profile.getTrack().setValue(key, value);
            profile.apply();
            reloadData();
        }
    }

    private final @NotNull Context context;
    private final @NotNull Profile profile;
    private final @NotNull AlertDialog.Builder builder;
    private final @NotNull Adapter adapter;
    private @Nullable Runnable onDone = null;

    private void addKey() {
        final @NotNull AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final @NotNull String[] supported = new String[]{"artist", "title"};

        builder.setItems(supported, (dialog, which) -> {
            dialog.dismiss();
            PromptDialog.prompt(context, supported[which], null, result -> adapter.setMetadata(supported[which], result));
        });

        builder.show();
    }

    private void done() {
        if (onDone != null)
            onDone.run();
    }

    private void setup() {
        final @NotNull ListView listView = new ListView(context);

        listView.setAdapter(adapter);

        builder.setView(listView);

        builder.setCancelable(true);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> done());
        builder.setNeutralButton(R.string.trackmetadata_add, null);

        builder.setOnCancelListener(dialog -> done());
    }

    @Contract(pure = true)
    public TrackMetadataDialog(@NotNull Context context, @NotNull Profile profile) {
        this.context = context;
        this.profile = profile;
        this.builder = new AlertDialog.Builder(context);
        this.adapter = new Adapter(context, profile);
        setup();
    }

    public void show() {
        final @NotNull AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> addKey()));
        dialog.show();
    }

    public void setOnDone(@Nullable Runnable onDone) {
        this.onDone = onDone;
    }
}
