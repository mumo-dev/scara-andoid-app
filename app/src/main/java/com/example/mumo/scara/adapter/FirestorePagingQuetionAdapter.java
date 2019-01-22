package com.example.mumo.scara.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mumo.scara.GlideApp;
import com.example.mumo.scara.R;
import com.example.mumo.scara.model.Question;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FirestorePagingQuetionAdapter extends FirestorePagingAdapter<Question, FirestorePagingQuetionAdapter.QuestionViewHolder> {

    private Context mContext;

    OnImageClickListener imageClickListener;

    public interface OnImageClickListener {
        void openImageFullScreenDialog(String imageRef);
    }

    public FirestorePagingQuetionAdapter(@NonNull FirestorePagingOptions<Question> options, Context context,
                                         OnImageClickListener listener) {
        super(options);
        mContext = context;
        imageClickListener = listener;
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

        private boolean mHasVoted;
        private int mCurrentVotes;

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

        public void bind(final Question question) {

            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            List<String> votes = question.getVotes();

            mHasVoted = votes.contains(userId);
            mCurrentVotes = votes.size();

            mUserNameTxtView.setText(question.getUsername());
            mQuestionTxtView.setText(question.getText());
            mQuestionAnswers.setText(question.getAnswers() + " Answers");
            mQuestionVotes.setText(mCurrentVotes + " votes");



            if (question.getImageReference().length() != 0 || question.getImageReference() != null) {
                mQuestionPhoto.setVisibility(View.VISIBLE);

                final StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReference("images/" + question.getImageReference() + ".jpg");

                GlideApp.with(mContext)
                        .load(storageReference)
                        .into(mQuestionPhoto);

                mQuestionPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageClickListener.openImageFullScreenDialog("images/" + question.getImageReference() + ".jpg");
                    }
                });



            }

            mQuestionVotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vote( question.getQuestionId(), userId, mHasVoted);

                }
            });
        }

        private void vote(String questionId, String userId, boolean hasVoted) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DocumentReference docRef = firestore.collection("questions").document(questionId);

            if (!hasVoted) {
                docRef.update("votes", FieldValue.arrayUnion(userId));
                mCurrentVotes++;
                mQuestionVotes.setText(mCurrentVotes + " votes");
                mHasVoted = true;
            } else {
                docRef.update("votes", FieldValue.arrayRemove(userId));
                mCurrentVotes--;
                mQuestionVotes.setText(mCurrentVotes + " votes");
                mHasVoted = false;
            }

        }
    }
}



