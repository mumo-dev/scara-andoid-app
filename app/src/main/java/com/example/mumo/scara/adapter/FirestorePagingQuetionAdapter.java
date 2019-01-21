package com.example.mumo.scara.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mumo.scara.GlideApp;
import com.example.mumo.scara.R;
import com.example.mumo.scara.model.Question;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirestorePagingQuetionAdapter extends FirestorePagingAdapter<Question, FirestorePagingQuetionAdapter.QuestionViewHolder> {

    private Context mContext;

    public FirestorePagingQuetionAdapter(@NonNull FirestorePagingOptions<Question> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull QuestionViewHolder holder, int position, @NonNull Question model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.question_layout, parent, false);
        return new QuestionViewHolder(view);
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        private ImageView mUserPhotoIcon;
        private TextView mUserNameTxtView;
        private TextView mQuestionPostTime;
        private TextView mQuestionTxtView;
        private ImageView mQuestionPhoto;
        private TextView mQuestionAnswers;
        private TextView mQuestionVotes;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserPhotoIcon = itemView.findViewById(R.id.imv_user_icon);
            mUserNameTxtView = itemView.findViewById(R.id.tv_username);
            mQuestionPostTime = itemView.findViewById(R.id.tv_post_time);
            mQuestionTxtView = itemView.findViewById(R.id.question);
            mQuestionPhoto = itemView.findViewById(R.id.question_photo);
            mQuestionAnswers = itemView.findViewById(R.id.tv_no_answers);
            mQuestionVotes = itemView.findViewById(R.id.tv_no_votes);

        }

        public void bind(Question question) {
            mUserNameTxtView.setText(question.getUsername());
            mQuestionTxtView.setText(question.getText());
            mQuestionAnswers.setText(question.getAnswers() + " Answers");
            mQuestionVotes.setText(question.getVotes() + " votes");

            if(question.getImageReference().length() != 0 || question.getImageReference() != null ) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReference("images/" + question.getImageReference() + ".jpg");

                GlideApp.with(mContext)
                        .load(storageReference)
                        .into(mQuestionPhoto);

            }
        }
    }
}



