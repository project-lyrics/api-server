package com.projectlyrics.server.domain.view.repository;

import com.projectlyrics.server.domain.view.domain.View;

public interface ViewQueryRepository {
    View findById(Long id);
}
