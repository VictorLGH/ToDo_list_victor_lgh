package com.isep.simov.todo.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.isep.simov.todo.R;
import com.isep.simov.todo.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.user_firstname)
    TextView userFirstName;
    @BindView(R.id.user_lastname)
    TextView userLastName;
    @BindView(R.id.user_mail)
    TextView userMail;
    @BindView(R.id.user_age)
    TextView userAge;


    /*SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());*/


    public UserHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(v -> {
        });
    }

    public void bindUser(User user) {
        userFirstName.setText(user.getUserFirstname());
        userLastName.setText(user.getUserLastname());
        userAge.setText(user.getUserAge()+"");
        userMail.setText(user.getUserEmail());
        /*if (task.isPriority()) {
            priorityLine.setVisibility(View.VISIBLE);
        }
        if (!task.getTags().isEmpty()) {
            String[] tags = task.getTags().split(";");
            tagsEditText.setTags(tags);
            tagsEditText.setVisibility(View.VISIBLE);

//            ViewGroup.LayoutParams lp = priorityLine.getLayoutParams();
//            lp.height = lp.height * 2;
//            priorityLine.setLayoutParams(lp);
        } else {
            tagsEditText.setVisibility(View.GONE);
        }
        if (task.getLocalization() != null) {
            if (!task.getLocalization().isEmpty()) {
                String placeName = (String) task.getLocalization().get("placeName");
                if (!Strings.isNullOrEmpty(placeName)) {
                    localizationTextView.setText(placeName);
                    locationIcon.setVisibility(View.VISIBLE);
                    localizationTextView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            locationIcon.setVisibility(View.GONE);
            localizationTextView.setVisibility(View.GONE);
        }*/

    }
}
