package org.triplepy.sh8email.sh8.activities.mailbox.list;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;

import org.triplepy.sh8email.sh8.R;
import org.triplepy.sh8email.sh8.activities.base.BaseActivity;
import org.triplepy.sh8email.sh8.activities.mailbox.list.di.DaggerMailListComponent;
import org.triplepy.sh8email.sh8.activities.mailbox.list.di.MailListModule;
import org.triplepy.sh8email.sh8.activities.mailbox.list.presenter.MailListPresenter;
import org.triplepy.sh8email.sh8.activities.mailbox.list.presenter.MailListPresenterImpl;
import org.triplepy.sh8email.sh8.adapter.MailBoxAdapter;
import org.triplepy.sh8email.sh8.api.Sh8Client;
import org.triplepy.sh8email.sh8.data.Mail;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MailListActivity extends BaseActivity implements MailListPresenter.View {
    private MailBoxAdapter adapter;
    private ArrayList<Mail> mails;

    @Inject
    Sh8Client client;
    @Inject
    MailListPresenterImpl presenter;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_list_activity);
        ButterKnife.bind(this);
        DaggerMailListComponent.builder().mailListModule(new MailListModule(this)).build().inject(this);

        mails = new ArrayList<>();
        adapter = new MailBoxAdapter(mails, this);

        presenter.getMailBoxWithId(getSessionId());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        collapsingToolbarLayout.setExpandedTitleGravity(Gravity.START);
        collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.START);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.getMailBoxWithId(getSessionId());
            swipeRefreshLayout.setRefreshing(false);
        });
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(v -> finish());

    }

    @Override
    public void setupMailBox(ArrayList<Mail> mails) {
        this.mails.clear();
        this.mails.addAll(mails);
        adapter.notifyDataSetChanged();
    }
}
